package com.incon.connect.user.ui.addnewmodel;

import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.R;
import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.defaults.DefaultsResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.Brand;
import com.incon.connect.user.apimodel.components.search.Division;
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
    private ConnectApplication appContext;

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }

    @Override
    public void getCategories() {
        getView().showProgress(appContext.getString(R.string.progress_categories));
        DisposableObserver<DefaultsResponse> observer = new
                DisposableObserver<DefaultsResponse>() {
                    @Override
                    public void onNext(DefaultsResponse defaultsResponse) {
                        appContext.setCategoriesList(defaultsResponse.getCategories());
                        getView().loadCategoriesList();
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
        AppApiService.getInstance().defaultsApi().subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void getDivisionsFromCategoryId(int categoryId) {
        getView().showProgress(appContext.getString(R.string.progress_divisions));
        DisposableObserver<List<Division>> observer = new
                DisposableObserver<List<Division>>() {
                    @Override
                    public void onNext(List<Division> divisionList) {
                        getView().loadDivisionsList(divisionList);
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
        AppApiService.getInstance().divisionsFromCategoryId(categoryId).subscribe(observer);
        addDisposable(observer);
    }

    @Override
    public void getBrandsFromDivisionId(int divisionId) {
        getView().showProgress(appContext.getString(R.string.progress_brands));
        DisposableObserver<List<Brand>> observer = new
                DisposableObserver<List<Brand>>() {
                    @Override
                    public void onNext(List<Brand> brandList) {
                        getView().loadBrandsList(brandList);
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
        AppApiService.getInstance().brandsFromDivisionId(divisionId).subscribe(observer);
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
    public void addingCustomProduct(AddCustomProductModel addCustomProductModel) {
        getView().showProgress(appContext.getString(R.string.progress_add_product));
        DisposableObserver<Object> observer = new
                DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object addNewModelResponse) {
                        getView().hideProgress();
                        getView().addNewModel();
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
        AppApiService.getInstance().addingCustomProduct(addCustomProductModel).subscribe(observer);
        addDisposable(observer);
    }

}
