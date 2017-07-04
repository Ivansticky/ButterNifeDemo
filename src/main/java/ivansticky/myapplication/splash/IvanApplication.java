package ivansticky.myapplication.splash;

import android.app.Application;
import android.content.Context;

/**
 * Created by _Ivan_ on 2017/6/28.
 */
public class IvanApplication extends Application {
    private static Context mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Context getInstance() {
        return mInstance;
    }
}
