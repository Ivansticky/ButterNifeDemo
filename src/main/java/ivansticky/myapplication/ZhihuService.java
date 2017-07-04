package ivansticky.myapplication;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ivan on 2017/6/30.
 */
public interface ZhihuService {

    @GET("http://gank.io/api/random/data/Android/")
    Observable<ZhihuEntity> getZhihu(@Query("page"));
}
