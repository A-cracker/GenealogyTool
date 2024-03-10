<template>
    <div>
        <mu-dialog scrollable :open.sync="showEditMember" :fullscreen="isCurrentClient('mobile')">

            <insert-member v-if="isInsert" ref="editMemberEl"
                           :id="member.id"
                           :treeId="member.treeId"
                           :isFirstMember="member.isFirstMember"
                           :type="member.type"
                           :memberId="member.targetId"
                           :genderId="member.gender"
                           @close="showEditMember = false"
                           @success="editSuccess"></insert-member>
            <edit-member v-else ref="editMemberEl"
                                     :treeId="member.treeId"
                                     @close="showEditMember = false"
                                     @success="editSuccess"></edit-member>
            <a-button slot="actions" @click="editMemberEl.cancel()">取消</a-button>
            <a-button slot="actions" @click="editMemberEl.submit()" type="primary">保存</a-button>
        </mu-dialog>

        <mu-dialog :open.sync="showMsg" dialog-class="my-dialog">
            <div>有未保存的内容, 是否继续编辑?</div>
            <a-button slot="actions" @click="showMsg = false">下次</a-button>
            <a-button slot="actions" @click="remove" type="danger">丢弃</a-button>
            <a-button slot="actions" @click="edit" type="primary">编辑</a-button>
        </mu-dialog>
    </div>

</template>
<script setup>
    import {getBaseUrl, getResourceUrl, isCurrentClient, toast, getUserId} from "../../common/env"
    import EditMember from './EditMember'
    import InsertMember from './InsertMember'
    import {provide, onMounted, ref, nextTick} from "vue"
    import MemberLocalStorage from '../../common/member_localstorage'
    import { Dialog } from 'vant'

    const memberLocalStorage = new  MemberLocalStorage()

    const showEditMember = ref(false)
    const editMemberEl = ref(null)
    const treeIdRef = ref(0)

    const isInsert = ref(false);
    let id;
    const member = ref({})

    function editSuccess() {
        showEditMember.value = false;
    }

    const showMsg = ref(false)
    function continueEdit(members) {
        id = members[0]
        showMsg.value = true;
    }

    function remove(){
        showMsg.value = false;
        memberLocalStorage.removeEditingMember(id)
    }

    function edit(){
        showEditMember.value = true
        showMsg.value = false;

        member.value = memberLocalStorage.getEditingMember(id)

        if (member.value.hasOwnProperty('isFirstMember')){ //只有插入成员才区分是否是树的第一成员
            isInsert.value = true;
        }else{
            isInsert.value = false;
            nextTick(() => {
                editMemberEl.value.init(id, null, true)
            })
        }
    }

    onMounted(()=>{
        const members = memberLocalStorage.getEditingMembers()

        if (members.length > 0){
            continueEdit(members)
        }
    })

</script>
<style lang="less">

</style>
