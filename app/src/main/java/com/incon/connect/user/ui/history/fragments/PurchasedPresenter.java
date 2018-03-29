package com.incon.connect.user.ui.history.fragments;

import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.apimodel.components.review.ReviewData;
import com.incon.connect.user.apimodel.components.servicecenter.ServiceCenterResponse;
import com.incon.connect.user.apimodel.components.status.DefaultStatusData;
import com.incon.connect.user.apimodel.components.status.ServiceStatus;
import com.incon.connect.user.apimodel.components.userslistofservicecenters.UsersListOfServiceCenters;
import com.incon.connect.user.dto.servicerequest.ServiceRequest;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.ui.favorites.FavoritesContract;
import com.incon.connect.user.ui.favorites.FavoritesPresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.observers.DisposableObserver;


/**
 * Created on 31 May 2017 11:19 AM.
 */
public class PurchasedPresenter extends BasePresenter<PurchasedContract.View> implements
        PurchasedContract.Presenter {

    private static final String TAG = PurchasedPresenter.class.getName();
    private ConnectApplication appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void doProductPastHistoryApi(int userId) {
        getView().showProgress(appContext.getString(R.string.progress_loading_history));
        if (appContext.getStatusListResponses() == null) {
            getDefaultStatusData(userId);
            return;
        }

        DisposableObserver<ArrayList<ServiceStatus>> observer = new DisposableObserver<ArrayList<ServiceStatus>>() {
            @Override
            public void onNext(ArrayList<ServiceStatus> statusListResponses) {

                getView().onProductPastHistoryApi(statusListResponses);
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
        AppApiService.getInstance().fetchUserRequests(userId).subscribe(observer);
        addDisposable(observer);
    }

    private void getDefaultStatusData(final int userId) {
        // get status list
        DisposableObserver<List<DefaultStatusData>> observer = new DisposableObserver<List<DefaultStatusData>>() {
            @Override
            public void onNext(List<DefaultStatusData> statusListResponses) {
                appContext.setStatusListData(statusListResponses);
                doProductPastHistoryApi(userId);
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
        AppApiService.getInstance().getStatusList().subscribe(observer);
        addDisposable(observer);
    }

    // purchased
    @Override
    public void purchased(int userId) {
//        getView().showProgress(appContext.getString(R.string.progress_purchased_history));
        DisposableObserver<List<ProductInfoResponse>> observer = new
                DisposableObserver<List<ProductInfoResponse>>() {
                    @Override
                    public void onNext(List<ProductInfoResponse> productInfoResponses) {
                        getView().loadPurchasedHistory(productInfoResponses);
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
        AppApiService.getInstance().purchasedApi(userId).subscribe(observer);
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
    public void saveReviewsApi(HashMap<String, String> reviewsMap) {

        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object review) {
                        getView().saveReviews(review);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        getView().handleException(errorDetails);
                        getView().showErrorMessage(errorDetails.second);
                    }

                    @Override
                    public void onComplete() {
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().saveReviewsApi(reviewsMap).subscribe(observer);
        addDisposable(observer);
    }

    // delete product
    @Override
    public void deleteProduct(int warrantyId) {
        getView().showProgress(appContext.getString(R.string.progress_deleting_product));
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
    public void reviewToProduct(int productId) {
        getView().showProgress(appContext.getString(R.string.progress_loading_reviews));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        if (o != null) {
                            List<ReviewData> reviewData = (List<ReviewData>) o;
                            getView().productReviews(reviewData);
                        }
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
        AppApiService.getInstance().reviewsApi(productId).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void doProductSuggestions(int userId, int productId) {
        getView().showProgress(appContext.getString(R.string.progress_loading_suggestions));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        if (o != null) {
                            List<ReviewData> reviewData = (List<ReviewData>) o;
                            getView().productSuggestions(reviewData);
                        }
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
        AppApiService.getInstance().productSuggestionsApi(userId, productId).subscribe(observer);
        addDisposable(observer);
    }

    // service request
    @Override
    public void serviceRequest(ServiceRequest serviceRequest) {
        getView().showProgress(appContext.getString(R.string.progress_update_status));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object obj) {
                        getView().loadServiceRequest();
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
        AppApiService.getInstance().serviceRequest(serviceRequest).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void nearByServiceCenters(String type, int brandId, int userId) {
        getView().showProgress(appContext.getString(R.string.progress_finding_service_centers));
        DisposableObserver<List<ServiceCenterResponse>> observer = new
                DisposableObserver<List<ServiceCenterResponse>>() {
                    @Override
                    public void onNext(List<ServiceCenterResponse> serviceCenterResponses) {
                        getView().loadNearByServiceCenters(serviceCenterResponses);
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
        AppApiService.getInstance().findNearByServiceCenters(type, brandId, userId).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void getUsersListOfServiceCenters(int serviceCenterId) {
        getView().showProgress(appContext.getString(R.string.progress_finding_service_centers));
        DisposableObserver<List<UsersListOfServiceCenters>> observer = new
                DisposableObserver<List<UsersListOfServiceCenters>>() {
                    @Override
                    public void onNext(List<UsersListOfServiceCenters> listOfServiceCenters) {
                        getView().loadUsersListOfServiceCenters(listOfServiceCenters);
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
        AppApiService.getInstance().getUsersListOfServiceCenters(serviceCenterId).subscribe(observer);
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
    public void doTransferProductApi(String phoneNumber, String warrantyId) {
        getView().showProgress(appContext.getString(R.string.progress_transfering_product));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        getView().transferMobileNumber(o);
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
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().transferRequest(phoneNumber, warrantyId).subscribe(observer);
        addDisposable(observer);
    }

    FavoritesContract.View favoritesView = new FavoritesContract.View() {
        @Override
        public void loadAddresses(List<AddUserAddressResponse> favoritesResponseList) {
            getView().loadAddresses(favoritesResponseList);
        }

        @Override
        public void onLocationChanged() {

        }

        @Override
        public void loadFavoritesProducts(List<ProductInfoResponse> favoritesResponseList) {

        }

        @Override
        public void loadServiceRequest() {

        }

        @Override
        public void loadNearByServiceCenters(List<ServiceCenterResponse> serviceCenterResponses) {

        }

        @Override
        public void loadUsersListOfServiceCenters(List<UsersListOfServiceCenters> listOfServiceCenters) {

        }

        @Override
        public void addedServiceEngineer(ProductInfoResponse productInfoResponse) {

        }

        @Override
        public void transferMobileNumber(Object response) {

        }

        @Override
        public void deleteProduct(Object response) {
        }

        @Override
        public void productReviews(List<ReviewData> reviewDataList) {
            getView().productReviews(reviewDataList);

        }

        @Override
        public void productSuggestions(List<ReviewData> reviewDataList) {
            getView().productSuggestions(reviewDataList);
        }

        @Override
        public void saveReviews(Object saveReviews) {
            getView().saveReviews(saveReviews);
        }

        @Override
        public void onProductPastHistoryApi(ArrayList<ServiceStatus> statusListResponses) {
            //DO nothing
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

    @Override
    public void addServiceEngineer(AddServiceEngineer serviceEngineer, int userId) {
        getView().showProgress(appContext.getString(R.string.progress_adding_engineer));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        getView().addedServiceEngineer((ProductInfoResponse) o);
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
        AppApiService.getInstance().addServiceEngineer(serviceEngineer, userId).subscribe(observer);
        addDisposable(observer);
    }
}
