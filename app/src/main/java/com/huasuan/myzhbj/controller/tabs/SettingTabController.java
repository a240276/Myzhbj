package com.huasuan.myzhbj.controller.tabs;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * @描述	     1.提供视图
 * @描述	     2.接收数据/加载数据
 * @描述	     3.数据和视图的绑定
 *

 */
public class SettingTabController extends TabController {

	public SettingTabController(Context context) {
		super(context);
	}

	@Override
	public void initStateBar() {
		mTvTitle.setText("设置");
	}

	/**
	 * 初始化controller里面持有的视图
	 * @param context
	 * @return
	 */
	public View initContentView(Context context) {
		TextView tv = new TextView(context);
		tv.setGravity(Gravity.CENTER);
		tv.setText("设置");
		mIbMenu.setVisibility(View.GONE);
		return tv;
	}

	/**
	 * 加载数据,然后数据和视图进行绑定
	 */
	public void initData() {

	}
}
