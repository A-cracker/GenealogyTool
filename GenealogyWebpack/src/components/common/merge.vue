<template>
    <div id="merge-root" :class="{'mobile': isCurrentClient('mobile') }">
        <div class="ctrl-wrap" v-if="!isCurrentClient('mobile')">
            <a-button type="danger" @click="emit('cancel')">取消</a-button>
            <div style="padding-left: 1rem">
                <span class="select-label">源家谱:</span>
                <a-select :value="sourceTreeId" style="min-width: 150px" @change="switchPedigree" placeholder="选择源家谱">
                    <van-icon slot="suffixIcon" class-prefix="iconfont" name="arrow_down" color="#4f7b92"
                              size="1rem"></van-icon>
                    <a-select-option :value="p.id" v-for="p in pedigreesRef" :disabled="p.id === targetTreeId">
                        {{ p.name }}
                    </a-select-option>
                </a-select>
            </div>
            <div style="padding-left: 1rem">
                <span class="select-label">目标家谱:</span>
                <a-select :value="targetTreeId" style="min-width: 150px" @change="switchTargetPedigree"
                          placeholder="选择目标家谱">
                    <van-icon slot="suffixIcon" class-prefix="iconfont" name="arrow_down" color="#4f7b92"
                              size="1rem"></van-icon>
                    <a-select-option :value="p.id" v-for="p in pedigreesRef" :disabled="p.id === sourceTreeId">
                        {{ p.name }}
                    </a-select-option>
                </a-select>
            </div>
            <div>
                <div v-show="sourceMemberName && targetMemberName">
                    <span class="select-label">将&nbsp;<span class="source-member">{{ sourceMemberName }}</span>&nbsp;作为&nbsp;<span
                            class="target-member">{{ targetMemberName }}</span>&nbsp;的&nbsp;</span>
                    <a-radio-group v-model="mergeType" @change="changeType" button-style="solid">
                        <a-radio-button :value="1" v-show="showTypes[0]">
                            父亲
                        </a-radio-button>
                        <a-radio-button :value="2" v-show="showTypes[1]">
                            母亲
                        </a-radio-button>
                        <a-radio-button :value="3" v-show="showTypes[2]">
                            配偶
                        </a-radio-button>
                        <a-radio-button :value="4" v-show="showTypes[3]">
                            子女
                        </a-radio-button>
                        <!--                      <a-radio-button :value="5" v-show="showTypes[4]">
                                                   合并
                                              </a-radio-button>-->
                    </a-radio-group>
                </div>
            </div>
            <a-button type="primary" :disabled="!mergeType" @click="mergePedigreeSubmit">合并</a-button>
        </div>

        <div class="trees">

            <div class="tree " id="source-tree" v-loading="sourceTreeLoading">
                <div style="padding-left: 1rem" v-if="isCurrentClient('mobile')">
                    <span class="select-label">源家谱:</span>
                    <a-select :value="sourceTreeId" style="min-width: 150px" @change="switchPedigree"
                              placeholder="选择源家谱">
                        <van-icon slot="suffixIcon" class-prefix="iconfont" name="arrow_down" color="#4f7b92"
                                  size="1rem"></van-icon>
                        <a-select-option :value="p.id" v-for="p in pedigreesRef" :disabled="p.id === targetTreeId">
                            {{ p.name }}
                        </a-select-option>
                    </a-select>
                </div>
                <p class="select-member-tip" v-show="sourceTreeId && !sourceMemberName">请选择一个成员节点</p>
                <div id="source-tree-wrap" class="wrap">
                    <empty2 msg="请选择源家谱" iconSize="120px" v-show="!sourceTreeId"></empty2>
                </div>

            </div>
            <div class="tree " id="target-tree" v-loading="targetTreeLoading">
                <div style="padding-left: 1rem" v-if="isCurrentClient('mobile')">
                    <span class="select-label">目标家谱:</span>
                    <a-select :value="targetTreeId" style="min-width: 150px" @change="switchTargetPedigree"
                              placeholder="选择目标家谱">
                        <van-icon slot="suffixIcon" class-prefix="iconfont" name="arrow_down" color="#4f7b92"
                                  size="1rem"></van-icon>
                        <a-select-option :value="p.id" v-for="p in pedigreesRef" :disabled="p.id === sourceTreeId">
                            {{ p.name }}
                        </a-select-option>
                    </a-select>
                </div>
                <p class="select-member-tip" v-show="targetTreeId && !targetMemberName">请选择一个成员节点</p>
                <div id="target-tree-wrap" class="wrap">

                    <empty2 msg="请选择目标家谱" iconSize="120px" v-show="!targetTreeId"></empty2>
                </div>
            </div>
            <div class="tree " id="merge-tree">
                <div v-show="sourceMemberName && targetMemberName"  v-if="isCurrentClient('mobile')">
                    <span class="select-label">将&nbsp;<span class="source-member">{{ sourceMemberName }}</span>&nbsp;作为&nbsp;<span
                            class="target-member">{{ targetMemberName }}</span>&nbsp;的&nbsp;</span>
                    <a-radio-group v-model="mergeType" @change="changeType" button-style="solid">
                        <a-radio-button :value="1" v-show="showTypes[0]">
                            父亲
                        </a-radio-button>
                        <a-radio-button :value="2" v-show="showTypes[1]">
                            母亲
                        </a-radio-button>
                        <a-radio-button :value="3" v-show="showTypes[2]">
                            配偶
                        </a-radio-button>
                        <a-radio-button :value="4" v-show="showTypes[3]">
                            子女
                        </a-radio-button>
                        <!--                      <a-radio-button :value="5" v-show="showTypes[4]">
                                                   合并
                                              </a-radio-button>-->
                    </a-radio-group>
                </div>

                <div id="merge-tree-wrap" class="wrap">

                    <empty2 msg="未合成" iconSize="120px" v-show="!mergeType"></empty2>
                </div>
            </div>

            <div v-if="isCurrentClient('mobile')" style="    text-align: center; background: white; padding: 5px; border-radius: 6px;">
                <a-button type="danger" @click="emit('cancel')">取消</a-button>

                <a-button type="primary" :disabled="!mergeType" @click="mergePedigreeSubmit">合并</a-button>
            </div>
        </div>

        <!--

                <mu-slide-right-transition>
                    <div v-if="isCurrentClient('pc')" id="member-info-side" v-show="showMemberDetailRef">

                        <a-spin :spinning="memberDetailLoadingRef" >
                            <member-detail :memberDetail="memberDetailRef" :treeId="treeIdRef"
                                           @close="showMemberDetailRef = false"></member-detail>
                        </a-spin>
                    </div>
                </mu-slide-right-transition>
        -->


        <!--
                <mu-bottom-sheet v-if="isCurrentClient('mobile')"
                                 scrollable :overlay="false" :open.sync="showMemberDetailRef">
                    <div style="padding: 1rem;max-height: 63vh;overflow-y: auto;">

                        <a-spin :spinning="memberDetailLoadingRef" >
                            <member-detail :memberDetail="memberDetailRef" :treeId="treeIdRef"
                                           @close="closeMemberDetail"></member-detail>
                        </a-spin>

                    </div>
                </mu-bottom-sheet>-->


        <!--        <div id="node-ctrl-btns" v-show="showNodeCtrlBtn">
                    <a-spin :spinning="judgeParentLoadingRef">
                        <p style="text-align: center;font-weight: bold;user-select: none">{{ nodeCtrlTitle }}</p>
                        <a-button @click="addNode(1)" size="small" type="primary" v-show="showAddFatherBtnRef">父亲</a-button>
                        <a-button @click="addNode(2)" size="small" type="primary" v-show="showAddMotherBtnRef">母亲</a-button>
                        <a-button @click="addNode(3)" size="small" type="primary">配偶</a-button>
                        <a-button @click="addNode(4)" size="small" type="primary">子女</a-button>
                        <a-button :loading="nodeSharing" @click="shareNode" size="small" type="primary">分享</a-button>
                        <a-button size="small" type="danger" @click="deleteMember(click_idRef)">删除</a-button>
                    </a-spin>
                </div>-->

    </div>
