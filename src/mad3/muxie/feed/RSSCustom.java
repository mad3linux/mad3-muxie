package mad3.muxie.feed;

import android.util.Patterns;

public class RSSCustom extends RSS {

	@Override
	protected String toUri(String uid) {
		if (uid != null && Patterns.WEB_URL.matcher(uid).matches()) {
			return uid;
		}
		return "";
	}
}
