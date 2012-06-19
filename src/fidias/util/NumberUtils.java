package fidias.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtils {

	public static String toRealFormat(double value) {
//		return "R$ " + new DecimalFormat(",##0.00", 
		return new DecimalFormat(",##0.00", 
				new DecimalFormatSymbols(new Locale("pt", "BR"))).format(value);
	}
	
	public static String itensRestantes(int value1, int value2) {
		DecimalFormat df = new DecimalFormat("00");
		return df.format(value1) + "/" + df.format(value2);
	}
	
	public static boolean isTrue(String value) {
		if ("1".equals(value)) {
			return true;
		}
		return false;
	}
	
	public static int parseBoolean(boolean value) {
		if (value) {
			return 1;
		}
		return 0;
	}
}
