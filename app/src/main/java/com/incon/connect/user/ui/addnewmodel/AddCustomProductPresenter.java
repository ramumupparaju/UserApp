package com.incon.connect.user.ui.addnewmodel;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.dto.addnewmodel.AddCustomProductModel;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by PC on 10/4/2017.
 */

public class AddCustomProductPresenter extends BasePresenter<AddCustomProductContract.View> implements
        AddCustomProductContract.Presenter {

    private static final String TAG = AddCustomProductPresenter.class.getName();
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

    // model search implemenatation
    @Override
    public void doModelSearchApi(String modelNumberToSearch) {
        getView().showProgress(appContext.getString(R.string.progress_loading));
        DisposableObserver<List<ModelSearchResponse>> observer =
                new DisposableObserver<List<ModelSearchResponse>>() {
                    @Override
                    public void onNext(List<ModelSearchResponse> searchResponseList) {
                        getView().loadModelNumberData(searchResponseList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgress();
                        Pair<Integer, String> errorDetails = ErrorMsgUtil.getErrorDetails(e);
                        getView().handleException(errorDetails);
                        if (errorDetails.first != AppConstants.ErrorCodes.NO_NETWORK) {
                            getView().loadModelNumberData(null);
                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().hideProgress();
                    }
                };
        AppApiService.getInstance().modelNumberSearch(modelNumberToSearch).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void addingNewModel(int merchantId, AddCustomProductModel addCustomProductModel) {
        getView().showProgress(appContext.getString(R.string.progress_add_new_model));
        DisposableObserver<ModelSearchResponse> observer = new
                DisposableObserver<ModelSearchResponse>() {
                    @Override
                    public void onNext(ModelSearchResponse addNewModelResponse) {
                        getView().hideProgress();
                        getView().addNewModel(addNewModelResponse);
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
       // AppApiService.getInstance().addingNewModel(merchantId, addCustomProductModel).subscribe(observer);
       // addDisposable(observer);
    }

}
