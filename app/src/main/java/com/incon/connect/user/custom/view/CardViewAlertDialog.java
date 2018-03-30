package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incon.connect.user.AppConstants;
import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.ServiceRequest;
import com.incon.connect.user.apimodel.components.status.StatusList;
import com.incon.connect.user.callbacks.AlertDialogCallback;
import com.incon.connect.user.databinding.DialogCardViewBinding;
import com.incon.connect.user.databinding.ItemCardViewLayoutBinding;
import com.incon.connect.user.databinding.StatusViewBinding;
import com.incon.connect.user.databinding.ViewEditTextDialogBinding;
import com.incon.connect.user.dto.DialogRow;
import com.incon.connect.user.utils.DeviceUtils;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.incon.connect.user.AppUtils.getStatusName;

public class CardViewAlertDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    //All final attributes
    private final String title; // optional
    private final List<DialogRow> descriptionData; // optional
    private final String button1text; // required
    private final String button2text; // required
    private final AlertDialogCallback mAlertDialogCallback; // required
    private int dialogType;

    /**
     * @param builder
     */
    private CardViewAlertDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.title = builder.title;
        this.descriptionData = builder.descriptionData;
        this.button1text = builder.button1Text;
        this.button2text = builder.button2Text;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        DialogCardViewBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_card_view, null, false);
        View contentView = binding.getRoot();

        LinearLayout.LayoutParams crlp = (LinearLayout.LayoutParams) binding.cardView.getLayoutParams();
        crlp.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.8);

        TextView titleTv = binding.dialogTitleTextView;
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
            titleTv.setMovementMethod(new ScrollingMovementMethod());
        } else {
            titleTv.setVisibility(GONE);
        }

        LinearLayout descLayout = binding.descLayout;
        if (descriptionData == null || descriptionData.size() == 0) {
            descLayout.setVisibility(GONE);
        } else {
            descLayout.setVisibility(VISIBLE);
            addViewsToDescriptionLayout(descLayout);
        }

        Button mFirstButtonTextView = contentView.findViewById(R.id.dialog_first_button);
        if (!TextUtils.isEmpty(button1text)) {
            mFirstButtonTextView.setText(button1text);
        } else {
            mFirstButtonTextView.setVisibility(GONE);
        }

        if (dialogType == AppConstants.DialogTypeConstants.WARRANTY_TYPE) {
            binding.checkbox.setVisibility(VISIBLE);
            binding.checkbox.setText(button1text);

            binding.buttonLayout.setVisibility(GONE);
        } else {
            binding.checkbox.setVisibility(GONE);
            binding.buttonLayout.setVisibility(VISIBLE);
        }
        binding.checkbox.setOnClickListener(this);
        mFirstButtonTextView.setOnClickListener(this);
        Button mSecondButtonTextView = contentView.findViewById(R.id.dialog_second_button);
        if (!TextUtils.isEmpty(button2text)) {
            mSecondButtonTextView.setText(button2text);
        } else {
            mSecondButtonTextView.setVisibility(GONE);
        }
        mSecondButtonTextView.setOnClickListener(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentView);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
    }

    private void addViewsToDescriptionLayout(LinearLayout descLayout) {
        int size = descriptionData.size();
        for (int i = 0; i < size; i++) {
            DialogRow dialogRow = descriptionData.get(i);

            ItemCardViewLayoutBinding layoutBinding = getCardView();
            layoutBinding.leftTv.setText(dialogRow.getLeftTv());
            layoutBinding.rightTv.setText(dialogRow.getRightTv());
            View statusRootView = layoutBinding.getRoot();

            descLayout.addView(statusRootView);
        }

    }

    private ItemCardViewLayoutBinding getCardView() {
        return DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.item_card_view_layout, null, false);
    }

    @Override
    public void onClick(View view) {
        if (mAlertDialogCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.checkbox:
                mAlertDialogCallback.alertDialogCallback(AlertDialogCallback.EXTENDED_WARRANTY);
                break;
            case R.id.dialog_first_button:
                mAlertDialogCallback.alertDialogCallback(AlertDialogCallback.OK);
                break;
            case R.id.dialog_second_button:
                mAlertDialogCallback.alertDialogCallback(AlertDialogCallback.CANCEL);
                break;
            default:
                break;
        }
    }

    public void setDialogType(int dialogType) {
        this.dialogType = dialogType;
    }

    public static class AlertDialogBuilder {

        private final Context context;
        private final AlertDialogCallback callback;
        private String title;
        private List<DialogRow> descriptionData;
        private String button1Text;
        private String button2Text;

        public AlertDialogBuilder(Context context, AlertDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AlertDialogBuilder content(List<DialogRow> descriptionData) {
            this.descriptionData = descriptionData;
            return this;
        }

        public AlertDialogBuilder button1Text(String button1Text) {
            this.button1Text = button1Text;
            return this;
        }

        public AlertDialogBuilder button2Text(String button2Text) {
            this.button2Text = button2Text;
            return this;
        }

        //Return the finally constructed User object
        public CardViewAlertDialog build() {
            CardViewAlertDialog dialog = new CardViewAlertDialog(this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }
    }
}
