// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.
package com.yahoo.garbage;

/**
 * This threads whole purpose in life is to create lots of garbage. We want the gc to run, so we stick garbage in the
 * landfill. When we get a notification, it'll empty the landfill and force a gc.
 * 
 * @author areese
 *
 */
public class GarbageRunnable implements Runnable {
    private final int amount;
    private Landfill landfill;

    public GarbageRunnable(long l, Landfill landfill) {
        this.amount = (int) l;
        this.landfill = landfill;
    }

    @Override
    public void run() {

        int i = 0;
        while (true) {
            i++;
            byte[] b = Utils.randomBytes(amount);
            landfill.addShort(b);
            landfill.addLong(Utils.randomBytes(amount));

            if (1 == i % 100) {
                // shortTermBytes.clear();
            }

            Utils.throttle();
        }
    }
}
