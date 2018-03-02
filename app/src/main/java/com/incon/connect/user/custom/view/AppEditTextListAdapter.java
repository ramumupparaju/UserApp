package com.incon.connect.user.custom.view;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.incon.connect.user.BR;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.FeedbackData;
import com.incon.connect.user.databinding.ItemEdittextListBinding;

import java.util.List;

/**
 * Created on 13 Jun 2017 4:05 PM.
 */
public class AppEditTextListAdapter extends RecyclerView.Adapter<AppEditTextListAdapter.ViewHolder> {

    public List<FeedbackData> feedbackDataList;

    public AppEditTextListAdapter(List<FeedbackData> feedbackDataList) {
        this.feedbackDataList = feedbackDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemEdittextListBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_edittext_list, parent, false);
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
        private final ItemEdittextListBinding binding;

        public ViewHolder(ItemEdittextListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FeedbackData feedbackData, int position) {
            binding.setVariable(BR.model, feedbackData);
            binding.executePendingBindings();
        }
    }
}
