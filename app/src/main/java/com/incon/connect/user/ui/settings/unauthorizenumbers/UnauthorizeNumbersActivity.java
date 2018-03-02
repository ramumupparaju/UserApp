package com.incon.connect.user.ui.settings.unauthorizenumbers;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.FeedbackData;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ActivityUnauthorizenumbersBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.settings.unauthorizenumbers.adapter.UnauthorizeSeNumbersAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY HOME on 02-Mar-18.
 */

public class UnauthorizeNumbersActivity extends BaseActivity {
    private UnauthorizeSeNumbersAdapter unauthorizeNumbersAdapter;

    private ActivityUnauthorizenumbersBinding binding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_unauthorizenumbers;
    }

    @Override
    protected void initializePresenter() {
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setActivity(this);

        initViews();

    }

    private void initViews() {
        List<FeedbackData> feedbackData = new ArrayList<>();
        feedbackData.add(new FeedbackData("test1", "7779992501"));
        feedbackData.add(new FeedbackData("test2", "9638527412"));
        feedbackData.add(new FeedbackData("test3", "7799050905"));

        unauthorizeNumbersAdapter = new UnauthorizeSeNumbersAdapter(feedbackData);
        binding.recyclerviewUnauthorize.setAdapter(unauthorizeNumbersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        Context context = binding.getRoot().getContext();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.list_divider));
        binding.recyclerviewUnauthorize.addItemDecoration(dividerItemDecoration);
        binding.recyclerviewUnauthorize.setLayoutManager(linearLayoutManager);
    }

    //recyclerview click event
    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
        }
    };
}
