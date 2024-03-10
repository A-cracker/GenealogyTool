package com.genealogy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fy.basejar.exception.ToolException;
import com.fy.basejar.tool.ActionToolBase;
import com.fy.basejar.tool.SqlParameterWebAdaptor;
import com.fy.javanode.declarative.annotation.HttpApiAutowired;
import com.fy.sqlparam.param.ISqlParameter;
import com.fy.toolhelper.util.RequestUtils;
import com.fy.toolhelper.util.ResponseUtils;
import com.genealogy.constant.Constant;
import com.genealogy.constant.DateType;
import com.genealogy.dao.PedigreeDao;
import com.genealogy.dao.impl.*;
import com.genealogy.httpapi.CoreApi;
import com.genealogy.pojo.entity.Lunar;
import com.genealogy.pojo.entity.Member;
import com.genealogy.pojo.entity.Pedigree;
import com.genealogy.pojo.entity.User;
import com.genealogy.service.impl.GenealogyServiceImpl;
import com.genealogy.util.ConnectionUtil;
import com.genealogy.util.CusResponseUtils;
import com.genealogy.vo.Resources;
import com.genealogy.vo.RoleResource;
import com.genealogy.vo.RoleResources;
import com.genealogy.vo.ToolResources;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.StringUtils;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class NewApp extends ActionToolBase {

    @HttpApiAutowired
    private CoreApi coreApi;

    @Action
    public Object test() throws SQLException {

        List<Lunar> lunars = new ArrayList<>();

        try (PreparedStatement prepareStatement = getConnection().prepareStatement("select * from lunar");
             ResultSet resultSet = prepareStatement.executeQuery();){

            while (resultSet.next()) {

                Lunar l = new Lunar();
                l.setId(resultSet.getInt("id"));
                l.setYear(resultSet.getString("year"));
                l.setLunarYear(resultSet.getString("lunar_year"));
                l.setDynasty(resultSet.getString("dynasty"));
                l.setReignTitle(resultSet.getString("reign_title"));

                lunars.add(l);
            }

        }

       Map<String, Map<String, List<Lunar>>> map = lunars.stream().collect(Collectors.groupingBy(Lunar::getDynasty, LinkedHashMap::new,Collectors.groupingBy(i -> i.getReignTitle().substring(0,2),LinkedHashMap::new,Collectors.toList())));



        JSONArray array = new JSONArray();
        map.forEach((k, v)->{
            JSONObject json = new JSONObject();
            json.put("v", k);
            json.put("l", k);

            JSONArray rs = new JSONArray();
            v.forEach((r, v2)->{

                JSONObject json1 = new JSONObject();
                json1.put("v", r);
                json1.put("l", r + "(" + v2.get(0).getYear() + ")");

                JSONArray ls = new JSONArray();
                v2.forEach(l -> {

                    JSONObject json2 = new JSONObject();
                    json2.put("v", l.getYear());
                    json2.put("l", l.getReignTitle() + "(" + l.getYear() + ")");

                    ls.add(json2);

                });

                json1.put("c", ls);
                rs.add(json1);
            });

            json.put("c", rs);

            array.add(json);

        });

        return array;





    }

    @Action
    public JSONObject getDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDaoImpl memberDao = new MemberDaoImpl();
        Long id = RequestUtils.getLongParameter( request, "id" );
        Member member = memberDao.getMemberByIdWithImg(id, true);
        JSONObject jo = new JSONObject();
        jo.put("name", member.getName());
        jo.put("gender", member.getGender());

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


        jo.put("birthplace", member.getBirthplace());
        jo.put("generation", member.getGeneration());
        jo.put("is_alive", member.getIs_alive());
        jo.put("restplace", member.getRestplace());
        jo.put("description", member.getDescription());
        jo.put("identityId", member.getIdentityId());
        jo.put("residence", member.getResidence());
        jo.put("phoneNumber", member.getPhoneNumber());
        jo.put("id", id);
        jo.put("openIdentity", member.getOpenIdentity());
        jo.put("openPhone", member.getOpenPhone());
        jo.put("img", member.getImg());
        jo.put("albumId", member.getAlbumId());
        jo.put("birthDateType", member.getBirthDateType());
        jo.put("deathDateType", member.getDeathDateType());


        jo.put("documents", new GenealogyServiceImpl(this).getDocumentsByMemberId(id));

        return jo;
    }


    @Action
    public JSONObject getUserDetail(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        UserDaoImpl userDao = new UserDaoImpl();
        Long id = RequestUtils.getLongParameter(request, "id");
        JSONObject jo = new JSONObject();
        User user = Optional.ofNullable(userDao.getUserByIdWithImg(id)).orElseThrow(() -> new ToolException("找不到" + id, "找不到用户信息"));

        jo.put("name", user.getName());
        jo.put("gender", user.getGender());
        if (user.getBrithdate() != null) {
            jo.put("birthdate", user.getBrithdate().toString());
        } else {
            jo.put("birthdate", null);
        }
        jo.put("identityId", user.getIdentityId());
        jo.put("birthplace", user.getBirthplace());
        jo.put("description", user.getDescription());
        jo.put("generation", user.getGeneration());
        jo.put("residence", user.getResidence());
        jo.put("phoneNumber", user.getPhoneNumber());
        if (user.getImg() != null) {
            jo.put("img", "true");
        } else {
            jo.put("img", "false");
        }

        return CusResponseUtils.successContent(jo);
    }

    @Action
    public JSONObject insertStartMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = RequestUtils.getStringParameter(request, "name");
        Integer gender = RequestUtils.getIntegerParameter(request, "gender");
        String birthDate = RequestUtils.getStringParameter(request, "birthdate");
        String deathDate = RequestUtils.getStringParameter(request, "deathDate");
        String birthplace = RequestUtils.getStringParameter(request, "birthplace");
        String description = RequestUtils.getStringParameter(request, "description");

        String img = RequestUtils.getStringParameter(request, "img");
        String generation = RequestUtils.getStringParameter(request, "generation");
        String identityId = RequestUtils.getStringParameter(request, "identityId");
        String residence = RequestUtils.getStringParameter(request, "residence");
        String phoneNumber = RequestUtils.getStringParameter(request, "phoneNumber");
        Long treeId = RequestUtils.getLongParameter(request, "treeId");
        String restplace = RequestUtils.getStringParameter(request, "restplace");
        Integer isAlive = RequestUtils.getIntegerParameter(request, "is_alive");
        Long albumId = RequestUtils.getLongParameter(request, "albumId");
        DateType birthDateType = DateType.valueOf(RequestUtils.getStringParameter(request, "birthDateType"));
        DateType deathDateType = DateType.valueOf(RequestUtils.getStringParameter(request, "deathDateType"));

        MemberDaoImpl memberDao = new MemberDaoImpl();
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Member member = new Member();
        member.setName(name);
        member.setGender(gender);
        if (!StringUtils.isEmpty(birthDate)) {
            member.setBrithdate(java.sql.Date.valueOf(birthDate));
        } else {
            member.setBrithdate(null);
        }
        if (!StringUtils.isEmpty(deathDate)) {
            member.setDeathDate(java.sql.Date.valueOf(deathDate));
        } else {
            member.setDeathDate(null);
        }
        member.setBirthplace(birthplace);
        member.setRestplace(restplace);
        member.setIs_alive(isAlive);
        member.setDescription(description);
            member.setImg(img);
        member.setGeneration(generation);
        member.setIdentityId(identityId);
        member.setResidence(residence);
        member.setPhoneNumber(phoneNumber);
        member.setAlbumId(albumId);
        member.setId(YitIdHelper.nextId());
        member.setBirthDateType(birthDateType);
        member.setDeathDateType(deathDateType);
        member.setCreatorId(getUserID());
        member.setCreateTime(new Date(System.currentTimeMillis()));
        long autoKey = memberDao.insertMember(member);

        genealogyServiceImpl.updateDocuments(member.getId(), RequestUtils.getStringParameter(request, "documents"));

        JSONObject jo = new JSONObject();
        if (autoKey < 0) {
            jo.put("flag", false);
        } else {
            FamilyDaoImpl familyDao = new FamilyDaoImpl();
            familyDao.insertMemberToFamily(treeId, autoKey);
            jo.put("flag", true);
        }

        return jo;
    }

    @Action
    public JSONObject insertUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = RequestUtils.getStringParameter(request, "name");
        Integer gender = RequestUtils.getIntegerParameter(request, "gender");
        String birthDate = RequestUtils.getStringParameter(request, "birthdate");
        String birthplace = RequestUtils.getStringParameter(request, "birthplace");
        String description = RequestUtils.getStringParameter(request, "description");

        FileItem img = getUploadFile("img");
        String generation = RequestUtils.getStringParameter(request, "generation");
        String identityId = RequestUtils.getStringParameter(request, "identityId");
        String residence = RequestUtils.getStringParameter(request, "residence");
        String phoneNumber = RequestUtils.getStringParameter(request, "phoneNumber");

        UserDaoImpl userDao = new UserDaoImpl();
        User user = new User();
        user.setId(getUserID());
        user.setName(name);
        user.setGender(gender);
        user.setBrithdate(Date.valueOf(birthDate));
        user.setBirthplace(birthplace);
        user.setDescription(description);
        if (Objects.nonNull(img)) {
            user.setImg(IOUtils.toByteArray(img.getInputStream()));
        }

        user.setGeneration(generation);
        user.setIdentityId(identityId);
        user.setResidence(residence);
        user.setPhoneNumber(phoneNumber);
        int autoKey = userDao.insertUser(user);
        JSONObject jo = new JSONObject();
        if (autoKey < 0) {
            jo.put("flag", false);
        } else {
            jo.put("flag", true);
            jo.put("id", autoKey);
        }
        return jo;
    }

    @Action
    public JSONObject insertPedigree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = RequestUtils.getStringParameter(request, "name");
        String branch = RequestUtils.getStringParameter(request, "branch");
        String doisser = RequestUtils.getStringParameter(request, "dossier");
        String faction = RequestUtils.getStringParameter(request, "faction");
        String originPlace = RequestUtils.getStringParameter(request, "originPlace");
        String description = RequestUtils.getStringParameter(request, "description");

        PedigreeDaoImpl pedigreeDao = new PedigreeDaoImpl();
        Pedigree pedigree = new Pedigree();
        pedigree.setId(YitIdHelper.nextId());
        pedigree.setName(name);
        pedigree.setBranch(branch);
        pedigree.setDossier(doisser);
        pedigree.setFaction(faction);
        pedigree.setOriginPlace(originPlace);
        pedigree.setDescription(description);
        pedigree.setCreateTime(new java.util.Date());
        pedigree.setCreatorId(getUserID());
        pedigreeDao.save(getDBConnection(), pedigree);
