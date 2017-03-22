package com.huasuan.myzhbj.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huasuan.myzhbj.R;


/**
 * @创建者	 伍碧林
 * @创时间 	 2015-12-1 下午4:38:58
 * @描述	     TODO
 *
 * @版本       $Rev: 56 $
 * @更新者     $Author: admin $
 * @更新时间    $Date: 2015-12-03 16:55:50 +0800 (星期四, 03 十二月 2015) $
 * @更新描述    TODO
 */
public class RefreshListView extends ListView implements OnScrollListener, OnClickListener {

	public static final String	TAG						= RefreshListView.class.getSimpleName();

	private ImageView			mHeaderViewArrow;
	private FrameLayout			mHeaderViewCustomContainer;
	private ProgressBar			mHeaderViewPb;
	private TextView			mHeaderViewState;
	private TextView			mHeaderViewUpdateTime;
	private View				mHeaderView;
	private RelativeLayout		mRefreshHeaderView;
	private int					mInitRefreshHeaderViewPaddingTop;									// 下拉刷新头布局初始的paddingTop
	private float				mDownX;
	private float				mDownY;

	// 下拉刷新
	public static final int		STATE_PULL_REFRESH		= 0;
	// 松开刷新
	public static final int		STATE_RELEASE_REFRESH	= 1;
	// 正在刷新
	public static final int		STATE_REFRESHING		= 2;
	// 记录当前的状态
	public int					mCurState				= STATE_PULL_REFRESH;

	private RotateAnimation		mUp2Down;

	private RotateAnimation		mDown2Up;

	private int					mRefreshHeaderViewHeight;

	private boolean				isStartScroll			= true;

	private OnItemClickListener	mListener;

	public RefreshListView(Context context) {
		this(context, null);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView(context);
		initFooterView(context);
		// 初始化动画
		initAnimation();

	}

	/**初始化尾布局*/
	private void initFooterView(Context context) {
		mFooterView = View.inflate(context, R.layout.inflate_refreshlistview_footerview, null);
		// 找孩子

		mFooterViewPb = (ProgressBar) mFooterView.findViewById(R.id.refresh_footerview_pb);
		mFooterViewState = (TextView) mFooterView.findViewById(R.id.refresh_footerview_state);
		// 加到listview中
		addFooterView(mFooterView);
		// 监听listview的滚动事件
		this.setOnScrollListener(this);
		// 设置点击重试
		mFooterView.setOnClickListener(this);
	}

	/**初始化头布局*/
	public void initHeaderView(Context context) {
		mHeaderView = View.inflate(context, R.layout.inflate_refreshlistview_headerview, null);
		// 找出下拉刷新头布局里面所有的孩子
		mHeaderViewArrow = (ImageView) mHeaderView.findViewById(R.id.refresh_headerview_arrow);
		mHeaderViewPb = (ProgressBar) mHeaderView.findViewById(R.id.refresh_headerview_pb);
		mHeaderViewState = (TextView) mHeaderView.findViewById(R.id.refresh_headerview_state);
		mHeaderViewUpdateTime = (TextView) mHeaderView.findViewById(R.id.refresh_headerview_updatetime);
		mHeaderViewCustomContainer = (FrameLayout) mHeaderView.findViewById(R.id.refresh_headerview_customheaderview);

		mRefreshHeaderView = (RelativeLayout) mHeaderView.findViewById(R.id.refresh_headerView);
		/**
		 UNSPECIFIED    wrap_content
		 EXACTLY        match_parent 100dp 100px
		 AT_MOST        忽略
		 */
		/*
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);//由系统决定到底宽度是多少
		int heightMeasureSpec= MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);//由系统决定到底高度是多少
		mRefreshHeaderView.measure(widthMeasureSpec, heightMeasureSpec);
		*/

		// 得到下拉刷新头布局应有的高度
		mRefreshHeaderView.measure(0, 0);// 测量一下
		mRefreshHeaderViewHeight = mRefreshHeaderView.getMeasuredHeight();

		mInitRefreshHeaderViewPaddingTop = -mRefreshHeaderViewHeight;

		// 通过设置paddingTop隐藏下拉刷新头布局
		mHeaderView.setPadding(0, mInitRefreshHeaderViewPaddingTop, 0, 0);
		addHeaderView(mHeaderView);
	}

