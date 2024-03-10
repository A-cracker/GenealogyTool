import {getBandId, getBandObjId, getBaseUrl, getCallToolUrl, getUserId, pageBack, toast} from "../common/env";
import https from '../common/https';
import axiosIns from 'axios';
import {getCreateDispatchUrl} from "../common/constants";


export default class ShareService{
    getPhotosByShareId(callback,shareID) {
        https.doGet(getBaseUrl(),
            {
                action: 'getPhotosByShareId',
                shareID: shareID
            }
        ).then((res) => {
            callback(res.data);
        })
    }

    getDispatchs(callback,param){
        https.doGet(getBaseUrl(),
            {
                action: 'getDispatchs',
                page: param.page,
                count: param.count
            }
        ).then((res) => {
            callback(res.data);
        })
    }

    stopDispatch(callback,dispatchID){
        https.doGet(getBaseUrl(),
            {
                action: 'stopDispatch',
                dispatchID: dispatchID,
            }
        ).then((res) => {
            callback(res.data);
        })
    }
    revokeDispatch(callback,dispatchID){
        https.doGet(getBaseUrl(),
            {
                action: 'revokeDispatch',
                dispatchID: dispatchID,
            }
        ).then((res) => {
            callback(res.data);
        })
    }

    async createShare(photoIds){
        let formData = new FormData();
        formData.append('photoIds',JSON.stringify(photoIds));

        let data = await axiosIns.post(getBaseUrl() + "&action=share", formData,{
            headers: {
                'Content-Type': 'multipart/form-data',
            }
        });

        return data.data.result ? data.data.msg : null;
    }

    // 获取当前帮区的当前工具的我的视图
    getImageRole(callback) {
        https.doGet(getBaseUrl(), {
            action: 'getShareRole'
        }).then((res) => {
            if (res.data.rows && res.data.rows.length){
                console.log()
                callback(res.data.rows[0])
            }else{
                callback(null)
            }
        })
    }
    //获取微信分享权限
    getSharePermission(callback){
        https.doGet(getBaseUrl(), {
            action: 'getSharePermission'
        }).then((res) => {
            if (res.data.rows && res.data.rows.length){
                callback(res.data.rows[0])
            }else{
                callback(null)
            }
        })
    }

    createShareInCore(callback, errCallback,params,permission) {
        if(!permission){
            errCallback && errCallback(permission)
        }else{
            https.doPost(getCreateDispatchUrl(), {
                bandID: getBandId(),
                dispatch: JSON.stringify({
                    "dispatchName": params.name,
                    "description": params.name,
                    "fromOrganizationID": window.tool.organizationID,
                    "toOrganizationID": window.tool.organizationID
                }),
                bandRole: JSON.stringify({"bandID": getBandId(), "roleID": window.role.roleID, roleName: window.role.roleName}),
                dispatchItems: JSON.stringify([{
                    "objName": "照片存储分享工具",
                    "objID": window.tool.objID+"",
                    "realObjID": window.tool.realObjID,
                    "objType": "TOOL",
                    "permissionNames": ["browse", "run"]
                }]),
                targetUserIDs: JSON.stringify([getUserId()])
            }).then((res) => {
                if (res.data.result && res.data.result.result) {
                    callback(res.data.result.result);
                } else {
                    errCallback && errCallback(permission)
                }
            }).catch((e)=>{
                console.log(e)
                errCallback && errCallback(permission)
            })
        }

    }

}
