import $ from 'jquery'
import localForage from "../../plugin/bfts/localforage.min";

import Fingerprint from './fingerprint.js'
const tmId = new Fingerprint().get();

var async = require('async');
const blockSize = 5*1024*1024;
var smUrl=""
var gid=""
const tmLocalForage = localForage.createInstance({
    name: "TMlocalForage"
  });
export default async function DownloadFiles(downloadParams,beforeDownloadOneFileCallback,
    blockFinishCallback,taskFinishCallbackm,pauseCallback) {
    console.log("进入BFTM下载模块，初始化下载环境")
    let initDownloadEnvResult=await initDownloadEnv(downloadParams)
    if(initDownloadEnvResult==0){return}
    let fileNum = downloadParams.fileIds.length
    //逐一下载文件
    for (let i = 0; i < fileNum; i++) {
        let beforeDownloadOneFileCallbackFormData=new FormData()
        beforeDownloadOneFileCallbackFormData.append("fileId",downloadParams.fileIds[i])
        beforeDownloadOneFileCallback(beforeDownloadOneFileCallbackFormData,function(){})
        let downloadOneFileParams=new FormData()
        downloadOneFileParams.append("fileId",downloadParams.fileIds[i])
        //downloadOneFileParams的参数还有downloadUrl,fileName
        downloadOneFileParams.append("tmId",tmId)
        downloadOneFileParams.append("fileObjectId",downloadParams.mongoObjIds[i])
        downloadOneFileParams.append("context",downloadParams.context)
        downloadOneFileParams.append("userId",downloadParams.userId)
        //downloadOneFileParams.append("downloadUrl",downloadParams.downloadUrls[i])
        downloadOneFileParams.append("accessToken",downloadParams.accessToken)
        // console.log("文件"+i+"的下载地址："+downloadParams.downloadUrls[i])
        downloadOneFileParams.append("fileName",downloadParams.fileNames[i])
        downloadOneFileParams.append("fileId",downloadParams.fileIds[i])
        let downloadFileResult = await downloadOneFile(downloadOneFileParams,blockFinishCallback,pauseCallback)
        console.log("downloadFileResult:"+downloadFileResult)
        if (downloadFileResult!=1){return}//暂停下载或下载失败
    }

    taskFinishCallbackm(function () {
        //待扩展
    })
}


/*
函数：
    下载一个文件
参数：
    downloadOneFileParams:下载一个文件所需参数
    blockFinishCallback：用于回调跟踪进度
    pauseCallback：用于触发暂停

返回值：
   -1为暂停下载
   0为下载失败
   1为下载成功
*/
function downloadOneFile(downloadOneFileParams,blockFinishCallback,pauseCallback){
    return new Promise(async function(resolve){
        //检查文件是否下载到SM
        let getDownloadTaskInfoParams=new FormData()
        getDownloadTaskInfoParams.append("tmId",tmId)
        getDownloadTaskInfoParams.append("context",downloadOneFileParams.get("context"))
        getDownloadTaskInfoParams.append("fileObjectId",downloadOneFileParams.get("fileObjectId"))
        getDownloadTaskInfoParams.append("userId",downloadOneFileParams.get("userId"))
        getDownloadTaskInfoParams.append("accessToken",downloadOneFileParams.get("accessToken"))
        let getDownloadTaskInfoResult=await getDownloadTaskInfo(getDownloadTaskInfoParams)
        if(getDownloadTaskInfoResult==0){console.log("获取下载信息失败");resolve(0);return}
        //总块数
        let totalBlock=getDownloadTaskInfoResult.totalBlock
        let downloadTaskId=getDownloadTaskInfoResult.id
        //判断Sm是否已经准备好文件
        //存到localForage的前缀
        let blockPrefix=downloadOneFileParams.get("fileObjectId")

        //总块数
        //查询上次的下载记录，从第0块开始
        console.log("准备下载文件")
        let firstBlock=await checkBlockOffset(blockPrefix,0)
        //下载文件块
        for(var i=firstBlock;i<totalBlock;i++){
            let downloadFileFromSmParams=new FormData()
            downloadFileFromSmParams.append("downloadTaskId",downloadTaskId)
            downloadFileFromSmParams.append("blockNo",i)
            downloadFileFromSmParams.append("accessToken",downloadOneFileParams.get("accessToken"))
            let downloadFileFromSmBlock=await getFileBlockFromSm(downloadFileFromSmParams,pauseCallback)
            if(downloadFileFromSmBlock==-1){resolve(-1);return}
            //保存文件块到localForage
            let blockKey=blockPrefix+i.toString()
            console.log("blockKey:"+blockKey)
            let saveResult=await saveBlockToLocalforage(blockKey,downloadFileFromSmBlock)
            if (saveResult!=true){console.log("保存文件块到localForage出错");resolve(0)}
            let blockFinishCallbackFormData=new FormData()
            blockFinishCallbackFormData.append("fileId",downloadOneFileParams.get("fileId"))
            blockFinishCallbackFormData.append("finishBlock",i+1)
            blockFinishCallbackFormData.append("totalBlock",totalBlock)
            blockFinishCallback(blockFinishCallbackFormData,function(){})
        }
        console.log("合并文件块并下载到本地")
        //所有文件块下载到localForage后，合并文件块并下载到本地
        console.log("文件名为："+downloadOneFileParams.get("fileName"))
        let downloadOneFileToLocalResult=await downloadOneFileToLocal(blockPrefix.toString(),downloadOneFileParams.get("fileName"))
        console.log("downloadOneFileToLocalResult"+downloadOneFileToLocalResult)
         //下载完成后清除块
        let removeResult=1
        let removeBlockNum=0
        while(removeResult!=0){//删除不成功表示全部已经删除
            // console.log("removeBlockNum:"+removeBlockNum)
            removeResult=await removeFileBlock(blockPrefix,removeBlockNum++)
        }
        resolve(1)
    })

}


