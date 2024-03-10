<template>
    <div id="home-root">

        <mu-slide-top-transition>
            <van-nav-bar :title="pedigreeDetailRef.name" v-show="isCurrentClient('pc') || (!showMemberDetailRef && !showPedigreeDetailRef && !showEditPedigreeRef)" style="    box-shadow: 0 4px 11px -3px #c2cad0;">
                <div class="navbar-header" slot="left" v-if="isCurrentClient('pc')">
                    <!-- Branding Image -->
                    <img @click="RouterHelper.toIndex(vueIns)" id="logo" src="../../images/logo.png" alt="logo">

                </div>
<!--                <p slot="title">{{ pedigreeDetailRef.name }}</p>-->
<!--                <div slot="right">-->

                    <!--                <div class="collapse navbar-collapse" id="app-navbar-collapse" style="flex: 1; padding-left: 1rem">
                                        &lt;!&ndash; Left Side Of Navbar &ndash;&gt;
                                        <ul class="nav navbar-nav">
                                            <li><a @click="RouterHelper.toFind(vueIns)">找亲</a></li>
                                        </ul>
                                        <ul class="nav navbar-nav navbar-right">
                                            <li><a @click="RouterHelper.toInfo(vueIns)" style="margin-right: 50px;"><span
                                                    class="iconfont icon-personalcenter" aria-hidden="true" style="margin-right: 4px;"></span>个人中心</a>
                                            </li>
                                        </ul>
                                    </div>-->
<!--                </div>-->
            </van-nav-bar>
        </mu-slide-top-transition>
        <!--        <nav id="navbar navbar-default" class="navbar navbar-default navbar-static-top" v-if="expansionTreeControllerRef">
                    <div class="navbar-header">
                        &lt;!&ndash; Branding Image &ndash;&gt;
                        <img @click="RouterHelper.toIndex(vueIns)" id="logo" src="../../images/logo.png" alt="logo">

                    </div>
                    <van-button color="#f8f8f8" @click="expansionTreeControllerRef = !expansionTreeControllerRef " v-show="isCurrentClient('mobile')" style=" position: absolute; right: 0; ">
                        <van-icon  class-prefix="iconfont" name="menu"
                                  color="#000"
                                  size="2rem"></van-icon>
                    </van-button>
                    <div class="collapse navbar-collapse" id="app-navbar-collapse" style="flex: 1; padding-left: 1rem">
                        &lt;!&ndash; Left Side Of Navbar &ndash;&gt;
                        <ul class="nav navbar-nav">
                            <li><a @click="RouterHelper.toFind(vueIns)">找亲</a></li>
                        </ul>
                        <ul class="nav navbar-nav navbar-right">
                            <li><a @click="RouterHelper.toInfo(vueIns)" style="margin-right: 50px;"><span
                                    class="iconfont icon-personalcenter" aria-hidden="true" style="margin-right: 4px;"></span>个人中心</a>
                            </li>
                        </ul>
                    </div>
                </nav>-->

        <div v-show="!showMergeTree" id="graph-and-side">
            <mu-slide-bottom-transition>
                <div id="infohead" v-show="isCurrentClient('pc') || expansionTreeControllerRef">
                    <p v-if="isCurrentClient('mobile')">
                     <span @click="closeTreeController" >
                          <van-icon class="close-member-btn" class-prefix="iconfont" name="close" color="#4f7b92"
                                    size="2rem"></van-icon>
                     </span>
                    </p>
                    <div class="swith-select" style="padding-left: 1rem">
                        <span class="head-info-label">展示家谱:</span>
                        <a-select :value="treeIdRef" style="width: 150px" @change="switchPedigree">
                            <van-icon slot="suffixIcon" class-prefix="iconfont" name="arrow_down" color="#4f7b92"
                                      size="1rem"></van-icon>
                            <a-select-option :value="p.id" v-for="p in pedigreesRef">
                                {{ p.name }}
                            </a-select-option>
                        </a-select>
                    </div>
                    <div class="swith-depth" style="display: flex; flex: 1;justify-content: center; align-items: center;">
                        <span class="head-info-label">搜索:</span>&nbsp;

                        <a-select
                                v-model:value="searchVal"
                                show-search
                                placeholder="输入姓名搜索"
                                style="width: 150px"
                                not-found-content="未找到"
                                :default-active-first-option="false"
                                :show-arrow="false"
                                :allowClear="true"
                                :filter-option="false"
                                @search="search"
                                @select="focusNode"
                        >
                            <a-select-option :value="s.id" :key="s.id +'_'+ i" v-for="(s, i) in searchResults">{{ s.name }}</a-select-option>
                        </a-select>
<!--
                        <div style="display:flex;">
                            <a-select v-model="selectedMemberIdRef" style="width: 120px" @change="changeTree"
                                      :show-search="isCurrentClient('pc')" option-filter-prop="children">
                                <van-icon slot="suffixIcon" class-prefix="iconfont" name="arrow_down"
                                          color="#4f7b92"
                                          size="1rem"></van-icon>
                                <a-select-option :value="m.id" v-for="m in membersRef">
                                    {{ m.name }}
                                </a-select-option>
                            </a-select>&nbsp;

                            <a-select v-model="depthRef" @change="changeTree" style="width: 65px">
                                <van-icon slot="suffixIcon" class-prefix="iconfont" name="arrow_down"
                                          color="#4f7b92"
                                          size="1rem"></van-icon>
                                <a-select-option :value="d" v-for="d in [3,4,5,6,7,100]">
                                    {{ d }}
                                </a-select-option>
                            </a-select>
                        </div>&nbsp;
                        <span class="head-info-label">代内亲网关系</span>&nbsp;
