import axios from 'axios'
import qs from 'qs'
import {toast} from "./env";

//请求发送前处理时间，这里对post请求非表单参数序列化
axios.interceptors.request.use((config) => {
    //在发送请求之前做某件事
    if(config.method  === 'post'){
        if (Object.prototype.toString.call(config.data) != '[object FormData]') {
            config.data = qs.stringify(config.data);
        }
    }
    return config;
},(error) =>{
    console.log('错误的传参')
    return Promise.reject(error);
});
/*

//请求返回时间处理
axios.interceptors.response.use((res) =>{
    return res;
}, (error) => {
    toast("网络断开");
    console.log(error);
    return Promise.reject(error);
});


*/





// 定义一个缓存池用来缓存数据
let cache = {}
const EXPIRE_TIME = 600000
// 利用axios的cancelToken来取消请求
const CancelToken = axios.CancelToken

// 请求拦截器中用于判断数据是否存在以及过期 未过期直接返回
axios.interceptors.request.use(config => {

    if (config.cacheConfig && config.cacheConfig.cache && config.cacheConfig.reset){
        delete cache[config.url + JSON.stringify(config.params)];
    }

    // 如果需要缓存--考虑到并不是所有接口都需要缓存的情况
    if (config.cacheConfig && config.cacheConfig.cache) {
        let source = CancelToken.source()
        config.cancelToken = source.token
        // 去缓存池获取缓存数据
        let data = cache[config.url + JSON.stringify(config.params)]
        // 获取当前时间戳
        let expire_time = getExpireTime()
        // 判断缓存池中是否存在已有数据 存在的话 再判断是否过期
        // 未过期 source.cancel会取消当前的请求 并将内容返回到拦截器的err中
        if (data && expire_time - data.expire < EXPIRE_TIME) {
            source.cancel(data)
        }
    }
    return config
})

// 响应拦截器中用于缓存数据 如果数据没有缓存的话
axios.interceptors.response.use(
    response => {
        // 只缓存get请求
        if (response.config.method === 'get' && response.config.cacheConfig && response.config.cacheConfig.cache) {
            // 缓存数据 并将当前时间存入 方便之后判断是否过期
            let data = {
                expire: getExpireTime(),
                data: response
            }
            cache[response.config.url + JSON.stringify(response.config.params)] = data
        }
        return response
    },
    error => {
        // 请求拦截器中的source.cancel会将内容发送到error中
        // 通过axios.isCancel(error)来判断是否返回有数据 有的话直接返回给用户
        if (axios.isCancel(error)) return Promise.resolve(error.message.data)
        // 如果没有的话 则是正常的接口错误 直接返回错误信息给用户
        return Promise.reject(error)
    }
)

// 获取当前时间
function getExpireTime() {
    return new Date().getTime()
}








//发送post请求
export function doPost(url, params) {
    return new Promise((resolve, reject) => {
        axios.post(url, params)
            .then(response => {
                resolve(response);
            }, err => {
                reject(err);
            })
            .catch((error) => {
                reject(error)
            })
    })
}
//发送get请求
export function doGet(url, param, config) {
    return new Promise((resolve, reject) => {
        axios.get(url, {params: param,cacheConfig: config})
            .then(response => {
                resolve(response)
            }, err => {
                reject(err)
            })
            .catch((error) => {
                reject(error)
            })
    })
}

export function clearCache() {
    cache = {}
}
export default {
    doPost,
    doGet,
    clearCache
}