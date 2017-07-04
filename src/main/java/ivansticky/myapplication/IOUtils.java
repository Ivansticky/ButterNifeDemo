package ivansticky.myapplication;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by _Ivan_ on 2017/6/8.
 */
public class IOUtils {
    /**
     * 删除已有的APk,返回Apk的路径
     *
     * @param context
     * @param apkname
     * @return
     */
    public static File clearApk(Context context, String apkname) {
        File mExternalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        File mFile = new File(mExternalFilesDir, apkname);
        if (mFile.exists()) {
            mFile.delete();
        }

        return mFile;
    }
}
