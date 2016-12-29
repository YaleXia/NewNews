package com.project.dailynews.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.project.dailynews.R;
import com.project.dailynews.activity.MainActivity;
import com.project.dailynews.config.AllUrlPaths;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by administrator on 2016/12/28
 */

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText login_et_username, login_et_password, login_et_email;
    private Button login_btn;

    private MainActivity mainActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, null);
        login_et_username = (EditText) view.findViewById(R.id.login_et_username);
        login_et_password = (EditText) view.findViewById(R.id.login_et_password);
        login_et_email = (EditText) view.findViewById(R.id.login_et_email);
        login_btn = (Button) view.findViewById(R.id.login_btn);

        //设置监听
        login_btn.setOnClickListener(this);

        //设置数据

        return view;
    }

    /**
     * 注册接口请求ver=版本号&uid=用户名&email=邮箱&pwd=登陆密码
     *
     *
     */
    public void registerRequest() {

        RequestParams params = new RequestParams();
        params.addBodyParameter("ver", "0");
        params.addBodyParameter("uid", login_et_username.getText().toString());
        params.addBodyParameter("pwd", login_et_password.getText().toString());
        params.addBodyParameter("email", login_et_email.getText().toString());

        new HttpUtils().send(HttpRequest.HttpMethod.POST, AllUrlPaths.register, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                mainActivity.showWaittingDialog();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                Log.e("TAL", "注册请求结果:" + responseInfo.result);

                try {

                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("status") == 0) {

                        JSONObject jsonObjectSub = jsonObject.getJSONObject("data");
                        if (jsonObjectSub.getInt("result") == 0) {
                            //注册成功