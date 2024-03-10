import $ from 'jquery'
import Fingerprint from './fingerprint.js'
import {getFileBlockMd5 as getFileBlockMd5, getFileHashMd5 as getFileHashMd5} from "./md5_util.js"
import {getAccessToken, getBandId, getUserId} from "../../common/env";

const blockSize = 5 * 1024 * 1024;

const tmId = new Fingerprint().get();


/**
 * 上传成功
 * @type {number}
 */
const STATUS_SUCCESS = 1;

/**
 * 重试上传
 * @type {number}
 */
const STATUS_TRY = -2;

/**
 * 中止上传
 * @type {number}
 */
const STATUS_ABORT = 3;


//传多个文件，一次只传一个文件
/*
文件状态顺序依次为
1.等待上传
2.正在计算md5
3.正在上传
4.完成上传
5.保存至MongoDb
*/

export default class BigFileTM {


    /**
     * @param smUrl    服务端URL
     * @param userId    用户ID，默认为当前会话的用户
     * @param gid   gid，默认为运行工具的帮区ID
     * @param context   应用上下文ID，每个接入大文件的应用都有一个context
     * @param accessToken   默认为当前会话accessToken
     */
    constructor({smUrl, userId = getUserId(), gid = getBandId(), context, accessToken = getAccessToken()}) {
        this.smUrl = smUrl;
        this.userId = userId;
        this.gid = gid;
        this.context = context;
        this.accessToken = accessToken;

        /**
         * 等待上传文件Map：唯一ID为key，file为value
         */
        this.waitUploadFileMap = new Map();

        /**
         * 上传失败文件Map：唯一ID为key，file为value
         */
        this.uploadFailureMap = new Map();

        /**
         * 是否在上传文件
         * @type {boolean}
         */
        this.uploading = false;

        /**
         * 当前上传文件ID
         */
        this.currentUploadId;

        /**
         * 是否暂停上传
         * @type {boolean}
         */
        this.isPause = false;

        /**
         * 上传文件块的ajax实例
         */
        this.uploadBlockAjax;


        /**
         * 上传单个文件前回调
         *
         * @param fileId 文件ID
         */
        this.onBeforeUploadFileCallback = (fileId) => {};


        /**
         * 单个文件上传完成回调
         *
         * @param fileId 文件ID
         * @param mongoId 后台返回的mongoID
         * @param file 文件实体
         * @param duration 视频文件时长
         */
        this.onSaveFileFinishCallback = ({fileId, mongoId, file, duration}, removeFile) => {};


        /**
         * 任务上传完成回调
         */
        this.onTaskFinishCallback = () => {};

        /**
         * 任务暂停回调
         */
        this.onPauseCallback = () => {};

        /**
         * 单个文件上传失败回调
         *
         * @param fileId 文件ID
         */
        this.onFailureCallBack = (fileId, e) => {};

        /**
         * 单个文件上传进度回调
         *
         * @param fileId 文件ID
         * @param progress 进度
         */
        this.onProgressCallback = (fileId, progress) => {};

        // _initUploadEnv();
    }


    /**
     * 添加文件
     *
     * @param idFileMap: json数组，[{id: 唯一ID，file: 文件}]。
     */
    addFiles(idFiles) {
        for (const {id, file} of idFiles){
            this.waitUploadFileMap.set(id, file);
        }

        this._start();
    }

    /**
     * 暂停上传
     */
    pause() {
        this.isPause = true;
        this.uploading = false;

        this.uploadBlockAjax && this.uploadBlockAjax.abort();
    }

    /**
     * 继续上传
     */
    continue() {
        this.isPause = false;

        this._start();
    }


    /**
     * 移除文件
     * @param fileId
     */
    remove(fileId){
        if (fileId == this.currentUploadId){
            console.log('移除的文件上传中，取消上传。')
            this.uploadBlockAjax && this.uploadBlockAjax.abort();
        }

        this.waitUploadFileMap.delete(fileId);
        this.uploadFailureMap.delete(fileId)
    }

    /**
     * 重试
     * @param fileId
     */
    retry(fileId){
        const file = this.uploadFailureMap.get(fileId);

        if (file != null){
            this.waitUploadFileMap.set(fileId, file);
        }

        this.uploadFailureMap.delete(fileId);
        this._start();
    }


