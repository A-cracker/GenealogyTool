package com.genealogy.dao;

import com.fy.sqlparam.param.ISqlParameter;
import com.fy.toolhelper.db.IBaseDao;
import com.genealogy.pojo.entity.Document;
import com.genealogy.pojo.entity.Pedigree;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DocumentDao extends IBaseDao<Document> {
    long getDocumentCountByParameter(Connection connection, ISqlParameter parameter) throws Exception;

    //获取所有家谱
    List<Document> getDocumentsByParameter(Connection connection, ISqlParameter parameter) throws Exception;

    void deleteByMemberId(Connection connection, Long memberId);
}
