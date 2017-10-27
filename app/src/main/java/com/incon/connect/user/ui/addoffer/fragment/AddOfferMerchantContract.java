package com.incon.connect.user.ui.addoffer.fragment;

import com.incon.connect.user.apimodel.components.addoffer.AddOfferMerchantFragmentResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 10/8/2017.
 */

public interface AddOfferMerchantContract {
    interface View extends BaseView {
        void loadAddOfferMerchant(AddOfferMerchantFragmentResponse merchantId);
        void loadCategoriesList(List<FetchCategories> categoriesList);
    }
    interface Presenter {
        void getCategories(int merchantId);

    }
}
