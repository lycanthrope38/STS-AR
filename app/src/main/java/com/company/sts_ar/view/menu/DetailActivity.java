package com.company.sts_ar.view.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.company.sts_ar.ARApplication;
import com.company.sts_ar.R;
import com.company.sts_ar.config.Config;
import com.company.sts_ar.config.Extra;
import com.company.sts_ar.data.SharedVariables;
import com.company.sts_ar.util.FileUtils;
import com.company.sts_ar.view.ar.UserDefinedTargets;
import com.company.sts_ar.vo.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by thong.le on 10/17/2017.
 */

public class DetailActivity extends AppCompatActivity {

    @Inject
    SharedVariables mSharedVariables;

    private static final String SEPARATOR = File.separator;

    private List<Project.Component> mComponents = new ArrayList<>();

    private Project mProject;
    private String mBaseUrl;
    private File mFolder;

    private ProgressDialog mPdDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ARApplication.getInstance().getAppComponent().inject(this);
        mBaseUrl = mSharedVariables.getBaseUrl();
        mProject = (Project) getIntent().getSerializableExtra(Extra.EXTRA_PROJECT);

        mFolder = new File(Config.DIRECTORY_PATH + SEPARATOR + mProject.folder);
        if (!mFolder.exists()) {
            mFolder.mkdirs();
        }

        mPdDownload   = new ProgressDialog(this);
        mPdDownload.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPdDownload.setMessage("Downloading. Please wait...");
        mPdDownload.setIndeterminate(true);
        mPdDownload.setCanceledOnTouchOutside(false);

        findViewById(R.id.fab).setOnClickListener(view -> {
            for (int i = 0; i < mProject.size; i++) {
                String standard = String.format("%s%s%s%s%s%s", mBaseUrl, SEPARATOR, mProject.folder, SEPARATOR, mProject.folder, i);
                Project.Component component = new Project.Component(standard + ".jpg", standard + ".obj", standard + ".mtl");
                mComponents.add(component);
            }
            downloadObjects();
        });

        ((TextView) findViewById(R.id.tv_description)).setText(mProject.description);

    }

    private void downloadObjects() {

        mPdDownload.show();

        Observable.fromIterable(mComponents)
                .flatMap(component -> {
                    download(component.image)
                            .subscribeOn(Schedulers.newThread())
                            .doOnNext(path -> {
                                Timber.tag("path-image").d(path);
                            });
                    download(component.obj)
                            .subscribeOn(Schedulers.newThread())
                            .doOnNext(path -> {
                                Timber.tag("path-obj").d(path);
                            });
                    download(component.mtl)
                            .subscribeOn(Schedulers.newThread())
                            .doOnNext(path -> {
                                Timber.tag("path-mtl").d(path);
                            });
                    return Observable.just(true);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> {
                        }, throwable -> {
                        }
                        , () -> {
                            mPdDownload.dismiss();
                            Timber.tag("path--onComplete");
                            Intent intent = new Intent(this, UserDefinedTargets.class);
                            intent.putExtra(Extra.EXTRA_PROJECT, mProject);
                            startActivity(intent);
                        });
    }

    private Observable<String> download(String path) {
        String pathFile = mFolder + SEPARATOR + path.substring(path.lastIndexOf("/") + 1);
        FileUtils.download(path, new File(pathFile));
        return Observable.just(pathFile);
    }
}
