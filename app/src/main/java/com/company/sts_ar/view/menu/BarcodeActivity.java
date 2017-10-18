package com.company.sts_ar.view.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import com.company.sts_ar.R;
import com.company.sts_ar.config.Extra;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

/**
 * Created by thong.le on 10/18/2017.
 */

public class BarcodeActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    private BarcodeReader barcodeReader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);
        barcodeReader.setBeepSoundFile("shutter.mp3");
        barcodeReader.setListener(this);

    }

    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();
        Intent intent = new Intent();
        intent.putExtra(Extra.EXTRA_URL, barcode.displayValue);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }
}
