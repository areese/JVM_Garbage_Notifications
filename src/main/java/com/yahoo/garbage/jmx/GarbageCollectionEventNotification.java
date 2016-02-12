// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.
package com.yahoo.garbage.jmx;

import javax.management.Notification;
import javax.management.NotificationListener;

import com.yahoo.garbage.Landfill;
import com.yahoo.garbage.Utils;


/**
 * This gets called when there is a gc event.
 * 
 * @author areese
 *
 */
public class GarbageCollectionEventNotification implements NotificationListener {
    public void handleNotification(Notification notification, Object handback) {
        // we should check the event as well. it could be one of 2 types.
        Utils.output("Received service notification: " + notification);
        if (null != handback && handback instanceof Landfill) {
            // if we have a handback call it's clear.
            // Really should be an interface so you can do more complext things.
            ((Landfill) handback).clear();
        }
    }
}
