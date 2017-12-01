package com.incon.connect.user.ui.status;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.incon.connect.user.R;
import com.incon.connect.user.databinding.FragmentStatusBinding;
import com.incon.connect.user.databinding.StatusViewBinding;
import com.incon.connect.user.ui.BaseFragment;
import com.incon.connect.user.ui.home.HomeActivity;

/**
 * Created by PC on 12/1/2017.
 */

public class StatusFragment extends BaseFragment implements StatusContract.View {
    FragmentStatusBinding binding;

    private View rootView;

    @Override
    protected void initializePresenter() {

    }

    @Override
    public void setTitle() {
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.title_status));

    }

    @Override
    protected View onPrepareView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_status,
                    container, false);
            createStatusView();
            rootView = binding.getRoot();
        }
        setTitle();
        return rootView;
    }

    private void createStatusView() {
        int length;
        int[] statusDrawables;
        String[] statusNames;

        statusNames = new String[4];
        statusNames[0] = getString(R.string.bottom_option_service);
        statusNames[1] = getString(R.string.bottom_option_product);
        statusNames[2] = getString(R.string.bottom_option_showroom);
        statusNames[3] = getString(R.string.bottom_option_delete);

        statusDrawables = new int[4];
        statusDrawables[0] = R.drawable.ic_option_service_support;
        statusDrawables[1] = R.drawable.ic_option_product;
        statusDrawables[2] = R.drawable.ic_option_customer;
        statusDrawables[3] = R.drawable.ic_option_delete;
        length = statusNames.length;

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.MATCH_PARENT, length);
        params.setMargins(1, 1, 1, 1);
        for (int i = 0; i < length; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setWeightSum(4f);
            linearLayout.setGravity(Gravity.CENTER);
            binding.viewTv.setText(statusNames[i]);
            binding.viewLogo.setImageResource(statusDrawables[i]);
           /* View statusRootView = binding.getRoot();
            statusRootView.setTag(i);
            linearLayout.addView(statusRootView);*/
        }
    }
}





