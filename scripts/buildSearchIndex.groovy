<%--
/*
 * This file is part of the NOTtheTalk distribution (https://github.com/jdudmesh/notthetalk).
 * Copyright (c) 2011-2021 John Dudmesh.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
--%>

@Grab('mysql:mysql-connector-java:5.1.49')
@Grab('org.apache.lucene:lucene-core:3.4.0')
@GrabConfig(systemClassLoader=true)

import groovy.sql.Sql
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.util.Version;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import java.io.File;
import java.text.SimpleDateFormat;

def engine = new SearchEngine("e:\\temp\\lucene2")
engine.indexAllPosts()


class SearchEngine {

	NIOFSDirectory _directory

	SearchEngine(location) {
		def path = new File(location)
		_directory = new NIOFSDirectory(path)
	}

	def indexAllPosts() {

		def sql = Sql.newInstance("jdbc:mysql://localhost:3306/notthetalk", "notthetalk_app","BeXuPHa3uC4c", "com.mysql.jdbc.Driver")

		IndexWriter writer = getWriter()
		writer.deleteAll()

		def i = 0
		println("Starting...")
		sql.eachRow("call GetPostSearchDetail();") {

			def doc = createPostDocumentFromDb(it)
			writer.addDocument(doc);

			i++
			if(i % 1000 == 0) {
				println i
			}
		}

		writer.optimize();
		writer.close();

		println("Done")
	}

	private Document createPostDocumentFromDb(post) {

		Document doc = new Document();

		SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

		doc.add(new Field("type", "post", Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new NumericField("id", Field.Store.YES, false).setLongValue(post.id));
		doc.add(new Field("uid", "post_$post.id", Field.Store.YES, Field.Index.ANALYZED));

		doc.add(new Field("post", post.text, Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new Field("username", post.username, Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new Field("thread", post.title, Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new Field("folder", post.description, Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new Field("date", fmt.format(post.created_date), Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new NumericField("datesort", Field.Store.YES, true).setLongValue(post.created_date.getTime()));

		return doc;
	}

	private IndexWriter getWriter() {

		StandardAnalyzer analyzer  = new StandardAnalyzer(Version.LUCENE_34);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34, analyzer)
			.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

		IndexWriter writer = new IndexWriter(_directory, config);

		return writer
	}

}

