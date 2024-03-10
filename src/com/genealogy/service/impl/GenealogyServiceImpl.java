package com.genealogy.service.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fy.sqlparam.impl.SqlParameter;
import com.fy.sqlparam.param.ISqlParameter;
import com.fy.sqlparam.param.ISqlQuery;
import com.fy.wetoband.tool.Tool;
import com.genealogy.constant.DateType;
import com.genealogy.dao.DocumentDao;
import com.genealogy.dao.impl.*;
import com.genealogy.pojo.entity.Document;
import com.genealogy.pojo.entity.Pedigree;
import com.github.yitter.idgen.YitIdHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import com.genealogy.pojo.dto.Link;
import com.genealogy.pojo.entity.Member;
import com.genealogy.pojo.entity.Relation;
import com.genealogy.util.ConnectionUtil;
import com.genealogy.util.GenderConverter;
import com.genealogy.util.LinkConverter;
import com.genealogy.util.PathUtil;
import org.springframework.util.StringUtils;


/**
 * @author zzb
 */
public class GenealogyServiceImpl {

    private MemberDaoImpl memberDao = new MemberDaoImpl();
    private MemberRelationDaoImpl memberRelationDao = new MemberRelationDaoImpl();
    private RelationDaoImpl relationDao = new RelationDaoImpl();
    private PedigreeDaoImpl pedigreeDao = new PedigreeDaoImpl();
    private FamilyDaoImpl familyDao = new FamilyDaoImpl();

    private Tool tool;

    public GenealogyServiceImpl(Tool tool) throws Exception {
        this.tool = tool;
    }


