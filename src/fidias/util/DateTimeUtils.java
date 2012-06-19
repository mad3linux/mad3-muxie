package fidias.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class DateTimeUtils {

	public static final String DEF_DIA_MES_ANO = "dd/MM/yyyy";
	public static final String DEF_MES_ANO = "MM/yyyy";
	public static final String ALT_MES_ANO = "MMMMM\nyyyy";
	public static final String MES_ANO = "MMMMM yyyy";
	public static final String DIASEMANA_DIA_MES_ANO = "E, d MMM yyyy";
	
	public static String formatDate(Date date, String pattern) {
		if (date != null) {
			try {
				return new SimpleDateFormat(pattern).format(date);
			} catch (Exception e) {
				Log.w("DateTimeUtils", e);
			}
		}
		return null;
	}
}
