import https from '../common/https';
import {getBandId, getBaseUrl, getUserId} from "../common/env";
import {getDocumentInfoUrl, getDocumentVersionInfoUrl, getViewURL, VISITOR_ID} from "../common/constants";

export default class ObjService {

    // 获取当前帮区的当前工具的我的视图
    getViewObj(objID) {
       return new Promise(async (resolve, reject)=>{
           let response = await https.doGet(getViewURL(objID));
           if (response.data.rows && response.data.rows.length > 0) {
               //返回的数据可能包含游客和岗位的obj，找到等级最高的obj：用户 > 岗位 > 游客
               let userObj;
               let noVisitorObj;
               let visitorObj;
               for (let o of response.data.rows) {
                   if (o.ownerID === getUserId()) {
                       userObj = o;
                       break
                   } else if (o.ownerID !== VISITOR_ID) {
                       noVisitorObj = o;
                   } else if (o.ownerID === VISITOR_ID) {
                       visitorObj = o;
                   }
               }
               resolve(userObj || noVisitorObj || visitorObj);
           } else {
               reject("找不到数据 ");
           }

       })
    }




     getDocumentVersionInfo(id, callback, errorCallback){
        https.doGet(getDocumentVersionInfoUrl(id)).then(function (response) {
            if (response.data.rows && response.data.rows.length > 0){
                callback(response.data.rows[0]);
            } else{
                errorCallback()
            }
        }).catch(function (error) {
            errorCallback()
        });
    }

    getDocumentInfo(id){
        return https.doGet(getDocumentInfoUrl(id),
            {
                gid: getBandId(),
            },
            {
                singleResult: true,
                errorTag: 'getDocumentInfo',
                errorMsg: '获取文档信息失败'
            }
        );
    }
};