import BaseService from 'service/base_service';

import https from '../common/https';
import {getBaseUrl} from "../common/env";

export default class UpgradeService extends BaseService {

    test() {

        return new Promise((resolve,reject)=>{
            https.doGet(getBaseUrl(), {action: 'test'}).then((res) => {
                if (res.data){
                    resolve(res.data)
                }else{
                    reject(null)
                }
            })
        })
    }

    upgrade(param) {
        param['action'] = 'upgrade'

        return new Promise((resolve,reject)=>{
            https.doGet(getBaseUrl(), param).then((res) => {
                if (res.data.result){
                    resolve(res.data)
                }else{
                    reject(null)
                }
            })
        })
    }
};