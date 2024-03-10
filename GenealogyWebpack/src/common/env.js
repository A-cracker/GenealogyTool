/* ! 环境配置, 把和上下文有关的所有公共方法或变量提取到这里 */

/* 环境变量方法 */
/* 数据处理基础方法 */
import Utils from 'common/utils';
import axios from 'axios';
import qs from 'qs';
/* 提示用的工具方法 */
import Vue from 'vue';

import 'vue2-toast/lib/toast.css';
import Toast from 'vue2-toast';
/* 数据基础组件及其初始化 */
import CacheManager from 'common/cache_manager';
import BaseService from 'service/base_service';

/**
 * 获取项目请求根路径
 */
export const getBaseUrl = () => {
	return typeof g_runToolUrl == 'undefined' ? 0: g_runToolUrl;
};

/**
 * 获取帮区ID
 */
export const getBandId = () => {
	return typeof g_bandId == 'undefined' ? 0: g_bandId;
};

export const getBandObjId = () =>{
    return typeof g_bandObjId == 'undefined' ? 0: g_bandObjId;
}

export const getToolID = () =>{
    return typeof g_thisToolId == 'undefined' ? 0: g_thisToolId;
}

/**
 * 获取当前用户ID
 * @returns {string}
 */
export const getUserId = () => {
    return typeof g_userId == 'undefined' ? 0: g_userId;

};


export const getUserAccount = () => {
    return typeof g_userAccount == 'undefined' ? 0: g_userAccount;
};
export const getUserName = () => {
    return typeof g_userName == 'undefined' ? 0: g_userName;
};

/**
 * 获取项目资源请求根路径
 */
export const getResourceUrl = () => {
	return g_resourceUrl;
};

/**
 * 获取工具运行参数
 */
export const getRunToolParam = () => {
    return typeof g_rtParam == 'undefined' ? 0: g_rtParam;
};

/**
 * 获取当前用户的AccessToken
 */
export const getAccessToken = () => {
    return typeof g_accessToken == 'undefined' ? 0: g_accessToken;
};

export const getCallToolUrl = () => {
    return g_callToolUrl;
};

/**
 * 获取当前的客户端类型
 */
export const getClientType = () => {
    return typeof g_clientType == 'undefined' ? 0: g_clientType;
};

export const getCoreUrl = () => {
	return g_coreUrl;
};



/**
 * 处理包括文件的远程Ajax请求参数
 * @param {Object} option 远程请求参数
 */
export const formatPOSTAjaxParams = (option) => {
	if(! option.data) {
		return;
	}
	const ajaxParams = option.data;
	const headers = option.headers = option.headers || {};
	const formData = new FormData();
	for(let key in ajaxParams) {
		if(! ajaxParams.hasOwnProperty(key)) {
			continue;
		}
		if(Utils.isInstance(ajaxParams[key], File)) {
			headers['Content-Type'] = 'multipart/form-data';
		}
		formData.append(key, ajaxParams[key]);
	}
	if(headers['Content-Type'] != 'multipart/form-data') {
		headers['Content-Type'] = 'application/x-www-form-urlencoded'
		return qs.stringify(ajaxParams);
	}
	return formData;
};

/**
 * 远程Ajax请求
 * @param {Object} option 远程请求参数
 */
export const ajax = async (option) => {
	let response;
	if(option.type && option.type.toUpperCase() == 'POST') {
		let data = formatPOSTAjaxParams(option);
		response = await axios.post(option.url, data, {
			headers: option.headers,
		}).catch(err => {
			if (typeof option.error == 'function') {
				option.error(err);
			}
		});
	} else {
		response = await axios.request({
			url: option.url,
			params: option.data,
			method: option.type,
			headers: option.headers,
			paramsSerializer: (params) => {
				return qs.stringify(params, { arrayFormat: 'brackets' });
			}
		}).catch(err => {
			if (typeof option.error == 'function') {
				option.error(err);
			}
		});
	}
	if(! response) {
		return;
	}
	return response.data;
};

/* 日志的工具方法 */
/**
 * 输出调试日志
 * @param {Object} msg 信息
 */
export const log = (msg) => {
	console.log(msg);
};
/**
 * 输出信息日志
 * @param {String} msg 信息
 */
export const info = (msg) => {
	console.info(msg);
};
/**
 * 输出警告日志
 * @param {String} msg 警告的消息
 */
export const warn = (msg) => {
	console.warn(msg);
};
/**
 * 输出错误日志
 * @param {String} msg 错误消息
 */
export const error = (msg) => {
	console.error(msg);
};

