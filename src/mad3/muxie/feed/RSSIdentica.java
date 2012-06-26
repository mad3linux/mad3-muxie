package mad3.muxie.feed;

public class RSSIdentica extends RSS {

	@Override
	protected String toUri(String uid) {
		if (uid != null) {
			return "http://identi.ca/api/statuses/user_timeline/" + uid + ".rss";
		}
		return "";
	}
}
