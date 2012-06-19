package mad3.muxie.table;

public interface TableRss {

	String NAME = "name";
	String UID = "uid";
	String TYPE = "type";
	String SELECT_ALL = "SELECT _id, ".concat(NAME).concat(", ")
			.concat(UID).concat(", ").concat(TYPE)
			.concat(" FROM rss ");
}
