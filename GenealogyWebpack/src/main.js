import '@babel/polyfill';
import Vue from 'vue';
import VueRouter from 'vue-router';
import {mapMutations} from 'vuex'
import FastClick from 'fastclick';

import {getRunToolParam, getUserAccount, getUserId} from 'common/env';
import {Config} from 'common/constants';
import routes from 'router/router';
import store from './store';

import topframe from "./components/common/topframe";
import slideshow from "./components/common/slideshow";
import globalVariable from 'common/global_variable'
import '../src/store/index'
import {RouterHelper} from "./router/RouterHelper";

/**
 * Sentry 异常监控
 */
Vue.prototype.GLOBAL = globalVariable
if ('addEventListener' in document) {
    document.addEventListener('DOMContentLoaded', function () {
        FastClick.attach(document.body);
    }, false);
}
// 注册SPA的Vue路由
Vue.use(VueRouter);

window.message = ''

const originalReplace = VueRouter.prototype.replace
VueRouter.prototype.replace = function replace(location) {
    return originalReplace.call(this, location).catch(err => err)
}

//修改首页路由重复点击报错
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

/* 根据平台选择类型 */

/* 创建路由 */
const router = new VueRouter({
    routes,
    mode: Config.ROUTER_MODE,
    strict: process.env.NODE_ENV !== 'production',
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            if (from.meta.keepAlive) {
                from.meta.savedPosition = document.body.scrollTop;
            }
            return {x: 0, y: to.meta.savedPosition || 0}
        }
    }
});

router.beforeEach((to, from, next) => {
    let doms = document.getElementsByClassName('routerLoading')
    if (!doms.length) {
        let div = document.createElement('div')
        div.className = 'routerLoading'
        document.body.appendChild(div)
    }
    next()
})

router.afterEach((to, from) => {
    let doms = document.getElementsByClassName('routerLoading')
    for (let d of doms) {
        d.remove()
    }
})



/**
 * Sentry 异常监控
 */
/*import * as Sentry from "@sentry/vue";
import { Integrations  } from "@sentry/tracing";
Sentry.init({
    Vue,
    logErrors: true,
    dsn: "https://1a4ea3444988485ab19aa0c57ac5bba2@o1089389.ingest.sentry.io/4504523110219776",
    integrations: [
        new Integrations.BrowserTracing({
            routingInstrumentation: Sentry.vueRouterInstrumentation(router),
            tracingOrigins: [ "wetoband.com", /^\//],
        }),
    ],
    allowUrls:["wetoband.com"],
    tracesSampleRate: 1.0,
});
Sentry.setUser({ username: getUserAccount(), id: getUserId() });*/


/* 注册组件 */
Vue.component("v-top", topframe)
Vue.component("v-slide", slideshow)
/* 创建根组件 */
new Vue({
    router,
    store,
    methods: {
        ...mapMutations(['BIND_CURRENT']),

        handleFontSize() {

// 设置网页字体为默认大小

            WeixinJSBridge.invoke('setFontSizeCallback', {'fontSize': 0});

// 重写设置网页字体大小的事件

            WeixinJSBridge.on('menu:setfont', function () {

                WeixinJSBridge.invoke('setFontSizeCallback', {'fontSize': 0});

            })

        },
    },
    async mounted() {
        /* 解析工具运行参数 */
        const runToolParam = getRunToolParam() || '';
        if (/^\{.*\}$/.test(runToolParam)) {
            g_rtParam = JSON.parse(runToolParam);

            if (g_rtParam.treeId) {
                RouterHelper.toHome(this, g_rtParam.treeId, g_rtParam.memberId)
            }

        } else if (/^\d+$/.test(runToolParam)) {

        }


        if (typeof WeixinJSBridge == "object" && typeof WeixinJSBridge.invoke == "function") {
            this.handleFontSize();
        } else {
            if( document.addEventListener ){
                document.addEventListener('WeixinJSBridgeReady', this.handleFontSize, false);
            }else if (document.attachEvent){
                document.attachEvent('WeixinJSBridgeReady', this.handleFontSize);
                document.attachEvent('onWeixinJSBridgeReady', this.handleFontSize);
            }
        }
    },
}).$mount('#app');
/*


(function () {

	if (typeof WeixinJSBridge == "object" && typeof WeixinJSBridge.invoke == "function") {

		handleFontSize();

	} else {

		document.addEventListener("WeixinJSBridgeReady", handleFontSize, false);

	}

	function handleFontSize () {

// 设置网页字体为默认大小

		WeixinJSBridge.invoke(\'setFontSizeCallback\', { \'fontSize\' : 0 });

// 重写设置网页字体大小的事件

		WeixinJSBridge.on(\'menu:setfont\', function() {

		WeixinJSBridge.invoke(\'setFontSizeCallback\', { \'fontSize\' : 0 });

	});

}

})();*/
