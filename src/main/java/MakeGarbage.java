// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.

import static com.yahoo.garbage.Utils.BytesToMB;
import static com.yahoo.garbage.Utils.dumpOldGen;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.yahoo.garbage.GarbageRunnable;
import com.yahoo.garbage.Landfill;
import com.yahoo.garbage.PSOldGenMonitorLoopRunnable;
import com.yahoo.garbage.Utils;
import com.yahoo.garbage.jmx.GarbageCollectionEventNotification;
import com.yahoo.garbage.jmx.NotificationService;


/**
 * A Simple class that lets us loop so we have enough time to run jps and jcmd in another terminal to check
 **/
public class MakeGarbage {

    static Random r = new Random();

    public static void main(String[] args) throws InterruptedException {
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

        MemoryPoolMXBean PSOldGenMbean = null;

        for (MemoryPoolMXBean mb : memoryPoolMXBeans) {
            if (MemoryType.HEAP == mb.getType()) {
                MemoryUsage usage = mb.getUsage();
                if (!"PS Old Gen".equals(mb.getName())) {
                    System.err.println(mb.getName() + "\n" + usage + "\n=============");
                }

                if (mb.isUsageThresholdSupported()) {
                    if ("PS Old Gen".equals(mb.getName())) {
                        PSOldGenMbean = mb;
                    }
                    try {

                        /**
                         * usage.getMax is the total tenured generations. For example -Xmx1g, I get a tenured generation
                         * of about 683Mb This code sets the used to 68Mb.
                         * 
                         * isUsageThresholdExceeded gets set when I hit 73Mb.
                         */

                        // collection threshold triggers a notification after a collection.
                        // we want this really really low, so we can get post collection notifications
                        long collectionThreshold = 1;//Utils.findThreshold(usage, 0.10d);

                        // usage threshold triggers a notification before a collection so we can cause one.
                        long usageThreshold = Utils.findThreshold(usage, 0.10d);

                        mb.setCollectionUsageThreshold(collectionThreshold);
                        mb.setUsageThreshold(usageThreshold);

                        System.err.println("Set collection threshold for " + mb.getName() + " to "
                                        + BytesToMB(collectionThreshold) + " of " + BytesToMB(usage.getMax()) + "MB");
                        System.err.println("Set usage threshold for " + mb.getName() + " to "
                                        + BytesToMB(usageThreshold) + " of " + BytesToMB(usage.getMax()) + "MB");
                    } catch (UnsupportedOperationException u) {
                    }
                }
            }
        }
        dumpOldGen("========start  usage:\n", PSOldGenMbean);

        int kbToGenerate = 1;
        int sleepBeforeGC = 0;

        switch (args.length) {
            case 2:
                sleepBeforeGC = Integer.valueOf(args[1]);

            case 1:
                kbToGenerate = Integer.valueOf(args[0]);
        }

        Landfill landfill = new Landfill(sleepBeforeGC);
        boolean useNotifications = true;

        if (useNotifications) {
            List<GarbageCollectionEventNotification> listeners = new ArrayList<GarbageCollectionEventNotification>();
            new NotificationService(listeners, landfill);
            new Thread(landfill.getCleaner()).start();
        }

        // if you enable this thread, we generate just enough to cause the gc to run a bunch
        // it's useful.
        boolean enableSpinningThread = true;

        if (enableSpinningThread) {
            new Thread(new PSOldGenMonitorLoopRunnable(PSOldGenMbean, landfill, false)).start();
        }


        new Thread(new GarbageRunnable(kbToGenerate, landfill)).start();

    }

    static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

}
