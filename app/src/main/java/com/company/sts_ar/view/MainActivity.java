package com.company.sts_ar.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.company.sts_ar.ARApplication;
import com.company.sts_ar.R;
import com.company.sts_ar.config.Config;
import com.company.sts_ar.config.Extra;
import com.company.sts_ar.data.SharedVariables;
import com.company.sts_ar.util.FileUtils;
import com.company.sts_ar.view.menu.BarcodeActivity;
import com.company.sts_ar.view.menu.MenuActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by thong.le on 10/18/2017.
 */

public class MainActivity extends AppCompatActivity {

    @Inject
    SharedVariables mSharedVariables;

    public static final int ACTION_SCAN = 121;

    private ProgressDialog mPdDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARApplication.getInstance().getAppComponent().inject(this);

        new RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        Toast.makeText(this, "All permissions are granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission check failed", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        mPdDownload = new ProgressDialog(this);
        mPdDownload.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPdDownload.setMessage("Downloading. Please wait...");
        mPdDownload.setIndeterminate(true);
        mPdDownload.setCanceledOnTouchOutside(false);

        findViewById(R.id.btn_scan).setOnClickListener(view -> {
            startActivityForResult(new Intent(this, BarcodeActivity.class), ACTION_SCAN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ACTION_SCAN) {
            String url = data.getStringExtra(Extra.EXTRA_URL);
            downloadProjectStructure(url);
        }
    }

    private void downloadProjectStructure(String url) {
        mPdDownload.show();
        mSharedVariables.putBaseUrl(url.substring(0, url.lastIndexOf("/")));
        if (!url.contains(Config.PROJECT_JSON)) {
            Toast.makeText(this, "URL is not valid", Toast.LENGTH_SHORT).show();
            return;
        }
        Flowable.fromCallable(() -> {
            String outputPath = Config.DIRECTORY_PATH + File.separator + url.substring(url.lastIndexOf("/") + 1);
            FileUtils.download(url, new File(outputPath));
            return outputPath;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(path -> {
                    mPdDownload.dismiss();
                    Intent intent = new Intent(this, MenuActivity.class);
                    intent.putExtra(Extra.EXTRA_PATH_STRUCTURE, url);
                    startActivity(intent);
                });
    }
}
