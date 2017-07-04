package ivansticky.myapplication.splash.http;

import io.reactivex.Observable;
import ivansticky.myapplication.splash.entity.CommonEntity;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by _Ivan_ on 2017/6/28.
 */
public interface Api {
    //http://beta.goldenalpha.com.cn/fundworks/media/getFlashScreen?type=1
    @GET("fundworks/media/getFlashScreen")
    Observable<CommonEntity> getSplashImg(@Query("type") int type);
}
