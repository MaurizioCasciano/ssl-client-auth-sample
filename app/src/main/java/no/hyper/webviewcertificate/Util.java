package no.hyper.webviewcertificate;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

public class Util {
	public static final String KEY_CODE = "code";
	public static final String KEY_COUNTRY = "country";
	public static final String KEY_LOCALE = "locale";
	private static JSONArray countryCode;

	public static Drawable loadImageFromWebOperations(URL url) {
	    try {
	        InputStream is = (InputStream) url.getContent();
	        Drawable d = Drawable.createFromStream(is, "src name");
	        return d;
	    } catch (Exception e) {
	        Util.printException(e);
	    }
	    return null;
	}
	
	public static Animation inFromUpAnimation(int duration) {
        Animation inFromRight = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        		Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromRight.setDuration(duration);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public static Animation outToDownAnimation(int duration) {
        Animation outtoLeft = new TranslateAnimation(
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        		Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f
        );      
        outtoLeft.setDuration(duration);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
    
	public static byte[] readAll(InputStream is) {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] byteChunk = new byte[8192]; 
			int n;
			while ((n = bis.read(byteChunk)) > 0) {
				bais.write(byteChunk, 0, n);
			}
		} catch (Exception e) {
			Util.printException(e);
		}
		
		return bais.toByteArray();
	}

	public static final Date iso8601StringTodate(String str) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ", Locale.US);
		try {
			return df.parse(str);
		} catch (ParseException e) {
			Log.e(Util.class.getSimpleName(), "Exception" + Util.printException(e));
		}
		return null;
	}
	
	public static final String dateToiso8601String(Date d) {
		TimeZone tz = TimeZone.getDefault();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.US);
		df.setTimeZone(tz);
		return df.format(d);
	}

	
	public static byte[] hexStringToByte(String s) {
		int len = s.length();
		
		if (len % 2 != 0) 
			throw new IllegalArgumentException("Illegal Hex String!");
		
		byte[] data = new byte[len / 2];
		int t1, t2;
		for (int i = 0; i < len; i += 2) {
			t1 = Character.digit(s.charAt(i), 16);
			t2 = Character.digit(s.charAt(i + 1), 16);
			
			if (t1 == -1 || t2 == -1)
				throw new IllegalArgumentException("Hex String are expected from [0-9] or [a-f]");
			
			data[i / 2] = (byte) (( t1 << 4) + t2);
		}
		return data;
	}

	public static String bytesToString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b & 0xFF));
		}
		return sb.toString();
	}

	
	public static String printException(Throwable th) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		th.printStackTrace(pw);
		return sw.toString();
	}
	
	public static void clearFolder(File dir) {
		if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	        	File toRemove = new File(dir, children[i]);
	            if (!toRemove.delete())
	            	Log.w(Util.class.getSimpleName(), "RM " + toRemove  + " failure!");
	        }
	    }
	}
	
	
	/**
	 * Utility function to hide the soft keyboard.
	 * @param activity
	 */
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);
	}
}
