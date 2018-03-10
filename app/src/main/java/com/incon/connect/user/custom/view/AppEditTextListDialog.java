package com.incon.connect.user.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RatingBar;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.FeedbackData;
import com.incon.connect.user.callbacks.FeedbackAlertDialogCallback;
import com.incon.connect.user.callbacks.TextAlertDialogCallback;
import com.incon.connect.user.databinding.ViewEditTextListDialogBinding;

import java.util.List;

public class AppEditTextListDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    //All final attributes
    private final String title; // required
    private final String leftButtonText; // required
    private final String rightButtonText; // required
    private final List<FeedbackData> feedbackDataList; // required
    private EditText editTextNotes; // required
    private RatingBar ratingBar; // required
    private final FeedbackAlertDialogCallback mAlertDialogCallback; // required

    /**
     * @param builder
     */
    private AppEditTextListDialog(AlertDialogBuilder builder) {
        super(builder.context);
        this.context = builder.context;
        this.title = builder.title;
        this.leftButtonText = builder.leftButtonText;
        this.rightButtonText = builder.rightButtonText;
        this.feedbackDataList = builder.feedbackDataList;
        this.mAlertDialogCallback = builder.callback;
    }

    public void showDialog() {
        ViewEditTextListDialogBinding viewEditTextListDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.view_edit_text_list_dialog, null, false);
        View contentView = viewEditTextListDialogBinding.getRoot();

        ratingBar = viewEditTextListDialogBinding.inputRatingbar;
        editTextNotes = viewEditTextListDialogBinding.edittextUsername;
        viewEditTextListDialogBinding.textVerifyTitle.setText(title);
        if (title.equals(getContext().getString(
                R.string.bottom_option_transfer))) {
            viewEditTextListDialogBinding.edittextUsername.setInputType(InputType.TYPE_CLASS_NUMBER);
            viewEditTextListDialogBinding.inputLayoutVerify.setHint(getContext().getString(
                    R.string.action_enter_transfer_phone_number));
        }
        viewEditTextListDialogBinding.includeRegisterBottomButtons.buttonLeft.setText(
                TextUtils.isEmpty(leftButtonText) ? context.getString(
                        R.string.action_back) : leftButtonText);
        viewEditTextListDialogBinding.includeRegisterBottomButtons.buttonRight.setText(
                TextUtils.isEmpty(leftButtonText) ? context.getString(
                        R.string.action_next) : rightButtonText);
        viewEditTextListDialogBinding.includeRegisterBottomButtons.buttonLeft.setOnClickListener(this);
        viewEditTextListDialogBinding.includeRegisterBottomButtons.buttonRight.setOnClickListener(this);

        loadFeedBackData(viewEditTextListDialogBinding);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentView);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_shadow);
        show();
    }

    private void loadFeedBackData(ViewEditTextListDialogBinding binding) {
        if (feedbackDataList != null && feedbackDataList.size() > 0) {
            AppEditTextListAdapter purchasedAdapter = new AppEditTextListAdapter(feedbackDataList);
            binding.feedbackRecyclerview.setAdapter(purchasedAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            Context context = binding.getRoot().getContext();
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                    linearLayoutManager.getOrientation());
            dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.list_divider));
            binding.feedbackRecyclerview.addItemDecoration(dividerItemDecoration);
            binding.feedbackRecyclerview.setLayoutManager(linearLayoutManager);
        }

    }

    @Override
    public void onClick(View view) {
        if (mAlertDialogCallback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.button_left:
                mAlertDialogCallback.alertDialogCallback(TextAlertDialogCallback.CANCEL);
                break;
            case R.id.button_right:
                mAlertDialogCallback.selectedRating(String.valueOf(ratingBar.getRating()));
                mAlertDialogCallback.enteredText(editTextNotes.getText().toString());
                mAlertDialogCallback.alertDialogCallback(TextAlertDialogCallback.OK);
                break;
            default:
                break;
        }
    }

    public static class AlertDialogBuilder {
        private final Context context;
        private final FeedbackAlertDialogCallback callback;
        private String title;
        private String leftButtonText;
        private String rightButtonText;
        private List<FeedbackData> feedbackDataList;


        public AlertDialogBuilder(Context context, FeedbackAlertDialogCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        public AlertDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AlertDialogBuilder leftButtonText(String leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }

        public AlertDialogBuilder rightButtonText(String rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }

        public AlertDialogBuilder feedbackDataList(List<FeedbackData> feedbackData) {
            this.feedbackDataList = feedbackData;
            return this;
        }


        //Return the finally constructed User object
        public AppEditTextListDialog build() {
            AppEditTextListDialog dialog = new AppEditTextListDialog(
                    this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            return dialog;
        }
    }
}
