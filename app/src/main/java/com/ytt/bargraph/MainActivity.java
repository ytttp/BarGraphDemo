package com.ytt.bargraph;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BarGraphView bargraphview = (BarGraphView) findViewById(R.id.bargraphview);
        final int[][] data = {{182, 89, 78, 88}, {34, 85, 16, 96}, {46, 29, 78, 41}, {54, 75, 54, 12}};
        final int[] colorData = {Color.RED, Color.BLACK, Color.GRAY, Color.GREEN, Color.LTGRAY};
        final String[] textData = {"一月份", "二月份", "三月份", "四月份"};
        bargraphview.setBarGraphData(data, colorData, textData);
        findViewById(R.id.start_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bargraphview.startAnimation(1000);
            }
        });
        final EditText bar_inttext = (EditText) findViewById(R.id.bar_inttext);
        findViewById(R.id.barwidth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(bar_inttext.getText().toString().trim()))
                    return;
                bargraphview.setBarGraphWidth(Integer.parseInt(bar_inttext.getText().toString().trim()));
            }
        });
        findViewById(R.id.bardistance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(bar_inttext.getText().toString().trim()))
                    return;
                bargraphview.setBarGraphDistance(Integer.parseInt(bar_inttext.getText().toString().trim()));
            }
        });
        final EditText textSize = (EditText) findViewById(R.id.textSize);
        findViewById(R.id.set_x_textSize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(textSize.getText().toString().trim()))
                    return;
                bargraphview.setXTextSize(Float.parseFloat(textSize.getText().toString().trim()));
            }
        });
        findViewById(R.id.set_Y_textSize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(textSize.getText().toString().trim()))
                    return;
                bargraphview.setYTextSize(Float.parseFloat(textSize.getText().toString().trim()));
            }
        });
        findViewById(R.id.first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[][] test = {data[0]};
                bargraphview.setBarGraphData(test, colorData, textData);
            }
        });
        findViewById(R.id.second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[][] test = {data[0], data[1]};
                bargraphview.setBarGraphData(test, colorData, textData);
            }
        });
        findViewById(R.id.third).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[][] test = {data[0], data[1], data[2]};
                bargraphview.setBarGraphData(test, colorData, textData);
            }
        });
        findViewById(R.id.fourth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[][] test = {data[0], data[1], data[2], data[3]};
                int[] colorData = {Color.BLACK, Color.GREEN, Color.RED, Color.GRAY, Color.LTGRAY};
                bargraphview.setBarGraphData(test, colorData, textData);
            }
        });
    }
}
