package com.huasuan.myzhbj.controller.menu.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huasuan.myzhbj.R;
import com.huasuan.myzhbj.bean.NewsCenterBean;
import com.huasuan.myzhbj.bean.NewsListPagerBean;
import com.huasuan.myzhbj.conf.Constants;
import com.huasuan.myzhbj.controller.menu.MenuController;
import com.huasuan.myzhbj.utils.SPUtils;
import com.huasuan.myzhbj.views.RefreshListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 1,提供视图
 * 2，自己加载数据
 * 3，数据绑定
 * Created by john on 2017/3/7.
 */

public class NewsListController extends MenuController implements ViewPager.OnPageChangeListener, AdapterView.OnItemClickListener,
        RefreshListView.OnRefreshListener {
    private NewsCenterBean.NewsCenterChildBean mNewsCenterChildBean;
    private static final String TAG = "NewsListController";
    @ViewInject(R.id.newslist_page_indicator_container)
    LinearLayout mIndicatorContainer;

    @ViewInject(R.id.newslist_page_title)
    TextView mTvTitle;

    @ViewInject(R.id.newslist_page_viewpager)
    ViewPager mViewPager;
    @ViewInject(R.id.newslist_page_listview)
    RefreshListView mRefreshListView;

    private List<NewsListPagerBean.NewsListPagerDataBean.NewsListPagerTopNewsBean> mTopnews;


    private Timer mTimer;
    private SPUtils mSpUtils;
    // public static boolean isAutoScroll = true;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

																/*
                                                                int currentItem = mViewPager.getCurrentItem();
																if (currentItem == mViewPager.getAdapter().getCount() - 1) {
																	currentItem = 0;
																} else {
																	currentItem++;
																}
																// 设置最新的条目索引
																mViewPager.setCurrentItem(currentItem);
																*/

        }

        ;
    };
    public AutoScrollTask mAutoScrollTask;
    private List<NewsListPagerBean.NewsListPagerDataBean.NewsListPagerNewsBean> mNews;
    private View mHeaderView;
    private String mMoreUrl;
    private NewsListBaseAdapter mNewsAdapter;


    /**
     * @param context 别人传递的已知数据，我们根据这个数据才知道发送什么样的网络请ß求
     */
    public NewsListController(Context context, NewsCenterBean.NewsCenterChildBean newsCenterChildBean) {
        super(context);
        mNewsCenterChildBean = newsCenterChildBean;
        mSpUtils = new SPUtils(mContext);
    }

    @Override
    public View initView(Context context) {
        View view = View.inflate(mContext, R.layout.newslist_page, null);
        x.view().inject(this, view);

        // 通过打气筒找出我们的头布局
        mHeaderView = view.inflate(mContext, R.layout.infalte_newslist_page_headerview, null);
        x.view().inject(this, mHeaderView);

        // 为listView添加头布局,必须放置在setAdapter之前
        mRefreshListView.addCustomHeader(mHeaderView);

        // 设置viewPager滚动的监听
        mViewPager.setOnPageChangeListener(this);

        // 设置refresListView的点击事件
        mRefreshListView.setOnItemClickListener(this);

        // 监听下拉刷新
        mRefreshListView.setOnRefreshListener(this);
        return view;

    }


    @Override
    public void initData() {
        //发起网络请求
        final String url = Constants.URL.BASEURL + mNewsCenterChildBean.url;

        String mCacheJsonString = mSpUtils.getString(url, "");

        if (!TextUtils.isEmpty(mCacheJsonString)) {
            Log.i(TAG, "预先从缓存加载数据-->" + url);
            processData(mCacheJsonString);
        }
        //发起网络请求
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "数据加载完成" + url);
                //Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                String resJsonString = result;
                // 缓存协议内容到sp中(持久化)
                mSpUtils.putString(url, resJsonString);
                processData(resJsonString);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                Toast.makeText(x.app(), "网络连接失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "CancelledException" + cex, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                // Toast.makeText(x.app(), "关闭" + mNewsCentrChildBean.id, Toast.LENGTH_LONG).show();
            }
        });


        //解析数据
        //刷新ui

        super.initData();


    }

    protected void processData(String jsonString) {
        // 2.解析数据
        Gson gson = new Gson();
        NewsListPagerBean newsListPagerBean = gson.fromJson(jsonString, NewsListPagerBean.class);
        mTopnews = newsListPagerBean.data.topnews;

        // 保存more字段值
        mMoreUrl = newsListPagerBean.data.more;
        if (TextUtils.isEmpty(mMoreUrl)) {
            // TODO 没有加载更多
        }

        // 设置ViewPager的Adapter信息

        NewsListPagerAdapter topNewsAdapter = new NewsListPagerAdapter();
        mViewPager.setAdapter(topNewsAdapter);

        // 设置ViewPager第一页应该显示的title信息
        mTvTitle.setText(mTopnews.get(0).title);

        // 移除mIndicatorContainer里面的所有的视图
        mIndicatorContainer.removeAllViews();
        // 添加indicatorContainer应有的视图
        for (int i = 0; i < mTopnews.size(); i++) {
            ImageView indicator = new ImageView(mContext);
            indicator.setImageResource(R.drawable.dot_normal);

            int width =
                    (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources()
                            .getDisplayMetrics()) + .5f);
            int height =
                    (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources()
                            .getDisplayMetrics()) + .5f);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.leftMargin =
                    (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources()
                            .getDisplayMetrics()) + .5f);

            mIndicatorContainer.addView(indicator, params);
            // 默认第一个是选中的效果
            if (i == 0) {
                indicator.setImageResource(R.drawable.dot_focus);
            }
        }

        // 自动轮播
        // startScroll();
        if (mAutoScrollTask == null) {
            mAutoScrollTask = new AutoScrollTask();
            mAutoScrollTask.start();
        }


        // 按下去的时候停止轮播
        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // isAutoScroll = false;
                        // 停止轮播
                        // stopScroll();
                        if (mAutoScrollTask != null) {
                            mAutoScrollTask.stop();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        // case MotionEvent.ACTION_CANCEL:
                        Log.i(TAG, "==ACTION_UP==");
                        // isAutoScroll = true;
                        // 开始轮播
                        // startScroll();
                        if (mAutoScrollTask != null) {
                            mAutoScrollTask.start();
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        // 处理listview应该展示的数据

        // dataSet
        mNews = newsListPagerBean.data.news;

        mNewsAdapter = new NewsListBaseAdapter();
        mRefreshListView.setAdapter(mNewsAdapter);

    }


    //新闻列表Adapter
    public class NewsAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            if (mNews != null) {
                return mNews.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mNews != null) {
                return mNews.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*--------------- 决定根视图 ---------------*/
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = View.inflate(mContext, R.layout.item_news, null);
                holder = new ViewHolder();
                holder.mIvIcon = (ImageView) convertView.findViewById(R.id.item_news_icon);
                holder.mTvTitle = (TextView) convertView.findViewById(R.id.item_news_title);
                holder.mTvDate = (TextView) convertView.findViewById(R.id.item_news_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            //data
            NewsListPagerBean.NewsListPagerDataBean.NewsListPagerNewsBean newsListPagerNewsBean = mNews.get(position);
            //数据绑定
            x.image().bind(holder.mIvIcon, newsListPagerNewsBean.listimage);
            holder.mTvTitle.setText(newsListPagerNewsBean.title);
            holder.mTvDate.setText(newsListPagerNewsBean.pubdate);

            return convertView;
        }

        class ViewHolder {
            TextView mTvTitle;
            TextView mTvDate;
            ImageView mIvIcon;
        }
    }

    public class AutoScrollTask implements Runnable {
        /**
         * 开始滚动
         */
        public void start() {
            // 得到一个主线程的handler
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, 3000);
        }

        /**
         * 停止滚动
         */
        public void stop() {
            // 移除任务
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            int currentItem = mViewPager.getCurrentItem();
            if (currentItem == mViewPager.getAdapter().getCount() - 1) {
                currentItem = 0;
            } else {
                currentItem++;
            }
            mViewPager.setCurrentItem(currentItem);
            // 递归
            start();
        }
    }

    /**
     * 开始轮播
     */
    public void startScroll() {
        // if (!isAutoScroll) {
        // return;
        // }
        Log.i(TAG, "==startScroll==");
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // 无法去更新ui
                    // 可以通过消息机制.通知刷新ui
                    mHandler.sendEmptyMessage(0);
                }
            }, 3000, 3000);
        }
    }

    /**
     * 停止轮播
     */
    public void stopScroll() {
        Log.i(TAG, "==stopScroll==");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    class NewsListBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mNews != null) {
                return mNews.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mNews != null) {
                return mNews.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*--------------- 决定根视图 ---------------*/
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_news, null);
                holder = new ViewHolder();
                holder.mIvIcon = (ImageView) convertView.findViewById(R.id.item_news_icon);
                holder.mTvTitle = (TextView) convertView.findViewById(R.id.item_news_title);
                holder.mTvDate = (TextView) convertView.findViewById(R.id.item_news_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // data
            NewsListPagerBean.NewsListPagerDataBean.NewsListPagerNewsBean newsListPagerNewsBean = mNews.get(position);

            //判断该条目是否看过
            boolean hasViewed = mSpUtils.getBoolean(newsListPagerNewsBean.id + "", false);
            if (hasViewed){
                holder.mTvTitle.setTextColor(Color.GRAY);
            }else {
                holder.mTvTitle.setTextColor(Color.BLACK);
            }

            // 数据绑定
            x.image().bind(holder.mIvIcon, newsListPagerNewsBean.listimage);
            holder.mTvDate.setText(newsListPagerNewsBean.pubdate);
            holder.mTvTitle.setText(newsListPagerNewsBean.title);

            return convertView;
        }

        class ViewHolder {
            TextView mTvTitle;
            TextView mTvDate;
            ImageView mIvIcon;
        }
    }


    class NewsListPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mTopnews != null) {
                return mTopnews.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            // TODO
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = new ImageView(mContext);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            // data
            NewsListPagerBean.NewsListPagerDataBean.NewsListPagerTopNewsBean newsListPagerTopNewsBean = mTopnews.get(position);
            // 加载图片
            x.image().bind(iv, newsListPagerTopNewsBean.topimage);
            container.addView(iv);

            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);

        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        NewsListPagerBean.NewsListPagerDataBean.NewsListPagerTopNewsBean newsListPagerTopNewsBean = mTopnews.get(position);
        String title = newsListPagerTopNewsBean.title;
        // 设置对应的title即可
        mTvTitle.setText(title);

        // 设置应该选中的点
        // 1.还原所有的点-->没有选中
        for (int i = 0; i < mTopnews.size(); i++) {
            ImageView indicator = (ImageView) mIndicatorContainer.getChildAt(i);
            indicator.setImageResource(R.drawable.dot_normal);
            // 2.选中应该选中的
            if (i == position) {
                indicator.setImageResource(R.drawable.dot_focus);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            Log.i(TAG, "SCROLL_STATE_IDLE");
            startScroll();
        }
    }


    @Override
    public void onRefresh(RefreshListView refreshListView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 在run方法体里面,代码执行严格按照顺序执行
                SystemClock.sleep(2000);
                // 重新加载数据
                // http://188.188.3.100:8080/zhbj/10007/list_1.json
                final String url = Constants.URL.BASEURL + mNewsCenterChildBean.url;
                // 1.发起网络请求
                RequestParams params = new RequestParams(url);
                x.http().get(params, new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(mContext, "下拉刷新成功", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "数据加载完成" + url);
                        String resJsonString = result;
                        // 缓存协议内容到sp中(持久化)
                        mSpUtils.putString(url, resJsonString);

                        processData(resJsonString);
                        // 结束下拉刷新的效果
                        mRefreshListView.stopRefresh();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(mContext, "下拉刷新失败", Toast.LENGTH_SHORT).show();
                        mRefreshListView.stopRefresh();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        }).start();

    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        Log.i(TAG, "---onLoadMore---");
        // 判断是否有加载更多
        if (TextUtils.isEmpty(mMoreUrl)) {
            Toast.makeText(mContext, "没有加载更多了", 0).show();

            //没有加载更多
            mRefreshListView.setHasLoadMore(false);
            return;
        }

        // 加载更多的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 在run方法体里面,代码执行严格按照顺序执行
                SystemClock.sleep(2000);
                // 重新加载数据
                // http://188.188.3.100:8080/zhbj/10007/list_1.json
                final String url = Constants.URL.BASEURL + mMoreUrl;
                // 1.发起网络请求
                // 1.发起网络请求
                RequestParams params = new RequestParams(url);
                x.http().get(params, new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        Log.i(TAG, "加载更多成功" + url);
                        String resJsonString = result;

                        // 得到加载更多的数据
                        Gson gson = new Gson();
                        NewsListPagerBean newsListPagerBean = gson.fromJson(resJsonString, NewsListPagerBean.class);
                        List<NewsListPagerBean.NewsListPagerDataBean.NewsListPagerNewsBean> loadMoreNews = newsListPagerBean.data.news;

                        // 修改mMoreUrl为最新的一个值
                        mMoreUrl = newsListPagerBean.data.more;

                        // 把加载更多的追加到之前数据集上面
                        mNews.addAll(loadMoreNews);

                        // 刷新listview
                        mNewsAdapter.notifyDataSetChanged();

                        //加载更多完成,而且是成功
                        mRefreshListView.stopLoadMore(true);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(mContext, "加载更多失败", Toast.LENGTH_SHORT).show();
                        //加载更多完成,而且是失败
                        mRefreshListView.stopLoadMore(false);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

            }
        }).start();
    }

    /**
     * 点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - mRefreshListView.getHeaderViewsCount();

        NewsListPagerBean.NewsListPagerDataBean.NewsListPagerNewsBean newsListPagerNewsBean = mNews.get(position);

        String url = newsListPagerNewsBean.url;
        String title = newsListPagerNewsBean.title;
        Intent intent = new Intent(mContext, NewsDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);

        mContext.startActivity(intent);
        //记录当前条目，代表已看
        mSpUtils.putBoolean(newsListPagerNewsBean.id + "", true);
        //刷新UI
        mNewsAdapter.notifyDataSetChanged();
    }

}
