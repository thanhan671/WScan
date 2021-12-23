package com.thanhan.wscan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

public class Scan extends AppCompatActivity {

    private static final int CAMERA_REQ_CODE = 100;
    private static final int REQUEST_CODE_SCAN_ONE = 222;
    final int PERMISSIONS_LENGTH = 2;
    String slink;
    TextView link;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        // CAMERA_REQ_CODE is user-defined and is used to receive the request code of the permission verification result.
        this.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_REQ_CODE);
        // QRCODE_SCAN_TYPE and DATAMATRIX_SCAN_TYPE are set for the barcode format, indicating that Scan Kit will support only QR code and Data Matrix.
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE, HmsScan.ALL_SCAN_TYPE).create();
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options);

        link = (TextView) findViewById(R.id.tv_link);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_access).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slink = link.getText().toString().trim();
                Intent intent = new Intent(getApplicationContext(),DisplayLink.class);
                intent.putExtra("link", slink);
                startActivity(intent);
            }
        });
    }
    // Use the onRequestPermissionsResult function to receive the permission verification result.

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Check whether requestCode is set to the value of CAMERA_REQ_CODE during permission application, and then check whether the permission is enabled.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQ_CODE && grantResults.length == PERMISSIONS_LENGTH && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // Call the barcode scanning API to build the scanning capability.

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            // Input an image for scanning and return the result.
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                // Display the parsing result.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(obj.originalValue);
                builder.setTitle("Result Scan");

                builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        link.setText(obj.originalValue);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

}