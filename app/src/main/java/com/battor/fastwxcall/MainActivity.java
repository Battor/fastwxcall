package com.battor.fastwxcall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;

import com.battor.fastwxcall.customview.ChangeColorIconWithTextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnPageChangeListener, OnClickListener {

    private ViewPager mViewPager;
    private List<Fragment> mTabFragments = new ArrayList<Fragment>();   // 上方 Fragment 的集合
    private FragmentPagerAdapter mAdapter;

    private String[] mContents = new String[] {"这是联系页面", "这是数据页面", "这是设置页面"};

    private List<ChangeColorIconWithTextView> mIndicators = new ArrayList<ChangeColorIconWithTextView>();   // 下方指示器的集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOverflowShowingAlways();
        //getActionBar().setDisplayShowHomeEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);

        initDatas();

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private void initDatas(){
        for (String title : mContents){
            TabFragment tabFragment = new TabFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            tabFragment.setArguments(args);
            mTabFragments.add(tabFragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mTabFragments.get(i);
            }

            @Override
            public int getCount() {
                return mTabFragments.size();
            }
        };

        initTabIndicator();
    }

    private void initTabIndicator(){
        ChangeColorIconWithTextView one = (ChangeColorIconWithTextView) findViewById(R.id.main_indicator1);
        ChangeColorIconWithTextView two = (ChangeColorIconWithTextView) findViewById(R.id.main_indicator2);
        ChangeColorIconWithTextView three = (ChangeColorIconWithTextView) findViewById(R.id.main_indicator3);

        mIndicators.add(one);
        mIndicators.add(two);
        mIndicators.add(three);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);

        one.setTextAndIconAlpha(1.0f);
    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffSetPixels) {

        Log.d("MainActivity", "position = " + position +
                                        "，positionOffset = " + positionOffset +
                                        "，positionOffsetPixels = " + positionOffSetPixels);

        if(positionOffset > 0){
            ChangeColorIconWithTextView left = mIndicators.get(position);
            ChangeColorIconWithTextView right = mIndicators.get(position + 1);

            left.setTextAndIconAlpha(1 - positionOffset);
            right.setTextAndIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();

        switch (v.getId()){
            case R.id.main_indicator1:
                mIndicators.get(0).setTextAndIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.main_indicator2:
                mIndicators.get(1).setTextAndIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.main_indicator3:
                mIndicators.get(2).setTextAndIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
        }
    }

    private void resetOtherTabs(){
        for (ChangeColorIconWithTextView thisIndicator : mIndicators){
            thisIndicator.setTextAndIconAlpha(0);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null)
        {
            if (menu.getClass().getSimpleName().equals("MenuBuilder"))
            {
                try
                {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e)
                {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setOverflowShowingAlways() {
        try
        {
            // true if a permanent menu key is present, false otherwise.
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
