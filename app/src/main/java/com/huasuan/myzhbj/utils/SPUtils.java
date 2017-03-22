package com.huasuan.myzhbj.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @描述	     对sp操作的封装
 *
 */
public class SPUtils {
	private SharedPreferences	mSp;
	private Editor				mEditor;

	public SPUtils(Context context) {
		mSp = context.getSharedPreferences("zhbj", Context.MODE_PRIVATE);
		mEditor = mSp.edit();
	}

	/**取出string*/
	public String getString(String key, String defValue) {
		return mSp.getString(key, defValue);
	}

	/**取出int*/
	public int getInt(String key, int defValue) {
		return mSp.getInt(key, defValue);
	}

	/**取出boolean*/
	public boolean getBoolean(String key, boolean defValue) {
		return mSp.getBoolean(key, defValue);
	}

	/**存入string*/
	public void putString(String key, String value) {
		mEditor.putString(key, value);
		mEditor.commit();
	}

	/**存入int*/
	public void putInt(String key, int value) {
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	/**存入boolean*/
	public void putBoolean(String key, boolean value) {
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}

}
