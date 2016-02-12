// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.
package com.yahoo.garbage;

import static com.yahoo.garbage.Utils.throttle;

import java.lang.management.MemoryPoolMXBean;

/**
 * This is a thread that loops and monitors the threshold. This is test code so you can get a feel for how it works. The
 * best way is with the notification.
 * 
 * @author areese
 *
 */
public class PSOldGenMonitorLoopRunnable implements Runnable {
    private final MemoryPoolMXBean PSOldGen;
    private Landfill landfill;

    public PSOldGenMonitorLoopRunnable(MemoryPoolMXBean PSOldGen, Landfill landfill) {
        this.PSOldGen = PSOldGen;
        this.landfill = landfill;
    }


    @Override
    public void run() {
        while (true) {
            if (null != PSOldGen && PSOldGen.isCollectionUsageThresholdExceeded()) {
                Utils.dumpOldGen("========isCollectionUsageThresholdExceeded  usage:\n", PSOldGen);
                landfill.clear();
                // gc();
                PSOldGen.resetPeakUsage();
                throttle();
            }

            if (PSOldGen.isUsageThresholdExceeded()) {
                Utils.dumpOldGen("========isUsageThresholdExceeded  usage:\n", PSOldGen);
                landfill.clear();
                Utils.gc();
                PSOldGen.resetPeakUsage();
                throttle();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

}