    /**
     * 新增帮语需要的接口
     *
     * @return
     * @throws Exception
     */
    //获取成员女儿
    public List<Member> getDaughter(long memberID) throws Exception {
        List<Member> daughterList = new ArrayList<>();
        String sql1 = "select * from relation where u1=? and type = 0";
        String sql2 = "select * from member where id=? and gender = 0";
        Connection conn = ConnectionUtil.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql1);
        statement.setLong(1, memberID);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int u2 = resultSet.getInt("u2");
            statement = conn.prepareStatement(sql2);
            statement.setInt(1, u2);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Member m = new Member();
                m.setId(rs.getLong("id"));
                m.setName(rs.getString("name"));
                m.setBrithdate(rs.getDate("birthdate"));
                daughterList.add(m);
            }
            rs.close();
        }
        resultSet.close();
        statement.close();
        return daughterList;
    }

    //获取树的祖宗
    public JSONObject getAncsetorByTreeId(long treeId) throws Exception {
        JSONObject jo = new JSONObject();
        List<Member> members = this.familyDao.getMembersByTreeId(treeId);
        for (Member member : members) {
            if (relationDao.getMotherId(member.getId(), treeId) == null && relationDao.getFatherId(member.getId(), treeId) == null) {//如果无父无母
                List<Member> spouses = memberRelationDao.getSpouses(member.getId(), member.getGender(), treeId);
                if (spouses.isEmpty()) {//如果没有配偶
                    jo.put("id", member.getId());
                    break;
                } else {
                    for (Member spouse : spouses) {//如果有配偶
                        if (relationDao.getMotherId(spouse.getId(), treeId) == null &&
                                relationDao.getFatherId(spouse.getId(), treeId) == null &&
                                member.getGender() == Member.MAN ) {
                                jo.put("id", member.getId());
                                break;
                        }
                    }
                }
            }
        }
        return jo;
    }

    //获取成员的父亲
    public Member getFather(long memberID) throws Exception {
        Member member = null;
        String sql = "select * from relation where u2=? and type = 0";
        Connection conn = ConnectionUtil.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setLong(1, memberID);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            member = memberDao.getMemberById(resultSet.getLong("u1"));
        }
        resultSet.close();
        statement.close();
        return member;
    }

    //获取直系成员祖宗
    public List<Member> getAncestors(long memberID) throws Exception {
        List<Member> list = new ArrayList<>();
        Member member = this.getFather(memberID);
        if (member != null) {
            member = this.getFather(member.getId());
            if (member != null) {
                member = this.getFather(member.getId());
                while (member != null) {
                    list.add(member);
                    member = this.getFather(member.getId());
                }
            }
        }
        return list;
    }

    //获取表单信息
    public Map<String, Object> getParams(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> returnResult = new HashMap<String, Object>();//返回给调用者的处理结果
        Map<String, Object> params = new HashMap<String, Object>();//用于封装请求参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        if (ServletFileUpload.isMultipartContent(request)) {
            //表单类型
            List<FileItem> list = upload.parseRequest(request);
            if (list == null || list.size() == 0) {
                returnResult.put("result", -1);
                returnResult.put("msg", "文件内容不能为空！");
                //this.sendResponse(response, returnResult);
                return null;
            }
            int i = 0;//用于记录接收到的文件个数
            for (FileItem item : list) {

                //如果不是表单对象，则是文件对象， 获取文件流
                if (!item.isFormField()) {
                    if (i > 0) {
                        returnResult.put("result", -1);
                        returnResult.put("msg", "该次请求包含多个文件，不允许一次传送多个文件！");
                        //this.sendResponse(response, returnResult);
                        return null;
                    }
                    if (item.getName().equals("")) {
                        params.put("img", null);
                    } else {
                        InputStream inputStream = item.getInputStream();
                        params.put("img", inputStream);
                    }
                    i++;
                    //如果是表单对象，则获取相关参数
                } else {
                    String name = item.getFieldName();
                    String value;
                    value = new String(item.getString().getBytes("iso-8859-1"), "utf-8");
                    params.put(name, value);
                }
            }
        }
        return params;
    }

    //插入成员事务
    public Member insertTransaction(Map<String, Object> maps, long treeId) throws Exception {
        Connection conn = ConnectionUtil.getConnection();

        DateType birthdateType = DateType.valueOf((String)maps.get("birthDateType"));
        DateType deathdateType = DateType.valueOf((String)maps.get("deathDateType"));

        int type = (int) maps.get("type");
        long targetId = (long) maps.get("targetId");
        String sql1 = "select * from relation where u2=? and type=? and treeId = ?";
        if (type == 1 || type == 2) {//点击添加父亲
            try (PreparedStatement statement = conn.prepareStatement(sql1)) {
                statement.setLong(1, targetId);
                statement.setInt(2, type - 1);
                statement.setLong(3, treeId);

                try (ResultSet resultSet = statement.executeQuery();) {
                    //如果已经有父亲
                    if (resultSet.next()) {
                        return null;
                    }
                }
            }
        }

        Member member = new Member();
        member.setId(YitIdHelper.nextId());
        member.setName((String) maps.get("name"));
        member.setGender(Integer.parseInt((String) maps.get("gender")));

        Object birthDate = maps.get("birthdate");
        if (DateType.DATE == birthdateType && !StringUtils.isEmpty(birthDate) ){
            member.setBrithdate(java.sql.Date.valueOf((String) birthDate));
        }else if (DateType.DYNASTY == birthdateType && !StringUtils.isEmpty(birthDate)){
            member.setLunarDeathDate((String) birthDate);
        }

        Object deathDate = maps.get("deathDate");
        if (DateType.DATE == deathdateType && !StringUtils.isEmpty(deathDate)) {
            member.setDeathDate(java.sql.Date.valueOf((String) deathDate));
        } else if (DateType.DYNASTY == deathdateType && !StringUtils.isEmpty(deathDate)){
            member.setLunarDeathDate((String) deathDate);
        }
        member.setBirthplace((String) maps.get("birthplace"));
        member.setRestplace((String) maps.get("restplace"));
        member.setIs_alive((Integer) maps.get("is_alive"));
        member.setDescription((String) maps.get("description"));
        member.setDescription((String) maps.get("img"));

        member.setGeneration((String) maps.get("generation"));
        member.setIdentityId((String) maps.get("identityId"));
        member.setResidence((String) maps.get("residence"));
        member.setPhoneNumber((String) maps.get("phoneNumber"));
        member.setAlbumId((Long) maps.get("albumId"));
        member.setBirthDateType(birthdateType);
        member.setDeathDateType(deathdateType);
        member.setCreatorId(tool.getUserID());
        member.setCreateTime(new Date());
        try {
            conn.setAutoCommit(false);
            //插入成员
            long autoInckey = memberDao.insertMember(member);
            updateDocuments(member.getId(), (String) maps.get("documents"));
            familyDao.insertMemberToFamily(treeId, autoInckey);
            if (memberRelationDao.insertRelation(autoInckey, targetId, type, treeId)) {
                conn.commit();
            }
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        }
        return member;
    }

    //删除家谱
    public boolean deletePedigree(long treeId) throws Exception {
        boolean flag = false;
        try {
            List<Long> membersId = familyDao.getMembersIdByTreeId(treeId);
            if (!membersId.isEmpty()) {
                for (Long memberId : membersId) {
                    deleteTransaction(memberId, treeId);
                }
            }
            pedigreeDao.deletePedigree(treeId);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    //合并家谱
    public boolean mergeTransaction(Map<String, Object> maps) throws Exception {
        Connection conn = ConnectionUtil.getConnection();

        long sourceTreeId = (long) maps.get("sourceTreeId");
        long targetTreeId = (long) maps.get("targetTreeId");
        long sourceId = (long) maps.get("sourceId");
        long targetId = (long) maps.get("targetId");
        int insertId = (int) maps.get("insertId");
        long newTreeId = (long) maps.get("newTreeId");

        String sql1 = "select * from relation where u2=? and type = ? and treeId = ?";
        if (insertId == 1 || insertId == 2) {//点击添加为父亲 或 母亲
            try (PreparedStatement statement = conn.prepareStatement(sql1)) {
                statement.setLong(1, targetId);
                statement.setInt(2, insertId - 1);
                statement.setLong(3, targetTreeId);
                try (ResultSet resultSet = statement.executeQuery();) {
                    if (resultSet.next()) { //如果目标成员已经有父亲
                        return false;
                    }
                }
            }
        }

        try {
            conn.setAutoCommit(false);
            //插入成员
            Pedigree pedigree = new Pedigree();
            pedigree.setName("合并家谱树-" + LocalDate.now());
            pedigree.setDossier("");
            pedigree.setBranch("");
            pedigree.setFaction("");
            pedigree.setDescription("");
            pedigree.setOriginPlace("");
            pedigree.setId(newTreeId);
            pedigree.setCreateTime(new Date());
            pedigree.setCreatorId(tool.getUserID());

            pedigreeDao.insertPedigree(pedigree);

            //获取target树和source树里所有Relation
            List<Relation> sourceRelations = relationDao.getRelationsByTreeId(sourceTreeId);
            List<Relation> targetRelation = relationDao.getRelationsByTreeId(targetTreeId);
            sourceRelations.addAll(targetRelation);
            sourceRelations.forEach(s -> s.setTreeId(newTreeId));

            sourceRelations = sourceRelations.stream().distinct().collect(Collectors.toList());
            for (Relation relation : sourceRelations) {
                relationDao.insertRelation(relation);
            }

            List<Long> sourceMemberIds = familyDao.getMemberIdsByTreeId(sourceTreeId);
            List<Long> targetMemberIds = familyDao.getMemberIdsByTreeId(targetTreeId);
            sourceMemberIds.addAll(targetMemberIds);

            sourceMemberIds = sourceMemberIds.stream().distinct().collect(Collectors.toList());
            for (Long id : sourceMemberIds) {
                familyDao.insertMemberToFamily(newTreeId, id);
            }

            if (insertId != 5) {
                memberRelationDao.insertRelation(sourceId, targetId, insertId, newTreeId);
            }
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        }
        return true;
    }

    //修改用户信息
    public boolean updateMember(Map<String, Object> maps) throws SQLException {


        DateType birthdateType = DateType.valueOf((String)maps.get("birthDateType"));
        DateType deathdateType = DateType.valueOf((String)maps.get("deathDateType"));

        String sql = "update member set name = ?,birthdate = ?,birthplace = ?,restplace = ?,is_alive = ?," +
                "description = ?,generation = ?,identityId = ?, residence = ?, phoneNumber = ?, deathDate = ?, " +
                "open_identity = ?, open_phone = ?,album_id = ?,birthdate_type = ?, deathdate_type = ?," +
                "lunar_birthdate = ?,lunar_deathdate = ?,gender = ? where id=?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, (String) maps.get("name"));
            if (!StringUtils.isEmpty(maps.get("birthdate")) && DateType.DATE == birthdateType) {
                pstm.setDate(2, java.sql.Date.valueOf(maps.get("birthdate").toString()));
            } else {
                pstm.setDate(2, null);
            }
            pstm.setString(3, (String) maps.get("birthplace"));
            pstm.setString(4, (String) maps.get("restplace"));
            pstm.setInt(5, (Integer) maps.get("is_alive"));
            pstm.setString(6, (String) maps.get("description"));
            pstm.setString(7, (String) maps.get("generation"));
            pstm.setString(8, (String) maps.get("identityId"));
            pstm.setString(9, (String) maps.get("residence"));
            pstm.setString(10, (String) maps.get("phoneNumber"));
            if (!StringUtils.isEmpty(maps.get("deathDate")) && DateType.DATE == deathdateType) {
                pstm.setDate(11, java.sql.Date.valueOf(maps.get("deathDate").toString()));
            } else {
                pstm.setDate(11, null);
            }
            pstm.setInt(12, (Integer) maps.get("openIdentity"));
            pstm.setInt(13, (Integer) maps.get("openPhone"));
            pstm.setLong(14, (Long) Optional.ofNullable(maps.get("albumId")).orElse(0L));
            pstm.setInt(15, birthdateType.value());
            pstm.setInt(16, deathdateType.value());

            if (DateType.DATE == birthdateType){
                pstm.setString(17, null);
            }else{
                pstm.setString(17, (String)maps.get("birthdate"));
            }

            if (DateType.DATE == deathdateType){
                pstm.setString(18, null);
            }else{
                pstm.setString(18, (String)maps.get("deathDate"));
            }

            pstm.setInt(19, Integer.parseInt((String) maps.get("gender")));
            pstm.setLong(20, (Long) maps.get("editid"));
            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //修改信息--不包括文件
    public boolean updateUser(Map<String, Object> maps) {
        String sql = "update user set name = ?,birthdate = ?,birthplace = ?,description = ?,generation = ?,identityId = ?, residence = ?, phoneNumber = ? where id=?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, (String) maps.get("name"));
            if (!StringUtils.isEmpty(maps.get("birthdate"))) {
                pstm.setDate(2, java.sql.Date.valueOf(maps.get("birthdate").toString()));
            } else {
                pstm.setDate(2, null);
            }
            pstm.setString(3, (String) maps.get("birthplace"));
            pstm.setString(4, (String) maps.get("description"));
            pstm.setString(5, (String) maps.get("generation"));
            pstm.setString(6, (String) maps.get("identityId"));
            pstm.setString(7, (String) maps.get("residence"));
            pstm.setString(8, (String) maps.get("phoneNumber"));
            pstm.setInt(9, (Integer) maps.get("editid"));

            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //修改家谱信息
    public boolean updatePedigree(Map<String, Object> maps) {
        String sql = "update pedigree set name = ?,doisser = ?,branch = ?,faction = ?,originPlace = ? ,description = ? where id=?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement pstm = conn.prepareStatement(sql);) {
            pstm.setString(1, (String) maps.get("name"));
            pstm.setString(2, (String) maps.get("doisser"));
            pstm.setString(3, (String) maps.get("branch"));
            pstm.setString(4, (String) maps.get("faction"));
            pstm.setString(5, (String) maps.get("originPlace"));
            pstm.setString(6, (String) maps.get("description"));
            pstm.setLong(7, (Long) maps.get("treeId"));
            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //是否修改文件
    public boolean updateFile(int id, InputStream inputStream) {
        if (inputStream != null) {
            String sql = "update member set img = ? where id = ?";
            Connection conn = ConnectionUtil.getConnection();
            try (PreparedStatement pstm = conn.prepareStatement(sql);) {
                pstm.setBytes(1, IOUtils.toByteArray(inputStream));
                pstm.setInt(2, id);
                return pstm.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //修改信息方法
    public boolean updateTransaction(Map<String, Object> maps) throws Exception {
        boolean flag = true;
        try {
            if (maps.get("editid") != null) {
                this.updateMember(maps);

                updateDocuments((Long) maps.get("editid"), (String) maps.get("documents"));
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    public void updateDocuments(Long memberId, String documentsStr) throws Exception {
        if (documentsStr == null || documentsStr.isEmpty()){
            return;
        }

        DocumentDao documentDao = new DocumentDaoImpl();
        documentDao.deleteByMemberId(ConnectionUtil.getConnection(), memberId);

        JSONArray documents = JSONArray.parseArray(documentsStr);
        for (int i = 0; i < documents.size(); i++) {
            JSONObject doc = documents.getJSONObject(i);

            Document d = new Document();
            d.setId(doc.getLong("id"));
            d.setMemberId(memberId);
            d.setName(doc.getString("name"));

            if (Objects.nonNull(doc.getLong("uploaderId"))){
                d.setCreateTime(doc.getDate("createTime"));
                d.setUploaderId(doc.getLong("uploaderId"));
            }else{
                d.setCreateTime(new Date());
                d.setUploaderId(tool.getUserID());
            }

            documentDao.save(ConnectionUtil.getConnection(), d);

        }
    }

    //删除成员关系
    public boolean deleteRelation(long id, long treeId) {
        String sql = "delete from relation where ( u1 = ? or u2 = ? ) and treeId = ? ";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setLong(1, id);
            pstm.setLong(2, id);
            pstm.setLong(3, treeId);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //删除成员
    public boolean deleteMember(long id) {
        String sql = "delete from member where id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setLong(1, id);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //删除成员方法
    public boolean deleteTransaction(long id, long treeId) throws Exception {
        boolean flag = false;
        try {
            if (this.deleteRelation(id, treeId)) {
                familyDao.deleteMemberFromFamily(treeId, id);
                flag = true;
                if (!familyDao.hasFamily(id)) {
                    flag = this.deleteMember(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @param id
     * @param depth 大于或等于3,否则为3
     * @return
     * @throws Exception
     */
    //获取亲戚
    public JSONArray getGenealogy(Long id, Integer depth, Long treeId) throws Exception {
        if (depth < 3) {
            depth = 3;
        }
        JSONArray array = new JSONArray();
        JSONObject object = null;
        Long fatherId = relationDao.getFatherId(id, treeId);
        Long motherId = relationDao.getMotherId(id, treeId);

        Long queryId = id;
        if(Objects.nonNull(fatherId)){
            queryId = fatherId;
        }else if(Objects.nonNull(motherId)){
            queryId = motherId;
        }

        object = this.generateTree(queryId, 1, depth, treeId);

        array.add(object);
        return array;
    }


    public JSONObject getMember(Long id) throws Exception {
        Member member = memberDao.getMemberByIdWithImg(id, false);
        JSONObject jo = new JSONObject();
        jo.put("id", member.getId());
        jo.put("name", member.getName() == null ? "" : member.getName());
        jo.put("gender", member.getGender() == 1 ? "男" : "女");


        if (DateType.DATE == member.getBirthDateType()){
            if (Objects.nonNull(member.getBirthdate())){
                jo.put("birthdate", member.getBrithdate().toString());
            }else{
                jo.put("birthdate", "");
            }
        }else{
            jo.put("birthdate", member.getLunarBirthDate());
        }

        if (DateType.DATE == member.getDeathDateType()){
            if (Objects.nonNull(member.getDeathDate())){
                jo.put("deathDate", member.getDeathDate().toString());
            }else{
                jo.put("deathDate", "");
            }
        }else{
            jo.put("deathDate", member.getLunarDeathDate());
        }
        jo.put("birthplace", member.getBirthplace() == null ? "" : member.getBirthplace());
        jo.put("restplace", member.getRestplace() == null ? "" : member.getRestplace());
        jo.put("is_alive", member.getIs_alive() == null ? "1" : member.getIs_alive());
        jo.put("generation", member.getGeneration() == null ? "" : member.getGeneration());
        jo.put("description", member.getDescription() == null ? "" : member.getDescription());
        jo.put("identityId", member.getIdentityId() == null ? "" : member.getIdentityId());
        jo.put("residence", member.getResidence() == null ? "" : member.getResidence());
        jo.put("phoneNumber", member.getPhoneNumber() == null ? "" : member.getPhoneNumber());
        jo.put("img", member.getImg());
        jo.put("openIdentity", member.getOpenIdentity());
        jo.put("openPhone", member.getOpenPhone());
        jo.put("documents", getDocumentsByMemberId(id));
        jo.put("albumId", member.getAlbumId());
        jo.put("birthDateType", member.getBirthDateType());
        jo.put("deathDateType", member.getDeathDateType());


        return jo;
    }

    public List<Document> getDocumentsByMemberId(Long id) throws Exception {
        DocumentDao documentDao = new DocumentDaoImpl();

        ISqlQuery query = SqlParameter.Query.to("memberId").eq(id);
        ISqlParameter parameter = new SqlParameter();
        parameter.query(query);

        return documentDao.getDocumentsByParameter(ConnectionUtil.getConnection(), parameter);
    }

    public Pedigree getPedigree(Long id) throws SQLException {
        return pedigreeDao.getPedigreeById(id);
    }

    public JSONArray getMembers() throws SQLException {
        JSONArray ja = new JSONArray();
        List<Member> members = this.memberDao.getMembers();
        for (Member member : members) {
            JSONObject jo = new JSONObject();
            jo.put("id", member.getId());
            jo.put("name", member.getName());
            jo.put("identityId", member.getIdentityId());
            ja.add(jo);
        }
        return ja;
    }

    public JSONObject getMembersByBirthAndDeath() throws SQLException {
        JSONArray ja1 = new JSONArray();
        JSONArray ja2 = new JSONArray();
        List<Member> members1 = this.memberDao.getMemberByBirth();
        List<Member> members2 = this.memberDao.getMemberByDeath();
        for (Member member : members1) {
            JSONObject jo = new JSONObject();
            jo.put("id", member.getId());
            jo.put("name", member.getName());
            ja1.add(jo);
        }
        for (Member member : members2) {
            JSONObject jo = new JSONObject();
            jo.put("id", member.getId());
            jo.put("name", member.getName());
            ja2.add(jo);
        }
        JSONObject ja3 = new JSONObject();
        ja3.put("birth", ja1);
        ja3.put("death", ja2);
        return ja3;
    }

    public JSONArray getMembersByTreeId(long treeId) throws SQLException {
        JSONArray ja = new JSONArray();
        List<Member> members = this.familyDao.getMembersByTreeId(treeId);
        for (Member member : members) {
            JSONObject jo = new JSONObject();
            jo.put("id", member.getId());
            jo.put("name", member.getName());
            jo.put("identityId", member.getIdentityId());
            ja.add(jo);
        }
        return ja;
    }

    public List<Member> deriveMembers(long treeId) throws SQLException {
        List<Member> members = this.familyDao.getMembersByTreeId(treeId);
        return members;
    }

    //生成树
    private JSONObject generateTree(Long id, Integer currentDepth, Integer depth, Long treeId) throws Exception {
        JSONObject result = new JSONObject();
        Member member = memberDao.getMemberById(id);
        if (member == null) {
            return result;
        }

        JSONObject extra = new JSONObject();
        extra.put("mid", member.getId());
        extra.put("gender", member.getGender());
        extra.put("isAlive", member.getIs_alive());
        extra.put("creatorId", member.getCreatorId());
        extra.put("depth", currentDepth);

        result.put("mid", member.getId());
        result.put("name", member.getName());
        result.put("class", GenderConverter.convert(member.getGender()));
        result.put("extra", extra);
        Integer count = relationDao.countMarriageRelation(member.getId(), member.getGender(), treeId);
        if (count <= 0) {
            return result;
        }

        JSONArray spouseJA = new JSONArray();
        List<Member> spouses = memberRelationDao.getSpouses(member.getId(), member.getGender(), treeId);
        if (spouses.size() == 0) {
            JSONObject spouseJO = new JSONObject();
            JSONObject jo = new JSONObject();
            jo.put("mid", "");
            jo.put("name", "未填");
            jo.put("class", GenderConverter.convertOpposite(member.getGender()) + " not-info");
            spouseJO.put("spouse", jo);
            spouseJA.add(spouseJO);
        } else {
            for (Member spouse : spouses) {
                JSONObject spouseJO = new JSONObject();
                JSONObject jo = new JSONObject();
                jo.put("mid", spouse.getId());
                jo.put("name", spouse.getName());
                jo.put("class", GenderConverter.convert(spouse.getGender()));

                JSONObject extra2 = new JSONObject();
                extra2.put("mid", spouse.getId());
                extra2.put("gender", spouse.getGender());
                extra2.put("isAlive", spouse.getIs_alive());
                extra2.put("creatorId", spouse.getCreatorId());
                extra2.put("depth", currentDepth);
                jo.put("extra",extra2);

                spouseJO.put("spouse", jo);
                spouseJA.add(spouseJO);
            }
        }
        if (currentDepth < depth) {
            Set<Long> childrenIds = null;
            if (member.getGender() == Member.MAN) {
                childrenIds = relationDao.getChildrenIds(id, treeId);
            } else {
                childrenIds = relationDao.getChildrenIds(id, spouses, treeId);
            }
            if (childrenIds.size() > 0) {
                JSONArray childrenJA = new JSONArray();
                for (Long childrenId : childrenIds) {
                    //递归
                    JSONObject childrenJO = this.generateTree(childrenId, currentDepth + 1, depth, treeId);
                    childrenJA.add(childrenJO);
                }
                ((JSONObject) (spouseJA.get(0))).put("children", childrenJA);
            }
        }
        result.put("marriages", spouseJA);
        return result;
    }

    //找亲获取路径（数组转化为Link）
    public JSONArray getPath(Long source, Long target) throws Exception {
        JSONArray result = new JSONArray();
        long[] path = getPathArrayByAL(source, target);
        Member sourceMember = memberDao.getMemberById(source);
        Member targetMember = memberDao.getMemberById(target);
        if (path.length == 1) {
            result.add(new Link(sourceMember.getId(), sourceMember.getName(), GenderConverter.convert(sourceMember.getGender()), targetMember.getId(), targetMember.getName(), GenderConverter.convert(targetMember.getGender()), Link.NONE));
        } else if (path.length == 2 && source.equals(target)) {
            result.add(new Link(sourceMember.getId(), sourceMember.getName(), GenderConverter.convert(sourceMember.getGender()), targetMember.getId(), targetMember.getName(), GenderConverter.convert(targetMember.getGender()), Link.SELF));
        } else {
            for (int i = path.length - 1; i > 0; i--) {
                Member u1 = memberDao.getMemberById(path[i]);
                Member u2 = memberDao.getMemberById(path[i - 1]);
                Relation relation = relationDao.getRelationByFK(path[i], path[i - 1]);
                Link link = LinkConverter.convert(u1, u2, relation);
                result.add(link);
            }
        }
        return result;
    }



    /**
     * AL == adjacency list(邻接表)
     *
     * @param source
     * @param target
     * @return
     * @throws Exception
     */
    private long[] getPathArrayByAL(Long source, Long target) throws Exception {
        long path[] = null;
        if (source.equals(target)) {
            path = new long[2];
            path[0] = source;
            path[1] = target;
            return path;
        }
        List<Member> memberList = memberDao.getMembers();
        int size = memberList.size();
        int min = size + 1000;
        //构造id与index的映射
        Map<Long, Integer> memberMap = new HashMap<>(size);
        //构造index与id的映射
//        long[] memberIndex = new long[size];
        Map<Integer, Long> memberIndex = new HashMap<>();
        for (int i = 0; i < size; i++) {
            memberMap.put(memberList.get(i).getId(), i);
            memberIndex.put(i, memberList.get(i).getId());
//            memberIndex[i] = memberList.get(i).getId();
        }
        //构造邻接表
        LinkedList<Integer>[] graph = new LinkedList[size];
        for (int i = 0; i < size; i++) {
            graph[i] = new LinkedList<>();
        }
        List<Relation> relationList = relationDao.getRelations();
        for (Relation relation : relationList) {
            int x = memberMap.get(relation.getU1());
            int y = memberMap.get(relation.getU2());
            graph[x].add(y);
            graph[y].add(x);
        }
        path = PathUtil.dijkstra(memberMap.get(source), memberMap.get(target), graph, min);
        for (int i = 0; i < path.length; i++) {
            path[i] = memberIndex.get(path[i]);
        }
        return path;
    }
}
