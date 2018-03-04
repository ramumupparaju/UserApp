package com.incon.connect.user.ui.pin.interfaces;

import android.app.Activity;

public interface LifeCycleInterface {

    /**
     * Called in {@link Activity#onResume()}
     */
    public void onActivityResumed(Activity activity);

    /**
     * Called in {@link Activity#onUserInteraction()}
     */
    public void onActivityUserInteraction(Activity activity);

    /**
     * Called in {@link Activity#onPause()}
     */
    public void onActivityPaused(Activity activity);
}