//        int autoKey = pedigreeDao.insertPedigree(pedigree);
        JSONObject jo = new JSONObject();
        jo.put("id", pedigree.getId());
        return jo;
    }
    @Action
    public Map<String, Object> getCurrentToolMyView(HttpServletRequest request) throws Exception {
        ToolResources tool = coreApi.getToolByToolShopToolID(getBandID(), getToolID());
        if(tool.isEmpty()) {
            return ResponseUtils.asRowsAndTotal(null, 0L);
        }
        return ResponseUtils.asRowsAndTotal(tool.getFirst(), 1L);
    }

    @Action
    public Map<String, Object> getSharePermission() throws Exception {
        JSONObject list = coreApi.getBandRolesByUserID(getUserID(),getBandID());
        JSONArray roleList = list.getJSONArray("rows");
        boolean tShare = false;
        boolean runTool = false;
        for (int i = 0; i < roleList.size(); i++) {
            tShare = tShare || roleList.getJSONObject(i).getJSONObject("toolPermission").getBoolean("run");
            runTool = runTool || roleList.getJSONObject(i).getJSONObject("bandPermission").getBoolean("tshare");
        }
        if(tShare && runTool){
            System.out.println(true);
            return ResponseUtils.asRowsAndTotal(true, 1L);
        }else{
            return ResponseUtils.asRowsAndTotal(false, 1L);
        }
    }
    @Action
    public Map<String, Object> getShareRole() throws Exception {
        RoleResources roles = coreApi.getBandRolesByRoleName(getBandID(), Constant.IMAGE_ROLE);
        if(roles.isEmpty()) {
            Map<String, Long> toolNameIDs = new HashMap<>();
//            ToolResources tool = coreApi.getToolByToolShopToolID(getBandID(), getToolID());
//            toolNameIDs.put(tool.getFirst().getName(), tool.getFirst().getObjID());
            coreApi.createBandRole(getBandID(), Constant.imageRole, Constant.getImageRoleAuthorizationItems(toolNameIDs));
            return getShareRole();
        }
        return ResponseUtils.asRowsAndTotal(roles.getFirst(), 1L);
    }
    @Action
    public JSONObject insertMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jo = new JSONObject();
        if (getUserID() == 28){
            String message = "添加失败,请登录!";
            jo.put("flag", false);
            jo.put("errorMessage", message);
            return jo;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("name", RequestUtils.getStringParameter(request, "name"));
        params.put("gender", RequestUtils.getStringParameter(request, "gender"));
        params.put("birthdate", RequestUtils.getStringParameter(request, "birthdate"));
        params.put("deathDate", RequestUtils.getStringParameter(request, "deathDate"));
        params.put("birthplace", RequestUtils.getStringParameter(request, "birthplace"));
        params.put("restplace", RequestUtils.getStringParameter(request, "restplace"));
        params.put("is_alive", RequestUtils.getIntegerParameter(request, "is_alive"));
        params.put("description", RequestUtils.getStringParameter(request, "description"));
        params.put("generation", RequestUtils.getStringParameter(request, "generation"));
        params.put("identityId", RequestUtils.getStringParameter(request, "identityId"));
        params.put("residence", RequestUtils.getStringParameter(request, "residence"));
        params.put("phoneNumber", RequestUtils.getStringParameter(request, "phoneNumber"));
        params.put("type", RequestUtils.getIntegerParameter(request, "type"));
        params.put("targetId", RequestUtils.getLongParameter(request, "targetId"));
        params.put("openIdentity", RequestUtils.getIntegerParameter(request, "openIdentity"));
        params.put("openPhone", RequestUtils.getIntegerParameter(request, "openPhone"));
        params.put("documents", RequestUtils.getStringParameter(request, "documents"));
        params.put("albumId", RequestUtils.getLongParameter(request, "albumId"));
        params.put("birthDateType", RequestUtils.getStringParameter(request, "birthDateType"));
        params.put("deathDateType", RequestUtils.getStringParameter(request, "deathDateType"));

        Long treeId = RequestUtils.getLongParameter(request, "treeId");

        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Member member = genealogyServiceImpl.insertTransaction(params, treeId);
        if (Objects.isNull(member)) {
            String message = "添加失败,请确认是否已存在该关系";
            jo.put("flag", false);
            jo.put("errorMessage", message);
        } else {
            jo.put("flag", true);
            jo.put("member", member);
        }

        return jo;
    }

    @Action
    public JSONObject mergePedigree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("insertId", RequestUtils.getIntegerParameter(request, "insertId"));
        params.put("targetId", RequestUtils.getLongParameter(request, "targetId"));
        params.put("sourceId", RequestUtils.getLongParameter(request, "sourceId"));
        params.put("sourceTreeId", RequestUtils.getLongParameter(request, "sourceTreeId"));
        params.put("targetTreeId", RequestUtils.getLongParameter(request, "targetTreeId"));
        params.put("newTreeId", YitIdHelper.nextId());

        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        boolean flag = genealogyServiceImpl.mergeTransaction(params);
        JSONObject jo = new JSONObject();
        if (!flag) {
            String message = "合并失败，请确认合并对象是否已存在该关系";
            jo.put("flag", false);
            jo.put("errorMessage", message);
        } else {
            jo.put("flag", true);
            jo.put("id", params.get("newTreeId"));
        }
        return jo;
    }

    @Action
    public boolean editUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", RequestUtils.getStringParameter(request, "name"));
        params.put("birthdate", RequestUtils.getStringParameter(request, "birthdate"));
        params.put("birthplace", RequestUtils.getStringParameter(request, "birthplace"));
        params.put("description", RequestUtils.getStringParameter(request, "description"));
        params.put("generation", RequestUtils.getStringParameter(request, "generation"));
        params.put("identityId", RequestUtils.getStringParameter(request, "identityId"));
        params.put("residence", RequestUtils.getStringParameter(request, "residence"));
        params.put("phoneNumber", RequestUtils.getStringParameter(request, "phoneNumber"));
        params.put("editid", RequestUtils.getIntegerParameter(request, "editid"));

        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        boolean flag = genealogyServiceImpl.updateUser(params);
        if (!flag) {
            String message = "修改成员失败";
            request.setAttribute("message", message);
            return false;
        }
        return true;
    }

    @Action
    public boolean editMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("editid", RequestUtils.getLongParameter(request, "id"));
        params.put("name", RequestUtils.getStringParameter(request, "name"));
        params.put("gender", RequestUtils.getStringParameter(request, "gender"));
        params.put("birthdate", RequestUtils.getStringParameter(request, "birthdate"));
        params.put("deathDate", RequestUtils.getStringParameter(request, "deathDate"));
        params.put("birthplace", RequestUtils.getStringParameter(request, "birthplace"));
        params.put("restplace", RequestUtils.getStringParameter(request, "restplace"));
        params.put("is_alive", RequestUtils.getIntegerParameter(request, "is_alive"));
        params.put("openIdentity", RequestUtils.getIntegerParameter(request, "openIdentity"));
        params.put("openPhone", RequestUtils.getIntegerParameter(request, "openPhone"));
        params.put("description", RequestUtils.getStringParameter(request, "description"));
        params.put("generation", RequestUtils.getStringParameter(request, "generation"));
        params.put("identityId", RequestUtils.getStringParameter(request, "identityId"));
        params.put("residence", RequestUtils.getStringParameter(request, "residence"));
        params.put("phoneNumber", RequestUtils.getStringParameter(request, "phoneNumber"));
        params.put("albumId", RequestUtils.getLongParameter(request, "albumId"));
        params.put("documents", RequestUtils.getStringParameter(request, "documents"));
        params.put("birthDateType", RequestUtils.getStringParameter(request, "birthDateType"));
        params.put("deathDateType", RequestUtils.getStringParameter(request, "deathDateType"));


        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        boolean flag = genealogyServiceImpl.updateTransaction(params);
        if (!flag) {
            return false;
        }
        return true;
    }


    @Action
    public boolean editPedigree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", RequestUtils.getStringParameter(request, "name"));
        params.put("doisser", RequestUtils.getStringParameter(request, "dossier"));
        params.put("branch", RequestUtils.getStringParameter(request, "branch"));
        params.put("faction", RequestUtils.getStringParameter(request, "faction"));
        params.put("originPlace", RequestUtils.getStringParameter(request, "originPlace"));
        params.put("description", RequestUtils.getStringParameter(request, "description"));
        params.put("treeId", RequestUtils.getLongParameter(request, "id"));
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);

        return genealogyServiceImpl.updatePedigree(params);
    }

    @Action
    public Map<String, Object> deleteMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long id = RequestUtils.getLongParameter(request, "id", 0L);
        Long treeId = RequestUtils.getLongParameter(request, "treeId");

        MemberDaoImpl memberDao = new MemberDaoImpl();
        Member member = memberDao.getMemberById(id);

        if (!getUserID().equals(member.getCreatorId())){

           return ResponseUtils.asResultAndMsg(false, "不是创建者") ;
        }

        return ResponseUtils.asResultAndMsg(genealogyServiceImpl.deleteTransaction(id, treeId), "");
    }

    @Action
    public Integer getGender(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDaoImpl memberDao = new MemberDaoImpl();
        if (!request.getParameter("id").equals("")) {
            Long id = RequestUtils.getLongParameter(request, "id");
            Integer gender = memberDao.getMemberById(id).getGender();
            return gender;
        }
        return null;
    }

    @Action
    public Integer judgeParents(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("id").equals("")) {
            return null;
        }
        MemberRelationDaoImpl memberRelationDaoImpl = new MemberRelationDaoImpl();
        RelationDaoImpl relationDaoImpl = new RelationDaoImpl();
        MemberDaoImpl memberDao = new MemberDaoImpl();

        Integer flag = null;
        Long id  = RequestUtils.getLongParameter(request, "id");
        Long treeId = RequestUtils.getLongParameter(request, "treeId");
        if (relationDaoImpl.getFatherId(id, treeId) != null) {//有父亲
            if (memberRelationDaoImpl.getSpouses(relationDaoImpl.getFatherId(id, treeId), Member.MAN, treeId).isEmpty()) {
                flag = 1;//有父无母节点
            } else {
                flag = 2;//父母节点均有
            }
        } else if (relationDaoImpl.getMotherId(id, treeId) != null) {
            if (memberRelationDaoImpl.getSpouses(relationDaoImpl.getMotherId(id, treeId), Member.WOMAN, treeId).isEmpty()) {
                flag = 3;//有母无父节点
            } else {
                flag = 2;//父母节点均有
            }
        } else {
            Member member = memberDao.getMemberById(id);
            List<Member> lists = memberRelationDaoImpl.getSpouses(id, member.getGender(), treeId);
            if (lists.isEmpty()) {
                flag = 4;//双无
            } else {
                if (relationDaoImpl.getFatherId(lists.get(0).getId(), treeId) == null && relationDaoImpl.getMotherId(lists.get(0).getId(), treeId) == null) {
                    if (relationDaoImpl.hasChildren(id)) {
                        flag = 4;
                    } else if (relationDaoImpl.hasChildren(lists.get(0).getId())) {
                        flag = 5;//父母节点均有且不能再添加配偶
                    } else {
                        if (member.getGender() == Member.MAN) {
                            flag = 4;
                        } else {
                            flag = 5;
                        }
                    }
                } else {
                    flag = 5;
                }
            }
        }
        return flag;
    }

    @Action
    public Long judgePosition(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberRelationDaoImpl memberRelationDaoImpl = new MemberRelationDaoImpl();
        RelationDaoImpl relationDaoImpl = new RelationDaoImpl();
        MemberDaoImpl memberDao = new MemberDaoImpl();
        if (!request.getParameter("id").equals("")) {
            Long result = Long.valueOf(0);
            Long id = RequestUtils.getLongParameter(request, "id");
            Long treeId = RequestUtils.getLongParameter(request, "treeId");
            Integer gender = memberDao.getMemberById(id).getGender();
            List<Member> lists = memberRelationDaoImpl.getSpouses(id, gender, treeId);
            //boolean hasChildren = relationDaoImpl.hasChildren(id);
            /***
             * 1、没有配偶 以自己为根构建 1
             * 2、有配偶 自己在树中没有父母节点，配偶有父母节点  以配偶为根构建
             * 3、有配偶 自己在树中有父母节点，配偶没有  以自己为根构建
             * 4、有配偶 自己及配偶都没有父母节点 自己是男以自己己是女以配偶
             */
            if (!lists.isEmpty()) {
                if (relationDaoImpl.getFatherId(id, treeId) == null) {
                    if (relationDaoImpl.getFatherId(lists.get(0).getId(), treeId) == null) {
                        if (relationDaoImpl.hasChildren(id)) {
                            result = id;
                        } else if (relationDaoImpl.hasChildren(lists.get(0).getId())) {
                            result = lists.get(0).getId();
                        } else {
                            if (gender == Member.MAN) {
                                result = id;
                            } else {
                                result = lists.get(0).getId();
                            }
                        }
                    } else {
                        result = lists.get(0).getId();
                    }
                } else {
                    result = id;
                }
            } else {
                result = id;
            }
            return result;
        }
        return null;
    }


    @Action
    public JSONArray getGenealogy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long id = RequestUtils.getLongParameter(request, "id");
        Integer depth = RequestUtils.getIntegerParameter(request, "depth");
        Long treeId = RequestUtils.getLongParameter(request, "treeId");
        if (id != null && depth != null) {
            return genealogyServiceImpl.getGenealogy(id, depth, treeId);
        }
        return null;
    }

    @Action
    public JSONArray getPath(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        response.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Long source = RequestUtils.getLongParameter(request, "source");
        Long target = RequestUtils.getLongParameter(request, "target");
        return genealogyServiceImpl.getPath(source, target);
    }

    @Action
    public JSONObject getMemberInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long id = RequestUtils.getLongParameter(request, "id");
        return genealogyServiceImpl.getMember(id);
    }

    @Action
    public Pedigree getPedigreeInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long id = RequestUtils.getLongParameter(request, "id");
        return genealogyServiceImpl.getPedigree(id);
    }

    @Action
    public JSONArray getMembers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        return genealogyServiceImpl.getMembers();
    }

    @Action
    public JSONObject getMembersByBirthAndDeath(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        return genealogyServiceImpl.getMembersByBirthAndDeath();
    }

    @Action
    public JSONArray getMembersByTreeId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long treeId = RequestUtils.getLongParameter(request, "treeId");
        return genealogyServiceImpl.getMembersByTreeId(treeId);
    }

    @Action
    public Map<String, Object> getPedigrees(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Connection connection = getDBConnection();
        final ISqlParameter parameter = SqlParameterWebAdaptor.getInstance()
                .adapt(request);

        PedigreeDao pedigreeDao = new PedigreeDaoImpl();
        parameter.markOrderBy("createTime", false);
        List<Pedigree> rows = pedigreeDao.getPedigrees(connection, parameter);
        parameter.deletePagination();
        final long total = pedigreeDao.getPedigreeCountByParameter(connection, parameter);
        return ResponseUtils.asRowsAndTotal(rows, total);
    }

    @Action
    public JSONArray getAncestor(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long id  = RequestUtils.getLongParameter(request, "id");
        List<Member> lists = genealogyServiceImpl.getAncestors(id);
        JSONArray ja = new JSONArray();
        if (lists.size() > 0) {
            for (Member m : lists) {
                JSONObject jo = JSONObject.parseObject(m.toString());
                ja.add(jo);
            }
        }
        return ja;
    }

    @Action
    public JSONArray getDaughter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long id = RequestUtils.getLongParameter(request, "id");
        List<Member> lists = genealogyServiceImpl.getDaughter(id);
        JSONArray ja = new JSONArray();
        if (lists.size() > 0) {
            for (Member m : lists) {
                JSONObject jo = JSONObject.parseObject(m.toString());
                ja.add(jo);
            }
        }
        return ja;
    }

    @Action
    public JSONObject getTreeAncestorByTreeId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long treeId = RequestUtils.getLongParameter(request, "treeId");

        return genealogyServiceImpl.getAncsetorByTreeId(treeId);
    }

    @Action
    public JSONObject getLatestTreeId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PedigreeDaoImpl pedigreeDao = new PedigreeDaoImpl();
        long flag = pedigreeDao.getLatestTreeId();
        JSONObject jo = new JSONObject();
        jo.put("treeId", flag);
        return jo;
    }

    @Action
    public JSONObject getGenders(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDaoImpl memberDao = new MemberDaoImpl();
        Long sourceId = RequestUtils.getLongParameter(request, "sourceId");
        Long targetId = RequestUtils.getLongParameter(request, "targetId");
        if (sourceId != null && targetId != null) {
            Integer source = memberDao.getMemberGenderById(sourceId);
            Integer target = memberDao.getMemberGenderById(targetId);
            JSONObject jo = new JSONObject();
            jo.put("source", source);
            jo.put("target", target);
            return jo;
        }

        return null;
    }

    @Action
    public JSONObject deletePedigree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        PedigreeDaoImpl pedigreeDao = new PedigreeDaoImpl();
        Long treeId = RequestUtils.getLongParameter(request, "treeId");

        Pedigree pedigree =  genealogyServiceImpl.getPedigree(treeId);

        if (!getUserID().equals(pedigree.getCreatorId())){
            return null;
        }

        if (genealogyServiceImpl.deletePedigree(treeId)) {
            JSONObject jo = new JSONObject();
            jo.put("treeId", pedigreeDao.getLatestTreeId());
            jo.put("transaction", true);
            return jo;
        }

        return null;
    }

    @Action
    public List<Member> deriveMembers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long treeId = RequestUtils.getLongParameter(request, "treeId");
        return genealogyServiceImpl.deriveMembers(treeId);
    }
    @Action
    public void downloadData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenealogyServiceImpl genealogyServiceImpl = new GenealogyServiceImpl(this);
        Long treeId = RequestUtils.getLongParameter(request, "treeId");


        List<Member> members = genealogyServiceImpl.deriveMembers(treeId);

        // 创建工作簿
        XSSFWorkbook wb = new XSSFWorkbook();
        // 创建工作表
        XSSFSheet sheet = wb.createSheet("Sheet1");

        // 1.输出列头
        XSSFRow columnRow = sheet.createRow(0);
        columnRow.setHeight((short) 500);
        String[]  headers = {"姓名","性别","身份证号","出生地","手机号","现居住地","字辈","是否在世","生日","忌日","简介","安息地"};

        // 输出单元格(第一列)
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = columnRow.createCell((short) i);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }

        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            XSSFCell column_0 = row.createCell(0);
            column_0.setCellValue(value(m.getName()));

            XSSFCell column_1 = row.createCell(1);
            if (Objects.nonNull(m.getGender()) && m.getGender() == 0){
                column_1.setCellValue("女");
            }else{
                column_1.setCellValue("男");
            }

            XSSFCell column_2 = row.createCell(2);
            column_2.setCellValue(value(m.getIdentityId()));
            XSSFCell column_3 = row.createCell(3);
            column_3.setCellValue(value(m.getBirthplace()));
            XSSFCell column_4 = row.createCell(4);
            column_4.setCellValue(value(m.getPhoneNumber()));
            XSSFCell column_5 = row.createCell(5);
            column_5.setCellValue(value(m.getResidence()));
            XSSFCell column_6 = row.createCell(6);
            column_6.setCellValue(value(m.getGeneration()));

            XSSFCell column_7 = row.createCell(7);
            if (Objects.nonNull(m.getIs_alive()) && m.getIs_alive() == 0){
                column_7.setCellValue("否");
            }else{
                column_7.setCellValue("是");
            }

            XSSFCell column_8 = row.createCell(8);
            if (Objects.nonNull(m.getBirthDateType()) && m.getBirthDateType() == DateType.DATE){
                column_8.setCellValue(value(m.getBirthdate()));
            }else{
                column_8.setCellValue(value(m.getLunarBirthDate()));
            }

            XSSFCell column_9 = row.createCell(9);
            if (Objects.nonNull(m.getDeathDateType()) && m.getDeathDateType() == DateType.DATE){
                column_9.setCellValue(value(m.getDeathDate()));
            }else{
                column_9.setCellValue(value(m.getLunarDeathDate()));
            }

            XSSFCell column_10 = row.createCell(10);
            column_10.setCellValue(value(m.getDescription()));
            XSSFCell column_11 = row.createCell(11);
            column_11.setCellValue(value(m.getRestplace()));
        }

        // 自动调整宽度
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
//            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
        }

        downloadExcelFile("家族成员信息", wb, response);
    }

    private String value(Object val){
        return Objects.isNull(val) ? "": val.toString();
    }

    public static void downloadExcelFile(String title, XSSFWorkbook wb,HttpServletResponse response) throws IOException {
        response.reset();

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename="+ new String((title + ".xlsx").getBytes(), "iso-8859-1"));

        try(ServletOutputStream outputStream = response.getOutputStream()) {
            wb.write(outputStream);
            outputStream.flush();
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected String onBeforeAct(HttpServletRequest request, HttpServletResponse response, TemporyContext context, String runToolParamStr) throws Exception {
        ConnectionUtil.init(this);
        YitIdHelper.setIdGenerator(new IdGeneratorOptions((short) 1));
        return super.onBeforeAct(request, response, context, runToolParamStr);
    }
}
