package mad3.muxie.twitter;

public class ParseTweet {
	
	private ParseTweet(){}
	
	public static String[] extractUser(String tweet) {
		String[] array = null;
		if (tweet.matches(".+:\\sRT\\s@.+:\\s.+$")) {
			array = tweet.split(": ", 3);
			return new String[]{ array[1], array[2] };
		} else if (tweet.matches(".+:\\s.+$")) {
			array = tweet.split(": ", 2);
			return array;
		}
		return null;
	}
}
