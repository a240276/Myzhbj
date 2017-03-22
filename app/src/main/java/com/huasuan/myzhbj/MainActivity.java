package com.huasuan.myzhbj;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huasuan.myzhbj.framelaout.ContentFragment;
import com.huasuan.myzhbj.framelaout.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    public static final String TAG_MAIN_CONTENT = "tag_main_content";
    public static final String TAG_MAIN_MENU_LEFT = "tag_main_menu_left";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 告诉它内容区域(above)
        setContentView(R.layout.fragment_main_content);

        // 告诉它菜单区域(behind)-->左菜单
        setBehindContentView(R.layout.fragment_main_menu_left);

        initSlidingMenu();

        initFragment();
    }

    /**
     * 对slidingMenu进行属性的设置
     */
    private void initSlidingMenu() {
        // 1宽度,2模式,3效果

        // 得到slidingMenu对象
        SlidingMenu slidingMenu = getSlidingMenu();

        // 设置菜单的宽度
        // slidingMenu.setBehindWidth(180);//直接设置方式
        slidingMenu.setBehindOffset(400);// 简介设置方式

        /**
         * SlidingMenu.LEFT 只有左菜单   默认
         * SlidingMenu.RIGHT 只有右菜单
         * SlidingMenu.LEFT_RIGHT 有左右菜单
         */
        // 2模式--菜单模式
        slidingMenu.setMode(SlidingMenu.LEFT);
        // 如果菜单模式是RIGHT/LEFT_RIGHT必须添加右菜单

        // 2模式--拖动模式
        /**
         * SlidingMenu.TOUCHMODE_FULLSCREEN 拖动整个屏
         * SlidingMenu.TOUCHMODE_MARGIN  拖动屏幕的边缘
         * SlidingMenu.TOUCHMODE_NONE  无法拖动
         */
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        // 3效果--缩放效果
        slidingMenu.setBehindScrollScale(0.5f);// 内容区域移动一个像素,菜单区域移动.5像素(视差动画)

        // 3效果--阴影效果
        slidingMenu.setShadowWidth(10);
        slidingMenu.setShadowDrawable(R.drawable.shadow);

        // 3效果--渐变效果
        slidingMenu.setFadeDegree(.5f);// 透明度从设置的值到-->完全透明的过程

    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.fl_main_content, new ContentFragment(), TAG_MAIN_CONTENT);
        transaction.add(R.id.fl_main_menu_left, new LeftMenuFragment(), TAG_MAIN_MENU_LEFT);

        transaction.commit();
    }

    /**
     * 得到LeftMenuFragment的实例
     *
     * @return
     */
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        LeftMenuFragment leftMenuFragment =
                (LeftMenuFragment) supportFragmentManager.findFragmentByTag(TAG_MAIN_MENU_LEFT);
        return leftMenuFragment;
    }

    /**
     * 得到ContentFragment的实例
     *
     * @return
     */
    public ContentFragment getContentFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        ContentFragment contentFragment = (ContentFragment) supportFragmentManager.findFragmentByTag(TAG_MAIN_CONTENT);
        return contentFragment;
    }
}
