package mad3.muxie.feed;

public class RSSBlogger extends RSS {

	@Override
	protected String toUri(String uid) {
		if (uid != null) {
			return "http://" + uid + "/feeds/posts/default?alt=rss";
		}
		return "";
	}
}
