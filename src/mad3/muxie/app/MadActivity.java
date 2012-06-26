package mad3.muxie.app;

import mad3.muxie.app.R;
import mad3.muxie.feed.RSSType;
import mad3.muxie.table.TableRss;
import mad3.muxie.view.FavoritesActivity;
import mad3.muxie.view.PostListActivity;
import mad3.muxie.view.WebViewActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fidias.model.Helper;
import fidias.view.FullActivity;
import fidias.view.Holder;

public class MadActivity extends FullActivity {
	
	private Cursor model = null;
	private MadAdapter adapter = null;
	private ListView listView = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        helper = new Helper(this);
        load();
        set();
    }
    
    @Override
	public void load() {
		listView = (ListView) findViewById(R.id.lv_main);
		model = listAll();
		startManagingCursor(model);
		adapter = new MadAdapter(model);
		listView.setAdapter(adapter);
	}

	@Override
	public void set() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				model.moveToPosition(position);
				go(PostListActivity.class, PostListActivity._ID,
						model.getString(model.getColumnIndex("_id")));
			}
		});
	}
    
	private Cursor listAll() {
		return helper.getReadableDatabase()
				.rawQuery(TableRss.SELECT_ALL, null);
	}
	
    private static class MadHolder extends Holder {
    	private TextView tvRssUid = null;
    	private ImageView ivAvatar = null;
    	
    	public MadHolder(View row) {
    		super(row);
    		tvRssUid = (TextView) row.findViewById(R.id.tv_rss_name);
    		ivAvatar = (ImageView) row.findViewById(R.id.iv_avatar);
    	}
    	
    	public void populate(Cursor cursor) {
    		tvRssUid.setText(cursor.getString(cursor.getColumnIndex(TableRss.NAME)));
    		int avatar = cursor.getInt(cursor.getColumnIndex(TableRss.TYPE));
    		switch (avatar) {
			case RSSType.blogger:
				ivAvatar.setImageResource(R.drawable.ic_launcher_blogger);
				break;
			case RSSType.twitter:
				ivAvatar.setImageResource(R.drawable.ic_launcher_twitter_bird);
				break;
			case RSSType.facebook:
				ivAvatar.setImageResource(R.drawable.ic_launcher_facebook);
				break;
			case RSSType.wordpress:
				ivAvatar.setImageResource(R.drawable.ic_launcher_wordpress);
				break;
			case RSSType.identica:
				ivAvatar.setImageResource(R.drawable.ic_launcher_identica);
				break;
			default:
				ivAvatar.setImageResource(R.drawable.ic_launcher_custom);
				break;
			}
    	}
    }
    
    private class MadAdapter extends CursorAdapter {

    	public MadAdapter(Cursor c) {
    		super(MadActivity.this, c);
    	}
    	
    	@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
    		LayoutInflater inflater = getLayoutInflater();
    		View row = inflater.inflate(R.layout.main_row, parent, false);
    		MadHolder holder = new MadHolder(row);
    		row.setTag(holder);
			return row;
		}
    	
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			MadHolder holder = (MadHolder) view.getTag();
			holder.populate(cursor);
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	new MenuInflater(this).inflate(R.menu.main_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.menu_about) {
			go(WebViewActivity.class, WebViewActivity._PAGE, "about");
			return true;
		} else if (item.getItemId() == R.id.menu_help) {
			go(WebViewActivity.class, WebViewActivity._PAGE, "help");
			return true;
		} else if (item.getItemId() == R.id.menu_favorites) {
			go(FavoritesActivity.class);
			return true;
		}
    	return super.onOptionsItemSelected(item);
    }

	@Override
	public void createOrUpdate(ContentValues values) {
		
	}

	@Override
	public boolean delete(String id) {
		return false;
	}

	@Override
	public boolean validate(ContentValues values) {
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		model.close();
	}
}