    /**
     * 开始上传
     */
    async _start() {

        if(this.uploading) {
            return;
        }

        this.uploading = true;

        //按次序上传文件
        for (const [id, file] of this.waitUploadFileMap) {

            this.currentUploadId = id;

            if (this.isPause) {
                this.uploading = false;
                return;
            }


            this.onBeforeUploadFileCallback && this.onBeforeUploadFileCallback(id);

            //传输文件
            let uploadOneFileToSmResult;

            try {
                uploadOneFileToSmResult = await this._uploadOneFileToSm(file);
            }catch(e){
                console.log(`上传文件${id}出错`, e);

                this._uploadFailure(id, file, e);

                if (this.waitUploadFileMap.size) {
                    continue;
                }

                this.uploading = false;
                return
            }

            if (uploadOneFileToSmResult === STATUS_ABORT){
                console.log('中止上传');
                continue
            }

            //上传后提交（现改为不提交）
            //上传后保存至mongoDb
            let saveFileToMongoDbParams = {
                "uploadTaskId": uploadOneFileToSmResult.uploadTaskId,
                "accessToken": this.accessToken
            };

            let saveFileToMongoDbResult;

            try {
                saveFileToMongoDbResult = await this._saveFileToMongoDb(saveFileToMongoDbParams);
            }catch (e){
                console.log(`保存文件${id}到MongoDb出错`, e);

                this._uploadFailure(id, file, e);

                if (this.waitUploadFileMap.size) {
                    continue;
                }

                this.uploading = false;
                return
            }

            //回调TA
            let saveFinishCallbackData = {
                "fileId": id,
                "mongoId": saveFileToMongoDbResult.fileObjectId,
                "duration": saveFileToMongoDbResult.duration,
                file: file
            };
            try {
                this.onSaveFileFinishCallback && await this.onSaveFileFinishCallback(saveFinishCallbackData,()=>{
                    this.waitUploadFileMap.delete(id);
                } );
            }catch (e){
                this._sendErr(e)
            }

        }

        this.onTaskFinishCallback && this.onTaskFinishCallback();
        console.log("完成所有文件的上传")

        this.uploading = false;

    }

    /**
     * 上传失败，将文件移动到失败Map
     * @param id
     * @param file
     * @private
     */
    _uploadFailure(id, file, e){
        try{
            this.onFailureCallBack && this.onFailureCallBack(id, e);
        }catch (e){
            this._sendErr(e)
            return
        }
        this.waitUploadFileMap.delete(id);
        this.uploadFailureMap.set(id, file);

        this._sendErr(e)
    }

    _sendErr(e){
        console.error(e);
        setTimeout(()=>{
            throw e;
        }, 1000);
    }


    /**
     *  上传一个文件
     *
     * @param id 唯一ID
     * @param file 文件
     * @return
     * @private
     */
    _uploadOneFileToSm(file) {
        const _this = this;
        return new Promise(async function (resolve, rejected) {
           /* if (window.failure){
                rejected('failure');
                return
            }*/

            if (file == null){
                rejected('没有文件');
                return
            }

            let fileSize = file.size;
            //生成文件md5
            let fileMd5
            try {
                fileMd5 = await getFileHashMd5(file)
            } catch (e) {
                console.log('获取md5失败', e);
                rejected(e)
                return
            }

            let getUploadTaskInfoFormData = {
                "tmId": tmId,
                "context": _this.context,
                "fileMd5": fileMd5,
                "userId": _this.userId,
                "accessToken": _this.accessToken,
                "fileSize": fileSize
            }

            let getUploadTaskInfoResult;
            try {
                getUploadTaskInfoResult = await _this._getUploadTaskInfo(getUploadTaskInfoFormData)
            }catch (e){
                console.log('getUploadTaskInfo失败')
                rejected(e)
                return
            }


            //判断是否上传过，根据情况不同，逻辑业务不同
            if (getUploadTaskInfoResult.isComplete === 0 && getUploadTaskInfoResult.finishBlock < getUploadTaskInfoResult.totalBlock) {//表示在SM端没有完整的临时文件，不能直接提交
                let firstBlock = getUploadTaskInfoResult.finishBlock; //该上传文件最先上传的是第几块
                let totalBlock = getUploadTaskInfoResult.totalBlock; //该上传文件多少块
                let uploadTaskId = getUploadTaskInfoResult.id; //初始化该上传文件对应SM端的上传任务id
                for (var i = firstBlock; i < totalBlock; i++) {
                    let start = i * blockSize;
                    let end = start + blockSize;
                    //如果最后一块小于blockSzie，则end改为文件大小
                    if (end > fileSize) end = fileSize;
                    //切割文件
                    let fileBlock = file.slice(start, end);

                    let tmFileBlockMd5;
                    try {
                        tmFileBlockMd5 = await getFileBlockMd5(fileBlock);
                    } catch (e) {
                        console.log('获取文件块md5失败', e);
                        rejected(e);
                        return;
                    }

                    //生成切割文件块的md5
                    let uploadFileBlockToSmFormData = {
                        "fileBlock": fileBlock,
                        "tmFileBlockMd5": tmFileBlockMd5,
                        "uploadTaskId": uploadTaskId,
                        "accessToken": _this.accessToken,
                        "currentBlock": i + 1,
                        "totalBlock": totalBlock
                    };

                    //上传文件块到SM
                    for (let tryTime = 0; ; tryTime++) {
                        let uploadFileBlockToSmResult
                        try {
                            uploadFileBlockToSmResult=  await _this._uploadFileBlockToSm(uploadFileBlockToSmFormData)
                        }catch (e){
                            console.log('uploadFileBlockToSm失败')
                            rejected(e);
                            return
                        }

                        if (uploadFileBlockToSmResult === STATUS_SUCCESS)
                            break;//上传成功

                        if (uploadFileBlockToSmResult === STATUS_ABORT){
                            resolve(STATUS_ABORT);
                            return
                        }

                        //网络开小差，一段时间后重新传输
                        await _waitSeconds(5)
                    }

                    //该文件上传完成，返回
                    if ((i + 1) == totalBlock) {
                        let uploadOneFileToSmResult = {
                            "fileMd5": fileMd5,
                            "uploadTaskId": uploadTaskId
                        }
                        resolve(uploadOneFileToSmResult)
                    }

                }
            } else {//该文件曾上传完成
                console.log("在有效期内已上传完毕，直接提交即可");
                let uploadOneFileToSmResult = {
                    "fileMd5": fileMd5,
                    "uploadTaskId": getUploadTaskInfoResult.id
                };

                resolve(uploadOneFileToSmResult)
            }
        })
    }