</template>
<script setup>
    import {computed, getCurrentInstance, onMounted, ref} from "vue";
    import {getBaseUrl, isCurrentClient, toast} from "../../common/env"
    import Loading from 'muse-ui-loading';
    import Utils from "../../common/utils";
    import Empty2 from "./empty2";
    import MemberDetail from "./MemberDetail"
    import {Dialog} from "vant";


    const vueIns = getCurrentInstance().proxy

    const props = defineProps(['sourceTree']);
    const emit = defineEmits(['cancel','success']);


    const TARGET_TREE_WRAP_ID = '#target-tree-wrap'
    const SOURCE_TREE_WRAP_ID = '#source-tree-wrap'
    const MERGE_TREE_WRAP_ID = '#merge-tree-wrap'

    var height = 750;
    var width = (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) + 50;
    (width > 1000) ? width = 1000 : null;

    const sourceTreeId = ref(props.sourceTree)
    const targetTreeId = ref(undefined)

    let sourceTree = {}
    let targetTree = {}
    let mergeTree = {}

    const sourceTreeLoading = ref(false)
    const targetTreeLoading = ref(false)

    let targetMemberId;
    let sourceMemberId;
    const sourceMemberGender = ref(1)
    const targetMemberGender = ref(1)
    const sourceMemberName = ref(undefined)
    const targetMemberName = ref('')

    //父亲, 母亲, 配偶, 子女
    const showTypes = computed(() => {
        if (sourceMemberGender.value == 0 && targetMemberGender.value == 0) {
            return [false, true, false, true, true]
        } else if (sourceMemberGender.value == 1 && targetMemberGender.value == 1) {
            return [true, false, false, true, true]
        } else if (sourceMemberGender.value == 1 && targetMemberGender.value == 0) {
            return [true, false, true, true, true]
        } else {
            return [false, true, true, true, true]
        }
    })

    const mergeType = ref(undefined)


    function mergeNodes(sourceTreeTemp, targetTreeTemp, sourceNode, targetNode) {
        querySameNode(sourceNode, targetNode)
    }

    //找相同和不同的节点
    function querySameNode(sourceNode, targetNode) {
        let sourceTreeArr = []
        let targetTreeArr = []
        treeToArray(sourceNode, sourceTreeArr)
        treeToArray(targetNode, targetTreeArr)

        console.log(sourceTreeArr)
        console.log(targetTreeArr)

        for (let i = 0; i < sourceTreeArr.length; i++) {
            sourceTreeArr[i].forEach((n) => {
                if (targetTreeArr[i]) {
                    const index = targetTreeArr[i].findIndex(n1 => n1.name == n.name)
                    if (index === -1) {

                        console.log('b异:', n.name)
                        /*  targetTreeArr[i-1][0].marriages[0]
                          && targetTreeArr[i-1][0].marriages[0].children
                          && targetTreeArr[i-1][0].marriages[0].children.push(n)*/
                    } else {
                        n.class += " merge-node"
                        console.log('同:', n.name)
                    }
                } else {
                    console.log('b异:', n.name)
                }
            })
        }

        for (let i = 0; i < targetTreeArr.length; i++) {
            targetTreeArr[i].forEach((n) => {
                if (sourceTreeArr[i]) {
                    const index = sourceTreeArr[i].findIndex(n1 => n1.name == n.name)
                    if (index === -1) {

                        console.log('t异:', n.name)
                    } else {

                        n.class += " merge-node"
                        console.log('同:', n.name)
                    }
                } else {
                    console.log('t异:', n.name)
                }
            })
        }

    }

    //树转二维数组
    function treeToArray(node, array = [], level = 0) {
        if (!array[level]) {
            array[level] = [node]
        } else {
            array[level].push(node)
        }

        if (!node.marriages) {
            return;
        }

        const l = level + 1
        for (const marriage of node.marriages) {
            if (marriage.spouse.mid) {
                array[level].push(marriage.spouse)
            }

            if (!marriage.children) {
                continue
            }

            for (const child of marriage.children) {
                treeToArray(child, array, l)
            }
        }

    }

    function changeType() {
        let targetTreeTemp = Utils.clone(targetTree)
        let sourceTreeTemp = Utils.clone(sourceTree)

        let targetNode = queryNode(targetTreeTemp[0], targetMemberId)
        let sourceNode = queryNode(sourceTreeTemp[0], sourceMemberId)

        if (sourceNode == null || targetNode == null) {
            return
        }

        sourceNode.class += ' source-node'
        targetNode.class += ' target-node'
        if (mergeType.value == 1) { //父亲
            if (!sourceNode.marriages) {
                sourceNode['marriages'] = [
                    {
                        children: [],
                        spouse: {
                            name: '无名氏',
                            mid: '',
                            class: 'woman not-info'
                        }
                    }
                ]
            }

            sourceNode.marriages[0].children.push(targetNode)

            mergeTree = sourceTreeTemp
        } else if (mergeType.value == 2) { //母亲
            if (!sourceNode.marriages[0].children) {
                sourceNode['marriages'][0]['children'] = []
            }

            sourceNode.marriages[0].children.push(targetNode)

            mergeTree = sourceTreeTemp
        } else if (mergeType.value == 3) { // 配偶

            targetNode.marriages[0].spouse = sourceNode

            mergeTree = targetTreeTemp
        } else if (mergeType.value == 4) { //子女

            if (!targetNode.marriages) {
                targetNode['marriages'] = [
                    {
                        children: [],
                        spouse: {
                            name: '无名氏',
                            mid: '',
                            class: 'woman not-info'
                        }
                    }
                ]
            }

            targetNode.marriages[0].children.push(sourceNode)

            mergeTree = targetTreeTemp
        } else if (mergeType.value == 5) { //合并
            mergeNodes(sourceTreeTemp, targetTreeTemp, sourceNode, targetNode)

            mergeTree = targetTreeTemp
        }

        initMergeTree(mergeTree)
    }

    function initMergeTree(tree) {
        $(MERGE_TREE_WRAP_ID + " svg").remove();
        window.tree = dTree.init(tree, {
            target: MERGE_TREE_WRAP_ID,
            debug: false,
            height: 750,
            width: document.body.clientWidth,
            margin: {
                top: 50,
                right: 0,
                bottom: 0,
                left: 0
            },
            callbacks: {
                nodeRenderer: nodeRenderer
            }
        })
    }

    function queryNode(node, memberId) {
        if (node.mid == memberId) {
            return node;
        }

        if (!node.marriages) {
            return null;
        }

        for (const marriage of node.marriages) {
            if (!marriage.children) {
                continue
            }

            for (const child of marriage.children) {
                let temp = queryNode(child, memberId)
                if (temp) {
                    return temp;
                }
            }
        }
    }

    //选择家谱下拉列表
    const pedigreesRef = ref([])

    function switchPedigree(id) {
        sourceTreeId.value = id
        mergeType.value = undefined
        sourceMemberName.value = undefined
        sourceTreeLoading.value = true;
        initTree(id, 'source')
    }

    function switchTargetPedigree(id) {
        targetTreeId.value = id
        mergeType.value = undefined
        targetMemberName.value = undefined
        targetTreeLoading.value = true;
        initTree(id, 'target')
    }

    //查看关系
    const membersRef = ref([])
    const selectedMemberIdRef = ref("")
    const depthRef = ref(100)       //查看按钮


    //成员详情
    const showMemberDetailRef = ref(false)
    const memberDetailRef = ref({})
    const memberDetailLoadingRef = ref(false)

    function closeMemberDetail() {
        showMemberDetailRef.value = false;
        tree.resetZoom()

    }

    //树
    const t_idRef = ref("")
    const showAddFirstMemberBtnRef = ref(false);
    const click_idRef = ref("")
    const expansionTreeControllerRef = ref(false);

    onMounted(() => {
        // initTree(vueIns.$route.query.treeId, true)
        getAllPedigree()

        switchPedigree(props.sourceTree)
    })




    function getMembers(treeId, type) {
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
                    sourceTreeLoading.value = targetTreeLoading.value = false;
                    return
                }

                showAddFirstMemberBtnRef.value = false;

                $.ajax({
                    type: "GET",
                    url: g_runToolUrl,
                    data: "action=getTreeAncestorByTreeId&treeId=" + treeId,
                    dataType: "json",
                    success: function (data) {
                        t_idRef.value = data.id;
                        selectedMemberIdRef.value = data.id
                        treeInit(depthRef.value, treeId, data.id, type);
                    }, error: function (data) {
                        console.log(data);
                        sourceTreeLoading.value = targetTreeLoading.value = false;
                    }
                });
            }, error: function (data) {
                toast("出现内部错误" + data);
                sourceTreeLoading.value = targetTreeLoading.value = false;
            }
        });
    }

    function getAllPedigree() {
        //加载全部家谱
        $.ajax({
            type: "GET",
            url: g_runToolUrl + "&action=getPedigrees",
            dataType: "json",
            success: function (data) {
                data = data.rows
                pedigreesRef.value = data

            }, error: function (data) {
                toast("加载家谱错误" + data);
                sourceTreeLoading.value = targetTreeLoading.value = false;
            }
        });

    }

    function initTree(viewTreeId, type) {

        $(MERGE_TREE_WRAP_ID + " svg").remove();

        getMembers(viewTreeId, type)
    }


    //节点点击
    function nodeClicked(memberId, id, type) {
        $(MERGE_TREE_WRAP_ID + " svg").remove();
        mergeType.value = undefined

        if (isCurrentClient('mobile')) {
            tree.zoomToNode(id, 0.8)
        }

        setNodeColor(memberId, type)

        getMemberDetail(memberId);
        // showMemberDetailRef.value = true
    }

    function setNodeColor(id, type) {
        let wrapId;

        if (type == 'source') {
            wrapId = SOURCE_TREE_WRAP_ID;
        } else if (type == 'target') {
            wrapId = TARGET_TREE_WRAP_ID
        }

        $(wrapId + ' div').each(function () {
            $(this).removeClass('selected');
        });

        $(`${wrapId} div[data-mid="${id}"]`).addClass('selected');

    }

    function getMemberDetail(id) {
        memberDetailLoadingRef.value = true
        $.ajax({
            type: "GET",
            url: g_runToolUrl + "&id=" + id,
            data: "action=getDetail",
            dataType: "json",
            success: function (data) {
                memberDetailLoadingRef.value = false
                memberDetailRef.value = data
            }, error: function (data) {
                memberDetailLoadingRef.value = false
                toast("获取成员信息出错")
            }
        });

    }


    function nodeRenderer(name, x, y, height, width, extra, id, nodeClass, textClass, textRenderer) {
        let node = '';
        node += '<div data-tooltip="node-ctrl"';
        node += 'style="height:100%;width:100%;" ';
        if (extra && extra.mid) {
            node += `data-mid="${extra.mid}" `;
        }

        node += `class="node ${nodeClass}" `;
        node += `id="node${id}"><img/>`;
        node += textRenderer(name, extra, textClass);
        node += '</div>';
        return node;
    }

    //树构建
    function treeInit(depth, treeId, memberId, type) {
        let treeWrapElId = ''
        if (type == 'source') {
            treeWrapElId = SOURCE_TREE_WRAP_ID
        } else {
            treeWrapElId = TARGET_TREE_WRAP_ID
        }

        $(treeWrapElId + " svg").remove();
        $.ajax({
            type: "GET",
            url: g_runToolUrl,
            data: "action=judgePosition&id=" + memberId + "&treeId=" + treeId,
            dataType: "json",
            success: function (data) {
                d3.json(g_runToolUrl + "&action=getGenealogy&id=" + data + "&depth=" + depth + "&treeId=" + treeId, function (error, treeData) {
                    sourceTreeLoading.value = targetTreeLoading.value = false;
                    ;
                    if (type == 'source') {
                        sourceTree = treeData
                        console.log('source:', treeData)
                    } else if (type == 'target') {
                        targetTree = treeData
                        console.log('target:', treeData)
                    } else {
                        mergeTree = treeData
                    }

                    window.tree = dTree.init(treeData, {
                        target: treeWrapElId,
                        debug: false,
                        height: 750,
                        width: document.body.clientWidth,
                        margin: {
                            top: 50,
                            right: 0,
                            bottom: 0,
                            left: 0
                        },
                        callbacks: {
                            nodeClick: function (name, extra, id) {
                                if (extra && extra.mid) {
                                    if (type == 'source') {
                                        sourceMemberGender.value = extra.gender
                                        sourceMemberId = extra.mid
                                        sourceMemberName.value = name
                                    } else if (type == 'target') {
                                        targetMemberGender.value = extra.gender
                                        targetMemberId = extra.mid
                                        targetMemberName.value = name
                                    }

                                    click_idRef.value = extra.mid;
                                    nodeClicked(click_idRef.value, id, type);
                                }
                            },
                            nodeRenderer: nodeRenderer
                        }
                    });

                    setNodeColor(t_idRef.value)
                });

            }
        });

    }

    //合并家谱表单提交
    function mergePedigreeSubmit() {

        const mergeFormData = new FormData();
        mergeFormData.append("targetTreeId", targetTreeId.value)
        mergeFormData.append("sourceTreeId", sourceTreeId.value)
        mergeFormData.append("sourceId", sourceMemberId)
        mergeFormData.append("targetId", targetMemberId)
        mergeFormData.append("insertId", mergeType.value)


        $.ajax({
            type: 'POST',
            url: getBaseUrl() + "&action=mergePedigree",
            data: mergeFormData,
            dataType: "json",
            processData: false,
            contentType: false,
            success: function (data) {
                if (data.flag) {
                    emit('success', data.id)
                } else {
                    Dialog.alert({
                        message: data.errorMessage
                    })
                }
            }
        });
    }