-->
                    </div>


                    <div style=" text-align: center;" class="pedigree-btns">
                        <a-button type="primary"
                                  @click="showPedigreeDetailRef = !showPedigreeDetailRef;showEditPedigreeRef = false;
                                  closeTreeController()">
                            {{ detailBtnTextComp }}
                        </a-button>
                        <!--                    <a-button type="primary" @click="editPedigree1">修改详情</a-button>-->
                        <a-button type="primary" @click="mergePedigree">合并家谱</a-button>
                        <a-button type="primary" @click="deriveMembers">导出成员</a-button>
                        <a-button type="primary" :loading="sharing" @click="sharePedigree">分享到微信</a-button>
                        <a-button v-show="showDelPedigreeBtn" type="danger" @click="deletePedigree">删除家谱</a-button>

                    </div>
                </div>
            </mu-slide-bottom-transition>

            <div style="flex:1; display: flex; overflow: hidden;">
                <div id="graph-wrap">
                    <a-button type="primary" style=" align-self: center; margin-top: 7rem; "
                              v-if="showAddFirstMemberBtnRef && !showPedigreeDetailRef && !showEditPedigreeRef"
                              @click="addFirstMember()">添加起始成员
                    </a-button>
                    <div v-show="!showPedigreeDetailRef && !showAddFirstMemberBtnRef && !showEditPedigreeRef"
                         id="graph">
                    </div>
                    <div v-show="showPedigreeDetailRef" id="pedigree-detail">
                        <a-descriptions title="家谱详情" bordered :column="{ xs: 3, sm: 3, md: 6}">
                            <a-descriptions-item label="家谱名" :span="3">
                                {{ pedigreeDetailRef.name }}
                            </a-descriptions-item>
                            <a-descriptions-item label="普籍地" :span="3">
                                {{ pedigreeDetailRef.originPlace }}
                            </a-descriptions-item>
                            <a-descriptions-item label="卷宗" :span="3">
                                {{ pedigreeDetailRef.dossier }}
                            </a-descriptions-item>
                            <a-descriptions-item label="派系" :span="3">
                                {{ pedigreeDetailRef.faction }}
                            </a-descriptions-item>
                            <a-descriptions-item label="房支" :span="6">
                                {{ pedigreeDetailRef.branch }}
                            </a-descriptions-item>
                            <a-descriptions-item label="简介" :span="6">
                                {{ pedigreeDetailRef.description }}
                            </a-descriptions-item>
                        </a-descriptions>
                        <p style="text-align:right; margin-top:1rem">
                            <a-button @click="showPedigreeDetailRef = false" type="primary">家谱树</a-button>
                            <a-button v-show="showDelPedigreeBtn" @click="editPedigree1" type="primary">编辑</a-button>
                        </p>
                    </div>
                    <div v-show="showEditPedigreeRef" id="edit-pedigree">
                        <edit-pedigree ref="editPedigreeEl" @success="editPedigreeSuccess" :pedigree="pedigreeDetailRef"></edit-pedigree>
                        <div class="ant-row">
                            <p class="ant-col-23" style="text-align:right">
                                <a-button @click="showEditPedigreeRef = false">取消</a-button>&nbsp;
                                <a-button @click="editPedigreeSubmit" type="primary">保存</a-button>
                            </p>
                        </div>
                    </div>
                </div>
                <mu-slide-right-transition>
                    <div v-if="isCurrentClient('pc')" id="member-info-side" v-show="showMemberDetailRef">

                        <a-spin :spinning="memberDetailLoadingRef" v-show="!showEditMember">
                            <member-detail :memberDetail="memberDetailRef" :treeId="treeIdRef" :depth="selectedNodeDepth"
                                           @edit="editForm"
                                           @pray="pray"
                                           @close="closeMemberDetail(false)"></member-detail>
                        </a-spin>
                        <div v-if="showEditMember" style="display: flex;flex-direction: column;width: 100%">
                            <div style="overflow-y: auto">
                                <a-spin :spinning="memberDetailLoadingRef" v-show="showEditMember">
                                <edit-member ref="editMemberEl"
                                             :treeId="treeIdRef"
                                             @close="showEditMember = false"
                                             @success="editSuccess"></edit-member>

                                </a-spin>
                            </div>
                            <p class="ant-col-23 top-dashed" style="text-align:right">
                                <a-button @click="editMemberEl.cancel()">取消</a-button>
                                <a-button @click="editMemberEl.submit()" type="primary">保存</a-button>
                            </p>
                        </div>
                    </div>
                </mu-slide-right-transition>
            </div>
        </div>

        <merge v-if="showMergeTree" :sourceTree="treeIdRef" @success="mergeSuccess" @cancel="showMergeTree = false"></merge>


        <!--祭祀编辑框-->
        <van-overlay :show="showIncense" >
            <div style="display:flex;align-items: center;flex-direction: column;margin-top: 12rem;">
                <div class="candle">
                    <span class="glow"></span>
                    <span class="flames"></span>
                    <span class="thread"></span>
                </div>
                <div style="margin-top: 150px;text-align: center;">
                    <audio id="audio" src="https://books.wetoband.com/genealogy_bgm.mp3" controls loop="loop"
                           style="height: 30px;width: 250px;"></audio>
                    <div>
                        <!--                        <button @click="uploadMusic()" data-toggle="tooltip" data-placement="bottom" type="button"
                                                        class="btn btn-util" title="切换歌曲"><img src="../../images/music.png"
                                                                                               style="width: 20px;"></button>-->
                        <button @click="flower()"   type="button"
                                class="btn btn-util"><img src="../../images/flower.png"
                                                                     style="width: 20px;">&nbsp;送花</button>
                        <button @click=" exitPray()"  type="button"
                                class="btn btn-util"><img src="../../images/exit.png"
                                                                       style="width: 20px;">&nbsp;结束</button>
                    </div>
                </div>

                <img id="uu" src="../../images/bouquet.png">

            </div>
        </van-overlay>

        <mu-dialog :title="insertNodeTitle" :open="showInsertMember"
                   scrollable width="600px" :fullscreen="isCurrentClient('mobile')">
            <insert-member @close="showInsertMember = false"
                           @success="insertSuccess"
                           @error="inserting = false"
                           ref="insertMemberEl"
                           :isFirstMember="false"
                           :type="insertType" :gender="memberGenderRef"
                           :memberId="selectedIdRef" :treeId="treeIdRef"></insert-member>
            <a-button slot="actions" @click="insertMemberEl.cancel()">关闭</a-button>&nbsp;
            <a-button slot="actions" @click="inserting = true; insertMemberEl.submit()" :loading="inserting" type="primary">保存</a-button>
        </mu-dialog>

        <mu-dialog title="添加起始成员"
                   scrollable :open="showInsertFirstMember" width="600px" :fullscreen="isCurrentClient('mobile')">
            <insert-member @close="showInsertFirstMember = false"
                         @success="insertSuccess"
                         :isFirstMember="true"
                           @error="inserting = false"
                         ref="insertFirstEl"
                         :treeId="treeIdRef"></insert-member>
            <a-button slot="actions" @click="insertFirstEl.cancel()">关闭</a-button>
            <a-button slot="actions" @click="inserting = true; insertFirstEl.submit()" :loading="inserting" type="primary">保存</a-button>
        </mu-dialog>

        <mu-bottom-sheet  v-if="isCurrentClient('mobile')" class="detail-bottom-sheet"
                         scrollable :overlay="false" :open.sync="showMemberDetailRef">
            <p @touchmove="memberSheetMove($event)" style="text-align:center; margin: 0" >
                <van-icon  class-prefix="iconfont" name="tuodong" color="#4f7b92"
                           size="1.4rem"></van-icon>
            </p>
            <van-icon  v-show="!showEditMember" @click="closeMemberDetail(false)" class="close-member-btn" class-prefix="iconfont" name="close" color="#4f7b92"
                       size="2rem"></van-icon>
            <div style="padding: 0 1rem;height: 63vh;overflow-y: auto;" ref="memberBottomSheet">

                <a-spin :spinning="memberDetailLoadingRef" v-show="!showEditMember">

                    <member-detail :memberDetail="memberDetailRef" :treeId="treeIdRef" :depth="selectedNodeDepth"
                                   @edit="editForm"
                                   @pray="pray"
                                   @close="closeMemberDetail(true)"></member-detail>
                </a-spin>
                <a-spin :spinning="memberDetailLoadingRef" v-if="showEditMember">
                    <div style="display: flex;flex-direction: column;width: 100%"  :class="{'mobile': isCurrentClient('mobile') }">

                        <div style="overflow-y: auto">
                            <edit-member ref="editMemberEl"
                                         :treeId="treeIdRef"
                                         @close="showEditMember = false"
                                         @success="editSuccess"></edit-member>
                        </div>
                        <p id="member-info-btns" style="text-align:right">
                            <a-button @click="editMemberEl.cancel()">取消</a-button>
                            <a-button @click="editMemberEl.submit()" type="primary">保存</a-button>
                        </p>
                    </div>
                </a-spin>
            </div>
        </mu-bottom-sheet>


        <div id="node-ctrl-btns" v-show="showNodeCtrlBtn">
            <a-spin :spinning="judgeParentLoadingRef">
                <p><span>{{ nodeCtrlTitle }}</span><van-icon  @click="showNodeCtrlBtn = false" class="close-node-ctrl-btn" class-prefix="iconfont" name="close" color="#4f7b92"
                               size="1.5rem"></van-icon>
                </p>
                <a-button @click="addNode(INSERT_FATHER)" size="small" type="primary" v-show="showAddFatherBtnRef">父亲</a-button>
                <a-button @click="addNode(INSERT_MOTHER)" size="small" type="primary" v-show="showAddMotherBtnRef">母亲</a-button>
                <a-button @click="addNode(INSERT_SPOUSE)" size="small" type="primary">配偶</a-button>
                <a-button @click="addNode(INSERT_CHILDREN)" size="small" type="primary">子女</a-button>
                <a-button :loading="nodeSharing" @click="shareNode" size="small" type="primary">分享</a-button>
                <a-button v-show="showDelBtn" size="small" type="danger" @click="deleteMember(selectedIdRef)">删除</a-button>
            </a-spin>
        </div>

        <mu-button id="float-btn"  color="primary" @click="expansionTreeControllerRef = !expansionTreeControllerRef "
                   v-show="isCurrentClient('mobile') && !showMergeTree" fab>
            <van-icon class-prefix="iconfont" name="menu"
                      color="#fff"
                      size="2rem"></van-icon>
        </mu-button>
    </div>