    /**
     *
     *  调用SM端接口
     *  传输文件块到SM
     *  传输成功后回调给TA
     *
     *  参数：
     *  fileMd5:文件块md5
     *  uploadTaskId：上传任务的id
     *  totalBlock：回调数据,表示总共有多少块要上传
     *  finishBlock：回调数据，表示准备要完成上传的是第几
     *  返回值：
     *  1为上传成功，0为上传失败
     *
     * @param uploadFileBlockToSmParams
     * @private
     */
    _uploadFileBlockToSm(uploadFileBlockToSmParams) {
        const _this = this;
        return new Promise(function (resolve, rejected) {

            //初始化ajax请求参数
            let uploadFileBlockToSmData = new FormData();
            uploadFileBlockToSmData.append("fileBlock", uploadFileBlockToSmParams.fileBlock);
            uploadFileBlockToSmData.append("uploadTaskId", uploadFileBlockToSmParams.uploadTaskId);
            uploadFileBlockToSmData.append("tmFileBlockMd5", uploadFileBlockToSmParams.tmFileBlockMd5);
            uploadFileBlockToSmData.append("accessToken", uploadFileBlockToSmParams.accessToken);
            //新增当前第几块
            uploadFileBlockToSmData.append("currentBlock", uploadFileBlockToSmParams.currentBlock);

            let currentBlock = uploadFileBlockToSmParams.currentBlock - 1;
            let totalBlock = uploadFileBlockToSmParams.totalBlock;

            let average = 100 / totalBlock;


            _this.uploadBlockAjax = $.ajax({
                url: _this.smUrl + "/uploadFileBlockToSmWithCheckProcess?gid=" + _this.gid,
                type: 'POST',
                cache: false,
                data: uploadFileBlockToSmData,
                processData: false,
                contentType: false,
                dataType: 'json',
                timeout: 100000,
                xhr: function () {
                    let xhr = new XMLHttpRequest();
                    //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
                    xhr.upload.addEventListener('progress', function (e) {
                        let progress = e.loaded / e.total;
                        let totalProgress = currentBlock * average + progress * average;
                        _this.onProgressCallback && _this.onProgressCallback(_this.currentUploadId, Math.floor(totalProgress))
                    });

                    return xhr;
                },
                success: function (res) {
                    console.log("uploadFileBlockToSm返回信息:" + res.Code)
                    if (res.Code == 1) {
                        //表示上传成功
                        resolve(STATUS_SUCCESS)
                    } else if (res.Code == -2) {
                        resolve(STATUS_TRY)
                    } else {
                        //表示上传失败
                        rejected(res)
                    }
                },
                complete: async function (status) { //请求完成后最终执行参数
                    if (status.statusText == 'abort'){
                        console.log("网络状况abort");
                        resolve(STATUS_ABORT);
                    }else if (status.statusText != 'success' && status.statusText != 'OK') {//超时,status还有success,error等值的情况
                        console.log("网络正在开小差！等待一段时间后重新上传文件块");
                        resolve(STATUS_TRY);
                    }
                }
            })

        })

    }

