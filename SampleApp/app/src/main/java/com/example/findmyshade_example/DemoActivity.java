package com.example.findmyshade_example;

import com.ringoai.findmyshade.FaceCapture;
import com.example.findmyshade_example.results.ResultsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.HashMap;

public class DemoActivity extends AppCompatActivity {
    private String kLicenseKey = "{license key}";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_activity_layout);
        // POQ: please make note of new setLicenseKey call - result has changed to a String
        String error = FaceCapture.setLicenseKey(getApplicationContext(), kLicenseKey);

        if (error != null) {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        // POQ: please make note of new preflight call - to verify that the device is supported
        HashMap<String, Object> preflight = FaceCapture.preflightRequirements();
        if (preflight.size() > 0) {
            Toast.makeText(this, preflight.toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        Button launchButton = findViewById(R.id.run_sdk);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSDK();
            }
        });
    }

    protected void launchSDK() {
        FaceCapture.FindMyShade(this, new FaceCapture.FindMyShadeCallback() {
            @Override
            public void onResult(JSONObject results) {
                Intent intent = new Intent(DemoActivity.this, ResultsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("matchResults", results.toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
