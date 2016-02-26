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

    static final String COLLECTION_TYPE_STRING = "java.management.memory.collection.threshold.exceeded";
    static final int COLLECTION_TYPE_HC = COLLECTION_TYPE_STRING.hashCode();
    static final String USAGE_TYPE_STRING = "java.management.memory.threshold.exceeded";
    static final int USAGE_TYPE_HC = USAGE_TYPE_STRING.hashCode();

    public void handleNotification(Notification notification, Object handback) {
        // we should check the event as well. it could be one of 2 types.
        Utils.output("Received service notification: " + notification + " of class " + notification.getClass());
        if (null != handback && handback instanceof Landfill) {
            // see what kind of notification it is.
            String type = notification.getType();
            // FIXME: add an enum and let landfill decide what to do.
            if (USAGE_TYPE_HC == type.hashCode()) {
                // if we have a handback call it's clear.
                // Really should be an interface so you can do more complex things.
                ((Landfill) handback).clearNotification();
            }
        }
    }
}
