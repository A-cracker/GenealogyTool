package com.genealogy.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import com.genealogy.pojo.entity.Member;
import com.genealogy.util.ConnectionUtil;

/**
 * 
 * @author zzb
 *
 */
public class MemberRelationDaoImpl {
	MemberDaoImpl memberDao = new MemberDaoImpl();
	RelationDaoImpl relationDaoImpl = new RelationDaoImpl();

	/**
	 *	把 sourceId 添加为 targetId 的某种关系
	 * @param sourceId
	 * @param targetId
	 * @param type 1: 父亲; 2.母亲; 3.配偶; 4.子女
	 * @param treeId
	 * @return
	 */
	public boolean insertRelation(long sourceId, long targetId, int type, long treeId) throws SQLException {
		//type 1 2 3 4
		//表中 type = 0 sourceId 为 targetId 的 父亲
		//表中 type = 2 代表sourceId 为 targetId 的 配偶
		boolean flag = true;
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement pstm = null;
		ResultSet resultSet = null;
		String sql = "insert into relation(u1,u2,type,treeId) values(?,?,?,?)";
		try{
			pstm = conn.prepareStatement(sql);
			if(type == 1){ //添加父亲
				if(relationDaoImpl.getMotherId(targetId,treeId) == null) {
					//关系添加
					pstm.setLong(1, sourceId);
					pstm.setLong(2, targetId);
					pstm.setInt(3, 0);
					pstm.setLong(4, treeId);
					pstm.executeUpdate();
				}else {
					//附属关系添加（该人的母亲节点存在，先给父亲添加配偶节点）
					String sql1 = "select * from relation where u2=? and type=1 and treeId = ?";
					PreparedStatement pstm_extra = conn.prepareStatement(sql1);
					pstm_extra.setLong(1, targetId);
					pstm_extra.setLong(2, treeId);
					resultSet = pstm_extra.executeQuery();
					if(resultSet.next()){
						long u1 = resultSet.getLong("u1");
						pstm_extra = conn.prepareStatement(sql);
						pstm_extra.setLong(1, sourceId);
						pstm_extra.setLong(2, u1);
						pstm_extra.setInt(3, 2);
						pstm_extra.setLong(4, treeId);
						pstm_extra.executeUpdate();
					}
					//添加的父亲是直系的
					if(relationDaoImpl.getFatherId(relationDaoImpl.getMotherId(targetId,treeId),treeId) == null && relationDaoImpl.getMotherId(relationDaoImpl.getMotherId(targetId,treeId),treeId) == null) {
						pstm.setLong(1, sourceId);
						pstm.setLong(2, targetId);
						pstm.setInt(3, 0);
						pstm.setLong(4, treeId);
						pstm.executeUpdate();
						String s = "delete from relation where u2=? and type = 1 and treeId = ?";
						pstm_extra = conn.prepareStatement(s);
						pstm_extra.setLong(1, targetId);
						pstm_extra.setLong(2, treeId);
						pstm_extra.executeUpdate();
					}
					pstm_extra.close();
				}
			}else if(type == 2){ //添加母亲
				if(relationDaoImpl.getFatherId(targetId,treeId) == null) {
					//关系添加
					pstm.setLong(1, sourceId);
					pstm.setLong(2, targetId);
					pstm.setInt(3, 1);
					pstm.setLong(4, treeId);
					pstm.executeUpdate();
				}else {
					//附属关系添加
					String sql2 = "select * from relation where u2=? and type = 0 and treeId = ?";
					PreparedStatement pstm_extra = conn.prepareStatement(sql2);
					pstm_extra.setLong(1, targetId);
					pstm_extra.setLong(2, treeId);
					resultSet = pstm_extra.executeQuery();
					if(resultSet.next()) {
						long u1 = resultSet.getLong("u1");
						pstm_extra = conn.prepareStatement(sql);
						pstm_extra.setLong(1, u1);
						pstm_extra.setLong(2, sourceId);
						pstm_extra.setInt(3, 2);
						pstm_extra.setLong(4, treeId);
						pstm_extra.executeUpdate();
					}
					pstm_extra.close();
				}
			}else if(type == 3){ //添加配偶
				Member member = memberDao.getMemberById(targetId);
				if(member.getGender() == Member.MAN) {
					pstm.setLong(1, targetId);
					pstm.setLong(2, sourceId);
				}else if(member.getGender() == Member.WOMAN) {
					pstm.setLong(1, sourceId);
					pstm.setLong(2, targetId);
				}
				pstm.setInt(3, 2);
				pstm.setLong(4, treeId);
				pstm.executeUpdate();
			}else{ //添加子女
				//pstm.setInt(1, targetId);
				//pstm.setInt(2, sourceId);
				Member member = memberDao.getMemberById(targetId);
				if(member.getGender() == Member.MAN){
					pstm.setLong(1, targetId);
					pstm.setLong(2, sourceId);
					pstm.setInt(3, 0);
					pstm.setLong(4, treeId);
					pstm.executeUpdate();
					List<Member> lists = this.getSpouses(targetId, Member.MAN,treeId);
					for(int i=0; i<lists.size(); i++){//每个配偶都作为其子女的母亲
						String sql3 = "insert into relation(u1,u2,type,treeId) values(?,?,?,?)";
						PreparedStatement pstm_extra = conn.prepareStatement(sql3);
						pstm_extra.setLong(1, lists.get(i).getId());
						pstm_extra.setLong(2, sourceId);
						pstm_extra.setInt(3, 1);
						pstm_extra.setLong(4, treeId);
						pstm_extra.executeUpdate();
					}
				}else if(member.getGender() == Member.WOMAN){
					pstm.setLong(1, targetId);
					pstm.setLong(2, sourceId);
					pstm.setInt(3, 1);
					pstm.setLong(4, treeId);
					pstm.executeUpdate();
					List<Member> lists = this.getSpouses(targetId, Member.WOMAN,treeId);
					if(lists.size()>0){//每个配偶都作为其子女的父亲
						String sql3 = "insert into relation(u1,u2,type,treeId) values(?,?,?,?)";
						PreparedStatement pstm_extra = conn.prepareStatement(sql3);
						pstm_extra.setLong(1, lists.get(0).getId());
						pstm_extra.setLong(2, sourceId);
						pstm_extra.setInt(3, 0);
						pstm_extra.setLong(4, treeId);
						pstm_extra.executeUpdate();
					}
				}
			}

		}catch(Exception e){
			flag = false;
			e.printStackTrace();
		}finally {
			if(resultSet != null) {
				resultSet.close();
			}
			if(pstm != null) {
				pstm.close();
			}
		}
		return flag;
	}
	//获取配偶
	public List<Member> getSpouses(Long id,Integer gender,Long treeId) throws Exception{
		List<Member> list = new ArrayList<Member>();
		Member member = null;
		String sql = null;
		if(gender == Member.MAN) {
			sql = "select member.* "
			    + "from (select u2 from relation where u1 = ? and type = 2 and treeId = ?) as A "
			    + "left join member on member.id = A.u2";
		}else if(gender == Member.WOMAN) {
			sql = "select member.* "
			    + "from (select u1 from relation where u2 = ? and type = 2 and treeId = ?) as A "
			    + "left join member on member.id = A.u1";
		}else {
			throw new Exception("参数有误");
		}
		Connection conn = ConnectionUtil.getConnection();

		try (PreparedStatement ps = conn.prepareStatement(sql);){
			ps.setLong(1, id);
			ps.setLong(2, treeId);

			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					member = new Member();
					member.setId(rs.getLong("id"));
					member.setName(rs.getString("name"));
					member.setGender(rs.getInt("gender"));
					member.setBrithdate(rs.getDate("birthdate"));
					member.setBirthplace(rs.getString("birthplace"));
					member.setRestplace(rs.getString("restplace"));
					member.setIs_alive(rs.getInt("is_alive"));
					member.setDescription(rs.getString("description"));
					member.setGeneration(rs.getString("generation"));
					member.setIdentityId(rs.getString("identityId"));
					member.setResidence(rs.getString("residence"));
					member.setPhoneNumber(rs.getString("phoneNumber"));
					list.add(member);
				}
			}
		}


		return list;
	}
	
}
