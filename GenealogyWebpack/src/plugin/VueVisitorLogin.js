import {getUserId} from "../common/env";
import Vue from 'vue'
import {getWxLoginUrl} from "../common/constants";
// import {Dialog} from "_vant@2.12.50@vant";
import {Dialog} from "vant";
const _init = (el,binding,vnode) => {

    el.onclick = function(e) {
        if(getUserId() != 28){
            binding.value()
            return
        }

        console.log("游客")
        Dialog.confirm({
            title: '未登录',
            message: '该操作需要注册用户登录后才能进行，是否前往登录？',
            cancelButtonText:'否',
            confirmButtonText: '是',
            confirmButtonColor: '#4f7b92',
            cancelButtonColor: '#686868'
        }).then(() => {

            location.href = getWxLoginUrl(location.href)

        })
    }
};


const VueVisitorLogin = {
    install: function(Vue, options) {
        /**
         * 如果游客则弹出登录框，否则执行传入的参数
         */
        Vue.directive('login-or-exec', {
            inserted: function (el, binding, vnode) {
                _init(el,binding,vnode);
            }
        });
    },
};
export default VueVisitorLogin;