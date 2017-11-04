package com.incon.connect.user.ui.favorites;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.incon.connect.user.ConnectApplication;
import com.incon.connect.user.R;
import com.incon.connect.user.api.AppApiService;
import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.ui.BasePresenter;
import com.incon.connect.user.utils.ErrorMsgUtil;

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
        super.initialize(extras);
        appContext = ConnectApplication.getAppContext();
    }



    @Override
    public void doFavoritesProductApi(int userId, int productId) {
        getView().showProgress(appContext.getString(R.string.progress_favorites));
        DisposableObserver<List<FavoritesResponse>> observer = new
                DisposableObserver<List<FavoritesResponse>>() {
                    @Override
                    public void onNext(List<FavoritesResponse> favoritesResponseList) {
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

        AppApiService.getInstance().favouritesProductApi(userId, productId).subscribe(observer);
        addDisposable(observer);

    }
}
