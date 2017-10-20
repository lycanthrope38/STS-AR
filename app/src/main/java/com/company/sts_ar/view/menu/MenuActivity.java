package com.company.sts_ar.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.company.sts_ar.ARApplication;
import com.company.sts_ar.R;
import com.company.sts_ar.config.Config;
import com.company.sts_ar.config.Extra;
import com.company.sts_ar.data.SharedVariables;
import com.company.sts_ar.di.ApplicationModule;
import com.company.sts_ar.util.AppUtils;
import com.company.sts_ar.util.FileUtils;
import com.company.sts_ar.util.GridDividerDecoration;
import com.company.sts_ar.vo.Data;
import com.company.sts_ar.vo.Project;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by thong.le on 10/17/2017.
 */

public class MenuActivity extends AppCompatActivity {


    @Inject
    SharedVariables mSharedVariables;

    @Inject
    RequestManager mRequestManager;

    public static final int ACTION_SCAN = 121;

    private RecyclerView mRecyclerView;
    private MenuAdapter mAdapter;
    private List<Project> mProjects = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ARApplication.getInstance().getAppComponent().inject(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new GridDividerDecoration(getResources(), 4, true));

        mAdapter = new MenuAdapter(this, mProjects, mRequestManager, mSharedVariables.getBaseUrl());
        mRecyclerView.setAdapter(mAdapter);
        String pathStructure = getIntent().getStringExtra(Extra.EXTRA_PATH_STRUCTURE);
        Observable.fromCallable(() -> new GsonBuilder().create().fromJson(FileUtils.readFile(Config.DIRECTORY_PATH + File.separator + pathStructure.substring(pathStructure.lastIndexOf("/") + 1)), Data.class).projects).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(projects -> mAdapter.addItems(projects));


    }

}
