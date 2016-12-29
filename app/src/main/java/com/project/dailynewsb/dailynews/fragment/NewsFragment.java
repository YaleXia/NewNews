package com.project.dailynewsb.dailynews.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.dailynewsb.dailynews.R;
import com.project.dailynewsb.dailynews.adapter.NewsAdapter;
import com.project.dailynewsb.dailynews.config.AllRequestUrl;
import com.project.dailynewsb.dailynews.entity.NewsTypeVo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class NewsFragment extends Fragment {

    private TabLayout news_tl;
    private ViewPager news_vp;
    private ArrayList<NewsTypeVo> newsTypeVos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, null);
        news_tl = (TabLayout) view.findViewById(R.id.news_tl);
        news_vp = (ViewPager) view.findViewById(R.id.news_vp);

        //初始化数据
        newsTypeVos = new ArrayList<>();

        // 准备适配器
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取新闻类型
                getNewsType();
            }
        }).start();


        return view;
    }

    /**
     * 获取新闻分类：ver=版本号&imei=手机标识符
     */
    public void getNewsType() {

        try {

            HttpPost httpPost = new HttpPost(AllRequestUrl.newsType);
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("ver", "0"));
            pairs.add(new BasicNameValuePair("imei", "0"));
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);

            if (httpResponse != null) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                Log.e("TAL", "result:" + result);

                //解析数据
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("status") == 0) {
                    //响应正常
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectSub = jsonArray.getJSONObject(i);
                        JSONArray jsonArraySub = jsonObjectSub.getJSONArray("subgrp");

                        for (int j = 0; j < jsonArraySub.length(); j++) {

                            JSONObject jsonObjectSubSub = jsonArraySub.getJSONObject(j);

                            NewsTypeVo newsTypeVo = new NewsTypeVo();
                            newsTypeVo.setSubgroup(jsonObjectSubSub.getString("subgroup"));
                            newsTypeVo.setSubid(jsonObjectSubSub.getInt("subid"));
                            newsTypeVos.add(newsTypeVo);
                        }
                    }

                    //数据获取完成
                    handler.sendEmptyMessage(1);

                } else {
                    //发生错误
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新主界面
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            news_vp.setAdapter(new NewsAdapter(getChildFragmentManager(), newsTypeVos));
            news_tl.setupWithViewPager(news_vp);
        }
    };
}
