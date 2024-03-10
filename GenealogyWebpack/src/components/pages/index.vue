<template>
    <div id="index-root" ref="indexRootEl">
        <div class="content-wrap">
            <mu-container>
                <img id="logo" src="../../images/logo.png" alt="logo">
                <div class="search-wrap">
                    <van-icon class-prefix="iconfont" name="search" color="#4f7b92" size="1.2rem"></van-icon>
                    <input v-model="searchValueRef" @keyup.enter="search" @keyup.esc="cancelSearch" id="searchBox" placeholder="请输入家谱名、谱籍地进行查询" type="text">
                    <div :style="{width: searchValueRef ? '128px':'0px'}" class="search-btns">
                        <van-button type="default" class="reset-btn" @click="cancelSearch">取消</van-button>
                        <van-button type="default" class="search-btn" @click="search">搜索</van-button>
                    </div>
                </div>
                <mu-load-more
                        class="album-load"
                        @load="load"
                        :loading="loadingRef"
                        id="images">
                    <div class="genealogy-list">
                        <div class="genealogy album-box create-genealogy"
                             v-login-or-exec="createPedigree" >
                            <van-icon prefix="iconfont" name="plus" color="#4f7b92" size="50"></van-icon>
                            <p>创建家谱</p>
                        </div>
                        <div class="genealogy album-box" v-for="(g, index) in genealogyListRef" @click="openGenealogy(g.id)">
                            <p :title="g.name" class="info-item genealogy-name" :style="{background: getNameBgColor(index)}">{{ g.name }}</p>
                            <p class="info-item"><span class="item-label">卷宗:</span><span class="value">{{ formatInfo(g.dossier)}}</span></p>
                            <p class="info-item"><span class="item-label">派系:</span><span class="value">{{ formatInfo(g.faction) }}</span></p>
                            <p class="info-item"><span class="item-label">房支:</span><span class="value">{{ formatInfo(g.branch) }}</span></p>
                            <p class="info-item"><span class="item-label">籍地:</span><span class="value">{{ formatInfo(g.originPlace) }}</span></p>
                        </div>
                    </div>
                </mu-load-more>
            </mu-container>
        </div>
        <mu-dialog title="新建家谱"
                   dialog-class="my-dialog"
                   scrollable :open="showCreatePedigree" width="600px" :fullscreen="isCurrentClient('mobile')">
            <edit-pedigree @close="showCreatePedigree = false" ref="createPedigreeEl" @success="createSuccess"></edit-pedigree>
            <a-button slot="actions" @click="showCreatePedigree = false">关闭</a-button>
            <a-button slot="actions" @click="createPedigreeEl.submit()" type="primary">确定</a-button>
        </mu-dialog>

        <mu-dialog title="公告" dialog-class="my-dialog" :open="showNote" width="500px">
            <div>
                <mu-list>
                    <mu-sub-header>🥳今日生辰家庭成员:</mu-sub-header>
                    <mu-list-item v-for="m in births">
                        <mu-list-item-title>{{ m.name }}</mu-list-item-title>
                    </mu-list-item>
                    <mu-sub-header>🕯️今日忌辰家庭成员:</mu-sub-header>
                    <mu-list-item v-for="m in deaths">
                        <mu-list-item-title>{{ m.name }}</mu-list-item-title>
                    </mu-list-item>
                </mu-list>
            </div>
            <a-radio slot="actions" v-model="dontShowToday">今日不再显示此提示</a-radio>
            <a-button slot="actions" @click="setCookie(); showNote = false">关闭</a-button>
        </mu-dialog>

        <a-back-top :target="()=>indexRootEl">

        </a-back-top>
    </div>
