package com.huasuan.myzhbj.controller.menu;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

/**
 * 1,提供视图
 * 2，加载数据
 * 3，数据和视图绑定
 */

public abstract class MenuController {
	public View		mRootView;
	public Context	mContext;

	public MenuController(Context context) {
		mContext = context;
		mRootView = initView(context);
	}

	/**
	 * @param context 
	 * @des 初始化controller中持有的视图
	 * @des 必须实现,但是不知道具体实现,定义成为抽象方法,交给子类具体实现
	 * @return
	 */
	public abstract View initView(Context context);

	/**
	 * @des 2.加载数据
	 * @des 3.数据和视图的绑定
	 * @call 需要加载数据和刷新ui的时候调用
	 */
	public void initData() {


	}


}
