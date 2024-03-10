import https from '../common/https';
import {getBaseUrl} from "../common/env";

export default class MaterialService{
    // 获取当前帮区的当前工具的我的视图
    getMaterialByID(id) {

        return new Promise((resolve,reject)=>{
            https.doGet(getBaseUrl(), {
                action: 'getMaterialByID',
                ID: id
            }).then((res) => {
                if (res.data){
                    resolve(res.data)
                }else{
                    reject(null)
                }
            })
        })
    }
    getWorkAssignmentByID(id){
        return new Promise((resolve,reject)=>{
            https.doGet(getBaseUrl(), {
                action: 'getWorkAssignmentByID',
                ID: id
            }).then((res) => {
                if (res.data){
                    resolve(res.data)
                }else{
                    reject(null)
                }
            })
        })
    }

};