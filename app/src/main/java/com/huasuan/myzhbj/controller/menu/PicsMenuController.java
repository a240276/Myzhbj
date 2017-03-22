package com.huasuan.myzhbj.controller.menu;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huasuan.myzhbj.MainActivity;
import com.huasuan.myzhbj.R;
import com.huasuan.myzhbj.bean.NewsListPagerBean;
import com.huasuan.myzhbj.conf.Constants;
import com.huasuan.myzhbj.utils.SPUtils;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class PicsMenuController extends MenuController {
    @ViewInject(R.id.newscenter_pics_gridview)
    GridView mGridView;

    @ViewInject(R.id.newscenter_pics_listview)
    ListView mListView;

    private List<NewsListPagerBean.NewsListPagerDataBean.NewsListPagerNewsBean> mNews;

    private static final String TAG = "PicsMenuController";
    private boolean isListView = true;
   // ImageLoader mImageLoader;

    public PicsMenuController(Context context) {
        super(context);

    }

    @Override
    public View initView(Context context) {
        View view = View.inflate(mContext, R.layout.newscenter_pics, null);
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        final SPUtils mSputils = new SPUtils(mContext);
        final String url = Constants.URL.BASEURL + "/photos/photos_1.json";
        String mSP = mSputils.getString(url, null);
        if (mSP!=null){
            Log.i(TAG, "缓存加载数据");
            processData(mSP);
        }
        //发起网络请求
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "网络数据加载完成" + url);
                //Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                String resJsonString = result;
                // 缓存协议内容到sp中(持久化)
                mSputils.putString(url,resJsonString);
                processData(resJsonString);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), "网络连接失败", Toast.LENGTH_LONG).show();
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
    }

    protected void processData(String jsonString) {
        Gson gson = new Gson();
        NewsListPagerBean newsListPagerBean = gson.fromJson(jsonString, NewsListPagerBean.class);
        mNews = newsListPagerBean.data.news;
        ((MainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(100);
//                mListView.setAdapter(new PicsAdapter());
//                mGridView.setAdapter(new PicsAdapter());
            }
        });
        // 设置adapter
        mListView.setAdapter(new PicsAdapter());
        mGridView.setAdapter(new PicsAdapter());
    }

    class PicsAdapter extends BaseAdapter {

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
            // view
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_pics, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_pics_icon);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.item_pics_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // data
            NewsListPagerBean.NewsListPagerDataBean.NewsListPagerNewsBean newsListPagerNewsBean = mNews.get(position);
            x.image().bind(holder.ivIcon, newsListPagerNewsBean.listimage);
            // data+view
            holder.tvTitle.setText(newsListPagerNewsBean.title);
            return convertView;
        }

        class ViewHolder {
            ImageView ivIcon;
            TextView tvTitle;
        }

    }

    /**
     * 接收NewscenterController中传递过来的ImageButton
     *
     * @param ibSwitch
     */
    public void setSwitchButton(final ImageButton ibSwitch) {
        ibSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isListView) {// 假如现在展示的是listview
                    // 切换为gridView
                    mListView.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);
                    ibSwitch.setImageResource(R.drawable.icon_pic_list_type);
                } else {
                    // 切换为ListView
                    mListView.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                    ibSwitch.setImageResource(R.drawable.icon_pic_grid_type);
                }
                isListView = !isListView;
            }
        });

    }
}
