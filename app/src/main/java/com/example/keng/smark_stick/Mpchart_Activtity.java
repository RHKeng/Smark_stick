package com.example.keng.smark_stick;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class Mpchart_Activtity extends AppCompatActivity {
    private LineChart mLineChart;
    private BarChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpchart__activtity);
        mLineChart=(LineChart) findViewById(R.id.chart);
        mLineChart.setDescription("显示所有天数的运动步数");
        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        LineData data = getData(36, 100);
        mLineChart.setData(data);
    }
    //生成折线图数据集的函数，返回数据集合
    LineData getData(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xVals.add("12/"+i);
        }
        // y轴的数据
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 3;
            yVals.add(new Entry(val, i));
        }
        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet set1 = new LineDataSet(yVals, "老人每天运动数据");
        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);
        set1.setLineWidth(2.75f); // 线宽
        set1.setColor(Color.YELLOW);// 显示颜色
        set1.setCircleColor(Color.RED);// 圆形的颜色
        set1.setHighLightColor(Color.WHITE); // 高亮的线的颜色
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        // create a data object with the datasets
        LineData data =new LineData(xVals,dataSets);
        return data;
    }
}
