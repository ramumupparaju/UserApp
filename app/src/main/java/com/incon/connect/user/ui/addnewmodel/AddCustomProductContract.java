package com.incon.connect.user.ui.addnewmodel;

import com.incon.connect.user.apimodel.components.defaults.CategoryResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.dto.addnewmodel.AddCustomProductModel;
import com.incon.connect.user.ui.BaseView;

import java.util.List;

/**
 * Created by PC on 10/4/2017.
 */

public interface AddCustomProductContract {

    interface View extends BaseView {
        void addNewModel(ModelSearchResponse modelSearchResponse);

        void loadCategoriesList();

        void loadModelNumberData(List<ModelSearchResponse> modelSearchResponseList);

    }

    interface Presenter {
        void getCategories();

        void doModelSearchApi(String modelNumberToSearch);

        void addingNewModel(int merchantId, AddCustomProductModel addCustomProductModel);
    }
}
