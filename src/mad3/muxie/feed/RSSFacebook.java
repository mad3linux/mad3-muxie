package mad3.muxie.feed;

public class RSSFacebook extends RSS {

	@Override
	protected String toUri(String uid) {
		if (uid != null) {
			return "https://www.facebook.com/feeds/page.php?id=" + uid + "&format=rss20";
		}
		return "";
	}

}
