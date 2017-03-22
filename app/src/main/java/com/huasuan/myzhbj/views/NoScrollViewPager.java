package com.huasuan.myzhbj.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;



public class NoScrollViewPager extends LazyViewPager {

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPager(Context context) {
		super(context);
	}

	/**
	 * 1.
	 * dispatchTouchEvent一般情况不做处理,如果修改了默认的返回值,子孩子都无法收到事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
		// return true;
	}

	/**
	 * 事件的处理谁具有最高的优先级?
	 * 父容器
	 */
	/**
	 * 是否拦截
	 * 	    拦截:会走到自己的onTouchEvent方法里面来
	 *  不拦截:事件传递给子孩子
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// return true;//不行,孩子无法处理事件
		 return false;//可行
		//return super.onInterceptTouchEvent(ev);// 可行
	}

	/**
	 * 是否消费事件
	 * 	    消费:事件就结束
	 *  不消费:往父控件传
	 */
	/**
	 * 为什么viewPager有滚动的效果?
	 * 肯定是ViewPager的源码里面在onTouchEvent里面的ACTION_MOVE分支里面产生的滚动效果
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		 //return false;// 可行
		return true;// 可行
		// super.onTouchEvent(ev);
	}
	/**
	 * 1.ViewPager不能滚动
	 * 2.ViewPager的孩子可以响应点击事件
	 */
}
