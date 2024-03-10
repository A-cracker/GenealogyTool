package com.genealogy.dao;

import com.fy.sqlparam.param.ISqlParameter;
import com.fy.toolhelper.db.IBaseDao;
import com.genealogy.pojo.entity.Pedigree;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PedigreeDao extends IBaseDao<Pedigree> {
    //新增家谱
    long insertPedigree(Pedigree pedigree) throws SQLException;

    long getPedigreeCountByParameter(Connection connection, ISqlParameter parameter) throws Exception;

    //获取所有家谱
    List<Pedigree> getPedigrees(Connection connection, ISqlParameter parameter) throws Exception;
}
