package and7.lektion2.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class OsmWebView extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osm_webview);
		Intent intent = this.getIntent();
		String filePath = intent.getExtras()
		.getString("osm_access");
		WebView webView = (WebView) findViewById(
		R.id.osm_webview);
		// JavaScript aktivieren:
		webView.getSettings().setJavaScriptEnabled(true);
		// Seite in WebView laden :
		//webView.loadUrl("file://" + filePath);
		String html = null;
		try {
		// Webseite als String einlesen
		BufferedReader reader = new BufferedReader(
		new InputStreamReader(new FileInputStream(new File(filePath))));
		StringBuffer sb = new StringBuffer();
		String line;
		while((line = reader.readLine()) != null)
		{
		sb.append(line);
		}
		html = sb.toString();
		reader.close();
		} catch (Exception e) {
		Log.e("TAG", e.toString());
		}
		if(html != null) {
		webView.loadDataWithBaseURL(
		"file:///android_res/drawable/", html, "text/html",
		"UTF-8", null);
		}
		
	}
}