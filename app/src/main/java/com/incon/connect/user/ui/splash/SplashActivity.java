package com.incon.connect.user.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.incon.connect.user.R;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.home.HomeActivity;
import com.incon.connect.user.ui.login.LoginActivity;
import com.incon.connect.user.utils.SharedPrefsUtils;

import static com.incon.connect.user.AppConstants.LoginPrefs.LOGGED_IN;


public class SplashActivity extends BaseActivity implements SplashContract.View {

    private static final int SPLASH_DELAY = 2000;
    private static final String TAG = SplashActivity.class.getName();
    private Handler handler;

    @Override
    protected int getLayoutId() {
        return (R.layout.activity_splash);
    }

    @Override
    protected void initializePresenter() {
        SplashPresenter splashPresenter = new SplashPresenter();
        splashPresenter.setView(this);
        setBasePresenter(splashPresenter);
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        setContentView(getLayoutId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void navigateToMainScreen() {
        splashTimeout();
    }

    private void splashTimeout() {
        handler = new Handler();
        Runnable splashRunnable = new Runnable() {
            @Override
            public void run() {

                boolean isLoggedIn = SharedPrefsUtils.loginProvider().
                        getBooleanPreference(LOGGED_IN, false);
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(splashRunnable, SPLASH_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
