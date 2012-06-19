package fidias.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {

	public static List<String> extractUrl(String content) {
		Pattern p = Pattern.compile("(https?://+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
	        result.add(content.substring(m.start(0),m.end(0)));
	    }
		return result;
	}
}
