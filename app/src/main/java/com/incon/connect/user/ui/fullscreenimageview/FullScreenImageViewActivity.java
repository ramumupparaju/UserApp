package com.incon.connect.user.ui.fullscreenimageview;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.incon.connect.user.R;
import com.incon.connect.user.databinding.ActivityFullscreenImageViewBinding;
import com.incon.connect.user.ui.BaseActivity;

/**
 * Created on 05 Dec 2016 at 12:19 PM.
 *
 */
public class FullScreenImageViewActivity extends BaseActivity {

    private ActivityFullscreenImageViewBinding binding;
    private String filePath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fullscreen_image_view;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        // handle events from here using android binding
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        loadData();
    }

    private void loadData() {
        Bundle bundle = getIntent().getExtras();
        filePath = bundle.getString(IntentConstants.IMAGE_PATH);
        loadImageUsingGlide(filePath, binding.uploadedImage);
    }

}
