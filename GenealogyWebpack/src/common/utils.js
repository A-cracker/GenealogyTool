import { DefaultValidators } from 'common/constants';
import {getClientType} from "./env";

/**
 * 常用的工具方法
 */
const Utils = {

	/**
	* 验证变量是否符合规则
	* @param {Object} val 检查的变量
	* @param {Object} rulesMap 检查的规则集, 格式参考Constant.DefaultValidators
	* @return {Boolean} 是否有效
	*/
	validate: function(val, rulesMap) {
		if(! rulesMap) {
			return;
		}
		for(var name in rulesMap) {
			if(rulesMap.hasOwnProperty(name)) {
				var rules = rulesMap[name];
				if(! Utils.isObject(rules)) {
					throw new Error('不正确的规则对象: ' + rules);
				}
				var validator = DefaultValidators[name];
				if(! validator) {
					if(! Utils.isObject(rules.validator)) {
						throw new Error('非默认规则池的规则中不能没有指定validator: ' + rules);
					}
					validator = rules.validator;
					rules = rules.rules;
				}
				if(! rules) {
					continue;
				}
				if(! (rules instanceof Array)) {
					rules = [rules];
				}
				for(var i = 0; i < rules.length; i ++) {
					var rule = rules[i];
					if(! rule.hasOwnProperty('fail')) {
						rule.fail = false;
					}
					if(Utils.isFunc(rule.always)) {
						rule.always();
					}
					if(rule.fail === validator.validate(val, rule)) {
						return rule.fail;
					}
				}
			}
		}
		return true;
	},
	/**
	* 检查一个变量是否有效
	* @param {Object} variable 检查的变量
	* @return {Boolean} 是否有效
	*/
	isValidVariable: function(variable){
		if(variable === null || variable === undefined
			|| variable === '' || variable === 'undefined'
			|| variable === 'null') {
			return false;
		}
		return true;
	},

	openUrl: function (url){
		if (getClientType() === '304' || getClientType() === '303') {
			window.open(url, "_top");
		} else {
			window.open(url);
		}
	},
	/**
	 * 判断某个对象是否是函数
	 * @param {Object} func 检查的对象
	* @return {Boolean} 是否是函数
	*/
	isFunc: function(func) {
		if(typeof func == 'function') {
			return true;
		}
		return false;
	},
	/**
	* 判断某个对象是否是对象
	* @param {Object} obj 检查的对象
	* @return {Boolean} 是否是对象
	*/
	isObject: function(obj) {
		if(obj instanceof Object || typeof obj == 'object') {
			return true;
		}
		return false;
	},
	/**
	* 判断某个对象是否是数组
	* @param {Object} arr 检查的对象
	* @return {Boolean} 是否是数组
	*/
	isArray: function(arr) {
		if(arr instanceof Array || (arr && arr.constructor.toString().indexOf('function Array() {') != -1)) {
			return true;
		}
		return false;
	},
	/**
	* 判断某个对象是否是字符串
	* @param {Object} str 检查的对象
	* @return {Boolean} 是否是字符串
	*/
	isString: function(str) {
		if(str instanceof String || typeof str == 'string') {
			return true;
		}
		return false;
	},
	/**
	* 判断某个对象是否数字
	* @param {Object} number 检查的对象
	* @return {Boolean} 是否是数字
	*/
	isNumber: function(number) {
		if(number instanceof Number || typeof number == 'number') {
			return true;
		}
		return false;
	},
	/**
	* 判断某个对象是否是指定类型实例
	* @param {Object} obj 检查的对象
	* @param {Object} type 指定类型实例
	* @return {Boolean} 对象是否是指定类型实例
	*/
	isInstance: function(obj, type) {
		if(obj instanceof type || (obj && type && obj.constructor.toString() == type.toString())) {
			return true;
		}
		return false;
	},
	/**
	* 复制属性
	* @param {Object} from 复制源对象
	* @param {Object} to 内容复制写入到对象
	*/
	copyProperties: function(from, to) {
		if(! from || ! to) {
			return;
		}
		if(typeof from != 'object') {
			throw new Error('复制的目标必须为对象({...}或[...])');
		}
		if(typeof to != 'object') {
			throw new Error('复制的结果必须为对象({...}或[...])');
		}
		for(var property in from) {
			if(! from.hasOwnProperty(property)) {
				continue;
			}
			var fromVal = from[property];
			if((to[property] !== undefined && fromVal === undefined)
					|| (to[property] !== null && fromVal === null)) {
				continue;
			}
			if(fromVal && typeof fromVal == 'object') {
				var copyObj = null;
				if(fromVal instanceof Array) {
					copyObj = [];
				} else {
					copyObj = {};
				}
				Utils.copyProperties(fromVal, copyObj);
				to[property] = copyObj;
				continue;
			}
			to[property] = fromVal;
		}
	},
	/**
	* 把List转换为Map
	* @param {Array} list List
	* @param {String} key Map的key的值对应在List中对象的属性名称
	* @return {Object} Map
	*/
	asMap: function(list, key, filter) {
		if(! list || ! key) {
			return;
		}
		var result = {};
		for(var i = 0; i < list.length; i ++) {
			var element = list[i];
			if(Utils.isFunc(filter) && ! filter(element, result)) {
				continue;
			}
			var elementKey = element[key];
			if(Utils.isFunc(key)) {
				elementKey = element[key(element)];
			}
			if(! Utils.isObject(element) || ! elementKey) {
				continue;
			}
			var existElement = result[elementKey];
			if(! existElement) {
				result[elementKey] = element;
				continue;
			}
			if(! Utils.isArray(existElement)) {
				existElement = [existElement];
				result[elementKey] = existElement;
			}
			existElement.push(element);
		}
		return result;
	},
	/**
	* 把Map转换为List
	* @param {Object} map Map
	* @return {Array} List
	*/
	asList: function(map, filter) {
		if(! map) {
			return;
		}
		var result = [];
		for(var key in map) {
			if(map.hasOwnProperty(key)) {
				var val = map[key];
				if(val) {
					if(Utils.isFunc(filter) && ! filter(key, val)) {
						continue;
					}
					result.push(val);
				}
			}
		}
		return result;
	},
	/**
	* 把Map的Key转换为List
	* @param {Object} map Map
	* @return {Array} List
	*/
	asKeyList: function(map, filter) {
		if(! map) {
			return;
		}
		var result = [];
		for(var key in map) {
			if(map.hasOwnProperty(key)) {
				if(Utils.isFunc(filter) && ! filter(key, map[key])) {
					continue;
				}
				result.push(key);
			}
		}
		return result;
	},
	/**
	* 把Map转换为List
	* @param {Object} map Map
	* @param {String} key 指定的Map的key, 对应的值组成List
	* @return {Array} List
	*/
	asListByKey: function(mapOrList, key) {
		if(! mapOrList || ! key) {
			return;
		}
		var result = [];
		for(var key1 in mapOrList) {
			if(mapOrList.hasOwnProperty(key1)) {
				var element = mapOrList[key1];
				var resultElement = element[key];
				if(Utils.isFunc(key)) {
					resultElement = element[key(element)];
				}
				result.push(resultElement);
			}
		}
		return result;
	},
	/**
	* 把第二个List中不在第一个List的元素添加到第一个List的结尾, 并且去掉重复的部分
	* @param {Array} list1 数组1
	* @param {Array} list2 数组2
	* @return {Array} List
	*/
	combindListWithoutRepeat: function(list1, list2) {
		if(! Utils.isArray(list1) || ! Utils.isArray(list2)) {
			throw new Error('合并的必须是数组');
		}
		for(var i = 0; i < list2.length; i ++) {
			var val = list2[i];
			if(val === undefined && val === null) {
				continue;
			}
			if(list1.indexOf(val) == -1 || list1.indexOf(val.toString()) == -1) {
				list1.push(val);
			}
		}
	},
	/**
	* 判断对象是否具备某些key
	* @param {Object} map 需要进行判断的目标对象
	* @param {String | String Array} keys 对象属性名称
	* @param {Boolean} isAny 是否是有一个出现就返回true
	* @return {Boolean} 是否指定的key在对象中出现
	*/
	hasKeys: function(map, keys, isAny) {
		if(! map || ! keys) {
			return false;
		}
		if(Utils.isString(keys)) {
			keys = [keys];
		}
		for(var i = 0; i < keys.length; i ++) {
			if(! keys[i]) {
				continue;
			}
			var includeKeys = keys[i].split('\|');
			var has = false;
			for(var j = 0; j < includeKeys.length; j ++) {
				if(! includeKeys[j]) {
					continue;
				}
				has = map.hasOwnProperty(includeKeys[j]);
				if(has) {
					if(isAny) {
						return true;
					}
					break;
				}
			}
			if(! has && ! isAny) {
				return false;
			}
		}
		return true;
	},
	/**
	* 对类数组对象进行遍历操作
	* @param {Array Like} obj 需要进行判断的目标节点
	* @param {Function} callback 事件名称
	*/
	forEach: function(obj, callback) {
		if(! obj || ! Utils.isFunc(callback)) {
			return;
		}
		if(Utils.isFunc(obj.forEach)) {
			obj.forEach(callback);
			return;
		}
		if(Utils.isArray(obj)) {
			for(var i = 0; i < obj.length; i ++) {
				const element = obj[i];
				callback(element, i, obj);
			}
			return;
		}
		for(var key in obj) {
			if(! obj.hasOwnProperty(key)) {
				continue;
			}
			const element = obj[key];
			callback(element, key, obj);
		}
	},
	/**
	* 判断节点是否具备事件
	* @param {DOMNode} node 需要进行判断的目标节点
	* @param {String} eventName 事件名称
	* @return {Boolean} 是否具备该事件
	*/
	hasEvent: function(node, eventName) {
		return false;
	},
	/**
	* 触发事件
	* @param {String} eventName 事件名称
	*/
	triggerEvent: function(dom, eventName) {
		if(Utils.isFunc(dom.fireEvent)) {
			if(! eventName.startsWith('on')) {
				eventName = 'on' + eventName;
			}
			dom.fireEvent(eventName.toLowerCase());
			return;
		}
		const event = document.createEvent('HTMLEvents');
		event.initEvent(eventName, false, true);
		dom.dispatchEvent(event);
	},
	/**
	* 停止事件冒泡
	* @param {Event Object} event 事件
	*/
	stopBubble: function(event) {
		event = event || window.event;
		event.cancelBubble && (event.cancelBubble = true);
		event.stopPropagation && event.stopPropagation();
		event.returnValue && (event.returnValue = false);
		event.preventDefault && event.preventDefault();
	},
	/**
	* 把格式字符串中的占位符替换指定上下文中指定名称的值
	* @param {String} format 格式字符串, 如{xxx}
	* @param {Object} context 某个上下文, 包含替换的名称对应的属性和值
	* @return {Boolean} 是否具备该事件
	*/
	replacePlaceHolder: function(format, context) {
		if(! Utils.isString(format)) {
			return '';
		}
		var isSingleVal = ! Utils.isObject(context);
		var matches = null, lastFormat = null;
		while((matches = format.match(/{.*?}/)) && matches.length > 0) {
			for(var i = 0; i < matches.length; i ++) {
				var match = matches[i];
				var val = (isSingleVal ? context : context[match.replace(/{|}/g, '')]) || '';
				format = format.replace(new RegExp(match, 'g'), val);
			}
			if(lastFormat == format) {
				break;
			}
			lastFormat = format;
		}
		return format;
	},
	/**
	* 截取数组中的元素作为新的数组
	* @param {String} arrayLike 类数组, 如Array或arguments
	* @param {Number} start 截取开始索引下标值
	* @param {Number} end 截取结束索引下标值
	* @return {Array} 截取到的数组
	*/
	sliceArrayLike: function(arrayLike, start, end) {
		if(! arrayLike) {
			return [];
		}
		if(! Utils.isArray(arrayLike)) {
			arrayLike = Utils.asList(arrayLike);
		}
		return arrayLike.slice(start, end);
	},
	/**
	* 生成一个临时ID
	* @return {String} 临时ID
	*/
	generateTemporyId: function() {
		var s = [];
		var hexDigits = '0123456789abcdef';
		for (var i = 0; i < 36; i++) {
			s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
		}
		s[14] = '4';  // bits 12-15 of the time_hi_and_version field to 0010
		s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
		s[8] = s[13] = s[18] = s[23] = '-';
		return s.join('');
	},
	/**
	* 生成字符串的hash码
	* @param {String} str 目标字符串
	* @return {String} 字符串的hash码
	*/
	hashCode: function(str) {
		if(! Utils.isString(str)) {
			return 0;
		}
		let hash = 0;
		if (str.length == 0) {
			return hash
		};
		for (let i = 0; i < str.length; i ++) {
			let char = str.charCodeAt(i);
			hash = ((hash << 5) - hash) + char;
			hash = hash & hash; // Convert to 32bit integer
		}
		return hash;
	},
	/**
	 * 转换为查询参数字符串
	 * @param {Object} obj 查询参数的对象
	 * @return {String} 查询参数字符串, 如: 传入{a:1,b:2}返回&a=1&b=2
	 */
	toQueryStr: function(obj) {
		if(! Utils.isValidVariable(obj)) {
			return '';
		}
		if(Utils.isString(obj)) {
			return obj;
		}
		if(Utils.isObject(obj)) {
			let queryStr = '';
			Utils.forEach(obj, (value, key) => {
				if(Utils.isString(value)) {
					queryStr += '&' + key + '=' + encodeURIComponent(value);
					return;
				}
				queryStr += '&' + key + '=' + encodeURI(JSON.stringify(value));
			});
			return queryStr;
		}
		return '';
	},
	/**
	 * 获取当天日期，返回字符串，如20101006
	 */
	getTodayStamp:function(){
		let today=new Date();
		let todayStr="";
		todayStr=today.getFullYear().toString()+(today.getMonth()+1)+today.getDate().toString();
		return todayStr;
	},
    clone: function (jsonObj) {
        return JSON.parse(JSON.stringify(jsonObj));
    },

	/**
	 * 调用手机振动
	 * @param milliseconds 时间数组 [100,50,100]: 振动100毫秒停50毫秒再振100毫秒
	 */
	vibrate:  function vibrate(milliseconds) {
		if('vibrate' in window.navigator) {
			window.navigator.vibrate(milliseconds); // 震动200停100再震动200，和qq的消息震动一样
		}else {
			console.log("浏览器不支持震动")
		}
	},

    getFileName: function (name){
        const dotIndex = name.lastIndexOf('.');

        const fileName = dotIndex > -1 ? name.substring(0,dotIndex) : name;

        return fileName;
    },

    getFileSuffix: function (name, isLowerCase = true){
        const dotIndex = name.lastIndexOf('.');

        const fileExt = dotIndex > -1 ? name.substring(dotIndex + 1,name.length) : '';
        return isLowerCase ? fileExt.toLowerCase() : fileExt;
    },

    isPoneAvailable: function (val) {
        var myreg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
        if (!myreg.test(val)) {
            return false;
        } else {
            return true;
        }
    },
    /**
     * 替换中间的字符串
     *
     *  replaceIntermediateStrings('123456','*') -> '12**56'
     *  replaceIntermediateStrings('123','-') -> '1-3'
     *
     * @param val
     * @param char
     * @returns {string}
     */
    replaceIntermediateStrings: function (val, char = '*') {
        if (!val) return '';

        const offset = val.length / 3;
        return val.substring(0, offset) + val.substring(offset, offset * 2).replace(/./g, char) + val.substring(offset*2, val.length);
    },

    convertUnit(size){
        var data = "";
        if (size < 0.1 * 1024) { //如果小于0.1KB转化成B
            data = size.toFixed(2) + "B";
        } else if (size < 0.1 * 1024 * 1024) {//如果小于0.1MB转化成KB
            data = (size / 1024).toFixed(2) + "KB";
        } else if (size < 0.1 * 1024 * 1024 * 1024) { //如果小于0.1GB转化成MB
            data = (size / (1024 * 1024)).toFixed(2) + "MB";
        } else { //其他转化成GB
            data = (size / (1024 * 1024 * 1024)).toFixed(2) + "GB";
        }
        let sizestr = data + "";
        let len = sizestr.indexOf("\.");
        let dec = sizestr.substr(len + 1, 2);
        if (dec == "00") {//当小数点后为00时 去掉小数部分
            return sizestr.substring(0, len) + sizestr.substr(len + 3, 2);
        }
        return sizestr;
    },

    getBrowserInfo() {
        let Sys = {};
        let ua = navigator.userAgent.toLowerCase();
        let re = /(msie|firefox|chrome|version).*?([\d.]+)/;
        let m = ua.match(re);
        Sys.browser = m[1].replace(/version/, "'safari");
        Sys.ver = m[2];
        return Sys;
    },

	/**
	 *
	 * @param type localStorage, sessionStorage
	 * @returns {boolean}
	 */
	storageAvailable(type) {
		var storage;
		try {
			storage = window[type];
			var x = '__storage_test__';
			storage.setItem(x, x);
			storage.removeItem(x);
			return true;
		}
		catch(e) {
			return e instanceof DOMException && (
					// everything except Firefox
					e.code === 22 ||
					// Firefox
					e.code === 1014 ||
					// test name field too, because code might not be present
					// everything except Firefox
					e.name === 'QuotaExceededError' ||
					// Firefox
					e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
				// acknowledge QuotaExceededError only if there's something already stored
				(storage && storage.length !== 0);
		}
	}
};

export default Utils;
