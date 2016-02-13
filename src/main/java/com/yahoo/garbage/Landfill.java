// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.
package com.yahoo.garbage;

import java.util.ArrayList;
import java.util.List;

/**
 * This is where the garbage goes. Clear will force a gc.
 * 
 * @author areese
 *
 */
public class Landfill {

    private final List<byte[]> shortTermBytes = new ArrayList<byte[]>();
    private final List<byte[]> longTermBytes = new ArrayList<byte[]>();
    private final int sleepBeforeGC;
    private int counter;
    private final CleaningRunnable cleaner;

    public Landfill(int sleepBeforeGC) {
        this.sleepBeforeGC = sleepBeforeGC;
        // terrible, cleaner can call us before we're completely constructed.
        this.cleaner = new CleaningRunnable(this);
    }

    public void clear() {
        shortTermBytes.clear();
        longTermBytes.clear();
        Utils.resetCleaned();
        if (sleepBeforeGC > 0) {
            System.err.println(Thread.currentThread().getId() + "========== Sleeping " + sleepBeforeGC);
            try {
                Thread.sleep(sleepBeforeGC);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Utils.gc();
    }

    public void addShort(byte[] b) {
        if (counter % 100 == 0) {
            shortTermBytes.clear();
        }
        counter++;
        shortTermBytes.add(b);
    }

    public void addLong(byte[] b) {
        longTermBytes.add(b);
    }

    public void clearNotification() {
        cleaner.setClean();
    }

    public CleaningRunnable getCleaner() {
        return cleaner;
    }
}
