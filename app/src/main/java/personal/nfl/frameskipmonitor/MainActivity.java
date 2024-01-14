package personal.nfl.frameskipmonitor;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import personal.nfl.frameskipmonitor.frame.activity.FrameSkipTestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_recyclerView).setOnClickListener(this);
        findViewById(R.id.btn_delay_recyclerView).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FrameSkipTestActivity.start(this, v.getId() == R.id.btn_delay_recyclerView);
    }
}
