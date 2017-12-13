package com.incon.connect.user.ui.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.R;
import com.incon.connect.user.custom.view.CirclePageIndicator;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.login.LoginActivity;
import com.incon.connect.user.ui.tutorial.adapter.TutorialPagerAdapter;
import com.incon.connect.user.utils.SharedPrefsUtils;


/**
 * Created by eFlair Android on 30-11-2017  03:35 PM.
 */

public class TutorialActivity extends BaseActivity implements View.OnClickListener {

    public static String TAG = TutorialActivity.class.getSimpleName();

    private int[] mResources;
    private TutorialPagerAdapter mTutorialPagerAdapter;
    private Button mContinueButton;
    private TextView mSkipButton;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tutorial;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        setContentView(getLayoutId());
        init();
    }

    private void init() {

        mViewPager = findViewById(R.id.pager);
        mIndicator = findViewById(R.id.indicator);
        mContinueButton = findViewById(R.id.tutorial_continue);
        mSkipButton = findViewById(R.id.tutorial_skip);


        mResources = new int[]{
                R.drawable.tutorial_1,
                R.drawable.tutorial_2,
                R.drawable.tutorial_3};

        mTutorialPagerAdapter = new TutorialPagerAdapter(this, mResources);
        mViewPager.setAdapter(mTutorialPagerAdapter);
        // ViewPager Indicator
        mIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setButtonVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setButtonVisibility(0);

        mSkipButton.setOnClickListener(this);
        mContinueButton.setOnClickListener(this);
    }


    private void setButtonVisibility(int position) {
        if (position == mResources.length - 1) {
            mContinueButton.setVisibility(View.VISIBLE);
        } else {
            mContinueButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        SharedPrefsUtils.cacheProvider().setBooleanPreference(AppConstants.CachePrefs.TUTORIAL_SHOWED, true);

        Intent intent = new Intent(TutorialActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
