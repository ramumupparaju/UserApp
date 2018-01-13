package com.incon.connect.user.ui.addnewmodel.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.search.ModelSearchResponse;
import com.incon.connect.user.databinding.ItemWarrantyregistrationFragmentBinding;

import java.util.List;

public class ModelSearchArrayAdapter extends ArrayAdapter<ModelSearchResponse> {


    private Context mContext;
    private List<ModelSearchResponse> modelNumbersResponseList;
    private LayoutInflater layoutInflater;

    public ModelSearchArrayAdapter(Context context, List<ModelSearchResponse>
            modelList) {
        super(context, R.layout.item_warrantyregistration_fragment, modelList);
        this.mContext = context;
        this.modelNumbersResponseList = modelList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(mContext);
        }

        // Perform the binding
        ItemWarrantyregistrationFragmentBinding binding = DataBindingUtil.getBinding(convertView);

        if (binding == null) {
            binding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.item_warrantyregistration_fragment, parent, false);
        }
        ModelSearchResponse modelNumber = modelNumbersResponseList.get(position);
        binding.setVariable(BR.modelSearchResponse, modelNumber);
        binding.executePendingBindings();
        return binding.getRoot();
    }
}