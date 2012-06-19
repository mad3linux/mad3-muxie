package mad3.muxie.view;

import java.sql.Date;

import mad3.muxie.app.R;
import mad3.muxie.table.TableFavorites;
import mad3.muxie.table.TableRss;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fidias.model.Helper;
import fidias.util.DateTimeUtils;
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
				model.moveToPosition(position);
				String link = model.getString(model.getColumnIndex(TableFavorites.LINK));
				show(getResources().getString(R.string.msg_opening_url, link));
				openUrl(link);
			}
		});
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
			case 1:
				tvType.setBackgroundResource(R.drawable.blogger_header);
				tvType.setText("   blogger   ");
				break;
			case 2:
				tvType.setBackgroundResource(R.drawable.twitter_header);
				tvType.setText("   twitter    ");
				break;
			case 3:
				tvType.setBackgroundResource(R.drawable.facebook_header);
				tvType.setText(" facebook ");
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
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validate(ContentValues values) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		model.close();
	}
}
