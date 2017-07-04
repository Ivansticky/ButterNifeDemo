package ivansticky.myapplication;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.LongSparseArray;

import java.io.File;

public class DownLoadService extends Service {

    private DownLoadBinder mbinder = new DownLoadBinder();
    private DownloadManager mDownLoadManager;

    private LongSparseArray<String> mApkPath;
    private DownLoadFinishReciver downLoadFinishReciver;
    private Boolean isroot = false;

    public DownLoadService() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        mDownLoadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mApkPath = new LongSparseArray<>();
        //注册下载 完成的广播
        downLoadFinishReciver = new DownLoadFinishReciver();
        registerReceiver(downLoadFinishReciver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downLoadFinishReciver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mbinder;
    }

    public class DownLoadBinder extends Binder {

        public void setRootMode(boolean isroot) {
            isroot = isroot;
        }
        //服务中要做的事

        /**
         * 开始下载,拿到下载的id
         *
         * @param apkUrl Apk的下载地址
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public long startDownLoad(String apkUrl) {
            //如果sd卡中有apk文件,首先要删除
            File mFile = IOUtils.clearApk(DownLoadService.this, "text.apk");
            DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(apkUrl));
            mRequest.setDestinationUri(Uri.fromFile(mFile));

            //添加请求开始下载
            long downloadId = mDownLoadManager.enqueue(mRequest);
            mApkPath.put(downloadId, mFile.getAbsolutePath());

            return downloadId;
        }


        /**
         * 拿到开始下载后的进度
         *
         * @param downloadId
         * @return
         */
        public int getProgress(Long downloadId) {

            DownloadManager.Query mQuery = new DownloadManager.Query();
            mQuery.setFilterById(downloadId);

            Cursor mCursor = null;
            int progress = 0;
            try {
                mCursor = mDownLoadManager.query(mQuery);// 拿到游标

                if (mCursor != null && mCursor.moveToFirst()) {
                    //当前下载量
                    int downloadSoFar = mCursor.getInt(mCursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    //文件总大小
                    int downloadTotal = mCursor.getInt(mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    progress = (int) (downloadSoFar * 1.0f / downloadTotal * 100);
                }
            } finally {
                if (mCursor != null) {
                    mCursor.close();
                }
            }
            return progress;
        }


    }

    /**
     * 下载完成的广播接收者
     */
    private class DownLoadFinishReciver extends BroadcastReceiver {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            long longExtra = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String path = mApkPath.get(longExtra);
            if (!path.isEmpty()) {
                //提升读写权限
                SystemManager.setPermission(path);
                InstallUtils.install(DownLoadService.this, path, isroot);
            } else {

            }
        }
    }

}
