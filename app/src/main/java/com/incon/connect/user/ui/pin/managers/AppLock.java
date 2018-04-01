package com.incon.connect.user.ui.pin.managers;

import android.app.Activity;

import java.util.HashSet;

public abstract class AppLock {
    /**
     * ENABLE_PINLOCK type, uses at firt to define the password
     */
    public static final int ENABLE_PINLOCK = 0;
    /**
     * DISABLE_PINLOCK type, uses to disable the system by asking the current password
     */
    public static final int DISABLE_PINLOCK = 1;
    /**
     * CHANGE_PIN type, uses to change the current password
     */
    public static final int CHANGE_PIN = 2;
    /**
     * CONFIRM_PIN type, used to confirm the new password
     */
    public static final int CONFIRM_PIN = 3;
    /**
     * UNLOCK_PIN type, uses to ask the password to the user, in order to unlock the app
     */
    public static final int UNLOCK_PIN = 4;

    /**
     * LOGO_ID_NONE used to denote when a user has not set a logoId using {@link #setLogoId(int)}
     */
    public static final int LOGO_ID_NONE = -1;

    public static final String EXTRA_TYPE = "type";

    /**
     * DEFAULT_TIMEOUT, define the default timeout returned by {@link #getTimeout()}.
     * If you want to modify it, you can call {@link #setTimeout(long)}. Will be stored using
     * {@link android.content.SharedPreferences}
     */
    public static final long DEFAULT_TIMEOUT = 1000 * 10; // 10sec

    protected HashSet<String> mIgnoredActivities;

    public AppLock() {
        mIgnoredActivities = new HashSet<String>();
    }

    /**
     * Add an ignored activity to the {@link HashSet}
     */
    public void addIgnoredActivity(Class<?> clazz) {
        String clazzName = clazz.getName();
        this.mIgnoredActivities.add(clazzName);
    }

    /**
     * Remove an ignored activity to the {@link HashSet}
     */
    public void removeIgnoredActivity(Class<?> clazz) {
        String clazzName = clazz.getName();
        this.mIgnoredActivities.remove(clazzName);
    }

    /**
     * Get the timeout used in {@link #shouldLockSceen(Activity)}
     */
    public abstract long getTimeout();

    /**
     * Set the timeout used in {@link #shouldLockSceen(Activity)}
     */
    public abstract void setTimeout(long timeout);

    public abstract int getLogoId();

    public abstract void setLogoId(int logoId);

    public abstract boolean shouldShowForgot(int appLockType);

    public abstract void setShouldShowForgot(boolean showForgot);

    /**
     * Get whether the user backed out of the {@link AppLockActivity} previously
     */
    public abstract boolean pinChallengeCancelled();

    /**
     * Set whether the user backed out of the {@link AppLockActivity}
     */
    public abstract void setPinChallengeCancelled(boolean cancelled);


    /**
     * Get the only background timeout option used to determine if the time
     * spent in the activity must NOT be taken into account while calculating the timeout.
     */
    public abstract boolean onlyBackgroundTimeout();

    /**
     * Set whether the time spent on the activity must NOT be taken into account when calculating timeout.
     */
    public abstract void setOnlyBackgroundTimeout(boolean onlyBackgroundTimeout);

    public abstract void enable();

    public abstract void disable();

    public abstract void disableAndRemoveConfiguration();

    /**
     * Get the last active time of the app used by {@link #shouldLockSceen(Activity)}
     */
    public abstract long getLastActiveMillis();

    public abstract void setLastActiveMillis();

    public abstract boolean setPasscode(String passcode);

    /**
     * Check the {@link android.content.SharedPreferences} to see if fingerprint authentication is
     * enabled.
     */
    public abstract boolean isFingerprintAuthEnabled();

    /**
     * Enable or disable fingerprint authentication on the PIN screen.
     * @param enabled If true, enables the fingerprint reader if it is supported.  If false, will
     *                hide the fingerprint reader icon on the PIN screen.
     */
    public abstract void setFingerprintAuthEnabled(boolean enabled);

    public abstract boolean checkPasscode(String passcode);

    /**
     * Check the {@link android.content.SharedPreferences} to see if a password already exists
     */
    public abstract boolean isPasscodeSet();

    public abstract boolean isIgnoredActivity(Activity activity);

    public abstract boolean shouldLockSceen(Activity activity);
}
