package com.wanjian.sao;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by wanjian on 2017/12/17.
 */

public class NotificationService extends AccessibilityService {
    public static final String ACTION = NotificationService.class.getName();


    @Override
    protected void onServiceConnected() {
//        Toast.makeText(this, "辅助功能已启动", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String s = event.getClassName().toString();
        if (s != null && s.equals("android.app.Notification")) {
            sendBroadcast(new Intent(ACTION));
        }


    }

    @Override
    public void onInterrupt() {

    }
}
