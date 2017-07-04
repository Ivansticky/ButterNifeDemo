package ivansticky.myapplication.splash;

/**
 * Created by _Ivan_ on 2017/6/28.
 */
public interface Constants {
    //动态闪屏序列化本地的地址
    String SPLASH_PATH = IvanApplication.getInstance().getFilesDir().getAbsolutePath() + "/alpha/splash";
    String SPLASH_FILE_NAME = "splash.srr";
    String BASE_URL = "http://beta.goldenalpha.com.cn/";
    String EXTRA_DOWNLOAD = "extra_download";
    String DOWNLOAD_SPLASH = "download_splash";

}
