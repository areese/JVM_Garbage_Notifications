// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.
package com.yahoo.garbage.jmx;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

import javax.management.NotificationEmitter;
import javax.management.NotificationFilterSupport;

import com.yahoo.garbage.Landfill;


/**
 * This class is the listener for gc events. It's passed to the {@link MemoryMXBean} (don't get it confused with the
 * {@link MemoryPoolMXBean} like I did and wonder why notifications don't work.
 * 
 * @author areese
 *
 */
public class NotificationService {
    private List<GarbageCollectionEventNotification> listeners;

    /**
     * @param iListeners list of listeners.
     * @param handback landfill object the notification will get to clear things out.
     */
    public NotificationService(List<GarbageCollectionEventNotification> iListeners, Landfill handback) {
        this.listeners = iListeners;
        GarbageCollectionEventNotification myListener = new GarbageCollectionEventNotification();

        try {

            MemoryMXBean mb = ManagementFactory.getMemoryMXBean();

            // Create notification filter
            NotificationFilterSupport myFilter = new NotificationFilterSupport();

            // we care about collection threshold exceeded, you get this after a collection.
            myFilter.enableType(MemoryNotificationInfo.MEMORY_COLLECTION_THRESHOLD_EXCEEDED);
            // we really care about threshold exceeded, you get this before a collection under best case scenario.
            myFilter.enableType(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED);

            try {
                if (mb instanceof NotificationEmitter) {
                    NotificationEmitter emitter = (NotificationEmitter) mb;
                    emitter.addNotificationListener(myListener, myFilter, handback);
                }
            } catch (javax.management.RuntimeOperationsException | ClassCastException e) {
                e.printStackTrace();
            }

            listeners.add(myListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
