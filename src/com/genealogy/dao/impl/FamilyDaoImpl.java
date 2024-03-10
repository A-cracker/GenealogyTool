package com.genealogy.dao.impl;

import com.genealogy.constant.DateType;
import com.genealogy.pojo.entity.Member;
import com.genealogy.util.ConnectionUtil;
import com.mysql.jdbc.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FamilyDaoImpl {
    //新增某棵子树中的家族成员
    public int insertMemberToFamily(long treeId, long memberId) throws SQLException {
        int autoInckey = -1;
        String sql = "insert into family(treeId,memberId) "
                + "values(?,?)";
        Connection conn = ConnectionUtil.getConnection();
        PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstm.setLong(1, treeId);
        pstm.setLong(2, memberId);
        pstm.executeUpdate();
        ResultSet rs = pstm.getGeneratedKeys();
        if(rs.next()){
            autoInckey = rs.getInt(1);
        }
        rs.close();
        pstm.close();
        return autoInckey;
    }
    //删除某棵子树中的家庭成员
    public boolean deleteMemberFromFamily(long treeId, long memberId) {
        boolean flag = false;
        String sql = "delete from family where treeId = ? and memberId = ?";
        try{
            Connection conn = ConnectionUtil.getConnection();
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setLong(1, treeId);
            pstm.setLong(2, memberId);
            int i = pstm.executeUpdate();
            flag = i>0 ? true : false;
            pstm.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }
    //返回某棵子树中的所有家族成员
    public List<Member> getMembersByTreeId(long treeId) throws SQLException{
        List<Member> list = new ArrayList<Member>();
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select m.* from member m left join family f on f.memberId = m.id where f.treeId = ? order by m.id";
       try(PreparedStatement ps = conn.prepareStatement(sql)) {
           ps.setLong(1, treeId);

           try (ResultSet rs = ps.executeQuery()){

               while(rs.next()) {
                   Member member = new Member();
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
                   member.setLunarDeathDate(rs.getString("lunar_deathdate"));
                   member.setLunarBirthDate(rs.getString("lunar_birthdate"));
                   member.setDeathDateType(DateType.valueOf(rs.getInt("deathdate_type")));
                   member.setBirthDateType(DateType.valueOf(rs.getInt("birthdate_type")));
                   member.setDeathDate(rs.getDate("deathDate"));
                   list.add(member);
               }
           }

       }

        return list;
    }
    //返回某棵子树中的所有家族成员Id
    public List<Long> getMemberIdsByTreeId(long treeId) throws SQLException{
        List<Long> list = new ArrayList<>();
        Member member = null;
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select memberId from family where treeId = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, treeId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            list.add(rs.getLong("memberId"));
        }
        rs.close();
        ps.close();
        return list;
    }
    //更新成员所属家族树
    public boolean updateFamily(int sourceTreeId,int targetTreeId) {
        boolean flag = false;
        String sql = "update family set treeId = ? where treeId = ?";
        try{
            Connection conn = ConnectionUtil.getConnection();
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setInt(1, targetTreeId);
            pstm.setInt(2, sourceTreeId);
            int i = pstm.executeUpdate();
            flag = i>0 ? true : false;
            pstm.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }
    //获取某棵家族树中所有MemberId
    public List<Long> getMembersIdByTreeId(long treeId) throws SQLException{
        List<Long> list = new ArrayList<>();
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select memberId from family where treeId = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, treeId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            list.add(rs.getLong("memberId"));
        }
        rs.close();
        ps.close();
        return list;
    }
    //判断某个成员是否有家谱
    public boolean hasFamily(long memberId){
        boolean flag = false;
        String sql = "select memberId from family where memberId = ?";
        try{
            Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, memberId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                flag=true;
            }
            rs.close();
            ps.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }
}
