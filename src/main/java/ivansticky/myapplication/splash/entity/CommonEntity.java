package ivansticky.myapplication.splash.entity;

/**
 * Created by _Ivan_ on 2017/6/28.
 */
public class CommonEntity {


    public int status;
    public String message;
    public String debug;
    public AttachMent attachment;

    public boolean isValid() {
        return status == 200;
    }

    public boolean isServiceBlock() {
        return status == 500;

    }

    public boolean isNeedOut() {
        return status == 1000;

    }

    @Override
    public String toString() {
        return "CommonEntity{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", debug='" + debug + '\'' +
                ", attachment=" + attachment +
                '}';
    }

}
