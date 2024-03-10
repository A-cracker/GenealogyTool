import https from '../common/https';
import {getBandId, getBaseUrl} from "../common/env";
import {getUploadToolUrl} from "../common/constants";
import Utils from "../common/utils";

export default class Service {

    // 获取当前帮区的当前工具的我的视图
    getPedigrees({page, count, query}) {
        return new Promise((resolve, reject) => {
            https.doGet(getBaseUrl(), {
                action: 'getPedigrees',
                page: page,
                count: count,
                query: query
            }).then((res) => {
                if (res.data) {
                    resolve(res.data)
                } else {
                    reject(null)
                }
            })
        })
    }


    /**
     * 上传文档
     */
    async uploadFile(file, callback) {
        let formData = new FormData();
        formData.append("fileToUpload", file);
        const resultJson = await https.doPost(getUploadToolUrl() + "&toolAction=uploadDocument&returnType=VALUE", formData,
            {
                errorTag: 'uploadFile',
                errorMsg: '上传文档失败'
            }
        );
        if (resultJson.data.success) {
            return this.submitFile(resultJson.data.storageID, file, callback);
        }

    }

    /**
     * 提交已上传文档到资料柜
     */
    submitFile(storageID, file) {
        const fileName = Utils.getFileName(file.name);
        const fileExt = Utils.getFileSuffix(file.name);
        const documentJson = {
            "name": fileName,
            "keywords": fileName,
            "description": fileName
        };
        const documentVersionJson = {
            "storageID": storageID,
            "size": file.size,
            "description": fileName,
            "extension": fileExt,
            "versionName": fileName
        };
        let param = {
            toolAction: 'confirmUploadDocument',
            returnType: 'VALUE',
            storageID: storageID,
            uploadType: 0,
            bandID: getBandId(),
            document: JSON.stringify(documentJson),
            documentVersion: JSON.stringify(documentVersionJson)
        }

        return https.doPost(getUploadToolUrl() + "&toolAction=confirmUploadDocument", param,
            {
                errorTag: 'uploadFile',
                errorMsg: '上传文档失败'
            }
        );

    };
}