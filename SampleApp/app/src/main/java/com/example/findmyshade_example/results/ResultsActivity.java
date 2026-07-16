package com.example.findmyshade_example.results;
import com.ringoai.findmyshade.FaceCapture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findmyshade_example.R;
import com.ringoai.findmyshade.results.ResultsHeaderItem;
import com.ringoai.findmyshade.results.ResultsListItem;
import com.ringoai.findmyshade.results.ResultsMatchAdapter;
import com.ringoai.findmyshade.results.ResultsMatchItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class ResultsActivity extends AppCompatActivity {
    public Toolbar toolbar;
    public RecyclerView recyclerView;
    private TextView textView;
    private ResultsMatchAdapter adapter;
    private List<ResultsListItem> itemList = new ArrayList<>();
    final String TAG = "ResultsActivity";
    public JSONObject matches;
    public JSONObject results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        toolbar = findViewById(R.id.resultsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Results");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });

        recyclerView = findViewById(R.id.matchRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textView = findViewById(R.id.sessionLabel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                String jsonString = (String) extras.getSerializable("matchResults");
                results = new JSONObject(jsonString);
                matches = results.getJSONObject("gridMatches");
                setupRecyclerView();
                String sessionID = results.getString("sessionId");
                double og_L = results.getDouble("OG_L");
                double og_H = results.getDouble("OG_H");
                double grid_L = results.getDouble("personXRiteL");
                double grid_H = results.getDouble("H");
                String info = String.format("SessionID: %s\nOG_L: %.2f, OG_H: %.4f\nL: %.2f, H: %.4f", sessionID,og_L, og_H, grid_L, grid_H);
                textView.setText(info);
            } catch (Exception e) {
                textView.setText(e.getMessage());
            }
        }
    }

    protected void setupRecyclerView() {
        TreeMap<String, JSONObject> smatches = new TreeMap<>();
        Iterator<String> keys = matches.keys();
        while (keys.hasNext()) {
            try {
                String product = keys.next();
                JSONObject match = matches.getJSONObject(product);
                smatches.put(product, match);
            } catch (Exception e) {
                Log.d(TAG, "generateResults() exception: " + e.toString());
            }
        }
        for (String product : smatches.keySet()) {
            try {
                JSONObject match = smatches.get(product);
                double LmapValue = match.getDouble("Lmap");
                JSONArray shadeMatches = match.getJSONArray("shadeMatches");
                int shadeCount = shadeMatches.length();
                boolean Lmap = true;
                String extra = Lmap ? String.format(" [LMap = %.2f]", LmapValue) : "";

                itemList.add(new ResultsHeaderItem(product + extra));
                for (int i = 0; i < shadeCount; i++) {
                    JSONObject smatch = shadeMatches.getJSONObject(i);
                    String shade = smatch.getString("shade");
                    double mL = smatch.getDouble("L");
                    double mH = smatch.getDouble("H");
                    int rank = smatch.getInt("rank");
                    JSONArray color = smatch.getJSONArray("rgb");
                    double[] rgb = new double[3];
                    for (int rgbi = 0; rgbi < 3; rgbi++) {
                        rgb[rgbi] = color.getDouble(rgbi);
                    }

                    itemList.add(new ResultsMatchItem(shade, mL, mH, rank, rgb));
                }
            } catch (Exception e) {
                Log.d(TAG, "generateResults() exception: " + e.toString());
            }
        }
        adapter = new ResultsMatchAdapter(itemList);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        FaceCapture.resultsExited();
    }
}
