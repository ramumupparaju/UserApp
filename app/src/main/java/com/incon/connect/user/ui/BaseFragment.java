package com.incon.connect.user.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.AppConstants;

public abstract class BaseFragment extends Fragment implements BaseView, AppConstants {

    protected BasePresenter presenter;
    private View v;

    protected abstract void initializePresenter();
    public abstract void setTitle();

    public boolean doRefresh;

    protected abstract View onPrepareView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState);

    public void setBasePresenter(BasePresenter basePresenter) {
        presenter = basePresenter;
        if (presenter != null) {
            this.presenter.initialize(null);
        }
    }

    public void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initializePresenter();
        v = onPrepareView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void refreshData(){

    }

    @Override
    public void showProgress(String message) {
        ((BaseActivity) getActivity()).showProgress(message);
    }

    @Override
    public void hideProgress() {
        ((BaseActivity) getActivity()).hideProgress();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        ((BaseActivity) getActivity()).showErrorMessage(errorMessage);
    }

    @Override
    public void handleException(Pair<Integer, String> error) {
        ((BaseActivity) getActivity()).handleException(error);
    }

}
