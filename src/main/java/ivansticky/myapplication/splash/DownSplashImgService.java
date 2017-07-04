package ivansticky.myapplication.splash;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ivansticky.myapplication.splash.entity.CommonEntity;
import ivansticky.myapplication.splash.entity.Splash;
import ivansticky.myapplication.splash.http.DownLoadUtils;
import ivansticky.myapplication.splash.http.HttpUtils;

public class DownSplashImgService extends IntentService {
    private Splash mFlashScreen;


    public DownSplashImgService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getStringExtra(Constants.EXTRA_DOWNLOAD);
            if (action.equals(Constants.DOWNLOAD_SPLASH)) {
                startRequestSplashData();
            }
        }

    }

    public static void startDownLoadSplashImg(Context context, String action) {
        Intent mIntent = new Intent(context, DownSplashImgService.class);
        mIntent.putExtra(Constants.EXTRA_DOWNLOAD, action);
        context.startService(mIntent);
    }

    public void startRequestSplashData() {
        HttpUtils.getInstance().request()
                .getSplashImg(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonEntity>() {


                    @Override
                    public void accept(CommonEntity commonEntity) throws Exception {
                        if (commonEntity.isValid() && commonEntity.attachment != null) {
                            mFlashScreen = commonEntity.attachment.flashScreen;
                            Splash mLocalSplash = getLocalSplash();
                            if (mFlashScreen != null) {
                                if (mLocalSplash == null) {
                                    //本地为空导致下载
                                    startDownLoadSplash(Constants.SPLASH_PATH, mFlashScreen.burl);
                                } else if (isNeedDownLoad(Constants.SPLASH_PATH, mFlashScreen.burl)) {
                                    //这是满足了需要下载的几种情况
                                    startDownLoadSplash(Constants.SPLASH_PATH, mFlashScreen.burl);
                                }
                            } else {
                                if (mLocalSplash != null) {
                                    File mFile = SerializableUtils.getSerializableFile(Constants.SPLASH_PATH, Constants.SPLASH_FILE_NAME);
                                    if (mFile.exists()) {
                                        mFile.delete();
                                        Log.d("SplashDemo", "mScreen为空删除本地文件");
                                    }
                                }
                            }

                        }
                    }
                });
    }

    /**
     * 开始下载,并回调结果
     */
    private void startDownLoadSplash(String path, String burl) {
        DownLoadUtils.startDownLoad(path, new DownLoadUtils.DownLoadInterface() {
            @Override
            public void afterDownLoad(ArrayList<String> savePaths) {
                if (savePaths.size() == 1) {
                    //闪屏页面下载成功
                    Log.d("SplashDemo", "闪屏页面下载完成" + savePaths);
                    if (mFlashScreen != null) {
                        mFlashScreen.savePath = savePaths.get(0);
                    }
                    SerializableUtils.writeObject(mFlashScreen, Constants.SPLASH_PATH + "/" + Constants.SPLASH_FILE_NAME);
                } else {
                    //下载失败
                    Log.d("SplashDemo", "闪屏页面下载失败" + savePaths);
                }
            }
        }, burl);

    }

    /**
     * 几种需要下载的情况
     *
     * @return
     */
    public boolean isNeedDownLoad(String path, String url) {

        //当路径不存在时
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        File mFile = new File(path);
        if (!mFile.exists()) {
            return true;
        }
        if (getImageName(path).hashCode() != getImageName(url).hashCode()) {
            Log.d("SplashDemo", "path hashcode " + getImageName(path) + " " + getImageName(path).hashCode());
            Log.d("SplashDemo", "url hashcode " + getImageName(url) + " " + getImageName(url).hashCode());
            return true;
        }
        return false;

    }

    /**
     * 都可以通过"/"来判断出名字hash值的不同来辨别
     *
     * @param url 下载地址或者保存的绝对路径
     */
    private String getImageName(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        String[] split = url.split("/");
        String nameWith_ = split[split.length - 1];
        String[] split1 = nameWith_.split("\\.");
        return split1[0];
    }

    private Splash getLocalSplash() {
        Splash mLocalSplash = null;

        try {
            File mFile = SerializableUtils.getSerializableFile(Constants.SPLASH_PATH, Constants.SPLASH_FILE_NAME);
            mLocalSplash = (Splash) SerializableUtils.readObject(mFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mLocalSplash;
    }
}
