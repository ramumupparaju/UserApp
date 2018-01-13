package com.incon.connect.user;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.incon.connect.user.apimodel.components.defaults.CategoryResponse;
import com.incon.connect.user.apimodel.components.fetchcategorie.FetchCategories;
import com.incon.connect.user.apimodel.components.status.DefaultStatusData;

import io.fabric.sdk.android.Fabric;
import net.hockeyapp.android.CrashManager;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ConnectApplication extends Application {

    private static Context context;
    private List<DefaultStatusData> statusListResponses;
    private List<CategoryResponse> categoriesList;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        context = getApplicationContext();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        // Added to fix : FileUriExposedException in version7.0 while trying to read file from
        // internal storage
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        if (BuildConfig.FLAVOR.equals("client_staging")) {
            CrashManager.register(this);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static ConnectApplication getAppContext() {
        return (ConnectApplication) context;
    }


    public void setStatusListData(List<DefaultStatusData> statusListResponses) {
        this.statusListResponses = statusListResponses;
    }

    public List<DefaultStatusData> getStatusListResponses() {
        return statusListResponses;
    }

    public List<CategoryResponse> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<CategoryResponse> categoriesList) {
        this.categoriesList = categoriesList;
    }
}
