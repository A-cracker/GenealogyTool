package com.genealogy.dao.impl;

import com.fy.sqlparam.map.ISqlMapResult;
import com.fy.sqlparam.map.config.FieldMapMeta;
import com.fy.sqlparam.map.config.MapMetaConfig;
import com.fy.sqlparam.param.ISqlParameter;
import com.fy.toolhelper.db.BaseDaoImpl;
import com.genealogy.dao.PedigreeDao;
import com.genealogy.pojo.entity.Pedigree;
import com.genealogy.util.ConnectionUtil;
import com.mysql.jdbc.Statement;

import java.sql.*;
import java.util.List;

public class PedigreeDaoImpl extends BaseDaoImpl<Pedigree> implements PedigreeDao {
    @MapMetaConfig(baseTables = "FROM pedigree p",
            queryFields = {
                    @FieldMapMeta(name = "id", value = "p.id"),
                    @FieldMapMeta(name = "name", value = "p.name"),
                    @FieldMapMeta(name = "createTime", value = "p.create_time"),
            }
    )
    public PedigreeDaoImpl() throws Exception {
    }

    //新增家谱
    @Override
    public long insertPedigree(Pedigree pedigree) throws SQLException {
        int autoInckey = -1;
        String sql = "insert into pedigree(doisser,faction,branch,name,originplace,description, create_time, creator_id, id) "
                + "values(?,?,?,?,?,?,?,?,?)";
        Connection conn = ConnectionUtil.getConnection();
        //使用PreparedStatement传入带占位符的sql语句
        PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        //传参
        pstm.setString(1, pedigree.getDossier());
        pstm.setString(2, pedigree.getFaction());
        pstm.setString(3, pedigree.getBranch());
        pstm.setString(4, pedigree.getName());
        pstm.setString(5, pedigree.getOriginPlace());
        pstm.setString(6, pedigree.getDescription());
        pstm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
        pstm.setLong(8, pedigree.getCreatorId());
        pstm.setLong(9, pedigree.getId());
        //执行更新操作
        pstm.executeUpdate();

        pstm.close();
        return pedigree.getId();
    }

    @Override
    public long getPedigreeCountByParameter(Connection connection, ISqlParameter parameter) throws Exception {
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
    public List<Pedigree> getPedigrees(Connection connection, ISqlParameter parameter) throws Exception {
        final String sql = "SELECT p.*" + PH_BASE_TABLES
                + PH_DYNAMIC_JOIN_TABLES
                + "WHERE"
                + PH_CONDITIONS
                + PH_ORDER_BY + PH_LIMIT;
        final ISqlMapResult mapResult = this.formatSql(sql, parameter);
        return this.listEntitiesBySql(connection, mapResult.getSql(), "p", mapResult.getArgObjs());
    }
    //根据Id获取家谱
    public Pedigree getPedigreeById(Long id) throws SQLException {
        Pedigree pedigree = new Pedigree();
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from pedigree where id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            pedigree.setId(rs.getLong("id"));
            pedigree.setName(rs.getString("name"));
            pedigree.setDossier(rs.getString("doisser"));
            pedigree.setFaction(rs.getString("faction"));
            pedigree.setBranch(rs.getString("branch"));
            pedigree.setOriginPlace(rs.getString("originPlace"));
            pedigree.setDescription(rs.getString("description"));
            pedigree.setCreatorId(rs.getLong("creator_id"));
        }
        rs.close();
        ps.close();
        return pedigree;
    }
    //获取最新的家谱id
    public long getLatestTreeId() throws SQLException{
        long treeId = 0;
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from pedigree order by id DESC LIMIT 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            treeId=rs.getLong("id");
        }
        rs.close();
        ps.close();
        return treeId;
    }
    //删除家谱
    public boolean deletePedigree(long treeId) throws SQLException{
        boolean flag = false;
        String sql = "delete from pedigree where id= ?";
        try{
            Connection conn = ConnectionUtil.getConnection();
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setLong(1, treeId);
            int i = pstm.executeUpdate();
            flag = i>0 ? true : false;
            pstm.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }
}
