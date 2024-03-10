package com.genealogy.httpapi;

import com.alibaba.fastjson.JSONObject;
import com.fy.javanode.declarative.annotation.*;
import org.springframework.core.io.ByteArrayResource;

@HttpApi(version = HttpApi.VERSION_2,toolAlias = "fileUploadTool")
@AccessToken
@Param(name="bandID", value = "|bandID|")
public interface IFileUploadToolCallApi {
	@Post
	@FormField(name = "file" ,value = "|file|" ,type = FormField.FILE)
	@Param(name = "toolAction",value ="uploadDocument")
	public JSONObject upload(@Mount("bandID") Long bandID, @Mount("file") ByteArrayResource file);

	@Post
	@Param(name="toolAction", value = "confirmUploadDocument")
	@Param(name="uploadType", value = "0")
	@Param(name="storageID", value = "|storageID|")
	@FormField(name="documentVersion", value = "{\"storageID\":\"|storageID|\",\"size\":|size|,\"description\":\"|name|\",\"extension\":\"|extension|\",\"versionName\":0}")
	@FormField(name = "document", value = "{\"name\":\"|name|\",\"keywords\":\"|name|\",\"description\":\"|name|\"}")
	@Header(name = "content-type", value = "application/x-www-form-urlencoded")
	public JSONObject confirmUpload(@Mount("bandID") Long bandID,
                                    @Mount("storageID") String storageID, @Mount("size") Integer size,
                                    @Mount("name") String name, @Mount("extension") String extension);

}
