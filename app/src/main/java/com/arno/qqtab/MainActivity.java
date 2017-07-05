package com.arno.qqtab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.arno.qqtabview.QQTabView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private QQTabView mTabBubble;
    private QQTabView mTabPerson;
    private QQTabView mTabStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.d(TAG, "onCreate: screenWidth="+screenWidth+",screenHeight="+screenHeight );
        mTabBubble = ((QQTabView) findViewById(R.id.activity_main_bubble));
        mTabPerson = ((QQTabView) findViewById(R.id.activity_main_person));
        mTabStar = ((QQTabView) findViewById(R.id.activity_main_star));
        mTabBubble.setOnClickListener(this);
        mTabPerson.setOnClickListener(this);
        mTabStar.setOnClickListener(this);
        setCurrentTab(0);
    }

    public void setCurrentTab(int position) {
        initTabState();
        switch (position) {
            case 0:
                mTabBubble.setTabAboveImg(R.drawable.bubble_above);
                mTabBubble.setTabBelowImg(R.drawable.bubble_below);
                break;
            case 1:
                mTabPerson.setTabAboveImg(R.drawable.person_above);
                mTabPerson.setTabBelowImg(R.drawable.person_below);
                break;
            case 2:
                mTabStar.setTabAboveImg(R.drawable.star_above);
                mTabStar.setTabBelowImg(R.drawable.star_below);
                break;
        }
    }

    public void initTabState() {
        mTabBubble.setTabAboveImg(R.drawable.pre_bubble_above);
        mTabBubble.setTabBelowImg(R.drawable.pre_bubble_below);
        mTabPerson.setTabAboveImg(R.drawable.pre_person_above);
        mTabPerson.setTabBelowImg(R.drawable.pre_person_below);
        mTabStar.setTabAboveImg(R.drawable.pre_star_above);
        mTabStar.setTabBelowImg(R.drawable.pre_star_below);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_main_bubble:
                setCurrentTab(0);break;
            case R.id.activity_main_person:
                setCurrentTab(1);break;
            case R.id.activity_main_star:
                setCurrentTab(2);break;
        }
    }
}
