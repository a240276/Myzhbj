package com.huasuan.myzhbj.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class InnerViewPager extends ViewPager {

    private float mDownX;
    private float mDownY;
    public static final String TAG = InnerViewPager.class.getSimpleName();

    public InnerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerViewPager(Context context) {
        super(context);
    }

    /**
     * getparent(父容器).request(请求)Disallow(不)Intercept(拦截)TouchEvent(touch事件)(true(同意))->请求父亲不拦截-->自己处理事件
     * requestDisallowInterceptTouchEvent(false)->父亲优先拦截
     * if(postion==0){
     * if(往右){
     * 父亲处理
     * }else{
     * 孩子处理
     * }
     * }else if(postion == getCount()-1){
     * if(往右){
     * 孩子处理
     * }else{
     * 父亲处理
     * }
     * <p>
     * }else{
     * 自己处理
     * }
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getRawX();
                float moveY = ev.getRawY();

                int diffX = (int) (moveX - mDownX + .5f);

                int position = this.getCurrentItem();
                /**
                 if(postion==0){
                 if(往右){
                 父亲处理
                 }else{
                 孩子处理
                 }
                 }else if(postion == getCount()-1){
                 if(往右){
                 孩子处理
                 }else{
                 父亲处理
                 }

                 }else{
                 自己处理
                 }
                 */
                if (position == 0) {
                    if (diffX > 0) {// 往右
                        Log.i(TAG, "往右-->父亲处理");
                        getParent().requestDisallowInterceptTouchEvent(false);// 父亲处理
                    } else {
                        Log.i(TAG, "往左-->自己处理");
                        getParent().requestDisallowInterceptTouchEvent(true);// 自己处理
                    }
                } else if (position == getAdapter().getCount() - 1) {
                    if (diffX > 0) {// 往右
                        Log.i(TAG, "往右-->自己处理");
                        getParent().requestDisallowInterceptTouchEvent(true);// 自己处理
                    } else {// 往左
                        Log.i(TAG, "往左-->父亲处理");
                        getParent().requestDisallowInterceptTouchEvent(false);// 父亲处理
                    }
                } else {
                    Log.i(TAG, "自己处理");
                    getParent().requestDisallowInterceptTouchEvent(true);// 自己处理
                }

                break;
            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO
        return super.onTouchEvent(ev);
    }

    public void setOnPageChangeListener(OnTouchListener onTouchListener) {
    }
}
