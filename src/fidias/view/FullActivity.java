package fidias.view;

import fidias.controller.Controller;
import fidias.model.Helper;

/**
 * Abstract class to use for a full Activity, with helper.
 * @author atila
 *
 */
public abstract class FullActivity extends SimpleActivity implements Controller, View {
	
	protected Helper helper = null;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			helper.close();
		}
	}
}
