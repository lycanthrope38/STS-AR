package com.company.sts_ar.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.company.sts_ar.R;
import com.company.sts_ar.view.UserDefinedTargets;

/**
 * Created by thong.le on 10/17/2017.
 */

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_PROJECT = "extra_project";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        findViewById(R.id.fab).setOnClickListener(view -> {
            Intent intent = new Intent(this, UserDefinedTargets.class);
            intent.putExtra(EXTRA_PROJECT, getIntent().getStringExtra(EXTRA_PROJECT));
            startActivity(intent);
        });

    }
}