</template>
<script setup>
    import {getBaseUrl, toast, isCurrentClient} from "../../common/env";
    import Vue, {ref, getCurrentInstance, onMounted} from "vue";
    import Service from "../../service/service"
    import {Dialog} from "vant";
    import {Modal} from "ant-design-vue";
    import Utils from "../../common/utils";
    import {RouterHelper} from "../../router/RouterHelper";
    import EditPedigree  from "../common/EditPedigree"


    import VueVisitorLogin from '../../plugin/VueVisitorLogin'
    Vue.use(VueVisitorLogin)

    const service = new Service();

    const vueIns = getCurrentInstance().proxy

    const indexRootEl = ref(null)

    const createPedigreeEl = ref(null)

    const  showCreatePedigree = ref(false)

    const page = {
        page: 1,
        count: 10,
        total: 0
    };

    //家谱列表
    let loadingRef = ref(false);
    let genealogyListRef = ref([]);
    let pageRef = ref(Utils.clone(page));

    function load(){
        console.log("load")
        if (loadingRef.value){
            return
        }

        if (genealogyListRef.value.length === pageRef.value.total){
            return;
        }

        pageRef.value.page++

        getGenealogyList(pageRef.value)
    }

    function getNameBgColor(index){
        return `hsl(${ 200 + index * 20}, 21%, 49%)`;
    }

    async function getGenealogyList(params) {
        loadingRef.value = true;
        let data = await service.getPedigrees(params)
        genealogyListRef.value.push(...data.rows)
        pageRef.value.total = data.total;
        loadingRef.value = false;
    }

    function resetGenealogyList(){
        pageRef.value = {
            page: 1,
            count: 10,
            total: 0
        };
        genealogyListRef.value = []
        getGenealogyList(pageRef.value)
    }

    function openGenealogy(id){
        RouterHelper.toHome(vueIns, id)
    }

    onMounted(()=>{
        resetGenealogyList()
    })

    //家谱列表end

    //搜索
    const searchValueRef = ref("");

    function search() {
        genealogyListRef.value = []
        pageRef.value = Utils.clone(page)
        pageRef.value['query'] = {
            name: {
                like:  `%${ searchValueRef.value }%`
            }
        }

        getGenealogyList(pageRef.value)
    }

    function cancelSearch() {
        searchValueRef.value = ""
        resetGenealogyList()
    }

    //搜索end

    const dontShowToday = ref(false)
    const showNote = ref(false)
    const births = ref([])
    const deaths = ref([])
    $(function () {
        $.ajax({
            type: "GET",
            url: getBaseUrl() + "&action=getMembersByBirthAndDeath",
            dataType: "json",
            success: function (data) {
                if (data.birth != "" || data.death != "") {
                    if (getCookie("note") != "true") {
                        showNote.value = true
                    }

                    if (data.birth != "") {
                        births.value = data.birth
                    }
                    if (data.death != "") {
                        deaths.value = data.death
                    }
                }
            }, error: function (data) {
                console.log(data)
            }
        });

    })

    function searchPedigree() {
        var hint = $("#searchBox").val();
        localStorage.setItem("hint", hint);
        vueIns.$router.push("search")
    }

    function setCookie() {
        if (dontShowToday.value) {
            var curDate = new Date();
            //当前时间戳
            var curTamp = curDate.getTime();
            //当前日期
            var curDay = curDate.toLocaleDateString();
            var curWeeHours = 0;
            curWeeHours = new Date(curDay).getTime() - 1;
            //当日剩余时间
            var leftTamp = 24 * 60 * 60 * 1000 - (curTamp - curWeeHours);
            var leftTime = new Date();
            leftTime.setTime(leftTamp + curTamp);
            var expires = "expires=" + leftTime.toGMTString();
            document.cookie = "note=" + Base64.encode("true") + ";" + expires;
        }
    }

    function getCookie(name) {
        var reg = RegExp(name + '=([^;]+)');
        var arr = document.cookie.match(reg);
        if (arr) {
            return Base64.decode(arr[1]);
        } else {
            return '';
        }
    }



    var createPedigree = function (data) {
        showCreatePedigree.value = true
    }

    function createSuccess(data) {
        resetGenealogyList()
        toast("创建成功")
        showCreatePedigree.value = false;
    }

    function formatInfo(value){
        return value ? value : '未填';
    }
