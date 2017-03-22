package com.huasuan.myzhbj.controller.menu;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huasuan.myzhbj.MainActivity;
import com.huasuan.myzhbj.R;
import com.huasuan.myzhbj.bean.NewsCenterBean;
import com.huasuan.myzhbj.controller.menu.news.NewsListController;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class NewsMenuController extends MenuController implements ViewPager.OnPageChangeListener {
    @ViewInject(R.id.newscenter_news_tabs)
    MagicIndicator mMagicIndicator;

    @ViewInject(R.id.newscenter_news_viewpager)
    ViewPager mViewPager;

    private static final String TAG = "NewsMenuController";

    private NewsCenterBean.NewsCenterMenuBean mNewsCenterMenuBean;

    private List<NewsCenterBean.NewsCenterChildBean> mChildren;

    private List<NewsListController> mNewsListControllers;


    public NewsMenuController(Context context, NewsCenterBean.NewsCenterMenuBean newsCenterMenuBean) {
        super(context);
        // 使用成员变量,保存NewsMenuController已知的数据
        mNewsCenterMenuBean = newsCenterMenuBean;
        mChildren = mNewsCenterMenuBean.children;

    }

    @Override
    public View initView(Context context) {
        View view = View.inflate(mContext, R.layout.newscenter_news, null);
        x.view().inject(this, view);

        //添加ViewPager的切换事件
        mViewPager.setOnPageChangeListener(NewsMenuController.this);
        return view;
    }

    @Override
    public void initData() {

        // NewsMenuController里面的全部内容通过一个集合进行管理
        mNewsListControllers = new ArrayList<NewsListController>();
        for (NewsCenterBean.NewsCenterChildBean newsCenterChildBean : mChildren) {
            // 应该通过构造方法传入已知条件
            NewsListController newsListController = new NewsListController(mContext, newsCenterChildBean);
            mNewsListControllers.add(newsListController);
        }
        //添加新闻tab导航
        mViewPager.setAdapter(new NewsMenuAdapter());
        initMagicIndicator();


        super.initData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setSlidingMenuTouchMode(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initMagicIndicator() {
//        mMagicIndicator.setBackgroundColor(Color.parseColor("#ffffff"));
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setScrollPivotX(0.8f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mChildren == null ? 0 : mChildren.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mChildren.get(index).title);
                simplePagerTitleView.setTextSize(20);
                simplePagerTitleView.setPadding(15, 0, 15, 0);
                simplePagerTitleView.setNormalColor(Color.parseColor("#616161"));
                simplePagerTitleView.setSelectedColor(Color.RED);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#f00000"));


                return indicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }


    public class NewsMenuAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mNewsListControllers == null ? 0 : mNewsListControllers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            TextView textView = new TextView(container.getContext());
//            textView.setText(mChildren.get(position).title);
//            textView.setGravity(Gravity.CENTER);
//            textView.setTextColor(Color.RED);
//            textView.setTextSize(24);
//            container.addView(textView);
            NewsListController newsListController = mNewsListControllers.get(position);
            View rootView = newsListController.mRootView;

            container.addView(rootView);

            // 现在还让他加载数据
            newsListController.initData();
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            TextView textView = (TextView) object;
            String text = textView.getText().toString();
            int index = mChildren.indexOf(text);
            if (index >= 0) {
                return index;
            }
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (CharSequence) mChildren.get(position);
        }
    }



    public void setSlidingMenuTouchMode(int position) {
        if (position == 0) {//可以拖动的SlidingMenu
            ((MainActivity) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        } else {
            ((MainActivity) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        }
    }

}
