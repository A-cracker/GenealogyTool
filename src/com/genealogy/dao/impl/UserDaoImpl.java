package com.genealogy.dao.impl;

import com.genealogy.pojo.entity.Member;
import com.genealogy.pojo.entity.User;
import com.genealogy.util.ConnectionUtil;
import com.mysql.jdbc.Statement;

import java.sql.*;

public class UserDaoImpl {
    //增加亲网用户
    public int insertUser(User user) throws SQLException {
        int autoInckey = -1;
        String sql = "insert into user(name,gender,birthdate,birthplace,description,img,generation,identityId,residence,phoneNumber, id) "
                + "values(?,?,?,?,?,?,?,?,?,?, ?)";
        Connection conn = ConnectionUtil.getConnection();
        //使用PreparedStatement传入带占位符的sql语句
        PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        //传参
        pstm.setString(1, user.getName());
        pstm.setInt(2, user.getGender());
        if(!(user.getBrithdate() == null)){
            pstm.setDate(3, user.getBrithdate());
        }else{
            pstm.setDate(3, null);
        }
        pstm.setString(4, user.getBirthplace());
        pstm.setString(5, user.getDescription());
        pstm.setBytes(6, user.getImg());
        pstm.setString(7, user.getGeneration());
        pstm.setString(8, user.getIdentityId());
        pstm.setString(9, user.getResidence());
        pstm.setString(10, user.getPhoneNumber());
        pstm.setLong(11, user.getId());
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

    public User getUserByIdWithImg(Long id) throws SQLException {
        User user = null;
        Connection conn = ConnectionUtil.getConnection();
        String sql = "select * from user where id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setGender(rs.getInt("gender"));
            user.setBrithdate(rs.getDate("birthdate"));
            user.setBirthplace(rs.getString("birthplace"));
            user.setDescription(rs.getString("description"));
            user.setGeneration(rs.getString("generation"));
            user.setImg(rs.getBytes("img"));
            user.setIdentityId(rs.getString("identityId"));
            user.setResidence(rs.getString("residence"));
            user.setPhoneNumber(rs.getString("phoneNumber"));
        }
        rs.close();
        ps.close();
        return user;
    }
    public Boolean updateAvatar(Integer id,byte[] img) throws SQLException {
        Boolean flag = false;
        String sql = "update user set img = ? where id=?";
        if(img!=null) {
            try {
                Connection conn = ConnectionUtil.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql);
                pstm.setBytes(1, img);
                pstm.setInt(2, id);
                int i = pstm.executeUpdate();
                pstm.close();
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }
}
