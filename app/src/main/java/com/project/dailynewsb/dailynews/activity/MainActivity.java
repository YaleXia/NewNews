package com.project.dailynewsb.dailynews.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.project.dailynewsb.dailynews.R;
import com.project.dailynewsb.dailynews.base.BaseActivity;
import com.project.dailynewsb.dailynews.fragment.CollectionFragment;
import com.project.dailynewsb.dailynews.fragment.LeftFragment;
import com.project.dailynewsb.dailynews.fragment.NewsFragment;
import com.project.dailynewsb.dailynews.util.slidingmenu.SlidingMenu;

public class MainActivity extends BaseActivity {

    //标题头信息
    private SlidingMenu slidingMenu;
    private TextView title_name;

    //Fragment
    private LeftFragment leftFragment;
    private NewsFragment newsFragment;
    private CollectionFragment collectionFragment;

    //切换
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        title_name = (TextView) findViewById(R.id.title_name);

    }

    @Override
    public void initOnClick() {

    }

    @Override
    public void initData() {

        //初始化SlidingMenu
        initSlidingMenu();

        //初始化Fragment
        initFragment();

    }

    /**
     * 初始化SlidingMenu
     */
    private void initSlidingMenu() {

        slidingMenu = new SlidingMenu(this);
        //设置为左右两边菜单栏
        slidingMenu.setMode(SlidingMenu.LEFT);
        //设置全屏范围都可以打开菜单栏
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置菜单栏的宽度
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        //设置菜单栏与类的关联：当前类显示的为菜单栏的中间界面
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //设置左菜单栏样式
        slidingMenu.setMenu(R.layout.menu_left);
        //设置右菜单栏样式
        //slidingMenu.setSecondaryMenu(R.layout.layout_menu_right);

        //初始化左侧菜单
        leftFragment = new LeftFragment();
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.menu_left, leftFragment);
        ft.commit();
    }

    /**
     * 展示左侧菜单栏
     */
    public void showLeftMenu(View v) {

        slidingMenu.showMenu();

    }

    /**
     * 初始化Fragment
     */
    public void initFragment() {

        newsFragment = new NewsFragment();
        collectionFragment = new CollectionFragment();

    }

    /**
     * 切换Fragment
     */
    public void changeFragment(int position, String title) {

        slidingMenu.showContent();//展示主界面
        title_name.setText(title);//更换标题头

        switch (position) {
            case 1:
                ft = fm.beginTransaction();
                ft.replace(R.id.main_contain, newsFragment);
                ft.commit();

                break;

            case 2:
                ft = fm.beginTransaction();
                ft.replace(R.id.main_contain, collectionFragment);
                ft.commit();

                break;
        }
    }
}
