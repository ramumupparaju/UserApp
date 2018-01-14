package com.incon.connect.user.ui.addnewmodel;

import com.incon.connect.user.apimodel.components.fetchcategorie.Brand;
import com.incon.connect.user.apimodel.components.search.Division;
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

        void loadBrandsList(List<Brand> brandList);

        void loadDivisionsList(List<Division> divisionList);
    }

    interface Presenter {
        void getCategories();

        void getDivisionsFromCategoryId(int categoryId);

        void getBrandsFromDivisionId(int divisionId);

        void doModelSearchApi(String modelNumberToSearch);

        void addingNewModel(int userId, AddCustomProductModel addCustomProductModel);
    }
}
