package com.genealogy.httpapi;

import com.fy.javanode.declarative.annotation.HttpApi;
import com.fy.javanode.declarative.annotation.Mount;
import com.fy.javanode.declarative.annotation.Param;
import com.fy.javanode.declarative.annotation.Post;

@HttpApi(urlFieldPath = "com.fy.tre.system.SystemConstants MAIN_NODE_URL", version = HttpApi.VERSION_2)
public interface MessageToolApi {
	@Post(url = "/runSystemTool?param=|param|&accessToken=|accessToken|")
	@Param(name = "gid", value = "|gid|")
	@Param(name = "returnType", value = "VALUE")
	@Param(name = "toolID", value = "2217851")
	String sendMsgToUser(@Mount("accessToken") String accessToken, @Mount("param") String param, @Mount("gid") Long gid);
}
