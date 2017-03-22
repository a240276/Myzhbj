package com.huasuan.myzhbj.framelaout;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huasuan.myzhbj.MainActivity;
import com.huasuan.myzhbj.R;
import com.huasuan.myzhbj.base.BaseFragment;
import com.huasuan.myzhbj.bean.NewsCenterBean;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by john on 2017/3/2.
 */

public class LeftMenuFragment extends BaseFragment implements AdapterView.OnItemClickListener {
//    @ViewInject(R.id.menu_left_recyclerview)
//    RecyclerView mRecyclerView;

    @ViewInject(R.id.menu_left_listview)
    ListView mListView;
    private int mCurSelectIndex = 0;

    private List<NewsCenterBean.NewsCenterMenuBean> mNewsCenterMenuBeans;
    ;

    private MenuAdapter mAdapter;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_menu_left, null);
        x.view().inject(this, view);
        // listView相关常用的设置
        mListView.setDividerHeight(0);
        int paddingTop =
                (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, mContext.getResources()
                        .getDisplayMetrics()) + .5f);
        mListView.setPadding(0, paddingTop, 0, 0);
        // 取消selector
        mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        // 针对2.3的手机做一些兼容性的设置
        mListView.setCacheColorHint(Color.TRANSPARENT);

        mListView.setFadingEdgeLength(0);

        return view;
    }



    /**
     * @param newsCenterMenuBeans
     * @des 接收新闻中心加载回来的数据
     * @des 新闻中心数据加载成功之后
     */
    public void setData(List<NewsCenterBean.NewsCenterMenuBean> newsCenterMenuBeans) {
        // 保存数据到成员变量
        mNewsCenterMenuBeans = newsCenterMenuBeans;

        // 刷新listview-->设置adapter
        mAdapter = new MenuAdapter();
        mListView.setAdapter(mAdapter);

        // 设置listview的点击事件
        mListView.setOnItemClickListener(this);
    }

    class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mNewsCenterMenuBeans != null) {
                return mNewsCenterMenuBeans.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mNewsCenterMenuBeans != null) {
                return mNewsCenterMenuBeans.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*--------------- 决定根布局 ---------------*/
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_menu_left, null);
                holder.tvMenu = (TextView) convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // data
            NewsCenterBean.NewsCenterMenuBean newsCenterMenuBean = mNewsCenterMenuBeans.get(position);

            // 数据的展示
            holder.tvMenu.setText(newsCenterMenuBean.title);

            // 做颜色修改,根据当前选中的索引修改颜色
            if (mCurSelectIndex == position) {
                holder.tvMenu.setEnabled(true);
            } else {
                holder.tvMenu.setEnabled(false);
            }
            return convertView;
        }

        class ViewHolder {
            TextView tvMenu;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCurSelectIndex = position;
        // 刷新listview
        mAdapter.notifyDataSetChanged();

        // 切换-->显示哪个TabController下的第一个具体的MenuController
        // 拿到contentFragment
        ContentFragment contentFragment = ((MainActivity) mContext).getContentFragment();

        contentFragment.switchContent(position);

        //进行slidingMenu的打开和关闭
        ((MainActivity)mContext).getSlidingMenu().toggle();
    }


}
