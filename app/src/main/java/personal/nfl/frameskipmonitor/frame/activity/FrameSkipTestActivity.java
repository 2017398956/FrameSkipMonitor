package personal.nfl.frameskipmonitor.frame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import personal.nfl.frameskipmonitor.R;
import personal.nfl.frameskipmonitor.frame.adapter.RecyclerAdapter;
import personal.nfl.frameskipmonitor.frame.model.RecyclerItem;

public class FrameSkipTestActivity extends AppCompatActivity {
    public static final String IS_DELAY = "is_delay";
    List<RecyclerItem> mList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    Button btnShowRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        btnShowRecycleView = (Button) findViewById(R.id.btn_show_recyclerview);
        btnShowRecycleView.setOnClickListener(v -> {
            boolean isDelay = false;
            if (getIntent() != null && getIntent().getExtras() != null) {
                isDelay = getIntent().getExtras().getBoolean(IS_DELAY, false);
            }
            showRecycleView(isDelay);
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    /**
     * @param isNeedDelay 绘制item的时候，是否需要故意延时，来体现掉帧统计功能
     */
    private void showRecycleView(boolean isNeedDelay) {
        RecyclerItem item;
        for (int i = 0; i < 100; i++) {
            item = new RecyclerItem();
            item.setImg("点击了 item");
            item.setTitle("title :" + i);
            mList.add(item);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new RecyclerAdapter(mList, isNeedDelay);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static void start(Activity activity, Boolean isDelay) {
        Bundle bundle = null;
        if (isDelay) {
            bundle = new Bundle();
            bundle.putBoolean(FrameSkipTestActivity.IS_DELAY, true);
        }
        Intent intent = new Intent(activity, FrameSkipTestActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }
}