</template>
<script setup>
    import Vue, {onMounted, getCurrentInstance, ref, computed, nextTick, watch, onBeforeUnmount} from "vue";
    import {RouterHelper} from "../../router/RouterHelper"
    import {getBaseUrl, getResourceUrl, isCurrentClient, toast, getUserId, pageBack} from "../../common/env"
    import MemberDetail from "../common/MemberDetail";
    import EditMember from "../common/EditMember";
    import InsertMember from "../common/InsertMember";
    import Merge from "../common/merge"
    import EditPedigree from "../common/EditPedigree"
    import {useShareToWechat} from "../common/Share";
    import Loading from 'muse-ui-loading';
    import { Dialog } from 'vant';
    import Utils from "../../common/utils";


    import VueVisitorLogin from '../../plugin/VueVisitorLogin'
    import {getWxLoginUrl} from "../../common/constants";
    Vue.use(VueVisitorLogin)


    const vueIns = getCurrentInstance().proxy

    onMounted(() => {
        initTree(vueIns.$route.query.treeId, true)
        needFocusLastEditNode = true
    })


    //家谱详情
    const pedigreeDetailRef = ref({})
    const showPedigreeDetailRef = ref(false)
    const showEditPedigreeRef = ref(false)

    const detailBtnTextComp = computed(() => {
        return showPedigreeDetailRef.value ? "家谱树" : "家谱详情";
    })

    //选择家谱下拉列表
    const treeIdRef = ref(0)
    const pedigreesRef = ref([])

    async function switchPedigree(id) {
        if (showEditMember.value) {
            let result = await editMemberEl.value.cancel()

            if (!result) {
                return
            }
        }

        initTree(id)
        showEditMember.value = false
        showMemberDetailRef.value = false
    }

    //搜索节点
    const searchResults = ref([])
    const searchVal = ref()
    function search(val) {
        if (!val){
            searchResults.value = []
            return
        }

        searchResults.value = membersRef.value.filter(item => {
            return item.name.indexOf(val) > -1;
        });
    }

    function findNodeEl(mid){
        const nodeEls = document.querySelectorAll(`[data-mid="${mid}"]`)

        if(!nodeEls.length){
            return null;
        }

        return nodeEls[0]
    }

    function focusNode(mid) {
        const nodeEl = findNodeEl(mid)

        if (!nodeEl){
            return
        }

        const parentEl = nodeEl.parentElement;
        const xVal = parentEl.attributes.x.value;
        const yVal = parentEl.attributes.y.value;

        const x = ~~xVal.substring(0, xVal.length-2)
        const y = ~~yVal.substring(0, yVal.length-2)

        tree.zoomTo(x, y, 1.2)
        focusNodeAnimate(nodeEl)
    }

    function focusNodeAnimate(nodeEl){
        setTimeout(() => {
            nodeEl.parentElement.classList.add('shakeX')

            setTimeout(()=>{
                nodeEl.parentElement.classList.remove('shakeX')
            }, 1000)

        }, 300)
    }

    //查看关系
    const membersRef = ref([])
    const selectedMemberIdRef = ref("")
    const depthRef = ref(100)       //查看按钮

    function changeTree() {
        t_idRef.value = selectedMemberIdRef.value;

        treeInit(depthRef.value);
        needFocusLastEditNode = true
    }

    //成员详情
    const showMemberDetailRef = ref(false)
    const memberDetailRef = ref({})
    const memberDetailLoadingRef = ref(false)
    function closeMemberDetail(resetZoom) {
        showMemberDetailRef.value = false;
        resetZoom && tree.resetZoom()

        // if (newVal && isCurrentClient('mobile')) {
        //     pageBack()
        // }
    }

    watch(showMemberDetailRef, (newVal)=>{

        if (newVal && isCurrentClient('mobile')) {
           pushHistory()
        }
    })

    //节点弹框
    const showNodeCtrlBtn = ref(false)
    const judgeParentLoadingRef = ref(false)
    const showAddFatherBtnRef = ref(false)
    const showAddMotherBtnRef = ref(false)
    const nodeCtrlTitle = ref('')
    const showDelBtn = computed(()=>{
        return getUserId() == nodeCreatorIdRef.value
    })

