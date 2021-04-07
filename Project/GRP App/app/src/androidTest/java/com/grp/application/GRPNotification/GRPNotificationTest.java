package com.grp.application.GRPNotification;

import android.content.Context;

import com.grp.application.Application;

import junit.framework.TestCase;

public class GRPNotificationTest extends TestCase {

    public void testGetInstance() {
        GRPNotification notification = GRPNotification.getInstance(Application.context);
        GRPNotification notificationTest = GRPNotification.getInstance(Application.context);
        assertEquals(notification, notificationTest);
    }

    public void testSendNotification() {
        GRPNotification notification = GRPNotification.getInstance(Application.context);
        notification.sendNotification(Application.context);
        assertEquals(notification.getProperSWorn(), false);
    }
}