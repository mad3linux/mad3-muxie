package fidias.view;

import android.database.Cursor;
import android.view.View;

public abstract class Holder {
	
	public Holder(View row) {
		super();
	}
	
	public abstract void populate(Cursor c);
}