Vue.use(Toast, {
    duration: 2000,
    wordWrap: true,
});


/**
 * 提示信息(主要)
 */
export const toast = (message, type = 'message', options) => {
    Vue.prototype.$toast(message);
};

/**
 * 通知信息(次要)
 */
export const notify = (message, type = 'info', options) => {
    Vue.prototype.$toast(message);
};

/**
 * 清除提示框
 */
export const clearDialog = () => {
};


const cacheManager = new CacheManager();

/**
 * 获取缓存管理器
 */
export const getCacheManager = () => {
	return cacheManager;
};

const baseService = new BaseService();

/**
 * 私有: 远程请求获取基础信息
 */
const fetchConfigs = async () => {
	return await baseService.queryTemplate(null, null, {
		ajaxParams: {
			action: 'getConfigs',
		},
		singleResult: true,
		errorTag: 'getConfigs',
		errorMsg: '获取基本信息失败',
	});
};

/**
 * 获取一些基础信息
 */
const CACHE_KEY_CONFIGS = 'DMT#COFNIGS';
export const getConfigs = async () => {
	const cacheManager = getCacheManager();
	const config = cacheManager.get(CACHE_KEY_CONFIGS);
	if (config) {
		return config;
	}
	const fetchConfig = await fetchConfigs();
	if (!fetchConfig) {
		throw new Error('获取工具配置信息失败');
	}
	cacheManager.cache(CACHE_KEY_CONFIGS, fetchConfig);
	return fetchConfig;
};
/* 界面处理 */
/**
 * 获取DOM元素的Style
 */
export const getElementComputedStyle = (el, attr) => {
	if(! el) {
		return null;
	}
	let style;
	if(el.currentStyle) {
		style = el.currentStyle[attr];
	} else {
		style = getComputedStyle(el, false)[attr];
	}
	if(style.indexOf('px') != -1) {
		return parseInt(style.match(/\d+/)[0]);
	}
	return style;
};

/**
 * 获取DOM元素的盒子信息
 */
export const getElementRect = (el) => {
	if(! el) {
		return null;
	}
	if(el === window || el === document) {
		return {
			top: 0,
			bottom: window.innerHeight,
			left: 0,
			right: window.innerWidth,
		};
	}
	if(! el.getClientRects) {
		return null;
	}
	return el.getClientRects()[0];
};

/**
 * 处理是否有上一页
 */
export const hasLastPage = (navigator, history) => {
	// 尝试回退到上一页
	if (navigator.userAgent.indexOf('MSIE') != -1 &&
		navigator.userAgent.indexOf('Opera') == -1) {
		// IE
		if (history.length > 0) {
			return true;
		}
	}
	if (navigator.userAgent.indexOf('Firefox') != -1 ||
		navigator.userAgent.indexOf('Opera') != -1 ||
		navigator.userAgent.indexOf('Safari') != -1 ||
		navigator.userAgent.indexOf('Chrome') != -1 ||
		navigator.userAgent.indexOf('WebKit') != -1) {
		//非IE浏览器
		if (history.length > 1) {
			return true;
		}
	}
	return history.length > 1;
}

/**
 * 处理浏览器返回
 */
export const pageBack = () => {
	if(hasLastPage(window.navigator, window.history)) {
		window.history.go(-1);
		return;
	}
	// 向iframe外部发送需要关闭iframe的消息(如果是)
	notifyParentFrame('pageclose');
	// 如果没有上一页, 则关闭标签页
	if(g_clientType < 200) {
		window.close();
		return;
	}
	// 如果是APP调用api
	require('plugin/API').quitToolPageAPI();
}

/**
 * 向父级Frame发送消息
 */
export const notifyParentFrame = (data) => {
	if(window.parent && window.parent.postMessage) {
		window.parent.postMessage(data, '*');
	}
};


/**
 * 生成微信分享请求URL
 */
const generateShareWxUrl = (params) => {
	if(! window.g_wxUrl) {
		return 'https://wx.wetoband.com/gotoShare?' + Utils.toQueryStr(params);
	}
	return window.g_wxUrl + '/gotoShare?' + Utils.toQueryStr(params);
};

/**
 * 处理分享到微信
 */