/*
函数：
    开始下载前先获取下任务的id
    如果此前下载过，则获取之前的任务信息，如果之前没有下载过

参数：
    saId:SA端文件id
    tmId：标识Tm
    context；下载上下文
    userId；用户id

返回值：
   任务信息，id为0则为没有下载到SM
   0为查询失败
*/
function getDownloadTaskInfo(params){
    return new Promise(function(resolve){
        //初始化ajax请求参数
        let formData=new FormData()
        formData.append("tmId",tmId)
        formData.append("fileObjectId",params.get("fileObjectId"))
        formData.append("context",params.get("context"))
        formData.append("userId",params.get("userId"))
        formData.append("accessToken",params.get("accessToken"))
        $.ajax({
        url: smUrl+"/getDownloadTaskInfo"+"?gid="+gid,
        type: 'POST',
        cache: false,
        data: formData,
        processData: false,
        contentType: false,
        dataType: 'json',
        }).done(function (res) {
               if(res.Code==1){
                //添加新上传任务进度成功
                resolve(res.Rows[0])
               }
               else{resolve(0)}
        }) //.done结束
    })//function(resolve){}结束

}

/*
函数：
    TM从SM下载文件块

参数：
    downloadTaskId:下载任务的id
    blockNo:第几块

返回值：
   res:文件块

   -1为暂停下载
*/
function getFileBlockFromSm(params,pauseCallback){
    return new Promise(function(resolve){
        pauseCallback(function(pauseDownload){
            if(pauseDownload){console.log("暂停下载");resolve(-1);return}
        })
        //初始化ajax请求参数
        let formData=new FormData()
        formData.append("downloadTaskId",params.get("downloadTaskId"))
        formData.append("blockNo",params.get("blockNo"))
        formData.append("accessToken",params.get("accessToken"))
        let xhr = new XMLHttpRequest();
        xhr.responseType = "blob";
        xhr.timeout=50000;
        xhr.onload = async function () {
            if (this.status == 200) {
                var blob = this.response;
                if (blob.type == "text/html") {
                    alert("服务器返回格式出错");
                    resolve(0)
                }
                resolve(blob)

            } else {
                console.log("准备重下");
                let waitResult= await waitSeconds(5)
                let result=await getFileBlockFromSm(params,pauseCallback)
                resolve(result)
            }

        }
        xhr.ontimeout=async function(){
            console.log("请求超时准备重下");
            let waitResult= await waitSeconds(5)
            let result=await getFileBlockFromSm(params,pauseCallback)
            resolve(result)
        }
        // xhr.onloadend = function(res){
        // }
        let url= smUrl+"/getFileBlockFromSm"+"?gid="+gid
        xhr.open("POST", url, true);
        // var formData = new FormData();
        // formData.append("offset", offset);//标识下载哪个文件块
        // formData.append("bftsId", bftsId);//根据文件id进行下载
        xhr.send(formData);

    })//function(resolve){}结束
}


