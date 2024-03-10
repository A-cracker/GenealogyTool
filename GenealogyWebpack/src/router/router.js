
import App from '../components/App';
/* 最后一个参数就是分出来的模块名称, 根据需要定义数量 */
const index = r => require.ensure([], () => r(require('../components/pages')), 'index');
const home2 = r => require.ensure([], () => r(require('../components/pages/home2')), 'home2');
const info = r => require.ensure([], () => r(require('../components/pages/info')), 'info');
const find = r => require.ensure([], () => r(require('../components/pages/find')), 'find');
const merge = r => require.ensure([], () => r(require('../components/common/merge')), 'merge');

//手机测试上传页

export default [{
    path: '/',
    component: App, //顶层路由，对应index.html
    children: [ //二级路由。对应App.vue
        {
            path: '',
            redirect: '/index'
        },
        {
            path: '/index',
            name: 'index',
            component: index,
            meta: {
                keepAlive: true
            },
        }, {
            path: '/home2',
            name: 'home2',
            component: home2,
            meta: {
                keepAlive: false
            },
        },{
            path: '/info',
            name: 'info',
            component:  info,
            meta: {
                keepAlive: true
            },
        },{
            path: '/find',
            name: 'find',
            component:  find,
            meta: {
                keepAlive: true
            },
        },{
            path: '/merge',
            name: 'merge',
            component:  merge,
            meta: {
                keepAlive: true
            },
        }

    ]
}];
