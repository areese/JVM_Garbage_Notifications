// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.

import static com.yahoo.garbage.Utils.BytesToMB;
import static com.yahoo.garbage.Utils.dumpOldGen;
import static com.yahoo.garbage.Utils.output;

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
import com.yahoo.garbage.jmx.NotificationService;
import com.yahoo.garbage.jmx.GarbageCollectionEventNotification;


/**
 * A Simple class that lets us loop so we have enough time to run jps and jcmd in another terminal to check
 **/
public class MakeGarbage {

    static Random r = new Random();

    public static void main(String[] args) throws InterruptedException {
        output("ABC");
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

        List<MemoryPoolMXBean> collections = new ArrayList<MemoryPoolMXBean>();
        List<MemoryPoolMXBean> usages = new ArrayList<MemoryPoolMXBean>();

        MemoryPoolMXBean PSOldGen = null;

        for (MemoryPoolMXBean mb : memoryPoolMXBeans) {
            if (MemoryType.HEAP == mb.getType()) {
                MemoryUsage usage = mb.getUsage();
                if (!"PS Old Gen".equals(mb.getName())) {
                    System.err.println(mb.getName() + "\n" + usage + "\n=============");
                }
                if (mb.isUsageThresholdSupported()) {
                    if ("PS Old Gen".equals(mb.getName())) {
                        PSOldGen = mb;
                    }
                    try {

                        /**
                         * usage.getMax is the total tenured generations. For example -Xmx1g, I get a tenured generation
                         * of about 683Mb This code sets the used to 68Mb.
                         * 
                         * isUsageThresholdExceeded gets set when I hit 73Mb.
                         */
                        long threshold = Math.round(usage.getMax() * 0.10);

                        mb.setCollectionUsageThreshold(threshold);
                        collections.add(mb);

                        System.err.println("Set collection threshold for " + mb.getName() + " to "
                                        + BytesToMB(threshold) + " of " + BytesToMB(usage.getMax()) + "MB");
                    } catch (UnsupportedOperationException u) {
                    }
                    try {
                        long threshold = Math.round(usage.getMax() * 0.10);
                        mb.setUsageThreshold(threshold);
                        System.err.println("Set usage threshold for " + mb.getName() + " to " + BytesToMB(threshold)
                                        + " of " + BytesToMB(usage.getMax()) + "MB");
                        usages.add(mb);
                    } catch (UnsupportedOperationException u) {
                    }
                }
            }
        }
        dumpOldGen("========start  usage:\n", PSOldGen);

        Landfill landfill = new Landfill();
        boolean useNotifications = true;

        if (useNotifications) {
            List<GarbageCollectionEventNotification> listeners = new ArrayList<GarbageCollectionEventNotification>();
            NotificationService ns = new NotificationService(listeners, landfill);
        } else {
            new Thread(new PSOldGenMonitorLoopRunnable(PSOldGen, landfill)).start();
        }


        new Thread(new GarbageRunnable(Utils.KBToBytes(1), landfill)).start();

    }

    static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

}
