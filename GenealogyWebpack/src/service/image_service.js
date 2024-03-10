import https from '../common/https';
import {getBandId, getBandObjId, getBaseUrl, getCallToolUrl} from "../common/env";

export default class ImageService{
     getPhotoStorageAndSharingUrl = () => {
        return getCallToolUrl() + "&alias=PhotoStorageAndSharing&returnType=VALUE&bandID=" + getBandId();
    }

    createAlbum(name) {

        return new Promise((resolve,reject)=>{
            https.doPost(this.getPhotoStorageAndSharingUrl(), {
                action: 'addAlbum',
                bandObjId: getBandObjId(),
                openAlbum: true,
                introduce: "",
                name: name
            }).then((res) => {
                if (res.data){
                    resolve(res.data)
                }else{
                    reject(null)
                }
            })
        })
    }



    addPhoto(params = { mongoObjId, size, name, suffix, albumId, type, taskCode, duration}){
         return new Promise((resolve,reject)=>{
             https.doPost(this.getPhotoStorageAndSharingUrl() + "&action=addPhoto",params).then((res)=>{
                 resolve(res.data);
             }).catch((e)=>{
                 reject(e)
             });
         })
    }

    getAllPhotoByAlbumId(albumId) {

        return new Promise((resolve,reject)=>{
            https.doGet(this.getPhotoStorageAndSharingUrl(), {
                action: 'getAllPhoto',
                bandId: getBandId(),
                albumId: albumId
            }).then((res) => {
                if (res.data.rows){
                    resolve(res.data)
                }else{
                    reject(null)
                }
            })
        })
    }

        //请求相册列表
    getPageAlbumsByPermission(params){
         return new Promise((resolve,reject)=>{
             https.doGet(this.getPhotoStorageAndSharingUrl(),{
                 action:'getPageAlbumsByPermission',
                 bandObjId:getBandObjId(),
                 pageNum:params.page,
                 pageSize:params.size,
                 isAdmin: params.isAdmin || false,
                 sortOrder: "DESC",
                 sortType: "used",
                 searchContent: "",
             }).then((res) => {
                 if (res.data.rows != undefined){
                     resolve(res.data);
                 }else {
                     reject(null)
                 }
             })
         })
    }

    getAlbumById(albumId){
         return new Promise((resolve,reject)=>{
             https.doGet(this.getPhotoStorageAndSharingUrl(),{
                 action:'getAlbumById',
                 albumId: albumId
             }).then((res) => {
                 if (res.data.result != undefined){
                     resolve(res.data.result);
                 }else {
                     reject(null)
                 }
             })
         })
    }
};