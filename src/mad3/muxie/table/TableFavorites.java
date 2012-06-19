package mad3.muxie.table;

public interface TableFavorites {

	String CONTENT = "content";
	String PUB_DATE = "pub_date";
	String LINK = "link";
	String RSS_ID = "rss_id";
	String SELECT_ALL = "SELECT f._id, ".concat(CONTENT).concat(", ")
			.concat(PUB_DATE).concat(", ").concat(LINK)
			.concat(", ").concat(TableRss.TYPE).concat(" FROM favorites f ")
			.concat("INNER JOIN rss r ON f.rss_id = r._id ");
}