/*
    watch(showNodeCtrlBtn, (newVal, oldVal)=>{
        newVal && pushHistory()
        // !newVal && oldVal && pageBack()
    })
*/


    //树
    let genealogy = [];
    const t_idRef = ref("");
    const showAddFirstMemberBtnRef = ref(false);
    const selectedIdRef = ref("")
    const selectedNodeDepth = ref(1)
    let selectNodeId;
    const nodeCreatorIdRef = ref("");
    const expansionTreeControllerRef = ref(false); //移动端家谱控制栏折叠
    let verticalNode = window.innerWidth <= 500; //节点竖直显示

    function closeTreeController() {
        expansionTreeControllerRef.value = false;
        if (isCurrentClient('mobile')) {
            pageBack()
        }
    }
    watch(expansionTreeControllerRef, (newVal)=>{
       newVal && pushHistory()
    })

    let treeLoading = null;


    const editPedigreeEl = ref(null)

    //编辑家谱信息
    function editPedigree1() {
        showEditPedigreeRef.value = true;
        showPedigreeDetailRef.value = false;
    }

    //修改家谱信息提交
    function editPedigreeSubmit() {
        editPedigreeEl.value.submit(false);
    }

    function editPedigreeSuccess(pedigree) {
        showEditPedigreeRef.value = false;
        showPedigreeDetailRef.value = true;
        pedigreeDetailRef.value = pedigree
    }

    //合并家谱
    const showMergeTree = ref(false)
    function mergePedigree() {
        showMergeTree.value = true
    }

    function mergeSuccess(id) {
        showMergeTree.value = false
        $.ajax({
            type: "GET",
            url: g_runToolUrl + "&action=getPedigrees",
            dataType: "json",
            success: function (data) {
                pedigreesRef.value = data.rows
                switchPedigree(id)
            }
        });
    }


    //导出
    function deriveMembers() {
        const link = document.createElement("a");
        link.href = g_runToolUrl + '&action=downloadData&treeId=' + treeIdRef.value;
        link.download = "家族成员信息.xlsx";
        link.click();
    }


    //上香
    const showIncense = ref(false)
    function pray() {
        // if (isCurrentClient('mobile')) {
        //     showMemberDetailRef.value = false
        // }
        showIncense.value = true
        document.getElementById('audio').play()
    }
    watch(showIncense, (newVal)=>{
        newVal && pushHistory()
    })


    function flower() {
        var f = document.getElementById("uu");
        f.style.opacity = "1";
        f.style.width = "150px";
        f.style.transform = "translateY(" + -240 + "px)" + "rotate(" + 140 + "deg)";

    }

    function exitPray() {
        showIncense.value = false
        var f = document.getElementById("uu");
        f.style.opacity = "0";
        f.style.width = "0px";
        f.style.transform = "translateY(" + 240 + "px)";

        document.getElementById("audio").pause();
        document.getElementById("audio").currentTime = '-1';
        // pageBack()
    }


    function getMembers(treeId, getIdFromQuery = false) {
        $.ajax({
            type: "GET",
            url: g_runToolUrl,
            data: "action=getMembersByTreeId&treeId=" + treeId,
            dataType: "json",
            success: function (data) {
                length = data.length;
                membersRef.value = data
                if (length <= 0) {
                    showAddFirstMemberBtnRef.value = true;
                    selectedMemberIdRef.value = ""
                    treeLoading.close()
                    return
                }

                showAddFirstMemberBtnRef.value = false;

                let memberId = vueIns.$route.query.memberId;
                if (memberId && getIdFromQuery) {
                    t_idRef.value = memberId;
                    selectedMemberIdRef.value = parseInt(memberId)
                    depthRef.value = 3
                    treeInit(depthRef.value);
                    getMemberDetail(memberId)
                    showMemberDetailRef.value = true
                } else {
                    $.ajax({
                        type: "GET",
                        url: g_runToolUrl,
                        data: "action=getTreeAncestorByTreeId&treeId=" + treeId,
                        dataType: "json",
                        success: function (data) {
                            t_idRef.value = data.id;
                            selectedMemberIdRef.value = data.id
                            treeInit(depthRef.value);
                        }, error: function (data) {
                            console.log(data);
                            treeLoading.close()
                        }
                    });
                }
            }, error: function (data) {
                toast("出现内部错误" + data);
                treeLoading.close()
            }
        });
    }
    function initTree(viewTreeId, getIdFromQuery = false) {
        treeIdRef.value = parseInt(viewTreeId);
        treeLoading = Loading();

        //加载全部家谱
        $.ajax({
            type: "GET",
            url: g_runToolUrl + "&action=getPedigrees",
            dataType: "json",
            success: function (data) {
                data = data.rows
                pedigreesRef.value = data
                for (var i = 0; i < data.length; i++) {
                    if (data[i].id == viewTreeId) {
                        pedigreeDetailRef.value = data[i]
                    }
                }

                if(!pedigreeDetailRef.value.id){
                   Dialog({ title: '该家谱已删除' }).then(()=>{
                       RouterHelper.toIndex(vueIns)
                   });
                }
            }, error: function (data) {
                toast("加载家谱错误" + data);
                treeLoading.close()
            }
        });


        getMembers(treeIdRef.value, getIdFromQuery)
    }

    function getTreeInfo(treeId){
        $.ajax({
            type: "GET",
            url: g_runToolUrl,
            data: "action=getPedigreeInfo&id=" + treeId,
            dataType: "json",
            success: function (data) {
                pedigreeDetailRef.value = data;
            }, error: function (data) {
                toast("加载家谱信息错误" + data);
            }
        });
    }


    const showDelPedigreeBtn = computed(()=>{
        return pedigreeDetailRef.value.creatorId == getUserId();
    })

    function deletePedigree() {
        if (pedigreeDetailRef.value.creatorId != getUserId()) {
            Dialog({title: "您不是该家谱创建者", confirmButtonText: '知道了'})
            return;
        }
        Dialog.confirm({
            title: '是否确定删除家谱?',
            confirmButtonText: '删除'
        }).then(() => {
            $.ajax({
                type: "POST",
                url: g_runToolUrl,
                data: "action=deletePedigree&treeId=" + treeIdRef.value,
                dataType: "json",
                success: function (data) {
                    if (data.transaction == true) {
                        if (data.treeId != 0) {
                            // initTree(data.treeId)

                            toast("已经删除");
                            RouterHelper.toIndex(vueIns)

                        } else {
                            // window.location = g_runToolUrl + "&action=showHome";
                        }
                    }
                }
            });
        })
    }

    //节点点击
    async function nodeClicked(memberId, id, depth) {
        console.log(id)
        if (showEditMember.value) {
            let result = await editMemberEl.value.cancel()

            if (!result) {
                return
            }

        }

        if (isCurrentClient('mobile')) {
            tree.zoomToNode(id, 0.8)
        }
        setNodeColor(memberId)

        memberDetailRef.value = {}
        getMemberDetail(memberId);
        showEditMember.value = false
        showMemberDetailRef.value = true
    }

    function setNodeColor(id) {
        $('#graph div').each(function () {
            $(this).css("background-color", "#FFF");
        });

        $(`#graph div[data-mid="${id}"]`).css("background-color", "#E0E0E0");

    }

    function getMemberDetail(id, success) {
        memberDetailLoadingRef.value = true
        $.ajax({
            type: "GET",
            url: g_runToolUrl + "&id=" + id,
            data: "action=getDetail",
            dataType: "json",
            success: function (data) {
                memberDetailLoadingRef.value = false
                memberDetailRef.value = data

                success && success(data)
            }, error: function (data) {
                memberDetailLoadingRef.value = false
                toast("获取成员信息出错")
            }
        });

    }

    function getJudgeParents(id){
        judgeParentLoadingRef.value = true
        $.ajax({
            type: "GET",
            url: g_runToolUrl,
            data: "action=judgeParents&id=" + id + "&treeId=" + treeIdRef.value,
            dataType: "json",
            success: function (data) {
                judgeParentLoadingRef.value = false
                if (data == 1) {
                    showAddFatherBtnRef.value = false;
                    showAddMotherBtnRef.value = true;
                } else if (data == 2) {
                    showAddFatherBtnRef.value = false;
                    showAddMotherBtnRef.value = false;
                } else if (data == 3) {
                    showAddFatherBtnRef.value = true;
                    showAddMotherBtnRef.value = false;
                } else if (data == 4) {
                    showAddFatherBtnRef.value = true;
                    showAddMotherBtnRef.value = true;
                } else {
                    showAddFatherBtnRef.value = false;
                    showAddMotherBtnRef.value = false;
                }
            }
        });
    }

    function nodeRenderer(name, x, y, height, width, extra, id, nodeClass, textClass, textRenderer) {
        let node = '';
        node += '<div data-tooltip="node-ctrl"';
        if (extra && extra.mid){
            node += `data-mid="${extra.mid}" `;
        }

        node += `class="node ${nodeClass} ${verticalNode?'vertical-node':''} ${extra && extra.isAlive ? '':'death'}" `;
        node += `id="node${id}"><img/>`;
        node += textRenderer(name, extra, textClass);
        node += '</div>';
        return node;
    }

    function textRenderer(name, extra, textClass) {
        if (extra){
            return `<p align='center' class='${textClass} ${verticalNode?'vertical-node-text':''}'>${ name }</p>`;
        }

        return `<p align='center' class='${textClass} ${verticalNode?'vertical-node-text vertical-node-not-text':''}'>${ name }</p>`;
    }



    //树构建
    function treeInit(depth) {

        if (t_idRef.value) {
            $.ajax({
                type: "GET",
                url: g_runToolUrl,
                data: "action=judgePosition&id=" + t_idRef.value + "&treeId=" + treeIdRef.value,
                dataType: "json",
                success: function (data) {
                    d3.json(g_runToolUrl + "&action=getGenealogy&id=" + data + "&depth=" + depth + "&treeId=" + treeIdRef.value, function (error, treeData) {
                        treeLoading.close();
                        drawTree(treeData);
                        genealogy = treeData;

                        setNodeColor(selectedIdRef.value)
                    });

                }
            });

        }
    }
    function drawTree(treeData) {

        $("svg").remove();
        window.tree = dTree.init(treeData, {
            target: "#graph",
            debug: false,
            height: 750,
            width: document.body.clientWidth,
            margin: {
                top: 50,
                right: 0,
                bottom: 0,
                left: 0
            },
            nodeWidth: verticalNode ? 40 : undefined,
            callbacks: {
                nodeClick: function (name, extra, id) {
                    if (extra && extra.mid) {
                        selectedIdRef.value = extra.mid;
                        selectedNodeDepth.value = extra.depth;
                        nodeClicked(selectedIdRef.value, id);
                    }
                },
                nodeRightClick: nodeRightClick,
                nodeRenderer: nodeRenderer,
                textRenderer: textRenderer
            }
        });

        if (needFocusLastEditNode) {
            setTimeout(()=>{
                focusLastEditMemberNode()
                needFocusLastEditNode = false
            }, 500)
        }
    }

    async function nodeRightClick(name, extra, id, e) {
        console.log("nodeRightClick", id)
        if(showEditMember.value){
            let result = await editMemberEl.value.cancel()

            if (!result){
                return
            }
        }

        if (!extra || !extra.mid) {
            return
        }

        if(getUserId() == 28){

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
            return
        }

        Utils.vibrate([50])
        let btns = $('#node-ctrl-btns')
        showNodeCtrlBtn.value = true;
        btns.css({"top": e.pageY - 5, "left": e.pageX - 5});
        btns.mouseleave(function (e) {
            showNodeCtrlBtn.value = false;
        });

        selectNodeId = id;
        nodeSharing.value = false
        nodeCreatorIdRef.value = extra.creatorId;
        selectedIdRef.value = extra.mid;
        memberGenderRef.value = extra.gender
        nodeCtrlTitle.value = name
        getJudgeParents(selectedIdRef.value)
    }

    //删除成员
    function deleteMember(id) {
        showNodeCtrlBtn.value = false
        Dialog.confirm({
            title: '删除成员',
            message: '删除成员将一并删除成员\n所连接的关系确认要删除吗？',
            confirmButtonText: '删除'
        }).then(() => {
            $.ajax({
                type: "POST",
                url: g_runToolUrl,
                data: "action=deleteMember&id=" + id + "&treeId=" + treeIdRef.value,
                dataType: "json",
                success: function (data) {
                    if (data.result) {
                        // initTree(treeIdRef.value)
                        showMemberDetailRef.value = false
                        removeNode()
                    }
                }
            });
        }).catch(() => {});
    }

    const showInsertFirstMember = ref(false);
    const insertFirstEl = ref(null)

    function addFirstMember() {
        if (isCurrentClient('mobile')) {
            showMemberDetailRef.value = false
        }
        showInsertFirstMember.value = true;
    }

    const INSERT_FATHER = 1;
    const INSERT_MOTHER = 2;
    const INSERT_SPOUSE = 3;
    const INSERT_CHILDREN = 4;

    const showInsertMember = ref(false);
    const inserting = ref(false)
    const memberGenderRef = ref(0)
    const insertType = ref(INSERT_FATHER)
    const insertMemberEl = ref(null)
    const insertNodeTitle = computed(() => {
        let cont = `为 ${nodeCtrlTitle.value} 添加`;

        switch (insertType.value) {
            case INSERT_FATHER:
                cont += '父亲'
                break;
            case INSERT_MOTHER:
                cont += '母亲'
                break;
            case INSERT_SPOUSE: {
                if (memberGenderRef.value == 0) {
                    cont += '丈夫'
                } else {
                    cont += '妻子'
                }
            }
                break;
            case INSERT_CHILDREN:
                cont += '子女'
                break;
        }

        return cont;
    })

    function addNode(value) {
        showNodeCtrlBtn.value = false
        insertType.value = value
        if (isCurrentClient('mobile')) {
            showMemberDetailRef.value = false
        }
        showInsertMember.value = true
    }

    function insertSuccess(member) {
        console.log(selectedIdRef, member)

        showInsertMember.value = false
        showInsertFirstMember.value = false
        inserting.value = false

       setTimeout(()=>{
           if (insertType.value === INSERT_FATHER || insertType.value === INSERT_MOTHER){
               getMembers(treeIdRef.value)
           }else{
               membersRef.value.push({
                   id: member.id,
                   name: member.name,
                   identityId: member.identityId
               })
               insertNode(member)
           }
       }, 50)

        recordEditMemberId(member.id)
        toast("已保存!")
    }

    function removeNode(){

        if (genealogy[0].mid == selectedIdRef.value){
            getMembers(treeIdRef.value)
        }else{
            let parentNode = findNode(genealogy[0], true)
            drawTree(genealogy);
            focusNode(parentNode.mid);
            const index = membersRef.value.findIndex(m => m.id == selectedIdRef.value)
            membersRef.value.splice(index, 1)
        }
    }

    function updateNode(member){
        const node = findNode(genealogy[0])
        node.name = member.name

        const nodeEl = findNodeEl(member.id)
        nodeEl.getElementsByClassName('nodeText')[0].innerHTML = member.name

        if (member.is_alive){
            nodeEl.classList.remove('death')
        }else{
            nodeEl.classList.add('death')
        }

        if (member.gender){
            nodeEl.classList.add('man')
            nodeEl.classList.remove('woman')
        }else{
            nodeEl.classList.remove('man')
            nodeEl.classList.add('woman')
        }


        membersRef.value.forEach(m => {
            if (m.id == member.id){
                m.name = member.name
            }
        })
    }

    function insertNode(member){
        let node;
        for (let g of genealogy) {
            node = findNode(g)

            if (node){
                break
            }
        }

        if (!node.marriages){
            node.marriages = [{
                "children": [], "spouse": {"name": "未填", "mid": "", "class": "woman not-info"}
            }]
        }

        if (insertType.value === INSERT_SPOUSE){

            const spouse = {
                "extra": {
                    "isAlive": member.is_alive,
                    "gender": member.gender,
                    "creatorId": member.creatorId ,
                    "mid": member.id,
                },
                "name": member.name,
                "mid": member.id,
                "class": member.gender ? "man" : "woman"
            }

            if(!node.marriages[0].spouse.mid){
                node.marriages[0].spouse = spouse
            }else{
                let marriage = {
                    children: [],
                    spouse: spouse
                }
                node.marriages.push(marriage)
            }
        }else if(insertType.value === INSERT_CHILDREN){

            if (!node.marriages[0].children){
                node.marriages[0].children = []
            }

            node.marriages[0].children.push({
                "extra": {
                    "isAlive": member.is_alive,
                    "gender": member.gender,
                    "creatorId": member.creatorId ,
                    "mid": member.id,
                },
                "name": member.name,
                "mid": member.id,
                "class": member.gender ? "man" : "woman"
            })

        }

        drawTree(genealogy);
        focusNode(member.id);
    }


    function findNode(node, isRemove = false){
        if (node.mid == selectedIdRef.value){
            return node;
        }

        if (node.marriages) {
            for (let i in node.marriages) {
                if (node.marriages[i].spouse.mid != selectedIdRef.value) {
                   continue
                }

                if (isRemove){
                    let child = node.marriages[i].children;
                    if (child){
                        if (node.marriages.length === 1){
                            node.marriages[i].spouse = {"name": "未填", "mid": "", "class": "woman not-info"}
                        }else{
                            node.marriages.splice(i, 1)
                            node.marriages[0]['children'] = child
                        }
                    }else{
                        node.marriages.splice(i, 1)
                    }
                }
                return node;
            }
        }else{
            return null;
        }

        let children = node.marriages[0].children;
        if (children){

            for (let ci in children) {

                let cNode = findNode(children[ci], isRemove);

                if (cNode){
                    if (isRemove && cNode.mid === selectedIdRef.value){
                        children.splice(ci, 1)
                        return node; //返回父节点
                    }

                    return cNode;
                }
            }
        }

        return null;
    }

    //编辑表单信息
    const showEditMember = ref(false);
    const editMemberEl = ref(null)
    const editFormTitle = computed(() => {
        return `
            编辑 ${memberDetailRef.value.name}
            的信息`
    })

    function editForm(id) {
        showEditMember.value = true;

        recordEditMemberId(memberDetailRef.value.id)

        if (isCurrentClient('mobile')) {
            memberBottomSheet.value.style.height = `80vh`
        }
        nextTick(() => {

            memberDetailLoadingRef.value = true
            editMemberEl.value.init(id, memberDetailLoadingRef)
        })
    }

    function editSuccess() {
        showEditMember.value = false;
        recordEditMemberId(memberDetailRef.value.id)
        getMemberDetail(memberDetailRef.value.id, (m)=>{
            updateNode(m)
        })
    }

    /**
     * 记录最后编辑的成员
     * @type {ComputedRef<string>}
     */

    let needFocusLastEditNode = false;

    let keyOfLastEditMemberID = computed(()=>{
        return `lastEditMemberId_${getUserId()}_${treeIdRef.value}`
    })

    function recordEditMemberId(id){
        localStorage[keyOfLastEditMemberID.value] = id;
    }

    function getLastEditMemberId() {
        return localStorage[keyOfLastEditMemberID.value]
    }

    function focusLastEditMemberNode(){
        const id = getLastEditMemberId();

        if (!id){
            return
        }

        focusNode(id)
    }

    /**
     * 分享树
     */

    let sharing = ref(false)

    function sharePedigree() {
        useShareToWechat(pedigreeDetailRef.value.name, "我分享了家谱给你,快来看看", {treeId: treeIdRef.value}, sharing, window.share)
    }


    /**
     * 分享节点
     */

    const nodeSharing = ref(false)
    watch(nodeSharing, (newVal, oldVal) => {
        if (!newVal) {
            showNodeCtrlBtn.value = false
        }
    })

    function shareNode() {
        useShareToWechat(nodeCtrlTitle.value, "我分享了家谱给你,快来看看", {
            memberId: selectedIdRef.value,
            treeId: treeIdRef.value
        }, nodeSharing,window.share)
    }

    const memberBottomSheet = ref(null);
    const vh = window.innerHeight / 100
    const max = vh * 80;
    const min = vh * 30;
    function memberSheetMove(e){
        const h = window.innerHeight - e.changedTouches[0].pageY
        if (h > min && h < max){
            memberBottomSheet.value.style.height = `${h}px`
        }
    }


    function pushHistory(){
        if (isCurrentClient('mobile'))
            window.history.pushState(null, "相册",  document.URL)
    }

    async function popstateListener(){
        console.log('popstateListener')

        if (!isCurrentClient('mobile')) return

        if(showIncense.value){
            exitPray()
            return
        }

        if (showMemberDetailRef.value){
            if(showEditMember.value){
                let result = await editMemberEl.value.cancel()

                if (!result){
                    pushHistory()
                    return
                }
            }

            showMemberDetailRef.value = false;
        }

        expansionTreeControllerRef.value && (expansionTreeControllerRef.value = false)
        // showNodeCtrlBtn.value && (showNodeCtrlBtn.value = false)

    }

    onMounted(()=>{
        window.addEventListener('popstate', popstateListener,false)
    })

    onBeforeUnmount(()=>{
        window.removeEventListener('popstate', popstateListener)
    })
