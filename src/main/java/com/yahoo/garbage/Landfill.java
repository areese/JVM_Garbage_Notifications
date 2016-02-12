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

    public void clear() {
        shortTermBytes.clear();
        longTermBytes.clear();
        Utils.gc();
    }

    public void addShort(byte[] b) {
        shortTermBytes.add(b);
    }

    public void addLong(byte[] b) {
        longTermBytes.add(b);
    }
}
