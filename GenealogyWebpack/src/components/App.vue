<template>
    <div id="app">
        <keep-alive>
            <router-view v-if="$route.meta.keepAlive"/>
        </keep-alive>
        <router-view v-if="!$route.meta.keepAlive"/>
        <edit-from-local-storage></edit-from-local-storage>
    </div>
</template>

<script>

    import Vue from "vue";
    import {pageBack} from "common/env";

    import "style/common";
    import ShareService from "../service/share_service";
    import UserService from "../service/user_service";
    const userService = new UserService();
    const shareService = new ShareService();
    import editFromLocalStorage from "./common/editFromLocalStorage"

    import {
         ImagePreview, Button, Icon, Image as VanImage,
          Dialog,  NavBar, DatetimePicker , Popup, Overlay, Cascader as VanCascader} from 'vant'

    Vue.use(ImagePreview)
    Vue.use(NavBar)
    Vue.use(Button)
    Vue.use(Icon)
    Vue.use(VanImage)
    Vue.use(Dialog)
    Vue.use(DatetimePicker)
    Vue.use(Popup)
    Vue.use( Overlay)
    Vue.use( VanCascader)

    import 'muse-ui/lib/styles/base.less';
    import {Grid, GridList, Dialog as MuDialog, LoadMore, Popover, DateInput,
        List, SubHeader,BottomSheet,Helpers, Progress, Button as MuButton, Picker, } from 'muse-ui';
    // import 'muse-ui/lib/styles/theme.less';
    Vue.use(Grid);
    Vue.use(GridList);
    Vue.use(MuDialog);
    Vue.use(LoadMore);
    Vue.use(Popover);
    Vue.use(List);
    Vue.use(SubHeader);
    Vue.use(BottomSheet);
    Vue.use(Helpers);
    Vue.use(Progress);
    Vue.use(MuButton);
    Vue.use(DateInput);
    Vue.use(Picker);



    import MuseUI from 'muse-ui';

    MuseUI.theme.add('teal', {
        primary: '#4f7b92',
        info: '#FF8900',
        secondary: '#3e8941',
        success: '#4caf50',
        warning: '#fdd835',
        error: '#f44336',
        track: '#bdbdbd',

    }, 'light').use('teal');

    import "muse-ui-loading/dist/muse-ui-loading.css";
    import Loading from "muse-ui-loading";
    Vue.use(Loading);

    import { Input as AntInput, Modal, BackTop, Select, Button as AntButton,
        Descriptions, FormModel,  Radio as AntRadio, Spin, Cascader, Icon as AntIcon} from 'ant-design-vue';
    Vue.use(AntInput)
    Vue.use(Modal)
    Vue.use(BackTop)
    Vue.use(Select)
    Vue.use(AntButton)
    Vue.use(Cascader)
    Vue.use(Descriptions)
    Vue.use(FormModel)
    Vue.use(AntRadio)
    Vue.use(Spin)
    Vue.use(AntIcon)
    // import 'ant-design-vue/dist/antd.css'
    export default {
        data() {
            return {};
        },
        methods: {
            back() {
                pageBack();
            },
        },
        mounted() {
            setTimeout(() => {
                userService.getCurrentToolMyView((obj) => {
                    window.tool = obj
                });
                shareService.getImageRole((obj) => {
                    window.role = obj
                });
                shareService.getSharePermission((obj) =>{
                  window.share = obj
                  console.log(window.share)
                })
            }, 1000);
        },
        components: {editFromLocalStorage},
    };
</script>
<style lang="less">
  @import url("//at.alicdn.com/t/c/font_3640614_rq93kmm2yus.css");
  @import "../style/main.less";

</style>
