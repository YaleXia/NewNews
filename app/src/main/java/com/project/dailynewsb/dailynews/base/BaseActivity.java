package com.project.dailynewsb.dailynews.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * Base类
 * Created by administrator on 2016/11/29.
 */

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLayout() != 0) {
            setContentView(getLayout());

            initView();
            initOnClick();
            initData();
        }
    }

    //获取布局
    public abstract int getLayout();

    //获取View
    public abstract void initView();

    //获取监听
    public abstract void initOnClick();

    //获取数据
    public abstract void initData();
}
