package com.genealogy.httpapi;

import com.alibaba.fastjson.JSONObject;
import com.fy.javanode.declarative.annotation.*;

@HttpApi(urlFieldPath = "com.fy.tre.system.SystemConstants MAIN_NODE_URL", version = HttpApi.VERSION_2)
@AccessToken
@Param(name = "gid", value = "1")
@Param(name = "alias", value = "OrganInterfaceTool")
@Param(name = "returnType", value = "VALUE")
public interface IOrganInterfaceToolApi {

	@Get(url = "/runSystemTool?")
	@Param(name="param", value =  "{'toolAction':'getPlatformUsers'}")
	@Param(name="userName", value = "|searchContent|")
	@Param(name="userAccount", value = "|searchContent|")
	@Param(name="page", value = "|page|")
	@Param(name="count", value = "|count|")
	@Param(name="field", value = "[\"userName\", \"userID\"]")
	JSONObject getUsers(@Mount("page") Integer page,
                           @Mount("count") Integer count,
                           @Mount("searchContent") String searchContent);


}
