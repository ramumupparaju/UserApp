package com.incon.connect.user.ui.favorites;

import com.incon.connect.user.apimodel.components.favorites.AddUserAddressResponse;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;
import com.incon.connect.user.dto.addfavorites.AddUserAddress;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 11/4/2017.
 */

public interface FavoritesContract {
    interface View extends BaseView {
        void loadAddresses(List<AddUserAddressResponse> favoritesResponseList);

        void loadFavoritesProducts(List<ProductInfoResponse> favoritesResponseList);
    }

    interface Presenter {
        void doGetAddressApi(int userId);

        void doFavoritesProductApi(int userId, int addressId);

        void doAddAddressApi(AddUserAddress addUserAddress);
    }
}
