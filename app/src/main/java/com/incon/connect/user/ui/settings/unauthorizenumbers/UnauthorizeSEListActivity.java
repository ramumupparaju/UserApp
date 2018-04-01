package com.incon.connect.user.ui.settings.unauthorizenumbers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.addserviceengineer.AddServiceEngineer;
import com.incon.connect.user.callbacks.IClickCallback;
import com.incon.connect.user.databinding.ActivityUnauthorizeSeListBinding;
import com.incon.connect.user.ui.BaseActivity;
import com.incon.connect.user.ui.settings.unauthorizenumbers.adapter.UnauthorizeSeNumbersAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.incon.connect.user.AppConstants.IntentConstants.SERVICE_ENGINEER_DATA;

/**
 * Created by MY HOME on 02-Mar-18.
 */

public class UnauthorizeSEListActivity extends BaseActivity {

    private UnauthorizeSeNumbersAdapter unauthorizeNumbersAdapter;
    private ActivityUnauthorizeSeListBinding binding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_unauthorize_se_list;
    }

    @Override
    protected void initializePresenter() {
    }

    public void onBackClick() {
        onBackPressed();
    }

    @Override
    protected void onCreateView(Bundle saveInstanceState) {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        binding.setActivity(this);

        initViews();

    }

    private void initViews() {
        List<AddServiceEngineer> serviceEngineerList = new ArrayList<>();
        serviceEngineerList.add(new AddServiceEngineer("test1", "7779992501"));
        serviceEngineerList.add(new AddServiceEngineer("test2", "9638527412"));
        serviceEngineerList.add(new AddServiceEngineer("test3", "7799050905"));

        unauthorizeNumbersAdapter = new UnauthorizeSeNumbersAdapter(serviceEngineerList);
        binding.recyclerviewUnauthorize.setAdapter(unauthorizeNumbersAdapter);
        unauthorizeNumbersAdapter.setClickCallback(iClickCallback);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        Context context = binding.getRoot().getContext();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.list_divider));
        binding.recyclerviewUnauthorize.addItemDecoration(dividerItemDecoration);
        binding.recyclerviewUnauthorize.setLayoutManager(linearLayoutManager);
    }

    //recyclerview click event
    private IClickCallback iClickCallback = new IClickCallback() {
        @Override
        public void onClickPosition(int position) {
            Intent intent = new Intent(UnauthorizeSEListActivity.this, UnauthorizeSEEditActivity.class);
            intent.putExtra(SERVICE_ENGINEER_DATA, unauthorizeNumbersAdapter.getItemAtPosition(position));
            startActivityForResult(intent, RequestCodes.REFRESH_SE_LIST);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.REFRESH_SE_LIST && resultCode == Activity.RESULT_OK) {
            //TODO have to refresh list
        }
    }
}
