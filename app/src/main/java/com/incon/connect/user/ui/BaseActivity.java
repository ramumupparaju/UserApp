package com.incon.connect.user.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.AppUtils;
import com.incon.connect.user.R;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.custom.view.AppAlertVerticalTwoButtonsDialog;
import com.incon.connect.user.ui.login.LoginActivity;
import com.incon.connect.user.utils.DeviceUtils;
import com.incon.connect.user.utils.Logger;
import com.incon.connect.user.utils.SharedPrefsUtils;

import java.io.File;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.incon.connect.user.AppConstants.HttpErrorCodeConstants.ERROR_UNAUTHORIZED;

public abstract class BaseActivity extends AppCompatActivity implements BaseView,
        AppConstants {

    public static final int TRANSACTION_TYPE_ADD = 0;
    public static final int TRANSACTION_TYPE_REPLACE = 1;
    public static final int TRANSACTION_TYPE_REMOVE = 2;
    private AppAlertVerticalTwoButtonsDialog dialog;

    protected abstract int getLayoutId();

    protected abstract void initializePresenter();

    protected abstract void onCreateView(Bundle saveInstanceState);

    protected BasePresenter presenter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(getLayoutId());

        initializePresenter();

        onCreateView(savedInstanceState);
    }

    public void loadImageUsingGlide(String imagePath, ImageView imageView) {

        if (imagePath.contains(WEB_IMAGE)) {
            AppUtils.loadImageFromApi(imageView, imagePath);
            return;
        }
        Glide.with(this).load(new File(imagePath))
                .into(imageView);
    }

    public void setBasePresenter(BasePresenter basePresenter) {
        presenter = basePresenter;
        if (presenter != null) {
            this.presenter.initialize(getIntent().getExtras());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void showProgress(String message) {
        hideProgress();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        AppUtils.showSnackBar(((ViewGroup) getWindow().getDecorView()
                .findViewById(android.R.id.content)).getChildAt(0), errorMessage);
    }

    @Override
    public void handleException(Pair<Integer, String> error) {
        if (error.first == 601) {
            updateAppDialog();
        } else if (error.first == ERROR_UNAUTHORIZED) {
            Logger.e("handleException", "onLogoutCalled() from BE ");
            onLogoutClick();
        } else {
            showErrorMessage(error.second);
        }

    }

    public void onLogoutClick() {
        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.loginProvider();
        String phoneNumber = sharedPrefsUtils.getStringPreference(LoginPrefs.USER_PHONE_NUMBER);

        clearData();
        sharedPrefsUtils.setStringPreference(LoginPrefs.USER_PHONE_NUMBER, phoneNumber);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent
                .FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void clearData() {
//        TCDbHelper.getDbInstance().clearAllTables();
        //new OfflineDataManager().clearAllCache(this);

        SharedPrefsUtils.appProvider().clear();
        SharedPrefsUtils.cacheProvider().clear();
        SharedPrefsUtils.loginProvider().clear();
    }

    public void updateAppDialog() {
        dialog = new AppAlertVerticalTwoButtonsDialog.AlertDialogBuilder(this, new
                AlertDialogCallback() {
                    @Override
                    public void alertDialogCallback(byte dialogStatus) {
                        switch (dialogStatus) {
                            case AlertDialogCallback.OK:
                                dialog.dismiss();
                                finish();
                                break;
                            case AlertDialogCallback.CANCEL:
                                dialog.dismiss();
                                finish();
                                openPlayStore();
                                break;
                            default:
                                break;
                        }
                    }
                }).title(getString(R.string.app_update))
                .button1Text(getString(R.string.action_cancel))
                .button2Text(getString(R.string.action_ok))
                .setButtonOrientation(LinearLayout.HORIZONTAL)
                .build();
        dialog.showDialog();
        dialog.setButtonBlueUnselectBackground();
    }

    public void onClickNavigattion(LatLng srcLatLng) {
        /*Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + mLatitude + "," + mLongitude));
        startActivity(intent);*/
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + srcLatLng.latitude + "," + srcLatLng.longitude + "&daddr="));
        startActivity(intent);
    }


    private void openPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="
                            + appPackageName)));
        }
    }

    public void replaceFragmentAndAddToStack(Class<? extends Fragment> claz,
                                             Bundle bundle) {
        replaceFragmentAndAddToStackWithTargetFragment(
                claz, null, -1, bundle, 0, 0, TRANSACTION_TYPE_REPLACE);

    }

    public void replaceFragmentAndAddToStackWithTargetFragment(
            Class<? extends Fragment> claz,
            Fragment targetFragment,
            int targetFragmentRequestCode, Bundle bundle,
            int animIn, int animOut, int transactionType) {
        // Code to replace the currently shown fragment with another one
        replaceFragment(claz, targetFragment,
                targetFragmentRequestCode, bundle, animIn, animOut, transactionType, true);
    }

    public Fragment replaceFragment(Class<? extends Fragment> claz,
                                    Bundle bundle) {
        return replaceFragment(claz, null, -1, bundle, 0, 0, TRANSACTION_TYPE_REPLACE, false);
    }

    @Nullable
    private Fragment replaceFragment(Class<? extends Fragment> claz, Fragment targetFragment,
                                     int targetFragmentRequestCode, Bundle bundle,
                                     int animIn, int animOut, int transactionType, boolean
                                                 backStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Fragment fragment = fragmentManager.findFragmentByTag(claz.getCanonicalName());
        Fragment fragment = null;
        try {
            fragment = (Fragment) Class.forName(claz.getCanonicalName()).newInstance();
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (fragment == null) {
            return fragment;
        }

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        if (targetFragment != null && targetFragmentRequestCode != -1) {
            fragment.setTargetFragment(targetFragment, targetFragmentRequestCode);
        }

        if (animIn != 0 && animOut != 0) {
            fragmentTransaction.setCustomAnimations(animIn, animOut);
        }
        if (transactionType == TRANSACTION_TYPE_ADD) {
            fragmentTransaction.add(R.id.container, fragment, claz.getCanonicalName());
        } else if (transactionType == TRANSACTION_TYPE_REMOVE) {
            fragmentTransaction.remove(fragment);
        } else {
            fragmentTransaction.replace(R.id.container, fragment, claz.getCanonicalName());
        }
        if (backStack) {
            fragmentTransaction.addToBackStack(claz.getClass().getName());
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
        return fragment;
    }

    /**
     * chnaged selected views with primary and remaining as gray
     */
    public void changeSelectedViews(LinearLayout parentLayout, int selectedTag) {

        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childAt = parentLayout.getChildAt(i);

            LinearLayout linearLayout;
            if (childAt instanceof LinearLayout) {
                linearLayout = (LinearLayout) childAt;
            } else {
                HorizontalScrollView horizontalScrollView = (HorizontalScrollView) childAt;
                LinearLayout childAt1 = (LinearLayout) horizontalScrollView.getChildAt(i);
                changeSelectedViews(childAt1, selectedTag);
                return;
            }
            int tag = (Integer) linearLayout.getTag();
            boolean isSelectedView = tag == selectedTag;
            (getBottomImageView(linearLayout)).setColorFilter(getResources().getColor(isSelectedView ? R.color.colorPrimary : R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            (getBottomTextView(linearLayout)).setTextColor(ContextCompat.getColor(this, isSelectedView ? R.color.colorPrimary : R.color.colorAccent));
        }
    }

    /**
     * if options is grater than 5 we are adding horizontall scrollview else adding in linear layout
     *
     * @param parentLayout
     * @param namesArray
     * @param imagesArray
     * @param onClickListener
     */
    public void setBottomViewOptions(LinearLayout parentLayout, ArrayList<String> namesArray, ArrayList<Integer> imagesArray, ArrayList<Integer> tagsArray, View.OnClickListener onClickListener) {
        int length = namesArray.size();

        boolean isScrollAdded = length > 5 ? true : false;
        HorizontalScrollView horizontalScrollView = null;
        LinearLayout linearLayout = null;
        //Implemented in scroll view
        if (isScrollAdded) {
            horizontalScrollView = getcustomHorizontalScroll();
            linearLayout = new LinearLayout(this);
            horizontalScrollView.addView(linearLayout);
            parentLayout.addView(horizontalScrollView);
        }

        for (int i = 0; i < length; i++) {
            LinearLayout customBottomView = getCustomBottomView(isScrollAdded);

            getBottomTextView(customBottomView).setText(namesArray.get(i));
            getBottomImageView(customBottomView).setImageResource(imagesArray.get(i));

            customBottomView.setTag(tagsArray.get(i));
            customBottomView.setOnClickListener(onClickListener);
            if (horizontalScrollView != null) {
                linearLayout.addView(customBottomView);
            } else {
                parentLayout.addView(customBottomView);
            }
        }
    }

    private HorizontalScrollView getcustomHorizontalScroll() {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        return horizontalScrollView;
    }

    public LinearLayout getCustomBottomView(boolean isScroll) {

        int dp5 = (int) DeviceUtils.convertPxToDp(5);
        Context context = this;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(dp5, dp5, dp5, dp5);
        linearLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llp;

        if (isScroll) {
            llp = new LinearLayout.LayoutParams((int) DeviceUtils.convertPxToDp(80), ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            llp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.weight = 1;
        }
        linearLayout.setLayoutParams(llp);
        int dp24 = (int) DeviceUtils.convertPxToDp(20);
        AppCompatImageView imageView = new AppCompatImageView(context);
        imageView.setId(R.id.view_logo);
        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(dp24, dp24);
        imageView.setLayoutParams(imageViewLayoutParams);
        imageView.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
        linearLayout.addView(imageView);

        TextView textView = new TextView(context);
        textView.setId(R.id.view_tv);
        textView.setTextColor(ContextCompat.getColor(context, R.color.black));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(DeviceUtils.convertSpToPixels(4, context));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        linearLayout.addView(textView);
        try {
            Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
            textView.setTypeface(type);
        } catch (Exception e) {

        }

        return linearLayout;
    }

    public AppCompatImageView getBottomImageView(LinearLayout linearLayout) {
        return ((AppCompatImageView) linearLayout.findViewById(R.id.view_logo));
    }

    public TextView getBottomTextView(LinearLayout linearLayout) {
        return ((TextView) linearLayout.findViewById(R.id.view_tv));
    }
}
