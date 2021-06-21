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

package com.notthetalk;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class FrontPageEntryTransformer  implements ResultTransformer {

	ResultTransformer aliasTransformer = Transformers.aliasToBean(FrontPageEntry.class);

	@Override
	public List transformList(List list) {
		List<FrontPageEntry> result = aliasTransformer.transformList(list);
		return result;
	}

	@Override
	public Object transformTuple(Object[] arg0, String[] arg1) {

		assert(arg1[0].equalsIgnoreCase("id"));
		assert(arg1[1].equalsIgnoreCase("version"));
		assert(arg1[2].equalsIgnoreCase("discussion_id"));
		assert(arg1[3].equalsIgnoreCase("discussion_name"));
		assert(arg1[4].equalsIgnoreCase("folder_id"));
		assert(arg1[5].equalsIgnoreCase("folder_key"));
		assert(arg1[6].equalsIgnoreCase("folder_name"));
		assert(arg1[7].equalsIgnoreCase("last_post"));
		assert(arg1[8].equalsIgnoreCase("last_post_id"));
		assert(arg1[9].equalsIgnoreCase("post_count"));

		FrontPageEntry obj = new FrontPageEntry();
		//obj.setId((Long)arg0[0]);
		//obj.setVersion(arg0[1]);
		obj.setDiscussionId((Long)arg0[2]);
		obj.setDiscussionName((String)arg0[3]);
		obj.setFolderId((Long)arg0[4]);
		obj.setFolderKey((String)arg0[5]);
		obj.setFolderName((String)arg0[6]);
		obj.setLastPost((Date)arg0[7]);
		obj.setLastPostId((Long)arg0[8]);
		obj.setPostCount((Integer)arg0[9]);
		obj.setAdminOnly(false);

		return obj;
	}


}


