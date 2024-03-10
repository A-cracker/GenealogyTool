package com.genealogy.dao.impl;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.fy.basejar.exception.ToolException;
import com.genealogy.constant.DateType;
import com.genealogy.pojo.entity.Member;
import com.genealogy.util.ConnectionUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.mysql.jdbc.Statement;

/**
 * 
 * @author 
 *
 */
public class MemberDaoImpl {
	
	//添加成员
	public Long insertMember(Member member) throws SQLException{
				String sql = "insert into member(name,gender,birthdate,birthplace,restplace,is_alive,description,img," +
						"generation,identityId,residence,phoneNumber,deathDate,album_id,id,birthdate_type,deathdate_type" +
						",lunar_birthdate,lunar_deathdate,creator_id, create_time) "
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				Connection conn = ConnectionUtil.getConnection();
				//使用PreparedStatement传入带占位符的sql语句
				PreparedStatement pstm = conn.prepareStatement(sql);
				//传参
				pstm.setString(1, member.getName());
				pstm.setInt(2, member.getGender());
				if(!(member.getBrithdate() == null)){
					pstm.setDate(3, java.sql.Date.valueOf(member.getBrithdate().toString()));
				}else{
					pstm.setDate(3, null);
				}
				pstm.setString(4, member.getBirthplace());
				pstm.setString(5, member.getRestplace());
				pstm.setInt(6, member.getIs_alive());
				pstm.setString(7, member.getDescription());
				pstm.setString(8, member.getImg());
				pstm.setString(9, member.getGeneration());
				pstm.setString(10, member.getIdentityId());
				pstm.setString(11, member.getResidence());
				pstm.setString(12, member.getPhoneNumber());
				if(!(member.getDeathDate() == null)){
					pstm.setDate(13, java.sql.Date.valueOf(member.getDeathDate().toString()));
				}else{
					pstm.setDate(13, null);
				}
				pstm.setLong(14, Optional.ofNullable(member.getAlbumId()).orElse(0L));
				pstm.setLong(15, member.getId());
				pstm.setInt(16, member.getBirthDateType().value());
				pstm.setInt(17, member.getDeathDateType().value());
				pstm.setString(18, member.getLunarBirthDate());
				pstm.setString(19, member.getLunarDeathDate());
				pstm.setLong(20, member.getCreatorId());
//				pstm.setDate(21, member.getCreateTime().);
				pstm.setTimestamp(21, new Timestamp(member.getCreateTime().getTime()));
				//执行更新操作
				pstm.executeUpdate();
				pstm.close();

				return member.getId();
		}
	//获取所有成员
	public List<Member> getMembers() throws SQLException{
		List<Member> list = new ArrayList<Member>();
		Member member = null;
		Connection conn = ConnectionUtil.getConnection();
		String sql = "select * from member";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
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
		rs.close();
		ps.close();
		return list;
	}
	//通过Id获取唯一成员
	public Member getMemberById(Long id) throws SQLException {
		Member member = new Member();
		Connection conn = ConnectionUtil.getConnection();
		String sql = "select id,name,gender,birthdate,birthplace,restplace,is_alive,description,generation,identityId,residence,phoneNumber,creator_id from member where id = ?";
		try(PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setLong(1, id);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
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
					member.setCreatorId(rs.getLong("creator_id"));
				}
			}
		}

		return member;
	}
	//通过Id获取带有头像的成员
	public Member getMemberByIdWithImg(Long id, boolean handlePhone) throws SQLException {
		if (Objects.isNull(id)){
			throw new ToolException("id不能为空");
		}
		Member member = null;
		Connection conn = ConnectionUtil.getConnection();
		String sql = "select * from member where id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			member = new Member();
			member.setId(id);
			member.setName(rs.getString("name"));
			member.setGender(rs.getInt("gender"));
			member.setBrithdate(rs.getDate("birthdate"));
			member.setDeathDate(rs.getDate("deathDate"));
			member.setBirthplace(rs.getString("birthplace"));
			member.setRestplace(rs.getString("restplace"));
			member.setIs_alive(rs.getInt("is_alive"));
			member.setDescription(rs.getString("description"));
			member.setGeneration(rs.getString("generation"));
			member.setImg(rs.getString("img"));
			member.setIdentityId(rs.getString("identityId"));
			member.setResidence(rs.getString("residence"));
			member.setPhoneNumber(rs.getString("phoneNumber"));
			member.setOpenIdentity(rs.getInt("open_identity"));
			member.setOpenPhone(rs.getInt("open_phone"));
			member.setAlbumId(rs.getLong("album_id"));
			member.setBirthDateType(DateType.valueOf(rs.getInt("birthdate_type")));
			member.setDeathDateType(DateType.valueOf(rs.getInt("deathdate_type")));
			member.setLunarBirthDate(rs.getString("lunar_birthdate"));
			member.setLunarDeathDate(rs.getString("lunar_deathdate"));

			if (handlePhone){
				if (member.getOpenPhone() == 2){
					member.setPhoneNumber("");
				}else if(member.getOpenPhone() == 1 && member.getPhoneNumber() != null && member.getPhoneNumber().length() == 11){
					String phone = member.getPhoneNumber();
					String protectPhone = "*******" + phone.substring(7);
					member.setPhoneNumber(protectPhone);
				}

				if (member.getOpenIdentity() == 2){
					member.setIdentityId("");
				}else if(member.getOpenIdentity() == 1 && member.getIdentityId() != null && member.getIdentityId().length() == 18){
					String identityId = member.getIdentityId();
					String protectIdentityId = "**************" + identityId.substring(identityId.length() - 4);
					member.setIdentityId(protectIdentityId);
				}
			}

		}
		rs.close();
		ps.close();
		return member;
	}
	//返回所有的人数
	public Integer countMembers() throws SQLException {
		Integer count = 0;
		Connection conn = ConnectionUtil.getConnection();
		String sql = "select count(*) from member";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		ps.close();
		return count;
	}
	//通过Id仅返回性别
	public Integer getMemberGenderById(Long id) throws SQLException {
		Member member = new Member();
		Connection conn = ConnectionUtil.getConnection();
		String sql = "select gender from member where id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			member.setGender(rs.getInt("gender"));
		}
		rs.close();
		ps.close();
		return member.getGender();
	}
	//插入用户成员
	public Integer insertUser(Member member) throws SQLException{
		int autoInckey = -1;
		String sql = "insert into member(name,gender,birthdate,birthplace,restplace,is_alive,description,img,generation,identityId,residence,phoneNumber,isUser) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = ConnectionUtil.getConnection();
		//使用PreparedStatement传入带占位符的sql语句
		PreparedStatement pstm = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		//传参
		pstm.setString(1, member.getName());
		pstm.setInt(2, member.getGender());
		if(!(member.getBrithdate() == null)){
			pstm.setDate(3, java.sql.Date.valueOf(member.getBrithdate().toString()));
		}else{
			pstm.setDate(3, null);
		}
		pstm.setString(4, member.getBirthplace());
		pstm.setString(5, member.getRestplace());
		pstm.setInt(6, member.getIs_alive());
		pstm.setString(7, member.getDescription());
		pstm.setString(8, member.getImg());
		pstm.setString(9, member.getGeneration());
		pstm.setString(10, member.getIdentityId());
		pstm.setString(11, member.getResidence());
		pstm.setString(12, member.getPhoneNumber());
		pstm.setInt(13, 1);
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
	//返回用户成员
	public Integer getUser() throws SQLException {
		Integer id = 0;
		Connection conn = ConnectionUtil.getConnection();
		String sql = "select id from member where isUser = 1";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			id =rs.getInt("id");
		}
		rs.close();
		ps.close();
		return id;
	}
	//返回当日生辰或忌辰的成员
	public List<Member> getMemberByBirth() throws SQLException{
		List<Member> list = new ArrayList<Member>();
		Member member = null;
		Connection conn = ConnectionUtil.getConnection();
		String sql = "select * from member where MONTH(birthdate) = ? and DAY(birthdate) = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		ps.setInt(1, calendar.get(Calendar.MONTH)+1);
		ps.setInt(2, calendar.get(Calendar.DATE));
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
				member = new Member();
				member.setId(rs.getLong("id"));
				member.setName(rs.getString("name"));
				list.add(member);
		}
		rs.close();
		ps.close();
		return list;
	}
	public List<Member> getMemberByDeath() throws SQLException{
		List<Member> list = new ArrayList<Member>();
		Member member = null;
		Connection conn = ConnectionUtil.getConnection();
		String sql = "select * from member where MONTH(deathDate) = ? and DAY(deathDate) = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		ps.setInt(1, calendar.get(Calendar.MONTH)+1);
		ps.setInt(2, calendar.get(Calendar.DATE));
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			member = new Member();
			member.setId(rs.getLong("id"));
			member.setName(rs.getString("name"));
			list.add(member);
		}
		rs.close();
		ps.close();
		return list;
	}
}
