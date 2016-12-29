package com.project.dailynews.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.project.dailynews.R;
import com.project.dailynews.activity.MainActivity;
import com.project.dailynews.activity.PersonCenterActivity;
import com.project.dailynews.config.AllUrlPaths;
import com.project.dailynews.config.Configs;
import com.project.dailynews.entity.VersionVo;

import java.io.File;

/**
 * Created by administrator on 2016/12/27
 */
public class RightFragment extends Fragment implements View.OnClickListener {

    private TextView right_login;
    private MainActivity mainActivity;
    private RelativeLayout relativelayout_unlogin, relativelayout_logined;
    private ImageView imageView_photo;
    private TextView textView_name;//用户名
    private TextView right_update;//版本更新

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_right, null);
        right_login = (TextView) view.findViewById(R.id.right_login);
        relativelayout_unlogin = (RelativeLayout) view.findViewById(R.id.relativelayout_unlogin);
        relativelayout_logined = (RelativeLayout) view.findViewById(R.id.relativelayout_logined);
        textView_name = (TextView) view.findViewById(R.id.textView_name);
        imageView_photo = (ImageView) view.findViewById(R.id.imageView_photo);
        right_update = (TextView) view.findViewById(R.id.right_update);

        //添加监听
        right_login.setOnClickListener(this);
        imageView_photo.setOnClickListener(this);
        textView_name.setOnClickListener(this);
        right_update.setOnClickListener(this);

        //添加数据

        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.right_login) {
            //登录
            mainActivity.changeFragment(6);

        } else if (v.getId() == R.id.imageView_photo || v.getId() == R.id.textView_name) {
            //跳转个人中心
            startActivity(new Intent(mainActivity, PersonCenterActivity.class));

        } else if (v.getId() == R.id.right_update) {
            //版本更新
            update();

        }
    }

    /**
     * 更新右侧是否登录状态
     */
    public void updateLoginStatus() {
        if (Configs.isLogin) {
            relativelayout_unlogin.setVisibility(View.GONE);
            relativelayout_logined.setVisibility(View.VISIBLE);
            textView_name.setText(Configs.userName);
            imageView_photo.setImageBitmap(Configs.iconBitmap);

        } else {
            relativelayout_unlogin.setVisibility(View.VISIBLE);
            relativelayout_logined.setVisibility(View.GONE);
        }
    }

    /**
     * 版本更新接口
     */
    public void update() {

        RequestParams params = new RequestParams();
        params.addBodyParameter("imei", "0");
        params.addBodyParameter("pkg", getContext().getPackageName());
        params.addBodyParameter("ver", "0");
        new HttpUtils().send(HttpRequest.HttpMethod.POST, AllUrlPaths.update, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                mainActivity.showWaittingDialog();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                Log.e("TAL", "版本更新：" + responseInfo.result);

                Gson gson = new Gson();
                final VersionVo vo = gson.fromJson(responseInfo.result, VersionVo.class);
                if (vo != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle("温馨提示");
                    builder.setMessage("发现新版本：" + vo.getVersion() + ",是否更新？");
                    builder.setNegativeButton("取消", null);
                    builder.setCancelable(false);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downApk(vo.getLink());
                        }
                    });
                    builder.show();
                }
                mainActivity.hideWaittingDialog();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mainActivity.hideWaittingDialog();
            }
        });
    }

    /**
     * 下载APK
     */
    public void downApk(String apkUrl) {

        // # 1 准备下载文件
        File fileDir = new File("/data/data/" + mainActivity.getPackageName() + "/apk/");
        fileDir.mkdirs();
        final File file = new File(fileDir, System.currentTimeMillis() + ".apk");

        // # 2 准备进度条弹出框
        final ProgressDialog dialog = new ProgressDialog(mainActivity);
        dialog.setTitle("温馨提示");
        dialog.setMessage("正在下载中请稍后……");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);//设置弹出框不可关闭
        dialog.show();


        new HttpUtils().download(apkUrl, file.getAbsolutePath(), new RequestCallBack<File>() {

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {

                Toast.makeText(mainActivity, "下载成功！", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                //弹出安装软件界面
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                mainActivity.startActivity(intent);

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mainActivity, "下载失败：" + msg, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                dialog.setMax((int) total);
                dialog.setProgress((int) current);
            }
        });
    }
}