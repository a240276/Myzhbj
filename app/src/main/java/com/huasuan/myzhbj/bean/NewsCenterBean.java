package com.huasuan.myzhbj.bean;

import java.util.List;


public class NewsCenterBean {
	public int						retcode;

	public List<NewsCenterMenuBean>	data;		//
	public List<Integer>			extend;

	public class NewsCenterMenuBean {
		public int							id;		// 10002
		public int							type;		// 10
		public String						url1;		// /10007/list1_1.json
		public String						dayurl;	//
		public String						excurl;	//
		public String						title;		// 互动
		public String						weekurl;	//
		public String						url;		// /10008/list_1.json
		public List<NewsCenterChildBean>	children;	// Array
	}

	public class NewsCenterChildBean {
		public int		id;	// 10010
		public String	title;	// 体育
		public int		type;	// 1
		public String	url;	// /10010/list_1.json
	}
}
