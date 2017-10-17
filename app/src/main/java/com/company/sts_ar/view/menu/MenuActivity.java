package com.company.sts_ar.view.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.company.sts_ar.R;
import com.company.sts_ar.util.GridDividerDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by thong.le on 10/17/2017.
 */

public class MenuActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MenuAdapter mAdapter;
    private List<Project> mProjects;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new GridDividerDecoration(getResources(), 4, true));

        mAdapter = new MenuAdapter(this,mProjects);
        mRecyclerView.setAdapter(mAdapter);

        Observable.fromCallable(() -> new Gson().fromJson("file:///android_asset/project.json", Data.class).projects).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(projects -> mAdapter.addItems(projects));
    }
}
