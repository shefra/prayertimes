package com.shefra.prayertimes.helper;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

// prevent Android bug that leads to memory leaks. 
// this workaround done by @HTH and @Brian. more info in :
// http://code.google.com/p/android/issues/detail?id=9904
public class Typefaces {
	private static final String TAG = "tomaanina";

	private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

	public static Typeface get(Context c, String assetPath) {
		synchronized (cache) {
			if (!cache.containsKey(assetPath)) {
				try {
					Typeface t = Typeface.createFromAsset(c.getAssets(),
							assetPath);
					cache.put(assetPath, t);
				} catch (Exception e) {
					//Log.e(TAG, "Could not get typeface '" + assetPath
					//		+ "' because " + e.getMessage());
					return null;
				}
			}
			return cache.get(assetPath);
		}
	}
}