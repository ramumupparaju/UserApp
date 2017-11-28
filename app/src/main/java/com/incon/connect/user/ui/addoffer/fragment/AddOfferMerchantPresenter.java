package com.incon.connect.user.ui.addoffer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.addoffer.AddOfferMerchantFragmentResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.dto.addoffer.AddOfferRequest;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 10/8/2017.
 */

public class AddOfferMerchantPresenter extends BasePresenter<AddOfferMerchantContract.View>
        implements AddOfferMerchantContract.Presenter {
    private static final String TAG = AddOfferMerchantPresenter.class.getName();
    private Context appContext;
    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void getCategories(int merchantId) {
        getView().showProgress(appContext.getString(R.string.progress_categories));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object categoriesList) {
                        getView().loadCategoriesList((List<FetchCategories>) categoriesList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        getView().handleException(errorDetails);
                    }

                    @Override
                    public void onComplete() {
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().getCategories(merchantId).subscribe(observer);
        addDisposable(observer);
    }

    public void addOffer(AddOfferRequest addOfferRequest) {
        getView().showProgress(appContext.getString(R.string.progress_addoffer_merchant_fragment));
        DisposableObserver<AddOfferMerchantFragmentResponse> observer = new
                DisposableObserver<AddOfferMerchantFragmentResponse>() {
                    @Override
                    public void onNext(AddOfferMerchantFragmentResponse
                                               addOfferMerchantFragmentResponse) {
                        getView().hideProgress();
                        getView().loadAddOfferMerchant(
                                addOfferMerchantFragmentResponse);

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        getView().handleException(errorDetails);

                    }

                    @Override
                    public void onComplete() {

                    }
                };
       // AppApiService.getInstance().addOffer(addOfferRequest).subscribe(observer);
      //  addDisposable(observer);

    }


}
