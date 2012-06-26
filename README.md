MAD3 Muxie
==========

MAD3 Muxie is a RSS Reader that can integrate Blogger, Twitter, Facebook, Wordpress and Identica.

## Initial configuration

After download the source, just change the file `sql.xml` located at `/res/values` as follow:

	<string-array name="sql_insert_rss">
        <item>            <![CDATA[INSERT INTO rss (name, uid, type) VALUES (\'@atilacamurca\', \'atilacamurca\', 2);]]>
        </item>
    </string-array>

This will configure a Twitter account (id : `2`) to the user [atilacamurca](https://twitter.com/atilacamurca "@atilacamurca").

Other values for `type`:

*	blogger : 1
*	twitter : 2
*	facebook : 3
*	wordpress : 4
*	identica : 5
*	custom : 99

More yet to come...
