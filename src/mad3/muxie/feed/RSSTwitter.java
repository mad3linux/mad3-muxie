package mad3.muxie.feed;

public class RSSTwitter extends RSS {

	@Override
	protected String toUri(String uid) {
		if (uid != null) {
			// https://twitter.com/statuses/user_timeline/atilacamurca.rss
			return "https://twitter.com/statuses/user_timeline/" + uid + ".rss";
		}
		return "";
	}
}
