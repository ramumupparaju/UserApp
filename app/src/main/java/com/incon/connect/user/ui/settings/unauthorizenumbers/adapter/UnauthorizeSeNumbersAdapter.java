package com.incon.connect.user.ui.settings.unauthorizenumbers.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.FeedbackData;
import com.incon.connect.user.databinding.ItemUnauthorizenumbersBinding;

import java.util.List;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class UnauthorizeSeNumbersAdapter extends RecyclerView.Adapter<UnauthorizeSeNumbersAdapter.ViewHolder> {

    public List<FeedbackData> feedbackDataList;

    public UnauthorizeSeNumbersAdapter(List<FeedbackData> feedbackDataList) {
        this.feedbackDataList = feedbackDataList;
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
        FeedbackData feedbackData = feedbackDataList.get(position);
        holder.bind(feedbackData, position);
    }

    @Override
    public int getItemCount() {
        return feedbackDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUnauthorizenumbersBinding binding;

        public ViewHolder(ItemUnauthorizenumbersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FeedbackData feedbackData, int position) {
            binding.itemNumber.setText(feedbackData.getComments());
            binding.executePendingBindings();
        }
    }
}
