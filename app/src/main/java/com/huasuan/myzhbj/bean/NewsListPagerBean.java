package com.huasuan.myzhbj.bean;

import android.widget.ImageView;

import java.util.List;

/**
 * Created by john on 2017/3/6.
 */

public class NewsListPagerBean {

    public NewsListPagerDataBean	data;
    public int						retcode;

    public class NewsListPagerDataBean {
        public String							countcommenturl;	// http://zhbj.qianlong.com/client/content/countComment/
        public String							more;				// /10007/list_2.json
        public String							title;				// 北京
        public List<NewsListPagerNewsBean>		news;				// Array
        public List<NewsListPagerTopicBean>		topic;				// Array
        public List<NewsListPagerTopNewsBean>	topnews;			// Array

        public class NewsListPagerNewsBean {
            public String	comment;		// true
            public String	commentlist;	// http://10.0.3.2:8080/zhbj/10007/comment_1.json
            public String	commenturl;	// http://zhbj.qianlong.com/client/user/newComment/35319
            public int		id;			// 35311
            public String	listimage;		// http://10.0.3.2:8080/zhbj/10007/2078369924F9UO.jpg
            public String	pubdate;		// 2014-10-1113:18
            public String	title;			// 网上大讲堂第368期预告：义务环保人人有责
            public String	type;			// news
            public String	url;			// http://10.0.3.2:8080/zhbj/10007/724D6A55496A11726628.html
        }

        public class NewsListPagerTopicBean {
            public String	description;	// 11111111
            public int		id;			// 10101
            public String	listimage;		// http://10.0.3.2:8080/zhbj/10007/1452327318UU91.jpg
            public int		sort;			// 1
            public String	title;			// 北京
            public String	url;			// http://10.0.3.2:8080/zhbj/10007/list_1.json
        }

        public class NewsListPagerTopNewsBean {
            public String	comment;		// true
            public String	commentlist;	// http://10.0.3.2:8080/zhbj/10007/comment_1.json
            public String	commenturl;	// http://zhbj.qianlong.com/client/user/newComment/35301
            public String	id;			// 35301
            public String	pubdate;		// 2014-04-0814:24
            public String	title;			// 蜗居生活
            public String topimage;		// http://10.0.3.2:8080/zhbj/10007/1452327318UU91.jpg
            public String	type;			// news
            public String	url;			// http://10.0.3.2:8080/zhbj/10007/724D6A55496A11726628.html
        }
    }

}
