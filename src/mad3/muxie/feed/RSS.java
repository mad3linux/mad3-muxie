package mad3.muxie.feed;

import java.net.UnknownHostException;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.util.Log;

public abstract class RSS {
	
	protected abstract String toUri(String uid);
	
	// https://www.facebook.com/feeds/notifications.php?id=164610873600079&viewer=100000156218932&key=AWhQMfeTr5ieKZP6&format=rss20
	
	public RSSFeed getRSSFeed(String uid) throws RSSReaderException, UnknownHostException, Exception {
		String uri = toUri(uid);
		
		try {
			RSSReader reader = new RSSReader();
			return reader.load(uri);
		} catch (RSSReaderException e) {
			Log.e("Rss", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
}
