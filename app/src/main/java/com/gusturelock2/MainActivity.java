package com.gusturelock2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import widget.LockMovePoint;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {
    public static final String LOCK = "lock";
    public static final String LOCK_KEY = "lock_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(LOCK, MODE_PRIVATE);
        String lockPattenString = preferences.getString(LOCK_KEY, null);
        if (lockPattenString != null) {
            Intent intent = new Intent(this, LockActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lock:
                Intent intent = new Intent(this, LockSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.unlock:
                getSharedPreferences(LOCK, MODE_PRIVATE).edit().clear().commit();
                break;

            default:
                break;
        }
    }

    public void begin(View view) {

        List<LockMovePoint> points = new ArrayList<LockMovePoint>();
        points.add(new LockMovePoint(100, 100, 100, 500));
        points.add(new LockMovePoint(100, 500, 500, 500));
        points.add(new LockMovePoint(500, 500, 500, 100));
        points.add(new LockMovePoint(500, 100, 100, 100));
        points.add(new LockMovePoint(100, 100, 500, 500));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
