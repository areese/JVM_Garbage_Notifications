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

    public PSOldGenMonitorLoopRunnable(MemoryPoolMXBean PSOldGen, Landfill landfill, boolean clear) {
        this.PSOldGen = PSOldGen;
        this.landfill = (clear) ? landfill : null;
    }


    @Override
    public void run() {
        while (true) {

            if (null != PSOldGen && PSOldGen.isCollectionUsageThresholdExceeded()) {
                if (!Utils.cleaned()) {
                    Utils.hit();
                    Utils.dumpOldGen("========isCollectionUsageThresholdExceeded  usage:\n", PSOldGen);
                    if (null != landfill) {
                        landfill.clear();
                        // gc();
                        PSOldGen.resetPeakUsage();
                    }
                }
                throttle();
            }

            if (PSOldGen.isUsageThresholdExceeded()) {
                if (!Utils.cleaned()) {
                    Utils.hit();
                    Utils.dumpOldGen("========isUsageThresholdExceeded  usage:\n", PSOldGen);
                    if (null != landfill) {
                        landfill.clear();
                        PSOldGen.resetPeakUsage();
                    }
                }
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
