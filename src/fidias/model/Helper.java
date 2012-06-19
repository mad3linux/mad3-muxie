package fidias.model;

import mad3.muxie.app.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "mad3_mobile.db";
	private static final int VERSION = 1;
	private static final String DROP_IF_EXISTS = "DROP TABLE IF EXISTS ";
	protected Resources resources = null;
	
	public Helper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		resources = context.getResources();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(resources.getString(R.string.sql_create_tb_rss));
		db.execSQL(resources.getString(R.string.sql_create_tb_favorites));
		db.execSQL(resources.getString(R.string.sql_create_tb_favorites_fast));
		String[] array = resources.getStringArray(R.array.sql_insert_rss);
		for (int i = 0; i < array.length; i++) {
			db.execSQL(array[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int odlVersion, int newVersion) {
		
	}
	
	/**
	 * insert a new row in the database.
	 * insere uma nova coluna no banco de dados.
	 * @param table
	 * @param nullColumnRack
	 * @param values
	 */
	public long create(String table, String nullColumnRack, ContentValues values) {
		return getWritableDatabase().insert(table, nullColumnRack, values);
	}
	
	/**
	 * delete an existing row in the database.
	 * deleta uma coluna existente no banco de dados.
	 * @param table
	 * @param id
	 * @return
	 */
	public int delete(String table, String id) {
		String whereClause = "_id = ?";
		String[] whereArgs = {id};
		return getWritableDatabase().delete(table, whereClause, whereArgs);
	}
	
	/**
	 * update existing row in the database.
	 * atualiza uma coluna existente no banco de dados.
	 * @param table
	 * @param id
	 * @param values
	 */
	public int update(String table, String id, ContentValues values) {
		String whereClause = "_id = ?";
		String[] whereArgs = {id};
		return getWritableDatabase().update(table, values, whereClause, whereArgs);
	}
}
