package fidias.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import mad3.muxie.app.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.widget.Toast;
import fidias.controller.Controller;

/**
 * Abstract class to use for a simple Activity, without helper.
 * Classe abstrata para utilizar como um Activity simples, sem um helper.
 * @author atila
 *
 */
public abstract class SimpleActivity extends Activity implements Controller, View {

	/**
	 * populate the elements in the ContentValue object.
	 * popula os elementos no objeto ContentValues.
	 * @param values
	 */
	public void populate(ContentValues values){ }
	
	/**
	 * show a message on the screen.
	 * mostra uma mensagem na tela.
	 * @param message
	 */
	public void show(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	public void show(int resource, Object... objs) {
		String text = getResources().getString(resource, objs);
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	public void go(Class<?> clazz) {
		go(clazz, null, null);
	}
	
	/**
	 * goes from an Activity to another.
	 * vai de um Activity para outro.
	 * @param clazz
	 * @param namespace
	 * @param id
	 */
	public void go(Class<?> clazz, String namespace, String id) {
		Intent intent = new Intent(this, clazz);
		if (id != null) {
			intent.putExtra(namespace, id);
		}
		startActivity(intent);
	}
	
	public void go(Class<?> clazz, HashMap<String, String> params) {
		Intent intent = new Intent(this, clazz);
		if (params != null) {
			Iterator<?> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				@SuppressWarnings("unchecked")
				Map.Entry<String, String> param = (Entry<String, String>) iter.next();
				intent.putExtra(param.getKey(), param.getValue());
				iter.remove(); // avoids a ConcurrentModificationException
			}
		}
		startActivity(intent);
	}
	
	public void forceLocale(String language, String country) {
		Locale locale = new Locale(language, country);
		Locale.setDefault(locale);
		Configuration configuration = new Configuration();
		configuration.locale = locale;
		getBaseContext().getResources()
			.updateConfiguration(configuration,
					getBaseContext().getResources().getDisplayMetrics());
	}

	/**
	 * Use when the error is a blocking to the app. This method shall call the finish method.
	 * @param e
	 */
	public void blockError(Exception e) {
		error(e, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
	}
	
	public void error(Exception e) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.err_access);
		dialog.setMessage(e.getMessage());
		dialog.setNeutralButton(R.string.btn_back, null);
		dialog.show();
	}
	
	private void error(Exception e, OnClickListener listener) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.err_access);
		dialog.setMessage(e.getMessage());
		dialog.setNeutralButton(R.string.btn_back, listener);
		dialog.show();
	}
}
