package com.company.sts_ar.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.company.sts_ar.R;
import com.company.sts_ar.util.AppUtils;
import com.company.sts_ar.util.GridDividerDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by thong.le on 10/17/2017.
 */

public class MenuActivity extends AppCompatActivity {

    public static final int ACTION_SCAN = 121;

    private RecyclerView mRecyclerView;
    private MenuAdapter mAdapter;
    private List<Project> mProjects = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new GridDividerDecoration(getResources(), 4, true));

        mAdapter = new MenuAdapter(this, mProjects);
        mRecyclerView.setAdapter(mAdapter);

        Observable.fromCallable(() -> new GsonBuilder().create().fromJson(AppUtils.loadJSONFromAsset(this), Data.class).projects).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(projects -> mAdapter.addItems(projects));

        findViewById(R.id.fab)
                .setOnClickListener(view -> startActivityForResult(new Intent(MenuActivity.this, BarcodeActivity.class), ACTION_SCAN));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ACTION_SCAN) {
            String folderName = data.getStringExtra(BarcodeActivity.EXTRA_FOLDER);
            for (int i = 0; i < mProjects.size(); i++) {
                Project project = mProjects.get(i);
                if (project.folder.trim().equalsIgnoreCase(folderName.trim())) {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_PROJECT, project);
                    startActivity(intent);
                    break;
                }
            }
        }
    }
}
