package com.pulego.mysafety.ui;

import com.pulego.mysafety.R;
import com.pulego.mysafety.custom.CustomFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * The Class Search is the Fragment class that is launched when the user clicks
 * on Search option in Left navigation drawer and it simply shows a few dummy
 * options for Search property with options for Searching property for Buy and
 * Rent. You can customize this to display actual contents.
 */
public class About extends CustomFragment {

	
	private View v;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.surveywebview, null);

		WebView myWebView = (WebView) v.findViewById(R.id.webview);
		myWebView.getSettings().setJavaScriptEnabled(true);
		
		myWebView.loadUrl("file:///android_asset/about.html");
		
		WebClientClass webViewClient = new WebClientClass();
		myWebView.setWebViewClient(webViewClient);
		WebChromeClient webChromeClient = new WebChromeClient();
		myWebView.setWebChromeClient(webChromeClient);

		return v;
	}
	
	public class WebClientClass extends WebViewClient {
		ProgressDialog pd = null;

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			pd = new ProgressDialog(v.getContext());
			pd.setTitle("Please wait");
			pd.setMessage("Page is loading..");
			pd.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			pd.dismiss();
		}
	}

	public class WebChromeClass extends WebChromeClient {
	}

}
