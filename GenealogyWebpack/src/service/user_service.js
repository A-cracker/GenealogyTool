import BaseService from 'service/base_service';

import https from '../common/https';
import {getBaseUrl} from "../common/env";

export default class UserService extends BaseService {

    // 获取当前帮区的当前工具的我的视图
    getCurrentToolMyView(callback) {
        https.doGet(getBaseUrl(), {
            action: 'getCurrentToolMyView'
        }).then((res) => {
            if (res.data.rows && res.data.rows.length){
                callback(res.data.rows[0])
            }else{
                callback(null)
            }
        })
    }
    // 获取当前帮区的当前工具的我的视图
    getBandAllUsers(callback) {
        https.doGet(getBaseUrl(), {
            action: 'getBandAllUsers'
        }).then((res) => {
                callback(res.data)
        })
    }
    // 获取当前帮区的当前工具的我的视图
    getUsers({page, size, searchName}) {
        return https.doGet(getBaseUrl(), {
            action: 'getUsers',
            page: page,
            pageSize: size,
            searchName: searchName
        })
    }
};