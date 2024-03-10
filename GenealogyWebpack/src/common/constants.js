import {toast,notify,alert} from 'common/env'
import {
    getAccessToken, getBandId, getBandObjId, getCallToolUrl, getClientType, getCoreUrl, getUserId,
    isCurrentClient
} from "./env";
export const aid = '21419389378723787447';
export const amapKey = 'a414cab18f3145c2041173d5b4ba0837';
const base64 = new(require('plugin/Base64').default)();
export const rules = {
    name: [
        { required: true, message: '请填写姓名', trigger: 'blur' },
        { min: 1, max: 20, message: '长度1到20', trigger: 'blur' },
    ],
    identityId: [
        { pattern: /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
            message: '请输入正确的身份证号码', trigger: 'blur' },
    ],
    phoneNumber: [
        {pattern: /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/,
            message: '请输入正确的手机号码', trigger: 'blur'},
    ],
    generation: [
        {max: 10, message: '不能超过10个字', trigger: 'blur'},
    ],
    birthplace: [
        {max: 100, message: '不能超过100个字', trigger: 'blur'},
    ],
    residence: [
        {max: 100, message: '不能超过100个字', trigger: 'blur'},
    ],
    restplace: [
        {max: 100, message: '不能超过100个字', trigger: 'blur'},
    ],
    description: [
        {max: 2000, message: '不能超过2000个字', trigger: 'blur'},
    ],
}

/**
 * 配置常量
 */
export const Config = {
	ROUTER_MODE: 'hash',
	MIN_SAME_TASK_POST_INTERVEL: 0, /* 单位为毫秒, <=0不启用*/
	MIN_PAGING_INTERVEL: 500,/* 单位为毫秒*/
	TIMEOUT_INTERVEL: 60000, /* 单位为毫秒*/
	COPY_PARAM_NAMES: ['query', 'resources', 'extend', 'field', 'sort'],
};

export const MULTIPLE_FILE = 0;
export const DIRECTORY = 1;
export const CAMERA = 2;
export const VIDEO = 3;
export const WEIXIN = 4;
export const VISITOR_ID = 28;

export const FILE_TYPE_IMG = 0; //图片
export const FILE_TYPE_VIDEO = 1; //视频
export const FILE_TYPE_CAMERA = 2; //摄像头

export const WTB_DEFAULT_HEADER = "https://rs.wetoband.com/wtbwebRs/images/icon/user2.png";


export const CHATROOM_TOOL_ID = 2217867;
export const docReviewToolID = 2217771;

export const BAND_SELECT_PEOPLE_TOOL_ID = 4389279;


export const getChatroomToolUrl = () => {
    return getRunSystemToolUrl(CHATROOM_TOOL_ID);
}

export const getBandSelectPeopleToolUrl = () => {
    return getRunSystemToolUrl(BAND_SELECT_PEOPLE_TOOL_ID);
}

export const getDeviceID = () =>  {
    if (!localStorage.fyMsgDeviceID) {
        localStorage.fyMsgDeviceID = 'pc-browser-' + new Date().getTime();
    }
    return localStorage.fyMsgDeviceID + '-' + g_userAccount;
}


/**
 * 常用的验证器, 配合Utils.validate使用
 */
export const DefaultValidators = {
	length : {
		validate: function(val, rule) {
			if(typeof val != 'string') {
				return rule.fail;
			}
			if(rule.min && val.length < rule.min) {
				return rule.fail;
			}
			if(rule.max && val.length > rule.max) {
				return rule.fail;
			}
			return true;
		},
	},
	format : {
		validate: function(val, rule) {
			if(typeof val != 'string') {
				return rule.fail;
			}
			if(! rule.regex.test(val)) {
				return rule.fail;
			}
			return true;
		},
	}
};

/**
 * 后台错误信息转换和处理, 配合BaseService._handleError使用
 */
export const ErrorLanguages = {
	DEFAULT_HANDLER: function(json) {
		toast(json.msg || json.result.msg);
	},
	COMMON: [{
		msgPiece: '会话超时',
		handler: function() {
			alert('您已会话超时, 请重新登录后运行工具!');
		},
	}, {
		msgPiece: '内部错误',
		handler: function() {
			notify('服务异常!');
		},
	}],
};

