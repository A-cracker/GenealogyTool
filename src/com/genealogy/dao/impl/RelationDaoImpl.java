package com.genealogy.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.genealogy.pojo.entity.Member;
import com.genealogy.pojo.entity.Relation;
import com.genealogy.util.ConnectionUtil;
import com.mysql.jdbc.Statement;

/**
 * 
 * @author zzb
 *
 */
public class RelationDaoImpl {
	public int insertRelation(Relation relation) throws SQLException {
		int autoInckey = -1;
		String sql = "insert into relation(u1,u2,type,treeId) "
				+ "values(?,?,?,?)";
		Connection conn = ConnectionUtil.getConnection();
		//使用PreparedStatement传入带占位符的sql语句
		PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		//传参
		pstm.setLong(1, relation.getU1());
		pstm.setLong(2, relation.getU2());
		pstm.setInt(3, relation.getType());
		pstm.setLong(4, relation.getTreeId());
		//执行更新操作
		pstm.executeUpdate();
		//
		ResultSet rs = pstm.getGeneratedKeys();
		if(rs.next()){
			autoInckey = rs.getInt(1);
		}
		rs.close();
		pstm.close();
		return autoInckey;
	}

	public Integer countMarriageRelation(Long id,Integer gender,Long treeId) throws SQLException {
		Integer count = 0;
		String sql = null;
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement ps = null;
		if(gender == Member.MAN) {
			sql = "select count(*) from relation where u1 = ? and treeId = ?";
			ps = conn.prepareStatement(sql);
			ps.setLong(1 , id);
			ps.setLong(2 , treeId);
		}else {
			sql = "select count(*) from relation where u1 = ? and type = 1 or(u2 = ? and type = 2) and treeId = ?";
			ps = conn.prepareStatement(sql);
			ps.setLong(1 , id);
			ps.setLong(2 , id);
			ps.setLong(3 , treeId);
		}
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		ps.close();
		return count;
	}
	
	/**
	 * 查找成员的子女(适用于成员为男性时)
	 * @param id
	 * @return
	 * @throws SQLException 
	 */
	public Set<Long> getChildrenIds(Long id,Long treeId) throws SQLException{
		Set<Long> userIds = new LinkedHashSet<>();

		String sql = "select u2 from relation where u1 = ? and type = 0 and treeId = ?";
		Connection conn = ConnectionUtil.getConnection();
		try(PreparedStatement ps = conn.prepareStatement(sql);){
			ps.setLong(1, id);
			ps.setLong(2, treeId);

			try(ResultSet rs = ps.executeQuery();){
				while(rs.next()) {
					userIds.add(rs.getLong("u2"));
				}
			}
		}

		//找与配偶建立了母子关系的孩子
		String sql2 = "select u2 from relation where u1 in (select u2 from relation where u1=? and type=2 and treeId = ?) and type=1 and treeId = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql2);){
			ps.setLong(1, id);
			ps.setLong(2, treeId);
			ps.setLong(3, treeId);

			try(ResultSet rs = ps.executeQuery();){
				while(rs.next()){
					userIds.add(rs.getLong("u2"));
				}
			}
		}

		return userIds;
	}
	
	/**
	 * 查找成员的子女(适用于成员为女性时)
	 * @param id
	 * @param spouses
	 * @return
	 * @throws SQLException 
	 */
	public Set<Long> getChildrenIds(Long id,List<Member> spouses,Long treeId) throws SQLException{
		Set<Long> list = new LinkedHashSet<>();
		String sql = "select distinct u2 from relation where treeId=? and u1 = ? and type =1";
		if(spouses!=null&&spouses.size()>0) {
			StringBuilder sb = new StringBuilder(sql);
			sb.append(" or (type = 0 and u1 in(");
			for (int i = 0; i < spouses.size(); i++) {
				sb.append( " ?");
				if (i != spouses.size() - 1) {
					sb.append( ",");
				}
			}
			sb.append("))");
			sql = sb.toString();
		}
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, treeId);
		ps.setLong(2, id);
		if(spouses!=null&&spouses.size()>0) {
			for(int i =0;i<spouses.size();i++) {
				ps.setLong(i+3, spouses.get(i).getId());
			}
		}
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			list.add(rs.getLong("u2"));
		}
		rs.close();
		ps.close();
		return list;
	}
	
	//是否有孩子
	public boolean hasChildren(Long id) throws Exception{
		boolean flag = false;
		String sql = "select u2 from relation where u1=? and (type=0 or type=1)";
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setLong(1, id);
		ResultSet rs = pst.executeQuery();
		if(rs.next()) {
			flag = true;
		}
		rs.close();
		pst.close();
		return flag;
	}
	
	//查找父亲id
	public Long getFatherId(Long id,Long treeId) throws SQLException {
		Long fatherId = null;
		String sql = "select u1 from relation where u2=? and type =0 and treeId = ?";
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, id);
		ps.setLong(2, treeId);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			fatherId = rs.getLong("u1");
		}
		rs.close();
		ps.close();
		return fatherId;
	}
	
	//查找母亲id
		public Long getMotherId(Long id,Long treeId) throws SQLException {
			Long motherId = null;
			String sql = "select u1 from relation where u2=? and type =1 and treeId = ?";
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, id);
			ps.setLong(2, treeId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				motherId = rs.getLong("u1");
			}
			rs.close();
			ps.close();
			return motherId;
		}
	
	public List<Relation> getRelations() throws SQLException{
		List<Relation> list = new ArrayList<Relation>();
		String sql = "select * from relation";
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		Relation relation = null;
		while(rs.next()) {
			relation = new Relation();
			relation.setId(rs.getInt("id"));
			relation.setU1(rs.getLong("u1"));
			relation.setU2(rs.getLong("u2"));
			relation.setType(rs.getInt("type"));
			list.add(relation);
		}
		return list;
	}

	public List<Relation> getRelationsByTreeId(Long treeId) throws SQLException{
		List<Relation> list = new ArrayList<Relation>();
		String sql = "select * from relation where treeId = ?";
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, treeId);
		ResultSet rs = ps.executeQuery();
		Relation relation = null;
		while(rs.next()) {
			relation = new Relation();
			relation.setU1(rs.getLong("u1"));
			relation.setU2(rs.getLong("u2"));
			relation.setType(rs.getInt("type"));
			relation.setTreeId(rs.getLong("treeId"));
			list.add(relation);
		}
		return list;
	}
	
	public Relation getRelationByFK(Long fid1,Long fid2) throws SQLException{
		Relation relation = new Relation();
		String sql = "select * from relation where (u1 = ? and u2 = ?) or (u1 = ? and u2 = ?)";
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, fid1);
		ps.setLong(2, fid2);
		ps.setLong(3, fid2);
		ps.setLong(4, fid1);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			relation.setId(rs.getInt("id"));
			relation.setU1(rs.getLong("u1"));
			relation.setU2(rs.getLong("u2"));
			relation.setType(rs.getInt("type"));
		}
		return relation;
	}


}