    /**
     *    函数：
     *      调用SM端接口
     *      添加新的上传任务记录
     *      参数：
     *      fileMd5:该文件的md5
     *      fileSize：文件大小
     *      tmId:标识设备的id
     *      context:调用大文件传输中间件的上下文
     *      userId：用户id
     *      返回值：
     *      resultFormData
     *      0请求失败
     *      -1网络异常
     *
     * @private
     */
    _getUploadTaskInfo(getUploadTaskInfoParams) {
        const _this = this;
        return new Promise(function (resolve, rejected) {

            //初始化ajax请求参数
            let getUploadTaskInfoFormData = new FormData();
            getUploadTaskInfoFormData.append("tmId", getUploadTaskInfoParams.tmId);
            getUploadTaskInfoFormData.append("context", getUploadTaskInfoParams.context);
            getUploadTaskInfoFormData.append("fileMd5", getUploadTaskInfoParams.fileMd5);
            getUploadTaskInfoFormData.append("userId", getUploadTaskInfoParams.userId);
            getUploadTaskInfoFormData.append("fileSize", getUploadTaskInfoParams.fileSize);
            getUploadTaskInfoFormData.append("accessToken", getUploadTaskInfoParams.accessToken);
            $.ajax({
                url: _this.smUrl + "/getUploadTaskInfo" + "?gid=" + _this.gid,
                type: 'POST',
                cache: false,
                data: getUploadTaskInfoFormData,
                processData: false,
                contentType: false,
                dataType: 'json',
            }).done(function (res) {
                if (res.Code == 1) {
                    //添加新上传任务进度成功
                    resolve(res.Rows[0])
                }
                else {
                    rejected(res)
                }
            }).fail(async (status) => {
                console.log("getUploadTaskInfo时网络出错，5秒后再次请求")
                if (status.statusText != 'success') {//超时,status还有success,error等值的情况
                    await _waitSeconds(5)
                    let r = await _this._getUploadTaskInfo(getUploadTaskInfoParams)
                    resolve(r)
                } else {
                    rejected(status)
                }
            })
        })
    }

    /**
     * 函数：
     *  将文件保存到mongoDb
     *  参数：
     *  返回值：
     *  resultFormData
     *  0请求失败
     *  -1网络异常函数：
     *  将文件保存到mongoDb，不提交的版本
     *  参数：
     *  返回值：
     *  resultFormData
     *  0请求失败
     *  -1网络异常
     */
    _saveFileToMongoDb(params) {
        const _this = this;

        return new Promise(function (resolve,rejected) {

            //初始化ajax请求参数f
            let formData = new FormData();
            formData.append("uploadTaskId", params.uploadTaskId);
            formData.append("accessToken", params.accessToken);
            $.ajax({
                url: _this.smUrl + "/saveFileToMongoDb" + "?gid=" + _this.gid,
                type: 'POST',
                cache: false,
                data: formData,
                processData: false,
                contentType: false,
                dataType: 'json',
            }).done(function (res) {
                if (res.Code == 1) {
                    //添加新上传任务进度成功
                    resolve(res.Rows[0])
                }
                else {
                    rejected(res)
                }
            }).fail(async (status) => {
                console.log("saveFileToMongoDb时网络出错，5秒后再次请求")
                if (status.statusText != 'success') {//超时,status还有success,error等值的情况
                    await _waitSeconds(5)
                    let r = await _this._saveFileToMongoDb(params)
                    resolve(r)
                } else {
                    rejected(status)
                }

            })
        })
    }
}

/**
 * 等待秒数函数
 * @param second
 * @private
 */
function _waitSeconds(second) {
    return new Promise(function (resolve) {
        setTimeout(function () {
            resolve()
        }, second * 1000)
    })
}


/**
 * 初始化上传环境
 *
 * @private
 */
function _initUploadEnv() {
    console.log('初始化大文件终端')
    return new Promise(function (resolve) {

        //初始化tmId，用于标识设备
        if (localStorage.getItem("tmId") == null) {//如果该终端没有id，则初始化终端id，tmId
            let timestamp = (new Date()).getTime();//当前时间戳
            let randomInt = Math.ceil(Math.random() * 10000);//时间戳和随机数拼接获得新的tmid
            let tmIdGenerated = timestamp.toString() + randomInt.toString();
            localStorage.setItem("tmId", tmIdGenerated)
        }

        console.log('初始化大文件终端结束')

        resolve()
    })
}
