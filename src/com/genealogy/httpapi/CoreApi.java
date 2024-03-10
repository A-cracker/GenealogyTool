package com.genealogy.httpapi;

import com.alibaba.fastjson.JSONObject;
import com.fy.javanode.declarative.annotation.*;
import com.genealogy.vo.*;
import org.springframework.http.ResponseEntity;

@HttpApi(urlFieldPath = "com.fy.tre.system.SystemConstants WTBCORE_URL", version = HttpApi.VERSION_2)
@Param(name = "format", value="json")
@Param(name = "aid", value = "21419389378723787447")
public interface CoreApi {
	@Get("/core/v4/band/|bandID|?")
	@Param(name = "field", value = "[\"bandID\",\"organizationID\"]")
	@Param(name = "gid", value = "|bandID|")
	@AccessToken
	BandResources getBandByBandID(@Mount("bandID") Long bandID);

	@Get("/core/v4/band/|bandID|/mountBand?")
	@Param(name = "field", value = "[\"bandID\",\"objID\"]")
	@Param(name = "gid", value = "|bandID|")
	BandResources getMountBandsByBandID(@Mount("bandID") Long bandID);

	@Get("/core/v4/bandRole/band/|bandID|/user/|userID|/bandRoles?")
	@Param(name = "field", value = "[\"roleName\"]")
	@Param(name = "gid", value = "|bandID|")
	@AccessToken
	RoleResources getRoles(@Mount("bandID") Long bandId, @Mount("userID") Long userID);

	@Get("/core/v4/band/|bandID|/role?")
	@Param(name = "query", value = "{\"roleName\":\"|roleName|\"}")
	@Param(name = "field", value = "[\"roleID\",\"roleName\"]")
	@Param(name = "gid", value = "|bandID|")
	@AccessToken
	RoleResources getBandRolesByRoleName(@Mount("bandID") Long bandID, @Mount("roleName") String roleName);

	@Post("/core/v4/band?")
	@Param(name="band",value="{\"name\":\"|name|\",\"keywords\":\"|name|\",\"bandNature\":\"OUT\"}")
	@Param(name="edgeTypeID", value="1")
	@Param(name="organizationID", value="|organizationID|")
	@Param(name = "gid", value = "|bandID|")
	@BandID
	@AccessToken
	ResultVO createBand(@Mount("name") String name, @Mount("bandID") Long bandId, @Mount("organizationID") Long organizationID);

	@Delete("/core/v4/band/|bandID|?")
	@Param(name = "gid", value = "|bandID|")
	@AccessToken
    JSONObject deleteBand(@Mount("bandID") Long bandID);

	@Post("/core/v4/bandRole?")
	@Param(name = "gid", value = "|bandID|")
	@FormField(name = "bandID", value = "|bandID|")
	@FormField(name = "bandRole", value = "|bandRole|")
	@AccessToken
	OrganizationResources createBandRole(@Mount("bandID") Long bandID, @Mount("bandRole") String bandRole);

    @Post("/core/v4/bandRole?")
    @Param(name = "gid", value = "|bandID|")
    @FormField(name = "bandID", value = "|bandID|")
    @FormField(name = "bandRole", value = "|bandRole|")
    @FormField(name = "authorizationItems", value = "|authorizationItems|")
    @AccessToken
	OrganizationResources createBandRole(@Mount("bandID") Long bandID, @Mount("bandRole") String bandRole,
										 @Mount("authorizationItems") String authorizationItems);

	@Post("/core/v4/band/|bandID|/user?")
	@Param(name="user",value="[{\"userID\":\"|userID|\",\"roleIDs\":[\"|roleID|\"]}]")
	@Param(name = "gid", value = "|bandID|")
	@Param(name = "accessToken", value = "|accessToken|")
	String addUsersToBand(@Mount("bandID") Long bandID, @Mount("userID") Long userID, @Mount("roleID") Long roleID, @Mount("accessToken") String accessToke);


	 @Put(url = "/core/v4/chatroom/private?receiverID=|receiverID|")
	 @AccessToken(name = "access_token")
     JSONObject createPrivateChatroom(@Mount("receiverID") Long receiverID);

	 @Put(url = "/core/v4/chatroom?")
	 @AccessToken
     @Param(name = "gid", value = "|bandID|")
     @Param(name = "bandID", value = "|bandID|")
     @Param(name = "chatroom", value = "{\"roomUserNumber\":100, \"chatroomType\":1,\"name\":\"|name|\", \"keywords\":\"|name|\", \"description\":\"|name|\"}")
     JSONObject createChatroom(@Mount("name") String name, @Mount("bandID") Long bandID);


