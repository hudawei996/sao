package com.wanjian.sao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

public class MainActivity extends AppCompatActivity {

    private View marquee;
    private WindowManager windowManagermanager;

    private View notificationMarquee;
    private View alwaysMarquee;

    private Handler handler = new Handler();
    private Runnable removeTask = new Runnable() {
        @Override
        public void run() {
            try {
                windowManagermanager.removeView(marquee);
            } catch (Exception e) {
            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showWindow();
            handler.postDelayed(removeTask, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        alwaysMarquee = findViewById(R.id.always_marquee);
        notificationMarquee = findViewById(R.id.notification_marquee);
        windowManagermanager = (WindowManager) getSystemService(WINDOW_SERVICE);
        marquee = LayoutInflater.from(this).inflate(R.layout.window, null);

        startService(new Intent(this, NotificationService.class));
        showWindow();


        findViewById(R.id.always).setSelected(true);
        findViewById(R.id.always).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    return;
                }
                v.setSelected(true);
                ((View) notificationMarquee.getParent()).setSelected(false);
                alwaysMarquee.setVisibility(View.VISIBLE);
                notificationMarquee.setVisibility(View.INVISIBLE);
                unregisterReceiver(receiver);
                handler.removeCallbacks(removeTask);
                try {
                    windowManagermanager.removeView(marquee);
                } catch (Exception e) {
                }
                showWindow();
            }
        });

        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    return;
                }
                if (isAccessibilitySettingsOn() == false) {
                    try {
                        windowManagermanager.removeView(marquee);
                    } catch (Exception e) {
                    }
                    Toast.makeText(MainActivity.this, "您需要先开启 骚 APP的辅助功能", Toast.LENGTH_SHORT).show();
//                    gotoNotificationAccessSetting();
                    startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 1);
                    return;

                }
                v.setSelected(true);
                ((View) alwaysMarquee.getParent()).setSelected(false);
                alwaysMarquee.setVisibility(View.INVISIBLE);
                notificationMarquee.setVisibility(View.VISIBLE);
                try {
                    windowManagermanager.removeView(marquee);
                } catch (Exception e) {
                }
                registerReceiver(receiver, new IntentFilter(NotificationService.ACTION));
            }
        });

        findViewById(R.id.reward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RewardActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            showWindow();
        }
    }

    private void showWindow() {

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.type = TYPE_SYSTEM_OVERLAY;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCHABLE | FLAG_NOT_TOUCH_MODAL;
        layoutParams.format = PixelFormat.RGBA_8888;
        try {
            windowManagermanager.addView(marquee, layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + NotificationService.class.getName();
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }

        return accessibilityFound;
    }
}
