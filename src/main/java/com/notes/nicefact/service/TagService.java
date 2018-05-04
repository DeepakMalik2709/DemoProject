package com.notes.nicefact.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.TagDAO;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TagTO;
import com.notes.nicefact.util.CacheUtils;

public class TagService extends CommonService<Tag> {

		
		private TagDAO tagDao;
		public TagService(EntityManager em) {
			tagDao = new TagDAO(em);
		}


		@Override
		protected CommonDAO<Tag> getDAO() {
			return tagDao;
		}


		public List<TagTO> search(SearchTO searchTO) {
			List<TagTO>  tagTos = new ArrayList<>();
			List<Tag>  tags = tagDao.search(searchTO);
			TagTO tagTo;
			for (Tag tag : tags) {
				tagTo = new TagTO(tag);
				tagTos.add(tagTo);
			}
			return tagTos;
		}


		public Tag upsert(TagTO tagTO) {
			Tag tag = new Tag(tagTO);
			if (tagTO.getId() > 0) {
				Tag tagDB = get(tagTO.getId());
				tagDB.updateProps(tag);
				upsert(tagDB);
				return tagDB;
			} else {
				upsert(tag);
			}
			return tag;
		}
		

}