export const shareToWx = async (config) => {

    if (isCurrentClient('pc') || config.forceQrCode) {

        /* PC */
        if (!config.dom) {
            config.dom = genterateDefaultQrCodeDom(config.text);
        } else {
            clearExistedQrCodeDom(config.dom);
        }
        const QRCode = require('qrcodejs2');
        const base64 = new(require('plugin/Base64').default)();
        let wxUrl = generateWxShareUrl({
            shareType: 'href',
            url: encodeURIComponent(base64.encode(config.url)),
            title: encodeURIComponent(base64.encode(config.title)),
            desc: encodeURIComponent(base64.encode(config.content)),
            imgUrl: encodeURIComponent(base64.encode(config.imgUrl))
        });
        if (wxUrl.length > 50) {
            const uuid = Utils.generateTemporyId();
            const result = await submitWebShareForwardData(uuid, {
				url: config.url,
                title: config.title,
                desc: config.content,
                imgUrl: config.imgUrl,
            });
            if (!result) {
                return;
            }
            wxUrl = generateWxShareUrl({
                shareType: 'href',
                exchange: uuid
            });
        }
        new QRCode(config.dom, {
            text: wxUrl,
            width: config.width || 320,
            height: config.height || 320,
            colorDark: config.color || '#3e9356', //二维码颜色
            colorLight: config.bgColor || '#ffffff', //二维码背景色
            correctLevel: QRCode.CorrectLevel.H //容错率，L/M/H
        });
        return;
    }
    if (isCurrentClient('app')) {

        /* APP */
        if(typeof plus == 'undefined' && window.parent && window.parent.plus){
            windown.plus = window.parent.plus
        }

        plus.share.getServices(function(services) {
            for(var i = 0; i < services.length; i++) {
                if(services[i].id != 'weixin') {
                    continue;
                }

                var msg = {
                    type: 'web',
                    href: config.url,
                    title: config.title,
                    content: config.content,
                    thumbs: [config.imgUrl || '_www/images/logo.png'],
                    pictures: [config.imgUrl ||　'_www/images/logo.png'],
                    extra: {
                        scene: 'WXSceneSession'
                    }
                };

                services[i].send(msg, ()=>{}, (e)=>{
                    console.log(e)
                })

            }
        });


      /*  require('plugin/API').callAppAPI('shareToWX', {
            url: config.url,
            title: config.title,
            content: config.content,
            imgUrl: config.imgUrl,
        });*/
        return;
    }

    /* wx */
    const base64 = new(require('plugin/Base64').default)();
    const url = generateWxShareUrl({
        shareType: 'href',
        url: base64.encode(config.url),
        title: config.title,
        desc: config.content,
        imgUrl: base64.encode(config.imgUrl),
    });
    // window.open(url);
	window.location.href = url;
};


/**
 * 处理分享到微信
 */
export const showQrCode = async (config) => {

        if (!config.dom) {
            config.dom = genterateDefaultQrCodeDom(config.text);
        } else {
            clearExistedQrCodeDom(config.dom);
        }


        if (config.url.length > 50) {
            const result = await  compressUrl(config.url)
            if (!result.result) {
                return;
            }

            config.url = result.msg
        }

        const QRCode = require('qrcodejs2');
        new QRCode(config.dom, {
            text: config.url,
            width: config.width || 320,
            height: config.height || 320,
            colorDark: config.color || '#3e9356', //二维码颜色
            colorLight: config.bgColor || '#ffffff', //二维码背景色
            correctLevel: QRCode.CorrectLevel.H //容错率，L/M/H
        });
};

/**
 * shareToWx 内部使用: 清理指定节点中的的二维码DOM
 * @param {*} dom 模板节点
 */
const clearExistedQrCodeDom = (dom) => {
    if (dom.querySelector('img')) {
        dom.removeChild(dom.querySelector('img'));
    }
    if (dom.querySelector('canvas')) {
        dom.removeChild(dom.querySelector('canvas'));
    }
};

/**
 * shareToWx 内部使用: 生成默认的全局的二维码DOM
 */
const genterateDefaultQrCodeDom = (text = '') => {
    const dom = document.querySelector('.qrcode-fixed-container>.qrcode');
    if (dom) {
        dom.parentElement.style.display = 'flex';
        clearExistedQrCodeDom(dom);

        let textDom = dom.getElementsByClassName('qr-text')[0];
        textDom.innerText = text;

        return dom;
    }
    // 容器
    const containerDom = document.createElement('div');
    const classAttr1 = document.createAttribute('class');
    classAttr1.value = 'qrcode-fixed-container';
    containerDom.setAttributeNode(classAttr1);
    // 二维码
    const qrCodeDom = document.createElement('div');
    const classAttr2 = document.createAttribute('class');
    classAttr2.value = 'qrcode';
    qrCodeDom.setAttributeNode(classAttr2);
    containerDom.appendChild(qrCodeDom);
    // 关闭按钮
    const closeBtnDom = document.createElement('div');
    const classAttr3 = document.createAttribute('class');
    classAttr3.value = 'closebtn';
    closeBtnDom.setAttributeNode(classAttr3);
    qrCodeDom.appendChild(closeBtnDom);
    // 关闭事件
    closeBtnDom.addEventListener('click', () => {
        containerDom.style.display = 'none';
    });
    // 追加到body下
    document.querySelector('body').appendChild(containerDom);

    if (text){
        let textDiv = document.createElement('p')
        textDiv.className = 'qr-text'
        textDiv.innerText = text;
        qrCodeDom.appendChild(textDiv)
    }

    return qrCodeDom;
}

