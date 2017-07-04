package ivansticky.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
/*
计时器案例
实现3秒闪屏倒计时功能,图片存在本地,每次请求没有做缓存
 */

public class MainActivity extends AppCompatActivity {
    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtnWeb;
    private Button mBtnSearch;
    private Button mBtnTimer;
    private Button mReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = (Button) this.findViewById(R.id.btn_yanzheng);
        mBtn2 = (Button) this.findViewById(R.id.btn_chongfu);
        mBtn3 = (Button) this.findViewById(R.id.btn_down);
        mBtnWeb = (Button) this.findViewById(R.id.btn_web);
        mBtnSearch = (Button) this.findViewById(R.id.search);
        mBtnTimer = (Button) this.findViewById(R.id.timer);
        mReview = (Button) this.findViewById(R.id.recyclerview);


        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dosth();
            }
        });
        /*
        防止按钮重复点击
         */
        RxView.clicks(mBtn2).throttleFirst(1, TimeUnit.SECONDS).
                subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e("----", "点击了按钮~~" + System.currentTimeMillis());
                    }
                });

        RxView.clicks(mBtn3).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(MainActivity.this, DownLoadApkActivity.class));
            }
        });

        mBtnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        mBtnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer();
            }
        });

        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RecycleViewActivity.class));
            }
        });
    }


    private void dosth() {
        //
        final int time = 20;
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(time)
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mBtn.setClickable(false);
                        mBtn.setTextColor(Color.BLUE);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e("---", aLong + "");

                        mBtn.setText("距离下次发送还有" + (time - aLong - 1));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("error", throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mBtn.setClickable(true);
                        mBtn.setTextColor(Color.BLACK);
                        mBtn.setText("发送验证码");
                    }
                });

    }

    public void timer() {
        RxHelper.countdown(10).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mBtnTimer.setText(integer + "");
                    }
                });
    }
}
