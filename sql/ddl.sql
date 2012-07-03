--
-- MAD3 Muxie
--

CREATE TABLE rss (
   _id INTEGER PRIMARY KEY AUTOINCREMENT,
   name TEXT NOT NULL,
   uid TEXT NOT NULL,
   type INTEGER NOT NULL DEFAULT 1 -- 1: blogger, 2: twitter, 3: facebook, 4: wordpress, 5: identica, 99: custom
);

CREATE TABLE favorites (
   _id INTEGER PRIMARY KEY AUTOINCREMENT,
   content TEXT NOT NULL,
   pub_date INTEGER NOT NULL,
   link TEXT NOT NULL,
   rss_id INTEGER NOT NULL REFERENCES rss(_id)
);

CREATE TABLE favorites_fast (
   rss_id INTEGER NOT NULL REFERENCES rss(_id),
   pub_date INTEGER NOT NULL REFERENCES favorites(pub_date)
);
