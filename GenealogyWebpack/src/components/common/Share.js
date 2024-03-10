import { ref } from 'vue'

import ShareService from "../../service/share_service";
import {buildDispatchToolUrl} from "../../common/constants";
import {shareToWx, toast} from "../../common/env";
const shareSevice = new ShareService();

export function useShareToWechat(title, content, param, sharing,permission){
    const imgUrl = '';
    sharing.value = true
    shareSevice.createShareInCore((data)=>{
        let url = buildDispatchToolUrl({
            dispatchID:data.dispatchID,
            objID:  window.tool.objID,
            param: param,
            title: title
        });
        sharing.value =  false
        shareToWx({
            url:url,
            title:title,
            content:content,
            imgUrl:imgUrl,
            text: title
        })
    },(shareAllow)=>{
        if(!shareAllow){
            toast("访问权限不足，无法分享");
        }else{
            toast("分享失败，请再次尝试分享。");
        }
        sharing.value = false;

    },{
        name: title,
    },permission);

}
