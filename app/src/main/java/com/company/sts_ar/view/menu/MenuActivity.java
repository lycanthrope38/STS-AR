package com.company.sts_ar.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.company.sts_ar.R;
import com.company.sts_ar.util.AppUtils;
import com.company.sts_ar.util.GridDividerDecoration;
import com.company.sts_ar.vo.Data;
import com.company.sts_ar.vo.Project;
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



    }

}
