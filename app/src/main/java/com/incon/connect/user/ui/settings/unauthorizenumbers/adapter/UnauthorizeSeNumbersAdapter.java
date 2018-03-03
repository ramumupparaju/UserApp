package com.incon.connect.user.ui.settings.unauthorizenumbers.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ItemUnauthorizenumbersBinding;

import java.util.List;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class UnauthorizeSeNumbersAdapter extends RecyclerView.Adapter<UnauthorizeSeNumbersAdapter.ViewHolder> {

    public List<AddServiceEngineer> serviceEngineerList;
    private IClickCallback clickCallback;

    public UnauthorizeSeNumbersAdapter(List<AddServiceEngineer> serviceEngineerList) {
        this.serviceEngineerList = serviceEngineerList;
    }

    public void setClickCallback(IClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemUnauthorizenumbersBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_unauthorizenumbers, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AddServiceEngineer serviceEngineer = serviceEngineerList.get(position);
        holder.bind(serviceEngineer, position);
    }

    public AddServiceEngineer getItemAtPosition(int position) {
        return serviceEngineerList.get(position);
    }

    @Override
    public int getItemCount() {
        return serviceEngineerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ItemUnauthorizenumbersBinding binding;

        public ViewHolder(ItemUnauthorizenumbersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this);

        }

        public void bind(AddServiceEngineer feedbackData, int position) {
            binding.itemNumber.setText(String.format("%1$s(%2$s)", feedbackData.getName(), feedbackData.getMobileNumber()));
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            clickCallback.onClickPosition(getAdapterPosition());
        }
    }
}