</script>
<style lang="less">
  @import "../../style/grid-media.less";


  html {
    //font-size:16px;
  }

  #index-root {
    background-image: url('https://books.wetoband.com/genealogy_bg.jpg');
    position: absolute;
    margin: 0;
    width: 100%;
    height: 100%;
    background-color: white;
    background-repeat: no-repeat;
    background-size: auto 100vh;
    background-position: top;
    overflow-y: auto;
    .container{
      padding: 0px;
    }

    .content-wrap {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      //justify-content: center;
      align-items: center;
      backdrop-filter: blur(0px) saturate(62%);
      padding-top: 140px;

      > div {
        //transform: translateY(-80px);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        #logo {
          width: 230px;
          margin-bottom: 2rem;
        }

        .search-wrap {
          background: rgba(255, 255, 255, 0.7);
          //box-shadow: 0px 5px 10px -6px #1d1d1d;
          padding: 4px;
          border-radius: 38px;
          display: flex;
          align-items: center;
          height: 48px;
          overflow: hidden;
          width: 30rem;
          max-width: 90vw;

          .iconfont-search {
            margin-left: 16px;
          }

          > input {
            border: none;
            background: transparent;
            text-align: center;
            color: rgb(79, 123, 146);
            font-size: 1.2rem;
            flex: 1;
            min-width: 56px;

            &::-webkit-input-placeholder { /* WebKit browsers */
              color: rgb(79, 123, 146);
              text-align: center;
              font-size: 1.1rem;
            }
          }

          .search-btns {
            overflow: hidden;
            transition: all 0.2s;
            display: flex;
          }

          button {
            background: #638697;
            border: none;
            color: white;
            font-size: 1rem;
            height: 40px;

            span {
              white-space: nowrap; /*强制span不换行*/
              overflow: hidden; /*超出宽度部分隐藏*/
            }
          }

          .reset-btn {
            margin-right: 4px;
          }

          .search-btn {
            border-radius: 0 30px 30px 0;
          }

        }
      }

      .genealogy-list {
        display: flex;
        margin-top: 3rem;
        flex-wrap: wrap;

        .genealogy {
          background: linear-gradient(180deg, white, #ffffffd9);
          margin:0 10px 20px 10px;
          box-shadow: 0px 5px 13px -11px #1d1d1d;
          color: #3e3e3e;
          border-radius: 8px;
          overflow: hidden;
          padding-bottom: 6px;
          cursor: pointer;
          transition: all 0.2s;

          &.create-genealogy{
            background: #ffffffd6;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-direction: column;
            position: relative;
            min-height: 170px;

            p{
              font-size: 1rem;
              font-weight: bold;
              position: absolute;
              bottom: 2rem;
              text-align: center;
              color: #4f7b92;
            }
          }

          &:last-child{
            margin-right: 0px;
          }

          &:hover {
            box-shadow: 0px 8px 18px -11px #1d1d1d;
            transform: translateY(-10px);
          }

          .info-item {
            font-size: 1rem;
            padding: 0 15px;
            min-width: 154px;
            margin-bottom: 0.5rem;

            &.genealogy-name {
              text-align: center;
              padding: 12px;
              font-size: 1.4rem;
              font-weight: bold;
              background: #638697;
              color: white;
              margin-bottom: 1rem;
              text-overflow: ellipsis;
              width: 100%;
              overflow: hidden;
              white-space: nowrap;
            }

            .item-label {
              font-weight: inherit;
              color: #000;
              margin-right: 6px;
            }

            .value {
              font-weight: bold;
              color: rgba(0, 0, 0, 0.7);
            }
          }


        }

      }
    }


  }

  #noteDialog {
    position: absolute !important;
    top: 50% !important;
    left: 50% !important;
    transform: translate(-50%, -50%) !important;
    -webkit-transform: translate(-50%, -50%) !important;
    -moz-transform: translate(-50%, -50%) !important;
    -ms-transform: translate(-50%, -50%) !important;
    -o-transform: translate(-50%, -50%) !important;
  }

  //<%--设置搜索下拉框滚动条--%>
  .ui-autocomplete {
    overflow-x: hidden;
  }



  .search-box {
    width: 60px;
    background-color: rgb(59 59 59);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .icon {
    transition: all 0.5s;
    width: 50px;
    height: 50px;
  }

  .icon:hover {
    transform: scale(1.2);
  }

  @media screen and (max-width: 768px) {
    #searchBox {
      width: 230px;
    }

    #noteDialog {
      width: 80%;
    }
  }

  @media screen and (min-width: 768px) {
    #searchBox {
      width: 500px;
    }

    #noteDialog {
      width: 35%;
    }
  }

  @media screen and (max-width: 425px){
      #index-root .content-wrap{
        padding-top: 40px;
      }
  }
  .mu-load-more{
    width: 100%;
  }

  .ant-select-dropdown{
    z-index: 100000000000;
  }
</style>
