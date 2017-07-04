package ivansticky.myapplication.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ivansticky.myapplication.MainActivity;
import ivansticky.myapplication.R;
import ivansticky.myapplication.RxHelper;
import ivansticky.myapplication.WebViewActivity;
import ivansticky.myapplication.splash.entity.Splash;

public class SplashActivity extends AppCompatActivity implements OnClickListener {

    @BindView(R.id.img_splash)
    ImageView mImgSplash;
    @BindView(R.id.btn_timer)
    Button mBtnTimer;
    private Splash mLoacalSpalsh;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mImgSplash.setOnClickListener(this);
        mBtnTimer.setOnClickListener(this);

        initView();
    }

    private void initView() {
        //首先展示spalsh
        showSplash();
        //开启服务判断并且更新splash的图片
        startServiceAndDownImg();
    }

    /**
     * 展示图片
     */
    private void showSplash() {
        //首先拿到本地的序列化splash对象
        mLoacalSpalsh = getLoacalSpalsh();
        if (mLoacalSpalsh != null && !TextUtils.isEmpty(mLoacalSpalsh.savePath)) {
            Glide.with(this).load(mLoacalSpalsh.savePath).into(mImgSplash);
            startTimer();
        } else {
            mBtnTimer.setVisibility(View.GONE);
            mBtnTimer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goMainActivity();
                }
            }, 1000);
        }
    }

    /**
     * 开启倒计时
     */
    private void startTimer() {
        mDisposable = RxHelper.countdown(3).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mBtnTimer.setText("跳过(" + integer + "S)");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("倒计时发生错误", throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //倒计时完成
                        Log.e("倒计时完成,计入到主界面", "-----------------");
                        goMainActivity();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }

    }

    /**
     * 进入到主界面
     */
    private void goMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private Splash getLoacalSpalsh() {
        Splash splash = null;

        try {
            File mFile = SerializableUtils.getSerializableFile(Constants.SPLASH_PATH, Constants.SPLASH_FILE_NAME);
            splash = (Splash) SerializableUtils.readObject(mFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("SplashDemo", "SplashActivity 获取本地序列化闪屏失败" + e.getMessage());
        }


        return splash;
    }

    /**
     * 开启后台服务下载图片
     */
    private void startServiceAndDownImg() {

        DownSplashImgService.startDownLoadSplashImg(SplashActivity.this, Constants.DOWNLOAD_SPLASH);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_splash:
                goWebActivity();
                break;
            case R.id.btn_timer:
                //点击跳过 ,直接进入到主页面,这里可以加上判断,是否登录过,否则进入到登录页面
                goMainActivity();
                break;

        }
    }



    private void goWebActivity() {
        //这里要传参数判断是否是从这个页面跳转过去的,需要对web返回键做出判断
        Intent mIntent = new Intent(SplashActivity.this, WebViewActivity.class);
        mIntent.putExtra("fromsplash", true);
        startActivity(mIntent);
        finish();

    }
}
