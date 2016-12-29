package com.project.dailynewsb.dailynews.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.project.dailynewsb.dailynews.R;
import com.project.dailynewsb.dailynews.adapter.NewsSubAdapter;
import com.project.dailynewsb.dailynews.config.AllRequestUrl;
import com.project.dailynewsb.dailynews.entity.NewsVo;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 列表Fragment
 * Created by administrator on 2016/12/1.
 */

public class NewsSubFragment extends Fragment implements XRecyclerView.LoadingListener {

    private XRecyclerView news_sub_xrv;
    private int newsId;
    private ArrayList<NewsVo> newsVoList;
    private int lastNewsId = 0;//上次请求的新闻ID

    public NewsSubFragment(int newsId) {
        this.newsId = newsId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_sub, null);
        news_sub_xrv = (XRecyclerView) view.findViewById(R.id.news_sub_xrv);

        //初始化数据
        newsVoList = new ArrayList<>();

        //初始化XRecyclerView
        initXRecyclerView();

        //获取数据
        getNewsList(true);


        return view;
    }

    /**
     * 获取新闻列表:ver=版本号&subid=分类名&dir=1&nid=新闻id&stamp=20140321&cnt=20
     */
    public void getNewsList(final boolean isRefresh) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    if (isRefresh) {
                        newsVoList.clear();//如果刷新数据清空
                    }

                    HttpPost httpPost = new HttpPost(AllRequestUrl.newsList);
                    List<NameValuePair> pairs = new ArrayList<>();
                    pairs.add(new BasicNameValuePair("ver", "0"));
                    pairs.add(new BasicNameValuePair("subid", String.valueOf(newsId)));//对应的subId是谁，列表就会展示谁
                    if (isRefresh) {
                        pairs.add(new BasicNameValuePair("dir", "1"));
                        pairs.add(new BasicNameValuePair("nid", "0"));//可以省略
                    } else {
                        pairs.add(new BasicNameValuePair("dir", "2"));
                        pairs.add(new BasicNameValuePair("nid", String.valueOf(lastNewsId)));//可以省略
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                    String date = simpleDateFormat.format(new Date());
                    pairs.add(new BasicNameValuePair("stamp", date));//当前时间
                    pairs.add(new BasicNameValuePair("cnt", "20"));

                    httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    if (httpResponse != null) {
                        String result = EntityUtils.toString(httpResponse.getEntity());
                        Log.e("TAL", "请求结果：" + result);

                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("status") == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObjectSub = jsonArray.getJSONObject(i);

                                NewsVo newsVo = new NewsVo();
                                newsVo.setIcon(jsonObjectSub.getString("icon"));
                                newsVo.setLink(jsonObjectSub.getString("link"));
                                newsVo.setNid(jsonObjectSub.getInt("nid"));
                                newsVo.setStamp(jsonObjectSub.getString("stamp"));
                                newsVo.setSummary(jsonObjectSub.getString("summary"));
                                newsVo.setTitle(jsonObjectSub.getString("title"));
                                newsVo.setType(jsonObjectSub.getInt("type"));

                                //如果是最后一条数据，要存储newsID以备下次上拉加载更多请求使用
                                if (i == jsonArray.length() - 1) {
                                    lastNewsId = jsonObjectSub.getInt("nid");
                                }

                                newsVoList.add(newsVo);
                            }
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    /**
     * 更新主界面
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            news_sub_xrv.refreshComplete();
            news_sub_xrv.loadMoreComplete();
            news_sub_xrv.setLoadingMoreEnabled(true);
            news_sub_xrv.setPullRefreshEnabled(true);
            news_sub_xrv.setAdapter(new NewsSubAdapter(newsVoList));
        }
    };

    /**
     * 初始化initXRecyclerView
     */
    public void initXRecyclerView() {

        // # 1 设置方向
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        news_sub_xrv.setLayoutManager(layoutManager);

        // # 2 设置可上拉下拉
        news_sub_xrv.setPullRefreshEnabled(true);
        news_sub_xrv.setLoadingMoreEnabled(true);

        // # 3 设置上拉下拉动画
        news_sub_xrv.setRefreshProgressStyle(ProgressStyle.BallBeat);
        news_sub_xrv.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        // # 4 设置上拉下拉监听
        news_sub_xrv.setLoadingListener(this);

        // # 5 这是下拉箭头
        news_sub_xrv.setArrowImageView(R.drawable.btn_back);

        // # 6 添加头文件
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_test, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
//        news_sub_xrv.addHeaderView(view);

    }

    @Override
    public void onRefresh() {

        //数据请求
        getNewsList(true);
        news_sub_xrv.setLoadingMoreEnabled(false);
        news_sub_xrv.setPullRefreshEnabled(false);
    }

    @Override
    public void onLoadMore() {

        getNewsList(false);
        news_sub_xrv.setLoadingMoreEnabled(false);
        news_sub_xrv.setPullRefreshEnabled(false);
    }
}