 	@Get("/core/v4/bandRole/|bandRoleID|/user?")
 	@Param(name = "gid", value = "|bandRoleID|")
	@AccessToken
	UserResources getUsersByBandRoleID(@Mount("bandRoleID") Long bandRoleID);

 	@Post("/core/v4/authc/robot?")
	@Param(name="organizationID",value="|organizationID|")
	@Param(name = "gid", value = "|organizationID|")
    ResponseEntity<JSONObject> getOrgRoot(@Mount("organizationID") Long organizationID);

 	@Get("/core/v4/band/|bandID|/organization?")
	@Param(name = "gid", value = "|bandID|")
	@AccessToken
	OrganizationResources getOrganizationByBandID(@Mount("bandID") Long bandID);

 	@Get("/core/v4/user/me/objs?")
	@Param(name = "gid", value = "|objID|")
 	@Param(name = "objIDs", value = "[|objID|]")
 	@Param(name = "accessToken", value = "|accessToken|")
	ObjResources getViewId(@Mount("objID") Long objID, @Mount("accessToken") String accessToke);

	@Get("/core/v4/band/|bandID|/chatroom?")
	@Param(name = "query", value = "{\"name\":\"|chatroomName|\"}")
	@Param(name = "field", value = "[\"objID\"]")
	@Param(name = "gid", value = "|bandID|")
	@AccessToken
	ChatroomResources getChatroomsByChatoomName(@Mount("bandID") Long bandID, @Mount("chatroomName") String chatroomName);

	@Get("/core/v4/band/|bandID|/tool?")
	@Param(name = "query", value = "{\"toolShopToolID\":\"|toolShopToolID|\"}")
	@Param(name = "field", value = "[\"objID\",\"organizationID\",\"realObjID\",\"mountBandID\",\"name\"]")
	@Param(name = "gid", value = "|bandID|")
	@AccessToken
	ToolResources getToolByToolShopToolID(@Mount("bandID") Long bandID, @Mount("toolShopToolID") Long toolShopToolID);

	@Get("/core/v4/bandRole/band/|bandID|/user/|userID|/bandRoles?")
	@Param(name = "bandID",value ="|bandID|")
	@Param(name = "userID",value ="|userID|")
	@AccessToken
	JSONObject getBandRolesByUserID(@Mount("userID") long userID,@Mount("bandID") long bandID);

	@Get("/core/v4/document/|documentID|?")
	@Param(name = "field", value = "[\"objID\"]")
	@Param(name = "gid", value = "|documentID|")
	@AccessToken
	DocumentResources getDocumentByDocumentID(@Mount("documentID") Long documentID);

	@Get("/core/v4/toolLibrary/toolSource/installable?")
	@Param(name = "bandID", value = "|bandID|")
	@Param(name = "gid", value = "|bandID|")
	@Param(name = "organizationID", value = "|organizationID|")
	@Param(name = "query", value = "{\"toolAlias\":\"|toolAlias|\"}")
	@AccessToken
	ToolSourceResources getInstallableToolsByBandID(@Mount("bandID") Long bandID, @Mount("organizationID") Long organizationID,
													@Mount("toolAlias") String toolAlias);

	@Post("/core/v4/toolLibrary/install/logical?")
	@Param(name = "bandID", value = "|bandID|")
	@Param(name = "gid", value = "|bandID|")
	@Param(name = "toolSourceID", value = "|toolSourceID|")
	@AccessToken
	ResultVO installTool(@Mount("bandID") Long bandID, @Mount("toolSourceID") Long toolSourceID);

	@Get("/core/v4/dispatch?")
	@Param(name = "gid", value = "|bandID|")
	@Param(name = "query", value = "{\"bandID\":\"|bandID|\"}")
	@Param(name = "extend", value = "{\"creator\":[\"userName\",\"userID\"]}")
	@Param(name = "page", value = "|page|")
	@Param(name = "count", value = "|count|")
	@Param(name = "sort", value = "[{\"createDate\":\"desc\"}]")
	@AccessToken
	DispatchResources getDispatchs(@Mount("bandID") Long bandID,
								   @Mount("aid") String aid,
								   @Mount("page") int page,
								   @Mount("count") int count);

    @Post("/core/v4/dispatch/|dispatchID|/stop?")
    @Gid
    @AccessToken
    JSONObject stopDispatch(@Mount("dispatchID") Long dispatchID);

    @Post("/core/v4/dispatch/|dispatchID|/revoke?")
    @Gid
    @AccessToken
    JSONObject revokeDispatch(@Mount("dispatchID") Long dispatchID);

}
