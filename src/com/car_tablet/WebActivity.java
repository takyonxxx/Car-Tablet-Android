package com.car_tablet;
import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.EditText;

public class WebActivity extends Activity {
	private WebView webView;
	private EditText urltext;
	private int current_scale_level;
	private Point Scroll;
	private Button btn_gourl,btn_goback,btn_goforward,btn_youtube,btn_facebook,btn_meteo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.webview);		
		this.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		webView = (WebView) findViewById(R.id.webview);
		webView.setBackgroundColor(Color.BLACK);	
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		btn_gourl=(Button) findViewById(R.id.run);
		btn_gourl.getBackground().setColorFilter(new LightingColorFilter(Color.YELLOW, Color.YELLOW));
		btn_goback=(Button) findViewById(R.id.back);
		btn_goforward=(Button) findViewById(R.id.forward);
		btn_youtube=(Button) findViewById(R.id.youtube);
		btn_facebook=(Button) findViewById(R.id.facebook);
		btn_meteo=(Button) findViewById(R.id.meteo);
		urltext= (EditText) findViewById(R.id.url);
		urltext.setText("http://www.google.com");		
		
		  WebSettings webSettings = webView.getSettings();
		  webSettings.setJavaScriptEnabled (true);
		  webSettings.setSupportZoom (true);
		  webSettings.setBuiltInZoomControls(true);         
          webView.setWebChromeClient(new WebChromeClient());
          if (Build.VERSION.SDK_INT < 8) {
        	  webSettings.setPluginsEnabled(true);
          } else {
        	  webSettings.setPluginState(PluginState.ON);
          }        
          String url = urltext.getText().toString();
          webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
          webView.loadUrl(url); 
        
          Scroll=new Point(0,0);
          current_scale_level=(int)webView.getScale()*100;
          Scroll.x=webView.getScrollX();
          Scroll.y=webView.getScrollY();

		btn_gourl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//webView.loadDataWithBaseURL("", html , "text/html",  "UTF-8", "");   
            	
            	String url = urltext.getText().toString();
            	webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
            	webView.setInitialScale(current_scale_level);
                webView.loadUrl(url);              
                webView.scrollTo(Scroll.x,Scroll.y);
            }
        });		
		btn_goback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	 webView.goBack();
            }
        });	
		btn_goforward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	 webView.goForward();
            }
        });		
		btn_youtube.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String url ="http://www.youtube.com";
            	webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
                webView.loadUrl(url);
            }
        });	
		btn_facebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String url ="http://www.facebook.com";
            	webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
                webView.loadUrl(url);
            }
        });	
		btn_meteo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String url ="http://www.meteoblue.com/tr_TR/hava/tahmin/hafta/ayas_tr_7143";
            	webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
                webView.loadUrl(url);
            }
        });	
		
	}	
	
	@Override
	public void onPause() {
	    super.onPause();
	    try {
	        Class.forName("android.webkit.WebView")
	                .getMethod("onPause", (Class[]) null)
	                            .invoke(webView, (Object[]) null);

	    } catch(ClassNotFoundException cnfe) {	        
	    } catch(NoSuchMethodException nsme) {	       
	    } catch(InvocationTargetException ite) {	       
	    } catch (IllegalAccessException iae) {	       
	    }
	}
	private class Callback extends WebViewClient{  //HERE IS THE MAIN CHANGE. 

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
        	   return false;	                
        }	        

    }
	
}