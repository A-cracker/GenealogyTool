package com.genealogy.constant;

import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

public class Constant {
	public static final String IMAGE_ROLE = "亲网发布角色";

	public static String imageRole;

	static {
		JSONObject role = new JSONObject();
		role.put("roleName", IMAGE_ROLE);
		role.put("description", IMAGE_ROLE);
		role.put("chatroomPermission",new String[]{"browse","sendMessage"});
		role.put("documentPermission",new String[]{"preview","download"});
		role.put("toolPermission", new String[]{"browse","run"});
		role.put("bandPermission",new String[]{"create","createDocument","createTool","createRole","deleteRole","deleteUser","sshare","tshare","updateRole"});
		imageRole = role.toJSONString();
	}
	public static String getImageRoleAuthorizationItems(Map<String, Long> toolNameIDs) {
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		StringBuilder arr = new StringBuilder("[");

		if (!toolNameIDs.isEmpty()){
			Iterator<Map.Entry<String, Long>> iterable= toolNameIDs.entrySet().iterator();
			while (iterable.hasNext()){
				Map.Entry<String, Long> entry = iterable.next();

				String s = "{\"authorizationItemObjType\":\"TOOL\",\"objID\":\"" + entry.getValue() + "\",\"objName\":\"" + entry.getKey() + "\",\"permissionNames\":[\"browse\",\"run\"],\"objCreateDate\":\"" + time + "\"}";
				arr.append(s).append(",");
			}
			arr.deleteCharAt(arr.length()-1);
		}

		return arr.append("]").toString();
	}
}
