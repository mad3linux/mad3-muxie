package mad3.muxie.feed;

public class RSSTwitter extends RSS {

	@Override
	protected String toUri(String uid) {
		if (uid != null) {
			return "https://twitter.com/statuses/user_timeline/" + uid + ".rss";
		}
		return "";
	}
}