/**
 * shareToWx 内部使用: 生成微信分享请求URL
 */
const generateWxShareUrl = (params) => {
    if (!window.g_wxUrl) {
        return 'https://www.wetoband.com/wx/gotoShare?' + Utils.toQueryStr(params);
    }
    return window.g_wxUrl + '/gotoShare?' + Utils.toQueryStr(params);
};

/**
 * shareToWx 内部使用: 生成web分享数据转发的请求地址
 */
const generateWebShareForwardUrl = (uuid) => {
    if (!window.g_webUrl) {
        return 'https://www.wetoband.com/web/qrCode/share/' + uuid + "?gid=1";
    }
    return window.g_webUrl + '/qrCode/share/' + uuid + "?gid=1";
};

/**
 * shareToWx 内部使用: 提交分享数据到web的转发控制器
 */
const submitWebShareForwardData = (uuid, data) => {
    return baseService.createTemplate(null, null, {
        url: generateWebShareForwardUrl(uuid),
        ajaxParams: {
            shareData: JSON.stringify(data),
        },
        ajaxHeaders:{'Content-Type':'multipart/form-data'},
        singleResult: true,
        errorTag: 'submitWebShareForwardData',
        errorMsg: '分享失败',
    });
};

const compressUrl = (url) => {
    return baseService.createTemplate(null, null, {
        url: "https://www.wetoband.com/wx/url",
        ajaxParams: {
            url: url,
        },
        ajaxHeaders:{'Content-Type':'multipart/form-data'},
        singleResult: true,
        errorTag: 'compressUrl',
        errorMsg: '生成失败',
    });
};


/**
 * 判断当前客户端
 */
/*
export const isCurrentClient = (type) => {
	switch(type) {
		case 'pc':
			return Math.floor(g_clientType / 100) == 1;
		case 'app':
			return Math.floor(g_clientType / 100) == 2;
		case 'wx':
			return Math.floor(g_clientType / 100) == 3;
	}
	return false;
};
*/


/**
 * 判断当前设备类型
 */
export const isCurrentClient = (type) =>{
    var ua = navigator.userAgent,
        isWindowsPhone = /(?:Windows Phone)/.test(ua),
        isSymbian = /(?:SymbianOS)/.test(ua) || isWindowsPhone,
        isAndroid = /(?:Android)/.test(ua),
        isFireFox = /(?:Firefox)/.test(ua),
        isChrome = /(?:Chrome|CriOS)/.test(ua),
        //平板
        isTablet = /(?:iPad|PlayBook)/.test(ua) || (isAndroid && !/(?:Mobile)/.test(ua)) || (isFireFox && /(?:Tablet)/.test(ua)),
        isPhone = /(?:iPhone)/.test(ua) && !isTablet,
        isPc = !isPhone && !isAndroid && !isSymbian && !isTablet,
        isWx = /(MicroMessenger)/.test(ua),
        isHtml5Plus = /(Html5Plus)/.test(ua);

    //判断是否为微信浏览器
    if(isWx && type == 'wx'){
        return true;
    }

    if(isPhone && type == "ios"){
        return true;
    }

    //判断是否为安卓系统
    if(type == "android" && isAndroid){
        return true;
    }

    //判断是否为APP客户端
    if (type == 'app' && (isAndroid || isPhone) && isHtml5Plus) {
        return true;
    }

    //判断是否是移动端，移动端泛指APP客户端、移动端网页和微信浏览器
    if (type == 'mobile' && (isAndroid || isPhone || isTablet)) {
        return true;
    }

    if(type == 'pc' && isPc) {
        return true;
    }
    return false;
}

/**
 * 打开url
 */
export const openUrl = (url, replace) => {
	if(replace) {
		window.location.replace(url);
		return;
	}
	if(isCurrentClient('pc')) {
		window.open(url);
		return;
	}
	window.location.href = url;
}

