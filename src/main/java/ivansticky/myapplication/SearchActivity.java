package ivansticky.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.et_keyword)
    EditText mEtKeyword;
    @BindView(R.id.tv_result)
    TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        request();

    }

    private void request() {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();

        //在这得到service对象
        final Retrofit mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl("https://suggest.taobao.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final searchProduct mService = mRetrofit.create(searchProduct.class);

        RxTextView.textChanges(mEtKeyword)
                .subscribeOn(AndroidSchedulers.mainThread())
                .debounce(600, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(CharSequence charSequence) throws Exception {
                        //清空搜索出来的结果
                        mTvResult.setText("");
                        return charSequence.length() > 0;
                    }
                })
                .observeOn(Schedulers.io())
                .switchMap(new Function<CharSequence, ObservableSource<Data>>() {
                    @Override
                    public ObservableSource<Data> apply(CharSequence charSequence) throws Exception {
                        //RxTextView返回的是一个Observable对象,而要通过这个操作符返回另一个Observable对象
                        //这里是做了一个转换而已
                        return mService.getProduct("utf-8", charSequence.toString());
                    }
                })
                .map(new Function<Data, ArrayList<ArrayList<String>>>() {

                    @Override
                    public ArrayList<ArrayList<String>> apply(Data data) throws Exception {
                        return data.result;
                    }
                }).flatMap(new Function<ArrayList<ArrayList<String>>, ObservableSource<ArrayList<String>>>() {
            @Override
            public ObservableSource<ArrayList<String>> apply(ArrayList<ArrayList<String>> arrayLists) throws Exception {
                return Observable.fromIterable(arrayLists);
            }

        }).filter(new Predicate<ArrayList<String>>() {
            @Override
            public boolean test(ArrayList<String> list) throws Exception {
                return list.size() > 0;

            }
        }).map(new Function<ArrayList<String>, String>() {
            @Override
            public String apply(ArrayList<String> list) throws Exception {
                return "[商品名称:" + list.get(0) + ", ID:" + list.get(1) + "]\n";
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mTvResult.append(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("-----" + throwable.getMessage());
                    }
                });
    }


    //https://suggest.taobao.com/sug?code=utf-8&q=%E6%89%8B%E6%9C%BA

    public interface searchProduct {
        @GET("/sug")
        Observable<Data> getProduct(@Query("code") String code, @Query("q") String content);
    }

    class Data {

        public ArrayList<ArrayList<String>> result;

    }




}
