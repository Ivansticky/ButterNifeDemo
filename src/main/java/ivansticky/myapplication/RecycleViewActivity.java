package ivansticky.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivansticky.myapplication.splash.http.HttpUtils;

public class RecycleViewActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //拿数据了
        HttpUtils.getInstance().
    }
}
