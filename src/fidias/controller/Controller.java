package fidias.controller;

import android.content.ContentValues;

/**
 * @author atila
 *
 */
public interface Controller {

	public void createOrUpdate(ContentValues values);
	
	public boolean delete(String id);
	
	public boolean validate(ContentValues values);
}
