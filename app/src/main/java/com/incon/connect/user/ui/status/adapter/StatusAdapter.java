package com.incon.connect.user.ui.status.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.incon.connect.user.R;
import com.incon.connect.user.databinding.ItemStatusFragmentBinding;

/**
 * Created by PC on 12/4/2017.
 */

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    int[] statusImages = {R.drawable.ic_option_complaint, R.drawable.ic_option_received,
            R.drawable.ic_option_attending, R.drawable.ic_option_checkup, R.drawable.ic_option_approval,
            R.drawable.ic_option_repair_done, R.drawable.ic_option_payment,R.drawable.ic_options_feedback};

 /*   public StatusAdapter(int[] statusImages) {
        this.statusImages = statusImages;
    }*/

    public StatusAdapter() {


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemStatusFragmentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_status_fragment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.imageviewStatus.setImageResource(statusImages[position]);

    }
    @Override
    public int getItemCount() {
        return statusImages.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       // ImageView imageviewStatus;

        public ViewHolder(ItemStatusFragmentBinding binding) {
            super(binding.getRoot());
          //  imageviewStatus = (ImageView) itemView.findViewById(R.id.imageview_status);
        }

    }
}