//获取上次下载进度，以便从上次断点继续下载
function checkBlockOffset(blockPrefix, i) {
    return new Promise(function (resolve) {
        let blockName = blockPrefix + i.toString()
        tmLocalForage.getItem(blockName)
            .then(function (value) {
                if (value != null) {
                    console.log("checkBlock：" + i.toString() + "已存在")
                    resolve(checkBlockOffset(blockPrefix, ++i))
                } else {
                    if ((i - 1) >= 0) resolve(i - 1);//之所以是i-1是因为有可能第i-1块上次还未保存完整
                    resolve(0)
                }
            })
    })
}


//下载文件到本地
function downloadOneFileToLocal(blockPrefix,fileName){
    console.log("开始合并块！")
    return new Promise(async function(resolve){
        //合并块
        let fileBlockMerge = []
        let blockNum=0
        let getFileBlockFromLocalForageResult=0

        while(getFileBlockFromLocalForageResult!=1){
            getFileBlockFromLocalForageResult=await getFileBlockFromLocalForage(blockPrefix,blockNum)
            fileBlockMerge[blockNum]=getFileBlockFromLocalForageResult
            blockNum++
        }
        //下载合并块到本地
        let merge = new Blob(fileBlockMerge, {type: "application/octet-stream;charset=utf-8"})
        console.log("创建下载连接")
        let link = document.createElement('a')//创建连接下载
        link.href = window.URL.createObjectURL(merge)//传入所有分片合并后生成的Blob对象
        link.download = fileName;//保存出来的文件名称
        link.click();//触发内存数据存到文件的操作
        window.URL.revokeObjectURL(link.href)//释放内存
        fileBlockMerge=[]
        resolve(1)
    })

}


function getFileBlockFromLocalForage(blockPrefix,blockNum){
    return new Promise(function(resolve){
        let blockName=blockPrefix.toString()+blockNum.toString()
        tmLocalForage.getItem(blockName.toString())//获取待文件块的文件块
        .then(function (value) {
            if (value != null) {
                console.log("获得localForage块：" + blockNum)
                resolve(value)
            } else {
                console.log("合并块已完成")
                resolve(1)
            }
        })
    })

}

function removeFileBlock(blockPrefix,removeBlockNum){
    return new Promise(function(resolve){
        let blockName=blockPrefix.toString()+removeBlockNum.toString()
        // console.log("删除块名为"+blockName)
        tmLocalForage.getItem(blockName.toString()).then(function(value){
            if(value==null){
            // 当删除完毕
            console.log("全部块删除完毕")
            resolve(0)
            }
            else{
            tmLocalForage.removeItem(blockName.toString()).then(function() {
                console.log("删除块"+removeBlockNum+"完毕")
                resolve(1)
                })
            }
        })

    })

}


//等待秒数函数
function waitSeconds(second){
    return new Promise(function(resolve){
        setTimeout(function(){
                resolve(second)
        },second*1000)
    })
}

//初始化上传环境
function initDownloadEnv(downloadParams){
    return new Promise(function (resolve){
        //确保有tm_id
        if(localStorage.getItem("tmId")==null){//如果该终端没有id，则初始化终端id，tmId
            let timestamp = (new Date()).getTime();//当前时间戳
            let radomInt=Math.ceil(Math.random()*10000);//时间戳和随机数拼接获得新的tmid
            let tmIdGenerated= timestamp.toString()+radomInt.toString()
            localStorage.setItem("tmId",tmIdGenerated)
        }
        tmLocalForage.config({//根据下载任务文件的大小配置indexedDB
            size: 10*1024*1024*1024, // Size of database, in bytes. WebSQL-only for now.
        });
        // downloadBlockLocalForage = localforage.createInstance({
        //     name: "downloadBlockLocalForage"
        //   });
        // tempBlockTotal = []
        gid=downloadParams.gid
        smUrl=downloadParams.smUrl
        resolve("初始化TM上传模块成功！")
        resolve(1)
    })
}
//获取文件分块后保存至indexedDB中
function saveBlockToLocalforage(key, block) {
    return new Promise(function (resolve) {
        tmLocalForage.setItem(key, block, () => {
        })
            .then(function (value) {    // 当值被存储后，可执行其他操作
                console.log(value);
                resolve(true)
            }).catch(function (err) {    // 当出错时，此处代码运行
            console.log(err);
        })
    })

}
export {
    DownloadFiles
}
