package com.huasuan.myzhbj.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by john on 2017/3/2.
 */

public  abstract class BaseFragment extends Fragment{
    public Context mContext;

    /**
     * 抽取基类有什么好处
     * 1.少写代码,基类里面放置共有的属性以及共有的方法
     * 2.不用关心Fragment的生命周期方法,只需要处理自己定义的方法即可
     * 3.控制某一个方法是必须实现,还是选择性实现
     */

    @Override
    public void onCreate( Bundle savedInstanceState) {
        init();// 可以接受别人传递过来的参数
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return  initView();
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }
    /**
     * 初始化的时候,可以接收别人传递进来的参数
     */
    public void init() {

    }

    /**
     * 初始化Fragment应有的视图
     * @return
     */
    public abstract View initView();

    /**
     * 初始化Fragment应有的数据
     */
    public void initData() {

    }

    /**
     * 初始化监听器
     */
    public void initListener() {

    }


}
