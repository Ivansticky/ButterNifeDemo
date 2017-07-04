package ivansticky.myapplication;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by _Ivan_ on 2017/6/6.
 */
public class GlideUtils {

    /**
     * 最普通的加载图片的方式
     *
     * @param mContext
     * @param url
     * @param img
     */
    public static void loadImgs(Context mContext, String url, ImageView img) {
        Glide.with(mContext).load(url).into(img);
    }

    /**
     * 静态加载gif图
     *
     * @param mContext
     * @param url
     * @param view
     */

    public static void loadStaticGif(Context mContext, String url, ImageView view) {
        Glide.with(mContext).asGif().load(url).into(view);
    }

    /**
     * 动态加载动图
     * @param mContext
     * @param url
     * @param view
     */
    public static void loadDynamicGif(Context mContext, String url, ImageView view) {
        Glide.with(mContext).asBitmap().load(url).into(view);
    }



}
