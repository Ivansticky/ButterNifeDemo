package ivansticky.myapplication.splash.http;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by _Ivan_ on 2017/6/28.
 */
public class DownLoadUtils {

    public interface DownLoadInterface {
        void afterDownLoad(ArrayList<String> savePaths);
    }

    /**
     * @param savePath            图片保存的路径
     * @param mdDownLoadInterface 回调接口
     * @param download            图片的url ,可变参数
     */
    public static void startDownLoad(String savePath, DownLoadInterface mdDownLoadInterface, String... download) {
        new DownLoadAsync(savePath, mdDownLoadInterface).execute(download);
    }

    private static class DownLoadAsync extends AsyncTask<String, Integer, ArrayList<String>> {

        private DownLoadInterface mDownLoadInterface;
        private String savePath;

        public DownLoadAsync(String savePath, DownLoadInterface mdDownLoadInterface) {
            this.savePath = savePath;
            this.mDownLoadInterface = mdDownLoadInterface;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected ArrayList<String> doInBackground(String... params) {

            ArrayList<String> names = new ArrayList<>();
            for (String url : params) {
                if (!TextUtils.isEmpty(url)) {
                    //如果SD卡已经装载
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        FileOutputStream out = null;
                        InputStream in = null;
                        try {
                            URL mURL = new URL(url);
                            HttpURLConnection mHttpURLConnection = (HttpURLConnection) mURL.openConnection();
                            mHttpURLConnection.connect();
                            //创建输入流
                            in = mHttpURLConnection.getInputStream();
                            File mFile = new File(savePath);
                            if (!mFile.exists()) {
                                //创建目录
                                mFile.mkdirs();
                            }
                            String[] mSplit = url.split("/");
                            //数组是从第零个开始
                            String fileName = mSplit[mSplit.length - 1];
                            File mApkFile = new File(savePath, fileName);//保存的路径,和保存的路径名
                            System.out.println("---" + mApkFile.getAbsolutePath() + "---");
                            names.add(mApkFile.getAbsolutePath());//这个文件的绝对路径
                            //获得输出流
                            out = new FileOutputStream(mApkFile, false);
                            int count = 0;
                            byte[] mBytes = new byte[1024];
                            while (true) {
                                int mRead = in.read(mBytes);
                                if (mRead == -1) {
                                    break;
                                }
                                out.write(mBytes, 0, mRead);
                                count += mRead;// 图片的大小
                                publishProgress(count);//给进度条

                            }
                            out.flush();


                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (in != null) {
                                    in.close();
                                }
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
            return names;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);
            if (mDownLoadInterface != null) {
                mDownLoadInterface.afterDownLoad(list);
            }
        }
    }

}
