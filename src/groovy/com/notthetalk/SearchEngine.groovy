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
package com.notthetalk

import groovy.sql.Sql
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.util.Version;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*;
import java.io.File;
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.commons.logging.LogFactory


class SearchEngine {

	private static final log = LogFactory.getLog(this)
	NIOFSDirectory _directory
	static SearchEngine _instance;

	protected SearchEngine() {
		def grailsApplication = ApplicationHolder.application
		def path = new File(grailsApplication.config.searchIndex.location)
		println(path)
		_directory = new NIOFSDirectory(path)
	}

	public static SearchEngine getInstance() {
		if(!_instance) {
			_instance = new SearchEngine()
		}
		return _instance
	}

	def search(term, max, offset) {

		def hitCount = 0
		def results = []

		synchronized(_directory) {

			IndexSearcher searcher = null

			try {

				IndexReader reader = DirectoryReader.open(_directory);
				searcher = new IndexSearcher(reader);

				QueryParser parser = new QueryParser(Version.LUCENE_46, "post", new StandardAnalyzer(Version.LUCENE_46));
				Query query = parser.parse(term);

				Sort sortOrder = new Sort(new SortField("datesort", SortField.Type.LONG, true))
				TopFieldCollector collector = TopFieldCollector.create(sortOrder, 100000, true, false, false, true)
				searcher.search(query, collector);

				hitCount = collector.getTotalHits();
				ScoreDoc[] hits = collector.topDocs(offset).scoreDocs;

				//TopDocs hits = searcher.search(query, (offset + 1) * 50)


				for (int i = 0; i < max && i < hits.length; i++) {

					ScoreDoc scoreDoc = hits[i];
					int docId = scoreDoc.doc;
					Document doc = searcher.doc(docId);

					if(doc.get("id")) {
						SearchResult res = new SearchResult(id: Long.parseLong(doc.get("id")), type: doc.get("type"), score: scoreDoc.score )
						results.add(res)
					}
				}

				if(reader) {
					reader.close()
				}

			}
			catch(Exception e) {
				log?.error(e)
			}


		}

		return [hits: results, count: hitCount]

	}

	def indexPost(post) {

		def doc = createPostDocument(post)

		synchronized(_directory) {

			IndexWriter writer = null

			try {
				writer = getWriter()
				writer.addDocument(doc);
			}
			catch(Exception e) {
				log?.error(e)
			}

			if(writer) {
				writer.close();
			}

		}

	}

	def updatePost(post) {

		def term = new Term("uid", "post_$post.id")
		def doc = createPostDocument(post)

		synchronized(_directory) {

			IndexWriter writer = null;
			try {
				writer = getWriter()
				writer.updateDocument(term, doc, new StandardAnalyzer(Version.LUCENE_46))
			}
			catch(Exception e) {
				log?.error(e)
			}

			if(writer) {
				writer.close();
			}

		}

	}

	def deletePost(post) {

		def term = new Term("uid", "post_$post.id")

		synchronized(_directory) {

			IndexWriter writer = null;
			try {
				writer = getWriter()
				writer.deleteDocuments(term)
			}
			catch(Exception e) {
				log?.error(e)
			}

			if(writer) {
				writer.close();
			}

		}

	}

	private Document createPostDocument(Post post) {

		Document doc = new Document();

		SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

		doc.add(new Field("type", "post", Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new LongField("id", post.id, Field.Store.YES));
		doc.add(new Field("uid", "post_$post.id", Field.Store.YES, Field.Index.ANALYZED));

		doc.add(new Field("post", post.text, Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new Field("username", post.user.username, Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new Field("thread", post.discussion.title, Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new Field("folder", post.discussion.folder.description, Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new Field("date", fmt.format(post.createdDate), Field.Store.YES, Field.Index.ANALYZED ));
		doc.add(new LongField("datesort", post.createdDate.getTime(), Field.Store.YES));

		return doc;
	}

	private IndexWriter getWriter() {

		StandardAnalyzer analyzer  = new StandardAnalyzer(Version.LUCENE_46);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer)
			.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

		IndexWriter writer = new IndexWriter(_directory, config);

		return writer
	}


}

