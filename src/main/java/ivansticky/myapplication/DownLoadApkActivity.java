package ivansticky.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

public class DownLoadApkActivity extends AppCompatActivity {
    private Switch installModeSwitch;
    private ProgressBar mProgressBar;
    private Button mDownBtn;

    private static final String APK_URL = "http://101.28.249.94/apk.r1.market.hiapk.com/data/upload/apkres/2017/4_11/15/com.baidu.searchbox_034250.apk";


    private DownLoadService.DownLoadBinder mDownLoadBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        //服务连接成功的时候调用
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mDownLoadBinder = (DownLoadService.DownLoadBinder) iBinder;
        }

        //服务连接失败
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mDownLoadBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_apk);

        installModeSwitch = (Switch) findViewById(R.id.install_mode_switch);
        mProgressBar = (ProgressBar) findViewById(R.id.down_progress);
        mDownBtn = (Button) findViewById(R.id.down_btn);
        /*
        与service通信并保持让他持续运行
        这种方式service不会随着Activity的销毁而销毁,一直保持运行,
        当不想在保持联系的时候调用unbind()方法就可以解除Activity与service的绑定,但是service没有被销毁
         */
        Intent mIntent = new Intent(DownLoadApkActivity.this, DownLoadService.class);
        startService(mIntent);
        bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);

        installModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    compoundButton.setText("root模式");
                } else {
                    compoundButton.setText("非root模式");
                }

                if (mDownLoadBinder != null) {
                    mDownLoadBinder.setRootMode(b);
                }
            }
        });
        mDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDownLoadBinder != null) {
                    long downloadId = mDownLoadBinder.startDownLoad(APK_URL);
//                    startCheckProgress(downloadId);
                }
            }
        });

    }

//    private void startCheckProgress(long downloadId) {
//        Observable.interval(100, 200, TimeUnit.MILLISECONDS, Schedulers.io())
//                .filter(new Predicate<Long>() {
//                    @Override
//                    public boolean test(Long aLong) throws Exception {
//                        return mDownLoadBinder != null;
//                    }
//                }).map(new Function<Long, Object>() {
//            @Override
//            public Object apply(Long aLong) throws Exception {
//                return mDownLoadBinder.getProgress(aLong);
//            }
//
//        })
//
//    }

}
