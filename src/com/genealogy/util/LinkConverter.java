package com.genealogy.util;

import com.genealogy.pojo.dto.Link;
import com.genealogy.pojo.entity.Member;
import com.genealogy.pojo.entity.Relation;

public class LinkConverter {
	public static Link convert(Member source,Member target,Relation relation) {
		Link link = new Link();
		link.setSourceId(source.getId());
		link.setSource(source.getName());
		link.setSourceClass(GenderConverter.convert(source.getGender()));
		link.setTargetId(target.getId());
		link.setTarget(target.getName());
		link.setTargetClass(GenderConverter.convert(target.getGender()));
		if(relation.getType()==Relation.FATHER || relation.getType()==Relation.MOTHER) {
			if(relation.getU1().equals(source.getId())) {
				link.setRelation(Link.FATHERORMOTHER);
			}else if(relation.getU2().equals(source.getId())) {
				link.setRelation(Link.SONORDAUGHTER);
			}
		}else if(relation.getType()==Relation.HUSBAND){
			link.setRelation(Link.SPOUSE);
		}else {
			link.setRelation(Link.NONE);
		}
		return link;
	}
}
