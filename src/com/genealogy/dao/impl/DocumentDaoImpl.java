package com.genealogy.dao.impl;

import com.fy.sqlparam.map.ISqlMapResult;
import com.fy.sqlparam.map.config.FieldMapMeta;
import com.fy.sqlparam.map.config.MapMetaConfig;
import com.fy.sqlparam.param.ISqlParameter;
import com.fy.toolhelper.db.BaseDaoImpl;
import com.genealogy.dao.DocumentDao;
import com.genealogy.dao.PedigreeDao;
import com.genealogy.pojo.entity.Document;
import com.genealogy.pojo.entity.Pedigree;
import com.genealogy.util.ConnectionUtil;
import com.mysql.jdbc.Statement;

import java.sql.*;
import java.util.List;

public class DocumentDaoImpl extends BaseDaoImpl<Document> implements DocumentDao {
    @MapMetaConfig(baseTables = "FROM document d",
            queryFields = {
                    @FieldMapMeta(name = "id", value = "d.id"),
                    @FieldMapMeta(name = "name", value = "d.name"),
                    @FieldMapMeta(name = "createTime", value = "d.create_time"),
                    @FieldMapMeta(name = "memberId", value = "d.member_id"),
            }
    )
    public DocumentDaoImpl() throws Exception {
    }


    @Override
    public long getDocumentCountByParameter(Connection connection, ISqlParameter parameter) throws Exception {
        final String sql = "SELECT COUNT(1)" + PH_BASE_TABLES
                + PH_DYNAMIC_JOIN_TABLES
                + "WHERE"
                + PH_CONDITIONS
                + PH_ORDER_BY + PH_LIMIT;
        final ISqlMapResult mapResult = this.formatSql(sql, parameter);
        return this.getCountBySql(connection, mapResult.getSql(), mapResult.getArgObjs());
    }
    //获取所有家谱
    @Override
    public List<Document> getDocumentsByParameter(Connection connection, ISqlParameter parameter) throws Exception {
        final String sql = "SELECT d.*" + PH_BASE_TABLES
                + PH_DYNAMIC_JOIN_TABLES
                + "WHERE"
                + PH_CONDITIONS
                + PH_ORDER_BY + PH_LIMIT;
        final ISqlMapResult mapResult = this.formatSql(sql, parameter);
        return this.listEntitiesBySql(connection, mapResult.getSql(), "d", mapResult.getArgObjs());
    }

    @Override
    public void deleteByMemberId(Connection connection, Long memberId) {
        String sql = "delete from document where member_id = ? ";
        try(PreparedStatement pstm = connection.prepareStatement(sql)){
            pstm.setLong(1, memberId);
            int i = pstm.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
