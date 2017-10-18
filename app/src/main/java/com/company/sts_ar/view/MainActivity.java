package com.company.sts_ar.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.company.sts_ar.R;
import com.company.sts_ar.config.Extra;
import com.company.sts_ar.view.menu.BarcodeActivity;
import com.company.sts_ar.view.menu.DetailActivity;
import com.company.sts_ar.view.menu.MenuActivity;
import com.company.sts_ar.vo.Project;

/**
 * Created by thong.le on 10/18/2017.
 */

public class MainActivity extends AppCompatActivity {

    public static final int ACTION_SCAN = 121;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_scan).setOnClickListener(view -> {
            startActivityForResult(new Intent(this, BarcodeActivity.class), ACTION_SCAN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ACTION_SCAN) {
            String url = data.getStringExtra(Extra.EXTRA_URL);
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra(Extra.EXTRA_URL, url);
            startActivity(intent);
        }
    }
}
