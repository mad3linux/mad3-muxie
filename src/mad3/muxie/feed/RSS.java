package mad3.muxie.feed;

import java.net.UnknownHostException;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.util.Log;

public abstract class RSS {
	
	protected abstract String toUri(String uid);
	
	public RSSFeed getRSSFeed(String uid) throws RSSReaderException, UnknownHostException, Exception {
		String uri = toUri(uid);
		
		try {
			RSSReader reader = new RSSReader();
			return reader.load(uri);
		} catch (RSSReaderException e) {
			Log.e("RSS", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			Log.e("RSS", e.getMessage(), e);
			throw e;
		}
	}
}
