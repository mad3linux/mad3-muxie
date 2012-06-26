package mad3.muxie.view;

import java.sql.Date;
import java.util.List;

import mad3.muxie.app.R;
import mad3.muxie.feed.RSSType;
import mad3.muxie.table.TableFavorites;
import mad3.muxie.table.TableRss;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fidias.model.Helper;
import fidias.util.DateTimeUtils;
import fidias.util.UrlUtils;
import fidias.view.FullActivity;
import fidias.view.Holder;

public class FavoritesActivity extends FullActivity {

	private ListView listView = null;
	private Cursor model = null;
	private FavoritesAdapter adapter = null;
	private TextView tvFavoritesEmpty = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites_list);
		
		helper = new Helper(this);
		load();
		set();
	}
	
	@Override
	public void load() {
		listView = (ListView) findViewById(R.id.lv_favorites);
		model = listAll();
		startManagingCursor(model);
		adapter = new FavoritesAdapter(model);
		listView.setAdapter(adapter);
		
		tvFavoritesEmpty = (TextView) findViewById(R.id.tv_favorites_empty);
		if (model.getCount() > 0) {
			tvFavoritesEmpty.setVisibility(View.GONE);
		}
	}

	@Override
	public void set() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				view(position);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(FavoritesActivity.this);
				dialog.setTitle(R.string.msg_options);
				dialog.setItems(R.array.favorites_options, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							view(position);
							break;
						case 1:
							scanForUrl(position);
							break;
						}
					}
				});
				dialog.show();
				return true;
			}
		});
	}
	
	private void view(int position) {
		model.moveToPosition(position);
		String link = model.getString(model.getColumnIndex(TableFavorites.LINK));
		show(getResources().getString(R.string.msg_opening_url, link));
		openUrl(link);
	}
	
	private void scanForUrl(int position) {
		model.moveToPosition(position);
		String content = model.getString(model.getColumnIndex(TableFavorites.CONTENT));
		final List<String> urls = UrlUtils.extractUrl(content);
		
		int size = urls.size();
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
			AlertDialog.Builder dialog = new AlertDialog.Builder(FavoritesActivity.this);
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
	
	private Cursor listAll() {
		return helper.getReadableDatabase()
				.rawQuery(TableFavorites.SELECT_ALL +
						" ORDER BY " + TableFavorites.PUB_DATE + " DESC", null);
	}
	
	private void openUrl(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}
	
	private static class FavoritesHolder extends Holder {
		private TextView tvContent = null, tvType = null, tvTimestamp = null;

		public FavoritesHolder(View row) {
			super(row);
			tvContent = (TextView) row.findViewById(R.id.tv_content);
			tvType = (TextView) row.findViewById(R.id.tv_type);
			tvTimestamp = (TextView) row.findViewById(R.id.tv_timestamp);
		}
		
		@Override
		public void populate(Cursor c) {
			tvContent.setText(c.getString(c.getColumnIndex(TableFavorites.CONTENT)));
			int type = c.getInt(c.getColumnIndex(TableRss.TYPE));
			switch (type) {
			case RSSType.blogger:
				tvType.setBackgroundResource(R.drawable.blogger_header);
				tvType.setText("   blogger   ");
				break;
			case RSSType.twitter:
				tvType.setBackgroundResource(R.drawable.twitter_header);
				tvType.setText("   twitter    ");
				break;
			case RSSType.facebook:
				tvType.setBackgroundResource(R.drawable.facebook_header);
				tvType.setText(" facebook ");
				break;
			case RSSType.wordpress:
				tvType.setBackgroundResource(R.drawable.wordpress_header);
				tvType.setText(" wordpress ");
				break;
			case RSSType.identica:
				tvType.setBackgroundResource(R.drawable.identica_header);
				tvType.setText("   identica   ");
				break;
			default:
				tvType.setBackgroundResource(R.drawable.blogger_header);
				tvType.setText("   custom   ");
				break;
			}
			Date date = new Date(c.getLong(c.getColumnIndex(TableFavorites.PUB_DATE)));
			tvTimestamp.setText(DateTimeUtils.formatDate(date, DateTimeUtils.DIASEMANA_DIA_MES_ANO));
		}
	}
	
	private class FavoritesAdapter extends CursorAdapter {

		public FavoritesAdapter(Cursor c) {
			super(FavoritesActivity.this, c);
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.favorites_row, parent, false);
			FavoritesHolder holder = new FavoritesHolder(row);
			row.setTag(holder);
			return row;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			FavoritesHolder holder = (FavoritesHolder) view.getTag();
			holder.populate(cursor);
		}
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
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		model.close();
	}
}
