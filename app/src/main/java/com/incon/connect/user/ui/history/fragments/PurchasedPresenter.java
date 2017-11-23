package com.incon.connect.user.ui.history.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.ui.favorites.FavoritesContract;
import com.incon.connect.user.ui.favorites.FavoritesPresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.HashMap;
import java.util.List;

import io.reactivex.observers.DisposableObserver;


/**
 * Created on 31 May 2017 11:19 AM.
 */
public class PurchasedPresenter extends BasePresenter<PurchasedContract.View> implements
        PurchasedContract.Presenter {

    private static final String TAG = PurchasedPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void purchased(int userId) {
        getView().showProgress(appContext.getString(R.string.progress_purchased_history));
        DisposableObserver<List<ProductInfoResponse>> observer = new
                DisposableObserver<List<ProductInfoResponse>>() {
                    @Override
                    public void onNext(List<ProductInfoResponse> productInfoResponses) {
                        getView().loadPurchasedHistory(productInfoResponses);
                        getView().hideProgress();
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
        AppApiService.getInstance().purchasedApi(userId).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void addToFavotites(HashMap<String, String> favoritesMap) {
        getView().showProgress(appContext.getString(R.string.progress_adding_to_favorites));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        getView().addedToFavorite();
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
        AppApiService.getInstance().addToFavotites(favoritesMap).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void deleteProduct(int warrantyId) {
        getView().showProgress(appContext.getString(R.string.progress_adding_to_favorites));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        getView().deleteProduct(o);
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
        AppApiService.getInstance().deleteProduct(warrantyId).subscribe(observer);
        addDisposable(observer);

    }

    @Override
    public void doGetAddressApi(int userId) {
        FavoritesPresenter favoritesPresenter = new FavoritesPresenter();
        favoritesPresenter.initialize(null);
        favoritesPresenter.setView(favoritesView);
        favoritesPresenter.doGetAddressApi(userId);
    }

    @Override
    public void doTransferProductApi(String phoneNumber, int userId) {
        getView().showProgress(appContext.getString(R.string.progress_adding_to_favorites));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        getView().transferMobileNumber(o);
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
        AppApiService.getInstance().transferRequest(phoneNumber,userId).subscribe(observer);
        addDisposable(observer);
    }

    FavoritesContract.View favoritesView = new FavoritesContract.View() {
        @Override
        public void loadAddresses(List<AddUserAddressResponse> favoritesResponseList) {
            getView().loadAddresses(favoritesResponseList);
        }

        @Override
        public void loadFavoritesProducts(List<ProductInfoResponse> favoritesResponseList) {

        }

        @Override
        public void showProgress(String message) {

        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void showErrorMessage(String errorMessage) {

        }

        @Override
        public void handleException(Pair<Integer, String> error) {

        }
    };

}
