package mad3.muxie.view;

import java.util.Locale;

import mad3.muxie.app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class WebViewActivity extends Activity {

	private WebView webView = null;
	public static final String _LINK = "mad3.mobile.item._LINK";
	public static final String _PAGE = "mad3.mobile.page._LINK";
	private String link = null;
	private String page = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		
		load();
	}
	
	private void load() {
		webView = (WebView) findViewById(R.id.web_view_general);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.getSettings().setDomStorageEnabled(true);
		
		link = getIntent().getStringExtra(_LINK);
		page = getIntent().getStringExtra(_PAGE);
		
		if (link != null) {
			webView.loadUrl(link);
		} else if (page != null) {
			webView.loadUrl(toInternalUrl());
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.warning);
			dialog.setMessage(R.string.err_page_not_found);
			dialog.setNeutralButton(R.string.btn_back, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			dialog.show();
		}
	}
	
	private String toInternalUrl() {
		Locale locale = Locale.getDefault();
		if ("pt".equals(locale.getLanguage())) {
			return "file:///android_asset/pt_BR/" + page + ".html";
		} else {
			return "file:///android_asset/en_US/" + page + ".html";
		}
	}
}
