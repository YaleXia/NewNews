package com.project.dailynewsb.dailynews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.dailynewsb.dailynews.entity.NewsTypeVo;
import com.project.dailynewsb.dailynews.fragment.NewsSubFragment;

import java.util.ArrayList;

/**
 * Created by administrator on 2016/12/6.
 */
public class NewsAdapter extends FragmentPagerAdapter {

    private ArrayList<NewsTypeVo> newsTypeVos;

    public NewsAdapter(FragmentManager fm, ArrayList<NewsTypeVo> newsTypeVos) {
        super(fm);
        this.newsTypeVos = newsTypeVos;
    }

    @Override
    public Fragment getItem(int position) {
        return new NewsSubFragment(newsTypeVos.get(position).getSubid());
    }

    @Override
    public int getCount() {
        return newsTypeVos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return newsTypeVos.get(position).getSubgroup();
    }
}
