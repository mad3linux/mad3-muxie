package mad3.muxie.feed;

import org.mcsoxford.rss.RSSItem;

@Deprecated
public class RSSMADItem extends RSSItem {

	private int type;
	
	RSSMADItem(byte categoryCapacity, byte thumbnailCapacity) {
		super(categoryCapacity, thumbnailCapacity);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
