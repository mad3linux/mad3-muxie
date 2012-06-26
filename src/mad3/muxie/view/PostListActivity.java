package mad3.muxie.view;

import java.net.UnknownHostException;
import java.util.List;

import mad3.muxie.app.R;
import mad3.muxie.feed.RSS;
import mad3.muxie.feed.RSSBlogger;
import mad3.muxie.feed.RSSCustom;
import mad3.muxie.feed.RSSFacebook;
import mad3.muxie.feed.RSSIdentica;
import mad3.muxie.feed.RSSTwitter;
import mad3.muxie.feed.RSSType;
import mad3.muxie.feed.RSSWordpress;
import mad3.muxie.table.TableRss;
import mad3.muxie.twitter.ParseTweet;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReaderException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fidias.model.Helper;
import fidias.util.DateTimeUtils;
import fidias.util.UrlUtils;
import fidias.view.FullActivity;

public class PostListActivity extends FullActivity {

	public static final String _ID = "mad3.muxie.rss._ID";
	private String id = null;
	
	private String uid = null, title = null;
	private int type = 1;
	static List<RSSItem> items = null;
	private TextView tvTitle = null;
	private ListView lvList = null;
	private BaseAdapter adapter = null;
	
	private boolean hasErrors = false;
	private Exception error = null;
	
	private ProgressDialog dialog = null;
	private ProgressThread thread = null;
	private static final int THREAD_DELAY = 1000 * 60 * 5;
	private boolean stopReload = false;
	
