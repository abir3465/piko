/*
 * Copyright (C) 2026 piko <https://github.com/crimera/piko>
 *
 * See the included NOTICE file for GPLv3 ยง7(b) terms that apply to this code.
 */

package app.morphe.extension.instagram.patches.performance;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import app.morphe.extension.instagram.utils.Pref;
import app.morphe.extension.shared.Logger;
import app.morphe.extension.shared.Utils;

/**
 * Asks Android to keep every Instagram screen at 60 Hz while "High performance mode" is on.
 * The request is a hint, so the system may still lower it (battery saver, heat, and so on).
 */
@SuppressWarnings("unused")
public final class HighPerformanceMode {
    private static final float TARGET_REFRESH_RATE = 60f;

    private static boolean initialized;

    private HighPerformanceMode() {
    }

    /** Called once from the application init hook. */
    public static synchronized void init() {
        if (initialized) return;
        initialized = true;
        try {
            Application app = (Application) Utils.getContext().getApplicationContext();
            app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override public void onActivityResumed(Activity activity) { apply(activity); }
                @Override public void onActivityCreated(Activity a, Bundle b) { }
                @Override public void onActivityStarted(Activity a) { }
                @Override public void onActivityPaused(Activity a) { }
                @Override public void onActivityStopped(Activity a) { }
                @Override public void onActivitySaveInstanceState(Activity a, Bundle b) { }
                @Override public void onActivityDestroyed(Activity a) { }
            });
        } catch (Throwable t) {
            Logger.printException(() -> "HighPerformanceMode init failed", t);
        }
    }

    private static void apply(Activity activity) {
        try {
            Window window = activity.getWindow();
            if (window == null) return;
            WindowManager.LayoutParams params = window.getAttributes();
            if (Pref.highPerformanceMode()) {
                if (params.preferredRefreshRate != TARGET_REFRESH_RATE) {
                    params.preferredRefreshRate = TARGET_REFRESH_RATE;
                    window.setAttributes(params);
                }
            } else if (params.preferredRefreshRate == TARGET_REFRESH_RATE) {
                // Only undo our own request, so Instagram's own choices are left alone.
                params.preferredRefreshRate = 0f;
                window.setAttributes(params);
            }
        } catch (Throwable t) {
            Logger.printException(() -> "HighPerformanceMode apply failed", t);
        }
    }
                }
