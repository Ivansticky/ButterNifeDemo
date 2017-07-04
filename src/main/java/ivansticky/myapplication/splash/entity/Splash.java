package ivansticky.myapplication.splash.entity;

import java.io.Serializable;

/**
 * Created by _Ivan_ on 2017/6/28.
 */
public class Splash implements Serializable {
    private static final long serialVersionUID = 7382351359868556980L;//这里需要写死 序列化Id

    public String id;
    public String burl;
    public String surl;
    public int type;
    public String savePath;//保存路径
    public String click_url;
    public String title;

    @Override
    public String toString() {
        return "FlashScreen{" +
                "id='" + id + '\'' +
                ", burl='" + burl + '\'' +
                ", surl='" + surl + '\'' +
                ", type=" + type +
                ", savePath='" + savePath + '\'' +
                ", click_url='" + click_url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

}