	/* constants for dialogs */
	private static final int PROGRESS_DIALOG = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		helper = new Helper(this);
		load();
		set();
	}
	
	@Override
	public void load() {
		
		setContentView(R.layout.rss_post_list);
		lvList = (ListView) findViewById(R.id.lv_rss_list);
		
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.msg_now_loading);
		
		id = getIntent().getStringExtra(_ID);
		try {
			Cursor rssCursor = read(id);
			rssCursor.moveToFirst();
			type = rssCursor.getInt(rssCursor.getColumnIndex(TableRss.TYPE));
			uid = rssCursor.getString(rssCursor.getColumnIndex(TableRss.UID));
			
			switch (type) {
			case RSSType.twitter:
				tvTitle.setBackgroundResource(R.drawable.twitter_header);
				break;
			case RSSType.facebook:
				tvTitle.setBackgroundResource(R.drawable.facebook_header);
				break;
			case RSSType.wordpress:
				tvTitle.setBackgroundResource(R.drawable.wordpress_header);
				break;
			case RSSType.identica:
				tvTitle.setBackgroundResource(R.drawable.identica_header);
				break;
			default:
				tvTitle.setBackgroundResource(R.drawable.blogger_header);
				break;
			}
			rssCursor.close();
			showDialog(PROGRESS_DIALOG);
		} catch (Exception e) {
			Log.e("PostList", e.getMessage(), e);
			blockError(e);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			dialog = new ProgressDialog(this);
			dialog.setProgressStyle(id);
			dialog.setMessage(getResources().getString(R.string.msg_now_loading));
			thread = new ProgressThread(handler);
			thread.start();
			return dialog;
		default:
			return null;
		}
		
	}

	@Override
	public void set() {
		lvList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				goToItem(position);
			}
		});
		
		lvList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(PostListActivity.this);
				dialog.setTitle(R.string.msg_options);
				dialog.setItems(R.array.post_list_options, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							goToItem(position);
							break;
						case 1:
							scanForUrl(position);
							break;
						case 2:
							ImageView ivRssFav = (ImageView) view.findViewById(R.id.iv_rss_fav);
							addToFavorites(position);
							if (hasErrors) {
								error(error);
								// reset error
								hasErrors = false;
								error = null;
							} else {
								ivRssFav.setImageResource(android.R.drawable.btn_star_big_on);
							}
							break;
						}
					}
				});
				dialog.show();
				return true;
			}
		});
	}
	
	private void goToItem(int position) {
		RSSItem item = items.get(position);
		go(WebViewActivity.class, WebViewActivity._LINK, item.getLink().toString());
	}
	
	private void scanForUrl(int position) {
		RSSItem item = items.get(position);
		final List<String> urls = null;
		switch (type) {
		case RSSType.facebook:
			UrlUtils.extractUrl(item.getDescription());
			break;
		default:
			UrlUtils.extractUrl(item.getTitle());
			break;
		}
		
		int size = (urls != null ? urls.size() : 0);
		// Workaround for remove unwanted url
		for (int i = 0; i < size; i++) {
			String s = urls.get(i);
			if (s.endsWith("</a")) {
				urls.remove(i);
				size--;
			}
		}
		// Workaround
		
		if (size == 0) {
			String s = getResources().getString(R.string.warn_no_url);
			show(s);
		} else if (size == 1) {
			String url = urls.get(0);
			show(getResources().getString(R.string.msg_opening_url, url));
			openUrl(url);
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(PostListActivity.this);
			ArrayAdapter<String> adapter = 
					new ArrayAdapter<String>(this, R.layout.simple_list_item_url, urls);
			
			dialog.setTitle(R.string.msg_links);
			dialog.setAdapter(adapter, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String url = urls.get(which);
					show(getResources().getString(R.string.msg_opening_url, url));
					openUrl(url);
				}
			});
			dialog.show();
		}
	}
	
	private void addToFavorites(int position) {
		RSSItem item = items.get(position);
		ContentValues values = new ContentValues();
		values.put("pub_date", item.getPubDate().getTime());
		values.put("link", item.getLink().toString());
		values.put("rss_id", id);
		String content = "";
		switch (type) {
		case RSSType.facebook:
			content = Html.fromHtml(item.getDescription()).toString();
			break;
		default: // for whom the bell tolls (for whom that use just the title)
			content = item.getTitle();
			break;
		}
		values.put("content", content);
		createOrUpdate(values);
	}
	
	private void openUrl(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}
	
	private void readFeed() {
		RSS rss = null;
		try {
			switch (type) {
			case RSSType.blogger:
				rss = new RSSBlogger();
				break;
			case RSSType.twitter:
				rss = new RSSTwitter();
				break;
			case RSSType.facebook:
				rss = new RSSFacebook();
				break;
			case RSSType.wordpress:
				rss = new RSSWordpress();
				break;
			case RSSType.identica:
				rss = new RSSIdentica();
				break;
			case RSSType.custom:
				rss = new RSSCustom();
				break;
			default:
				hasErrors = true;
				error = new Exception(getResources().getString(R.string.err_unknow_rss));
			}
			RSSFeed feed = rss.getRSSFeed(uid);
			title = feed.getTitle();
			items = feed.getItems();
		} catch (UnknownHostException e) {
			Log.e("PostListActivity", e.getMessage(), e);
			hasErrors = true;
			error = new Exception(getResources().getString(R.string.err_network_access));
		} catch (RSSReaderException e) {
			Log.e("PostListActivity", e.getMessage(), e);
			if ("Bad Request".equals(e.getMessage())) {
				hasErrors = true;
				error = new Exception(getResources().getString(R.string.err_request_limit));
			} else {
				hasErrors = true;
				error = e;
			}
			
		} catch (Exception e) {
			Log.e("PostListActivity", e.getMessage(), e);
			hasErrors = true;
			error = new Exception(getResources().getString(R.string.err_network_access));
		}
	}
	
	private void loadFeed() {
		if (validate(null)) {
			tvTitle.setText(title);
			
			if (type == RSSType.twitter || type == RSSType.identica) {
				adapter = new TwitterPostListAdapter(this);
			} else {
				adapter = new BasePostListAdapter(this, type);
			}
			
			adapter.notifyDataSetChanged();
			lvList.setAdapter(adapter);
			// reload the rss
			handler.postDelayed(reload, THREAD_DELAY);
		}
	}
	
	private static class BasePostListHolder {
		public TextView tvBaseTitle = null, tvBaseTimestamp = null;
		public ImageView ivBaseFav = null;
	}
	
	private class BasePostListAdapter extends BaseAdapter {
	    private PostListActivity context;
	    private int type;
	    
		public BasePostListAdapter(PostListActivity context, int type) {
			super();
			this.context = context;
			this.type = type;
		}
		
		@Override
		public int getCount() {
			if (items != null) {
				return items.size();
			}
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			return items.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View row = convertView;
	    	BasePostListHolder holder = null;
	    	
	    	if (row == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				row = inflater.inflate(R.layout.base_post_row, null);
				
				holder = new BasePostListHolder();
				holder.tvBaseTitle = (TextView) row.findViewById(R.id.tv_blogger_title);
				holder.tvBaseTimestamp = (TextView) row.findViewById(R.id.tv_blogger_timestamp);
				holder.ivBaseFav = (ImageView) row.findViewById(R.id.iv_rss_fav);
				row.setTag(holder);
			} else {
				holder = (BasePostListHolder) row.getTag();
			}
	    	populate(items.get(position), holder);
	    	return row;
	    }
	    
	    private void populate(RSSItem item, BasePostListHolder holder) {
	    	switch (type) {
			case RSSType.facebook:
				holder.tvBaseTitle.setText(Html.fromHtml(item.getDescription()));
				break;
			default:
				holder.tvBaseTitle.setText(item.getTitle());
				break;
			}
	    	holder.tvBaseTimestamp.setText(
	    			DateTimeUtils.formatDate(item.getPubDate(), DateTimeUtils.DIASEMANA_DIA_MES_ANO));
	    	Cursor c = context.exists(item.getPubDate().getTime(), String.valueOf(type));
	    	context.startManagingCursor(c);
			if (c != null && c.moveToFirst()) {
				holder.ivBaseFav.setImageResource(android.R.drawable.btn_star_big_on);
			} else {
				holder.ivBaseFav.setImageResource(android.R.drawable.btn_star_big_off);
			}
			c.close();
	    }
	}
	
	private static class TwitterPostListHolder {
		public TextView tvTwitterTitle = null, tvTwitterTimestamp = null, tvTwitterUser = null;
		public ImageView ivTwitterFav = null;
	}
	
	private class TwitterPostListAdapter extends BaseAdapter {
		private PostListActivity context;
		
		public TwitterPostListAdapter(PostListActivity context) {
			super();
			this.context = context;
		}
		
		@Override
		public int getCount() {
			if (items != null) {
				return items.size();
			}
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			return items.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			TwitterPostListHolder holder = null;
			
			if (row == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				row = inflater.inflate(R.layout.twitter_post_row, null);
				
				holder = new TwitterPostListHolder();
				holder.tvTwitterTitle = (TextView) row.findViewById(R.id.tv_twitter_description);
				holder.tvTwitterTimestamp = (TextView) row.findViewById(R.id.tv_twitter_timestamp);
				holder.tvTwitterUser = (TextView) row.findViewById(R.id.tv_twitter_user);
				holder.ivTwitterFav = (ImageView) row.findViewById(R.id.iv_rss_fav);
				row.setTag(holder);
			} else {
				holder = (TwitterPostListHolder) row.getTag();
			}
			
			RSSItem item = items.get(position);
			String[] array = ParseTweet.extractUser(item.getTitle());
			if (array != null) {
				holder.tvTwitterTitle.setText(Html.fromHtml(array[1]));
				holder.tvTwitterUser.setText(array[0]);
			}
			holder.tvTwitterTimestamp.setText(
	    			DateTimeUtils.formatDate(item.getPubDate(), DateTimeUtils.DIASEMANA_DIA_MES_ANO));
			Cursor c = context.exists(item.getPubDate().getTime(), "2");
			context.startManagingCursor(c);
			if (c != null && c.moveToFirst()) {
				holder.ivTwitterFav.setImageResource(android.R.drawable.btn_star_big_on);
			} else {
				holder.ivTwitterFav.setImageResource(android.R.drawable.btn_star_big_off);
			}
			c.close();
			return row;
		}
	}
	
	private Cursor read(String id) throws Exception {
		try {
			return helper.getReadableDatabase()
					.rawQuery(TableRss.SELECT_ALL + " WHERE _id = ?", new String[]{id});
		} catch (Exception e) {
			Log.e("PostList", e.getMessage(), e);
			throw e;
		}
	}
	
	public Cursor exists(long date, String type) {
		try {
			return helper.getReadableDatabase()
					.rawQuery("SELECT r._id FROM favorites_fast ff " +
							"INNER JOIN rss r ON ff.rss_id = r._id WHERE ff.pub_date = ? AND r.type = ?",
							new String[]{String.valueOf(date), type});
		} catch (Exception e) {
			Log.e("PostList", e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public void createOrUpdate(ContentValues values) {
		try {
			helper.getWritableDatabase().beginTransaction();
			
			helper.create("favorites", "content", values);
			
			helper.getWritableDatabase()
				.execSQL("INSERT INTO favorites_fast (rss_id, pub_date) VALUES (?, ?)",
						new String[]{
						id, values.getAsString("pub_date")
			});
			
			helper.getWritableDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("PostList", e.getMessage(), e);
			hasErrors = true;
			error = e;
		} finally {
			helper.getWritableDatabase().endTransaction();
		}
	}

	@Override
	public boolean delete(String id) {
		return false;
	}

	@Override
	public boolean validate(ContentValues values) {
		if (hasErrors) {
			blockError(error);
			return false;
		}
		return true;
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			boolean done = msg.getData().getBoolean("done");
			if (done) {
				if (dialog.isShowing()) {
					dismissDialog(PROGRESS_DIALOG);
				}
				loadFeed();
			}
		}
	};
	
	private class ProgressThread extends Thread {
		Handler handler = null;
		
		public ProgressThread(Handler handler) {
			this.handler = handler;
		}
		
		@Override
		public void run() {
			if (uid != null) {
				readFeed();
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putBoolean("done", true);
				message.setData(bundle);
				handler.sendMessage(message);
			}
		}
	}
	
	private Runnable reload = new Runnable () {
		@Override
		public void run() {
			if (!stopReload) {
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("PostList", "start reload");
		stopReload = false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("PostList", "stop reload");
		stopReload = true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("PostList", "removing reload thread...");
		if (thread.isAlive()) {
			Log.i("PostList", "interrupting thread...");
			thread.interrupt();
			thread = null;
		}
		handler.removeCallbacks(reload);
	}
}