export const getUserInfoUrl = (userId) => {
	return getCoreUrl() + "/core/v4/user/" + userId +"?format=json&aid=" + aid + "&access_token=" + getAccessToken() + "&gid=" + getBandId();
}

export const getHeaderUrl = (userID) => {
	return "https://rs.wetoband.com/wtbwebRs/userPhoto/"+ userID +".png";
}

export const getViewURL = (objID) =>{
    let q = {
        realObjID: objID,
    }
    return getCoreUrl() + "/core/v4/user/me/myObjs?format=json&gid="+ objID + "&aid=" + aid + "&access_token=" + getAccessToken() + "&query=" + encodeURI(JSON.stringify(q));
};

export const getDocumentVersionInfoUrl = (id) => {
    return getCoreUrl() + "/core/v4/document/"+ id +"/currentDocumentVersion?format=json&aid=" + aid + "&access_token=" + getAccessToken() + "&gid=" + id;
}

export const getCreateDispatchUrl = () =>{
    let param = {
        organizationID: window.tool.organizationID,
    }
    return getWtbUrl() + 'tre//runSystemTool?returnType=VALUE&action=createDispatch&isPublic=true&toolID=4389165&param=' + encodeURI(JSON.stringify(param)) + "&gid=" + getBandId() + "&access_token=" + getAccessToken()
};


export const getRunSystemToolUrl = (toolId) =>{
    return getWtbUrl() + "tre/runSystemToolWithUser?toolID=" + toolId + "&clientType=" + getClientType() +  "&accessToken=" + getAccessToken();
}


export const getDocumentInfoUrl = (id) => {
    return getCoreUrl() + "/core/v4/document/"+ id +"?format=json&aid=" + aid + "&access_token=" + getAccessToken() + "&gid=" + id;
}

export const getUploadToolUrl = () =>{
    return getWtbUrl() + "/tre/runSystemTool?toolID=2217835&returnType=VALUE&param=%7B%22pageType%22:%22uploadDocument%22%7D&gid=" + getBandId()
}
export const getPayTool = () =>{
    let clientType = isCurrentClient('pc') ? 101 : 201;
    return getWtbUrl() + 'tre//runSystemTool?git=35&toolID=4389197&clientType=' + clientType;
};


export const getPhotoStorageAndSharingUrl = (bandId) => {
    return getCallToolUrl() + "&alias=PhotoStorageAndSharing&bandID=" + bandId;
}


export const getWxLoginUrl = (gotoUrl) =>{
    return getWtbUrl() + 'wx/gotoLogin?toPage=' + base64.encode(gotoUrl);
};

export const getCameraToolUrl = ()=>{
    return  getCallToolUrl() + "&toolID=4389357&bandID=" + getBandId();
}

export const buildDispatchToolUrl = (params) =>{

    let param = {
        organizationID: window.tool.organizationID,
        dispatchID: params.dispatchID,
        needHeader: false,
        dispatchTime: new Date().getTime(),
        objID: params.objID,
        objParams:{
            param: params.param
        }
    }

    let encodeP = encodeURI(JSON.stringify(param));
    return `${getWtbUrl()}wx//gotoRunTool?runType=runSystemTool&toolID=4389165&bandID=${getBandId()}&param=${encodeP}&headerTitle=${params.title}&needBack=false`;
};


export const getWtbUrl = () =>{
    return getCoreUrl().indexOf("www") > -1 ? "https://www.wetoband.com/" : "https://test.wetoband.com/";
}

export const getShopUrl = () =>{
    return getWtbUrl() + "shop";
}

export const getPhotoParamsKey = () =>{
    return"photoParams_" + getUserId()+"_"+getBandId();
}

export const minDate=  new Date(1800, 0, 1)
export const maxDate =  new Date()
export const vanDateFormatter = (type, val) => {
    if (type === 'year') {
        return `${val}年`;
    } else if (type === 'month') {
        return `${val}月`;
    }
    return `${val}日`;
}