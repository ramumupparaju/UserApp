package com.incon.connect.user.ui.favorites;

import com.incon.connect.user.apimodel.components.favorites.FavoritesResponse;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 11/4/2017.
 */

public interface FavoritesContract {
    interface View extends BaseView {
        //void favoritesProductResponse();
        void loadFavoritesProducts(List<FavoritesResponse> favoritesResponseList);
    }

    interface Presenter {
        void doFavoritesProductApi(int userId, int productId);

    }
}
