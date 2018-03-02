package com.incon.connect.user.ui.favorites;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.ui.history.fragments.PurchasedContract;
import com.incon.connect.user.ui.history.fragments.PurchasedPresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.HashMap;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 11/4/2017.
 */

public class FavoritesPresenter extends BasePresenter<FavoritesContract.View> implements
        FavoritesContract.Presenter {

    private static final String TAG = FavoritesPresenter.class.getName();
    private Context appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }


    @Override
    public void doGetAddressApi(int userId) {
        getView().showProgress(appContext.getString(R.string.progress_get_addresses));
        DisposableObserver<List<AddUserAddressResponse>> observer = new
                DisposableObserver<List<AddUserAddressResponse>>() {
                    @Override
                    public void onNext(List<AddUserAddressResponse> favoritesResponseList) {
                        getView().loadAddresses(favoritesResponseList);
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

        AppApiService.getInstance().getAddressesApi(userId).subscribe(observer);
        addDisposable(observer);
    }

    // add favotites
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
    public void doFavoritesProductApi(int userId, int addressId) {
        getView().showProgress(appContext.getString(R.string.progress_favorites));
        DisposableObserver<List<ProductInfoResponse>> observer = new
                DisposableObserver<List<ProductInfoResponse>>() {
                    @Override
                    public void onNext(List<ProductInfoResponse> favoritesResponseList) {
                        getView().loadFavoritesProducts(favoritesResponseList);
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

        AppApiService.getInstance().favouritesProductApi(userId, addressId).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void doAddAddressApi(final AddUserAddress addUserAddress) {
        getView().showProgress(appContext.getString(R.string.progress_favorites));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object favoritesResponseList) {
                        doGetAddressApi(addUserAddress.getSubscriberId());
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

        AppApiService.getInstance().addProductAddress(addUserAddress).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void serviceRequest(ServiceRequest serviceRequest) {
        PurchasedPresenter purchasedPresenter = new PurchasedPresenter();
        purchasedPresenter.initialize(null);
        purchasedPresenter.setView(purchasedView);
        purchasedPresenter.serviceRequest(serviceRequest);


    }

    @Override
    public void nearByServiceCenters(int brandId) {
        PurchasedPresenter purchasedPresenter = new PurchasedPresenter();
        purchasedPresenter.initialize(null);
        purchasedPresenter.setView(purchasedView);
        purchasedPresenter.nearByServiceCenters(brandId);

    }

    @Override
    public void getUsersListOfServiceCenters(int serviceCenterId) {
        PurchasedPresenter purchasedPresenter = new PurchasedPresenter();
        purchasedPresenter.initialize(null);
        purchasedPresenter.setView(purchasedView);
        purchasedPresenter.getUsersListOfServiceCenters(serviceCenterId);
    }

    public void addServiceEngineer(AddServiceEngineer serviceEngineer, int userId) {
        PurchasedPresenter purchasedPresenter = new PurchasedPresenter();
        purchasedPresenter.initialize(null);
        purchasedPresenter.setView(purchasedView);
        purchasedPresenter.addServiceEngineer(serviceEngineer, userId);
    }

    PurchasedContract.View purchasedView = new PurchasedContract.View() {
        @Override
        public void loadPurchasedHistory(List<ProductInfoResponse> productInfoResponses) {
            // do nothing
        }

        @Override
        public void loadAddresses(List<AddUserAddressResponse> favoritesResponseList) {
            // do nothing
        }

        @Override
        public void addedToFavorite() {
            // do nothing
        }

        @Override
        public void transferMobileNumber(Object response) {
            // do nothing
        }

        @Override
        public void deleteProduct(Object response) {
            // do nothing
        }

        @Override
        public void loadServiceRequest() {
            getView().loadServiceRequest();
        }

        @Override
        public void loadNearByServiceCenters(List<ServiceCenterResponse> serviceCenterResponses) {
            getView().loadNearByServiceCenters(serviceCenterResponses);
        }

        @Override
        public void loadUsersListOfServiceCenters(List<UsersListOfServiceCenters> listOfServiceCenters) {
            getView().loadUsersListOfServiceCenters(listOfServiceCenters);

        }

        @Override
        public void addedServiceEngineer(ProductInfoResponse o) {
            getView().addedServiceEngineer(o);
        }

        @Override
        public void showProgress(String message) {
            getView().showProgress(message);

        }

        @Override
        public void hideProgress() {
            getView().hideProgress();

        }

        @Override
        public void showErrorMessage(String errorMessage) {
            getView().showErrorMessage(errorMessage);

        }

        @Override
        public void handleException(Pair<Integer, String> error) {
            getView().handleException(error);

        }
    };


}
