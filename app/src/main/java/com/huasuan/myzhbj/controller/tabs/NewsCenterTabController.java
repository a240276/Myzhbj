package com.huasuan.myzhbj.controller.tabs;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huasuan.myzhbj.MainActivity;
import com.huasuan.myzhbj.bean.NewsCenterBean;
import com.huasuan.myzhbj.conf.Constants;
import com.huasuan.myzhbj.controller.menu.InteractMenuController;
import com.huasuan.myzhbj.controller.menu.MenuController;
import com.huasuan.myzhbj.controller.menu.NewsMenuController;
import com.huasuan.myzhbj.controller.menu.PicsMenuController;
import com.huasuan.myzhbj.controller.menu.SubjectMenuController;
import com.huasuan.myzhbj.framelaout.LeftMenuFragment;
import com.huasuan.myzhbj.utils.SPUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * @描述 1.提供视图
 * @描述 2.接收数据/加载数据
 * @描述 3.数据和视图的绑定
 */
public class NewsCenterTabController extends TabController {
    private static final String TAG = "NewsCenterTabController";
    private FrameLayout mNewsCenterContentContainer;
    private List<MenuController> mMenuControllers;
    private TextView mTv;
    private boolean hasError;
    private SPUtils mSPUtils = new SPUtils(mContext);
    private String mResult = mSPUtils.getString("mResult",null);
    public NewsCenterTabController(Context context) {
        super(context);
    }

    @Override
    public void initStateBar() {
        mTvTitle.setText("新闻中心");

    }

    /**
     * 初始化controller里面持有的视图
     *
     * @param context
     * @return
     */
    public View initContentView(Context context) {
        // 可能放很多很多的data+view
        // 现在不知道具体应该放啥
        // 所以这里给一个容器
        mNewsCenterContentContainer = new FrameLayout(mContext);
        return mNewsCenterContentContainer;

    }

    /**
     * 加载数据,然后数据和视图进行绑定
     */
    public void initData() {

        if (!TextUtils.isEmpty(mResult)){
            processData(mResult);
        }
        getUrl();
    }

    protected void processData(String jsonString) {
        //这里之前跟你说过了。gson解析比较耗时， 放到线程里面去执行。不要放ui线程。
        // 2.解析网络的数据-->解析
        Gson gson = new Gson();
        NewsCenterBean newsCenterBean = gson.fromJson(jsonString, NewsCenterBean.class);
        // 3.根据数据,刷新ui-->展示
        // LeftMemuFragment-->传递数据给LeftMemuFragment,然后进行ui展示
        LeftMenuFragment leftMenuFragment = ((MainActivity) mContext).getLeftMenuFragment();
        // 取出数据
        List<NewsCenterBean.NewsCenterMenuBean> newsCenterMenuBeans = newsCenterBean.data;
        // 传递数据给leftMenuFragment
        leftMenuFragment.setData(newsCenterMenuBeans);

        // 把mNewsCenterContentContainer里面应有的View全部加进来

		/*--------------- 所有的MenuController通过集合进行管理 ---------------*/
        mMenuControllers = new ArrayList<MenuController>();
        // 根据type匹配进行初始化
        for (NewsCenterBean.NewsCenterMenuBean newsCenterMenuBean : newsCenterMenuBeans) {
            MenuController menuController = null;
            int type = newsCenterMenuBean.type;
            switch (type) {
                case 1:// 新闻
                    //把一些已知的数据通过构造方法传入进去
                    menuController = new NewsMenuController(mContext, newsCenterMenuBean);
                    break;
                case 10:// 专题
                    menuController = new SubjectMenuController(mContext);
                    break;
                case 2:// 组图
                    menuController = new PicsMenuController(mContext);
                    break;
                case 3:// 互动
                    menuController = new InteractMenuController(mContext);
                    break;

                default:
                    break;
            }
            // 加入集合中
            mMenuControllers.add(menuController);
            // 加入容器中
            mNewsCenterContentContainer.addView(menuController.mRootView);
        }

        // 初始化显示LeftMenuFragment中的第一个条目对应的视图信息
        switchContent(0);
    }

    /**
     * 切换到MenuController集合中的第几个视图
     *
     * @param position
     */
    @Override
    public void switchContent(int position) {
        //移除容器里面的所有内容
        mNewsCenterContentContainer.removeAllViews();

        MenuController menuController = mMenuControllers.get(position);
        View rootView = menuController.mRootView;
        mNewsCenterContentContainer.addView(rootView);
        //让MenuController加载对应的数据，然后刷新UI
        menuController.initData();
        //更新标题
        //如果此时的MenuController是我们的组图那就显示对应的切换按钮

        if (menuController instanceof PicsMenuController){

            mIbSwitch.setVisibility(View.VISIBLE);
            //传递切换按钮switchbutton到PicsMenuController里面去
            ((PicsMenuController)menuController).setSwitchButton(mIbSwitch);
        }else {
            mIbSwitch.setVisibility(View.GONE);
        }

        super.switchContent(position);
    }

    public void getUrl() {

        String url = Constants.URL.NEWSCENTERURL;
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
               // Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                String resJsonString = result;
                mSPUtils.putString("mResult",resJsonString);
                processData(resJsonString);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), "网络连接失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                // Toast.makeText(x.app(), "关闭", Toast.LENGTH_LONG).show();
            }
        });
    }
}
