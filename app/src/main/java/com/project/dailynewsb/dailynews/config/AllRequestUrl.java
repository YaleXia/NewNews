package com.project.dailynewsb.dailynews.config;

/**
 * Created by administrator on 2016/12/6.
 */

public class AllRequestUrl {

    //根路径（调试环境→测试环境→生产环境）
    public static String rootPath = "http://118.244.212.82:9092/newsClient/";

    //新闻分类
    public static String newsType = rootPath + "news_sort";

    //新闻列表
    public static String newsList = rootPath + "news_list";

}
