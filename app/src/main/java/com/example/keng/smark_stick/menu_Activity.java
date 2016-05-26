package com.example.keng.smark_stick;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class menu_Activity extends Activity {
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    MyAdapter mAdapter;
    String[] dt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        //设置横向布局
        //mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        mAdapter = new MyAdapter(getdeta());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MyAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                //DO your fucking bussiness here!
            }
        });
    }

    private String[] getdeta() {
        dt= new String[]{"少年不识愁滋味，爱上层楼", "爱上层楼，为赋新词强说愁", "而今识尽愁滋味，欲说还休", "欲说还休，却道天凉好个秋"};
        return dt;
    }

}