	/**
	 * 初始化动画
	 */
	private void initAnimation() {
		mUp2Down =
				new RotateAnimation(180, 360, RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF,
						.5f);
		mUp2Down.setDuration(400);
		mUp2Down.setFillAfter(true);// 动画停止之后就固定动画效果

		mDown2Up =
				new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF,
						.5f);
		mDown2Up.setDuration(400);
		mDown2Up.setFillAfter(true);// 动画停止之后就固定动画效果
	}

	/**
	 * 添加自定义的头
	 */
	public void addCustomHeader(View customHeaderView) {
		mHeaderViewCustomContainer.addView(customHeaderView);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "MotionEvent.ACTION_DOWN");

			mDownX = ev.getRawX();
			mDownY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "MotionEvent.ACTION_MOVE");
			if (mDownX == 0 && mDownY == 0) {// 避免事件没有进入,按住ViewPager拖动不会进入
				mDownX = ev.getRawX();
				mDownY = ev.getRawY();
			}

			/*--------------- 1. 正在刷新,Listview父容器处理事件,ListView实现应有效果 ---------------*/
			if (mCurState == STATE_REFRESHING) {
				Log.i(TAG, "正在刷新,不能继续拖动 ");
				return super.onTouchEvent(ev);
			}

			float moveX = ev.getRawX();
			float moveY = ev.getRawY();

			int diffX = (int) (moveX - mDownX + .5f);
			int diffY = (int) (moveY - mDownY + .5f);// 拖动的距离

			/**
			 	下拉刷新布局左下角.Y>=ListView左上角.Y并且是 下拉操作 
			 */

			// 下拉刷新布局左上角
			int[] refreshHeaderViewLocation = new int[2];
			mRefreshHeaderView.getLocationInWindow(refreshHeaderViewLocation);
			// 左上角
			int refreshHeaderViewTopY = refreshHeaderViewLocation[1];
			int refreshHeaderViewBottomY = refreshHeaderViewTopY + mRefreshHeaderViewHeight;

			int[] listViewLocation = new int[2];
			this.getLocationInWindow(listViewLocation);
			int listViewY = listViewLocation[1];

			if (refreshHeaderViewBottomY >= listViewY && diffY > 0) {// 修改paddingTop处理滑动效果

				if (isStartScroll) {
					// 进入这里面来的时候,才开始计算我们的diffY,就不会出现一个滚动跳跃问题
					// 从先赋值
					mDownY = moveY;
					diffY = (int) (moveY - mDownY + .5f);// 拖动的距离

					isStartScroll = false;
				}

				int paddingTop = mInitRefreshHeaderViewPaddingTop + diffY;

				// 根据paddingTop的正负值切换状态(下拉刷新,松开刷新)
				if (paddingTop > 0 && mCurState != STATE_RELEASE_REFRESH) {
					mCurState = STATE_RELEASE_REFRESH;// 松开刷新
					Log.i(TAG, "松开刷新");
					// 刷新头布局ui
					refreshHeaderViewUI();
				} else if (paddingTop <= 0 && mCurState != STATE_PULL_REFRESH) {
					mCurState = STATE_PULL_REFRESH;// 下拉刷新
					Log.i(TAG, "下拉刷新");
					// 刷新头布局ui
					refreshHeaderViewUI();
				}

				// 公式
				// paddingTop = mInitRefreshHeaderViewPaddingTop+diffY
				// 控制下拉刷新头布局实时显示隐藏
				mHeaderView.setPadding(0, paddingTop, 0, 0);

				// break;
				// add
				super.setOnItemClickListener(null);// 置空事件
				return true;
			} else {// listview处理事件,产生滑动效果
				return super.onTouchEvent(ev);
			}

		case MotionEvent.ACTION_UP:
			Log.i(TAG, "MotionEvent.ACTION_UP");
			// 清空mDonwX mDownY
			mDownX = 0;
			mDownY = 0;
			isStartScroll = true;

			if (mCurState == STATE_PULL_REFRESH) {// 现在是下拉刷新-->下拉刷新
				mCurState = STATE_PULL_REFRESH;

				// 刷新头布局的ui
				refreshHeaderViewUI();

				// 修改下拉刷新头布局的显示隐藏
				// TODO
				// mHeaderView.setPadding(0, mInitRefreshHeaderViewPaddingTop, 0, 0);

				changePaddingTopAnimation(mHeaderView.getPaddingTop(), mInitRefreshHeaderViewPaddingTop);

			} else if (mCurState == STATE_RELEASE_REFRESH) {// 现在是下拉刷新-->正在刷新
				mCurState = STATE_REFRESHING;

				// 刷新头布局的ui
				refreshHeaderViewUI();

				// 修改下拉刷新头布局的显示隐藏
				// TODO
				// mHeaderView.setPadding(0, 0, 0, 0);

				changePaddingTopAnimation(mHeaderView.getPaddingTop(), 0);

				// 通过接口对象进行传值操作
				if (mOnRefreshListener != null) {
					// 3.在需要传值的地方,使用接口对象,调用接口方法
					mOnRefreshListener.onRefresh(this);
				}
			}

			break;

		default:
			break;
		}
		// 还原事件
		super.setOnItemClickListener(mListener);
		return super.onTouchEvent(ev);// 这个里面产生的listView的滚动效果
	}

	/**
	 * 刷新头布局的ui(3种状态)
	 */
	private void refreshHeaderViewUI() {
		switch (mCurState) {
		case STATE_PULL_REFRESH:// 下拉刷新
			// 箭头
			mHeaderViewArrow.setVisibility(View.VISIBLE);

			// 箭头 由 上 到 下
			mHeaderViewArrow.startAnimation(mUp2Down);

			// 加载进度条
			mHeaderViewPb.setVisibility(View.INVISIBLE);
			// 下拉的状态
			mHeaderViewState.setText("下拉刷新");
			// 刷新时间

			break;
		case STATE_RELEASE_REFRESH:// 松开刷新
			// 箭头
			mHeaderViewArrow.setVisibility(View.VISIBLE);

			// 箭头 由 下 到 上
			mHeaderViewArrow.startAnimation(mDown2Up);

			// 加载进度条
			mHeaderViewPb.setVisibility(View.INVISIBLE);
			// 下拉的状态
			mHeaderViewState.setText("松开刷新");

			// 刷新时间
			break;
		case STATE_REFRESHING:// 正在刷新
			// 清除箭头之前的动画
			mHeaderViewArrow.clearAnimation();

			// 箭头
			mHeaderViewArrow.setVisibility(View.INVISIBLE);
			// 加载进度条
			mHeaderViewPb.setVisibility(View.VISIBLE);
			// 下拉的状态
			mHeaderViewState.setText("正在刷新");

			// 刷新时间
			mHeaderViewUpdateTime.setText(getUpdateTimeStr());

			break;

		default:
			break;
		}
	}

	public String getUpdateTimeStr() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * 通过动画的方式修改mHeaderView的paddingTop,不能直接使用ObjectAnimator,因为没有对应的PaddingTop的属性
	 * @param start 动画的开始值
	 * @param end  动画的结束值
	 */
	public void changePaddingTopAnimation(int start, int end) {
		// 通过ValueAnimator生成渐变值
		ValueAnimator animator = ValueAnimator.ofInt(start, end);
		animator.setDuration(400);
		animator.start();
		// 通过监听,拿到渐变值
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// 拿到动画执行过程中的渐变值
				int paddintTop = (Integer) valueAnimator.getAnimatedValue();
				mHeaderView.setPadding(0, paddintTop, 0, 0);
			}
		});

	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
		super.setOnItemClickListener(listener);
	}

	/**1.开始下拉刷新的效果*/
	public void startRefresh() {
		mCurState = STATE_REFRESHING;

		refreshHeaderViewUI();
		// 修改paddingTop
		changePaddingTopAnimation(mInitRefreshHeaderViewPaddingTop, 0);
	}

	/**2.结束下拉刷新的效果*/
	public void stopRefresh() {
		mCurState = STATE_PULL_REFRESH;
		refreshHeaderViewUI();
		changePaddingTopAnimation(0, mInitRefreshHeaderViewPaddingTop);
	}

	/*--------------- 3.监听下拉刷新---------------*/
	/**
	 接口回调
	 	1.定义接口以及接口方法
	 	2.定义接口对象
	 	3.在需要传值的地方,使用接口对象,调用接口方法
	 	4.暴露公共方法
	 */
	// 1.定义接口以及接口方法
	public interface OnRefreshListener {
		void onRefresh(RefreshListView refreshListView);

		void onLoadMore(RefreshListView refreshListView);
	}

	// 2.定义接口对象
	OnRefreshListener	mOnRefreshListener;

	private View		mFooterView;

	private ProgressBar	mFooterViewPb;

	private TextView	mFooterViewState;

	private boolean		isLoadMoreSuccess;

	private boolean		isLoadingMore;

	// 4.暴露公共方法
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (isLoadingMore) {
			return;
		}

		// 滑到底的时候
		if (getLastVisiblePosition() == getAdapter().getCount() - 1) {
			if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {

				// 可以通知,现在已经滑到底部了
				if (mOnRefreshListener != null) {
					mOnRefreshListener.onLoadMore(this);
					isLoadingMore = true;
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO

	}

	/**
	 * 结束上滑加载更多
	 * @param isLoadMoreSuccess
	 */
	public void stopLoadMore(boolean isLoadMoreSuccess) {
		this.isLoadMoreSuccess = isLoadMoreSuccess;
		isLoadingMore = false;
		if (isLoadMoreSuccess) {
			// 进度条
			mFooterViewPb.setVisibility(View.VISIBLE);
			// 加载更多的状态
			mFooterViewState.setText("正在加载更多");
		} else {// 失败
			// 进度条
			mFooterViewPb.setVisibility(View.INVISIBLE);
			// 加载更多的状态
			mFooterViewState.setText("点击重试");
		}
	}

	/**
	 * 设置是否有加载更多
	 * @param
	 */
	public void setHasLoadMore(boolean isLoadMore) {
		if (isLoadMore) {
			// 进度条
			mFooterViewPb.setVisibility(View.VISIBLE);
			// 加载更多的状态
			mFooterViewState.setText("正在加载更多");
		} else {
			// 进度条
			mFooterViewPb.setVisibility(View.INVISIBLE);
			// 加载更多的状态
			mFooterViewState.setText("没有加载更多");
		}

	}

	@Override
	public void onClick(View v) {
		if (!isLoadMoreSuccess) {
			// 重试
			if (mOnRefreshListener != null) {
				mOnRefreshListener.onLoadMore(this);
				// 修改ui
				// 进度条
				mFooterViewPb.setVisibility(View.VISIBLE);
				// 加载更多的状态
				mFooterViewState.setText("正在加载更多");
			}
		}
	}
}