</script>
<style lang="less">
  @import "../../css/dTree.css";
  @import "../../css/candle.css";
  @import "../../css/glorify.css";
  @import "../../iconfont/iconfont.css";

  :root {
    --source-color: #009688;
    --target-color: #4f7b92;
  }


  #merge-root {
    height: 100vh;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 1rem;
    background: #f4f9ff;
  }

  .ctrl-wrap {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    background: white;
    align-items: center;
    flex-wrap: wrap;
    padding: 8px;
    border-radius: 8px;
    margin-bottom: 1rem;
    box-shadow: 0 4px 11px -3px #c2cad0;

    > div {
      flex: 1;
      text-align: center;
    }
  }

  .trees {
    display: flex;
    flex: 1;
  }


  #source-tree-wrap .node.selected, .source-node {
    background: var(--source-color);
    border: 1px solid var(--source-color);
    color: rgba(255, 255, 255, 0.9);

    img {
      filter: saturate(11);
    }
  }

  #target-tree-wrap .node.selected, .target-node {
    background: var(--target-color);
    border: 1px solid var(--target-color);
    color: rgba(255, 255, 255, 0.9);

    img {
      filter: saturate(11);
    }
  }


  .merge-node {
    background: linear-gradient(to right, var(--source-color), var(--target-color));
    color: white;
    border-color: linear-gradient(to right, var(--source-color), var(--target-color));

    img {
      filter: saturate(11);
    }
  }

  .tree {
    flex: 1;
    background: white;
    margin: 6px;
    display: flex;
    flex-direction: column;
    transition: all 0.3s;
    position: relative;
    border-radius: 8px;

    &:hover {
      flex: 1.6;
    }

    .wrap {
      flex: 1;
    }
  }

  .select-label {
    font-size: 1rem;
    font-weight: bold;
  }

  .source-member {
    color: white;
    background: var(--source-color);
    padding: 4px 10px;
    border-radius: 4px;
  }

  .target-member {
    color: white;
    padding: 4px 10px;
    border-radius: 4px;
    background: var(--target-color);
  }


  .select-member-tip {
    padding: 10px 1rem;
    font-size: 1rem;
    font-weight: bold;
    color: grey;
    margin: 0;
  }

  #merge-root.mobile {
    padding: 6px;
    overflow-y: scroll;
    padding-top: 5rem;

    .trees {
      flex-direction: column;

      .tree {
        margin: 6px 0;
        max-height: 50vh;
        padding-top: 10px;
        overflow: hidden;
        min-height: 50vh;


        &:hover {
          flex: inherit;
        }
      }
    }
  }
</style>
