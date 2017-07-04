package ivansticky.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by _Ivan_ on 2017/6/15.
 */
public class InstallUtils {

    public static void install(Context context, String apkpath, boolean rootMode) {
        if (rootMode) {
            installRoot(context, apkpath);
        } else {
            installNormal(context, apkpath);
        }
    }

    public static void install(Context context, String apkpath) {
        install(context, apkpath, false);
    }

    //普通安装
    private static void installNormal(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            File file = (new File(apkPath));
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.example.chenfengyao.installapkdemo", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    /**
     * root模式下安装
     *
     * @param context
     * @param apkpath
     */
    private static void installRoot(final Context context, final String apkpath) {

        Observable.just(apkpath)
                .map(new Function<String, String>() {
                         @Override
                         public String apply(String s) throws Exception {
                             return "pm install -r" + apkpath;
                         }
                     }
                ).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return SystemManager.RootCommand(s);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == 0) {
                            Toast.makeText(context, "安装成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "root权限获取失败,尝试普通安装", Toast.LENGTH_SHORT).show();
                            install(context, apkpath);
                        }

                    }
                });
    }
}