</script>
<style lang="less">
  @import "../../css/dTree.css";
  @import "../../css/candle.css";
  @import "../../css/glorify.css";
  .ant-spin-blur {
    opacity: .2 !important;
  }
  #home-root {
    height: 100vh;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    .van-nav-bar__title{
      color: #286d91;
      font-size: 1.2rem;
      font-weight: bold;
    }

    #logo {
      height: 50px;
      margin-left: 1rem;
    }

    .navbar-brand {
      padding: 0px;
    }
  }

  .navbar {
    height: 60px;
    display: flex;
    align-items: center;
  }


  .panel-heading {
    border-top-left-radius: unset;
    border-top-right-radius: unset;
  }

  .dropdown-menu {
    min-width: auto;
  }

  .panel {
    border-radius: unset;
  }

  .navbar {
    margin-bottom: 0px;
  }

  .dropdown-menu > .active > a, .dropdown-menu > .active > a:focus, .dropdown-menu > .active > a:hover {
    color: rgb(59 59 59);
    text-decoration: none;
    background-color: #EEEEEE;
    outline: 0;
  }

  .bootstrap-select.btn-group .dropdown-menu li.active small {
    color: rgb(59 59 59);
  }

  table td {
    text-align: center;
    vertical-align: middle !important;
  }

  #member {
    margin: 0 10px;
    width: 78px;
    padding-left: 0px;
    margin: 0 0;
  }

  #depth {
    margin: 0 10px;
    width: 45px;
    padding-left: 0px;
    margin: 0 2px;
  }

  .head-text {
    font-size: 14px;
    height: 35px;
    white-space: nowrap;
    padding: 6px;
  }


  #collapse-father, #collapse-mother, #collapse-spouse, #collapse-child {
    padding: 0 2px;
  }

  #info a {
    cursor: pointer;
    text-decoration: underline;
    font-size: 15px;
  }

  #infohead {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    padding: 8px;
    border-radius: 8px;
    margin: 1rem;
    background: white;
    box-shadow: 0 4px 11px -3px #c2cad0;


    .head-info-label {
      font-weight: bold;
      font-size: 1rem;
    }
  }

  .insert-dialog {
    width: 500px;
    margin: 50px auto;
  }

  .hint {
    color: red;
  }


  @media screen and (max-width: 768px) {
    #container-div {
      width: 100%;
    }

    #content {
      margin: 0px;
      height: 100%;
    }

    .panel {
      border: unset;
      -webkit-box-shadow: unset;
      box-shadow: unset;
    }

    #incense-tr {
      margin-top: 30px;
    }

    .navbar-brand {
      padding-left: 15px;
    }
  }

  @media screen and (min-width: 768px) {
    #container-div {
      width: 85%;
    }

    #content {
      margin: 20px 0 0 0;
    }

    .navbar-brand {
      padding-left: 50px;
      padding-right: 30px;
    }
  }

  /*bootstrap-select渲染时使用的控件上添加margin*/
  .bootstrap-select.btn-group:not(.input-group-btn), .bootstrap-select.btn-group[class*="col-"] {
    margin-left: 2px;
  }

  #incense-tr img {
    border: 7px solid #ffffff33;
  }

  #uu {
    /*动画的过渡效果*/
    transition-duration: 1s;
    transition-property: width, opacity;
    opacity: 0;
    width: 0px;
  }

  .btn-util {
    border-radius: 9px;
    width: 93px;
    height: 40px;
    padding: unset;
    background-color: #555;
    margin: 10px;
    border: none;
    color: white;
    display: inline-flex;
    text-align: center;
    align-items: center;
    justify-content: center;
  }

  #graph {
    width: 100%;
    top: 0;
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  #member-info-side {
    right: 0;
    width: 30%;
    background: white;
    margin: 0 1rem 1rem 0;
    box-shadow: 0 4px 11px -3px #8c9aa6;
    border-radius: 8px;
    padding: 1rem;
    position: relative;
    display: flex;
    overflow: auto;

    .ant-spin-nested-loading {
      width: 100%;
    }
  }


  #graph-and-side {
    display: flex;
    flex: 1;
    background: #f4f9ff;
    flex-direction: column;
    overflow: hidden;
  }

  #graph-wrap {
    position: relative;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  #pedigree-detail, #edit-pedigree {
    background: white;
    margin: 0 1rem;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 11px -3px #c2cad0;
    overflow-y: auto;
    margin-bottom: 8rem;
  }
  .pedigree-btns{
    button{
      margin: 2px 0;
    }
  }

  .ant-select .ant-select-arrow i {
    transition: all 0.3s;
  }

  .ant-select-open .ant-select-arrow i {
    transform: rotate(180deg);
  }

  .ant-descriptions-item-label {
    text-align: right;
    font-size: 1rem;
  }

  .ant-descriptions-item-content {
    font-size: 1rem;
    text-align: left;
  }

  @media screen and (max-width: 414px) {
    #infohead {
      position: fixed;
      z-index: 100;
      flex-direction: column;
      bottom: 0px;
      margin: 0;
      width: 100vw;
      padding: 0 8px;
      border-radius: 8px 8px 0 0;
      box-shadow: 0 0px 17px -3px #c2cad0;
      transition: all 0.2s;
      min-height: 230px;


    }
    .close-member-btn {
      right: 16px;
    }
    .pedigree-btns {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      grid-template-rows: repeat(2, 1fr);
      grid-column-gap: 6px;
      grid-row-gap: 6px;
      margin: 6px 0;
      width: 100%;
      order: 3;
    }

    .container-handle {
      height: 30px;
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;

      span {
        height: 5px;
        width: 50px;
        background: #4f7b92;
        display: block;
        border-radius: 3px;
      }
    }

    .swith-select {
      width: 100%;
      padding-left: 0 !important;
      order: 1;
      margin: 6px 0;
    }

    .swith-depth {
      width: 100%;
      order: 2;
      margin: 6px 0;
      justify-content: flex-start !important;
    }

    .van-nav-bar {
      position: absolute;
      width: 100vw;
    }

    #pedigree-detail, #edit-pedigree {
      margin: 0 !important;
       border-radius: 0px;
       box-shadow: none;
      padding: 10px;
    }
  }
  .close-node-ctrl-btn{
    right: 8px;
    top: 8px;
    width: 28px;
    height: 28px;
    box-sizing: content-box;
    line-height: 28px;
    border-radius: 50px;
    text-align: center;
    cursor: pointer;
    background: white;
    z-index:10000;
  }
  #node-ctrl-btns {
    position: fixed;
    z-index: 100000000000;
    display: flex;
    padding: 10px;
    background: white;
    box-shadow: 0 3px 8px -2px #b8b8b8;
    border-radius: 8px;

    p{
      text-align: center;font-weight: bold;user-select: none;display: flex;
      align-items: center;
      span{
        flex: 1;
      }
    }



    button {
      margin: 2px;
    }

    > * {
      user-select: none;
    }
  }

  .ant-descriptions-item-content {
    overflow-wrap: anywhere;
  }

  .van-nav-bar {
    padding: 6px 0;
  }

  .van-nav-bar__content {
    > div {
      padding: 0;
    }
  }

  #incense-Modal{
    z-index: 100000;
  }

  #float-btn{
    position: fixed;
    bottom: 2rem;
    right: 1rem;

    i{
      font-weight: bold
    }
  }

  .top-dashed{
    text-align: right;
    border-top: 1px dashed #c3c3c3;
    padding-top: 10px;
    margin-top: 30px;
  }

  .detail-bottom-sheet{
    box-shadow: 0px 7px 18px 0px #4f7b92;
  }

  .van-cascader__option{
    cursor: pointer;
    border-bottom: 1px #e7e7e7 dashed;

    &:hover {
      background:whitesmoke;
    }
  }

  .ant-descriptions-bordered .ant-descriptions-item-label {
    min-width: 6rem;
  }
  @keyframes shakeX {
    from,
    to {
      transform: translate3d(0, 0, 0);
    }

    10%,
    30%,
    50%,
    70%,
    90% {
      transform: translate3d(-10px, 0, 0);
    }

    20%,
    40%,
    60%,
    80% {
      transform: translate3d(10px, 0, 0);
    }
  }

  .shakeX {
    animation-duration: 1s;
    animation-name: shakeX;
  }
</style>
