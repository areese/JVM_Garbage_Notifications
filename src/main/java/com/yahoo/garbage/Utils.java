// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.
package com.yahoo.garbage;

import java.lang.management.MemoryPoolMXBean;
import java.util.Date;
import java.util.Random;

/**
 * Small bits like Random, output, gc, and throttle.
 * 
 * @author areese
 *
 */
public class Utils {
    public static final Random r = new Random();
    public static final ThreadSafeSimpleDateFormat fmt = new ThreadSafeSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static byte[] randomBytes(long len) {
        byte[] ret = new byte[(int) len];
        r.nextBytes(ret);
        return ret;
    }


    /**
     * Yes, I'm that lazy
     * 
     * @param kb in
     * @return MB out
     */
    public static long BytesToMB(long kb) {
        return KB(kb) / 1024;
    }

    /**
     * Yes, I'm that lazy
     * 
     * @param bytes in
     * @return KB out
     */
    public static long KB(long bytes) {
        return bytes / 1024;
    }


    /**
     * Thread.sleep allows the gc to do too much work. So you'll never see the usage notification as easily. This busy
     * work that ends up working nicely.
     */
    public static void throttle() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // for (int k = 0; k < 100; k++) {
        // double a = Utils.r.nextDouble();
        // double db = Math.sqrt(a);
        // double c = db * a;
        // Math.hypot(a, c);
        // }

    }


    public static void dumpOldGen(String s, MemoryPoolMXBean PSOldGen) {
        Utils.output(s + PSOldGen.getUsage() + "\ncollection\n" + PSOldGen.getCollectionUsage() + "---------------");
    }


    /**
     * Force a gc. Some people recommend calling it 3 times, but with 8u60 once seems to work.
     */
    public static void gc() {
        cleaned = false;
        for (int i = 0; i < 1; i++) {
            System.gc();
        }
    }


    /**
     * This outputs a string starting with a datestamp just like the gc. When you're dumping gc logs to stdout, it's
     * really nice to have it take the same format.
     * 
     * @param value string to write
     */
    public static void output(String value) {
        // 2016-02-02T20:32:45.523+0800:
        System.err.println(fmt.format(new Date()) + " " + Thread.currentThread().getId() + " " + value);
    }


    public static long MBToBytes(int amount) {
        return KBToBytes(amount) * 1024;
    }

    public static long KBToBytes(int amount) {
        return amount * 1024;
    }


    static volatile boolean cleaned;

    public static void resetCleaned() {
        cleaned = false;
    }

    public static boolean cleaned() {
        return cleaned;
    }

    public static void hit() {
        cleaned = true;
    }

}
