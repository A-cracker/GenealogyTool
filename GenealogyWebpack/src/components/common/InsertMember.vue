<template>
    <div class="edit-member-root">
        <a-form-model ref="formEl" :model="editForm" :rules="rules" :label-col="{span: 6}" :wrapper-col="{span: 17}">
            <a-form-model-item label="姓名(必填)" prop="name">
                <a-input v-model="editForm.name"/>
            </a-form-model-item>
            <a-form-model-item label="性别" v-show="props.isFirstMember || !onlyRead">
                <a-radio-group v-model="editForm.gender" >
                    <a-radio :value="1">
                        男
                    </a-radio>
                    <a-radio :value="0">
                        女
                    </a-radio>
                </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="字辈">
                <a-input v-model="editForm.generation"/>
            </a-form-model-item>
            <a-form-model-item label="是否在世">
                <a-radio-group v-model="editForm.is_alive">
                    <a-radio :value="1">
                        在世
                    </a-radio>
                    <a-radio :value="0">
                        已逝
                    </a-radio>
                </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="出生日期">
                <CustomDatePicker v-model="editForm.birthdate" :dateType.sync="editForm.birthDateType"/>
            </a-form-model-item>
            <a-form-model-item label="出生地">
                <a-input v-model="editForm.birthplace"/>
            </a-form-model-item>

            <a-form-model-item label="现居住地" v-if="editForm.is_alive == ALIVE">
                <a-input v-model="editForm.residence"/>
            </a-form-model-item>
            <a-form-model-item label="手机号" prop="phoneNumber" v-if="editForm.is_alive == ALIVE">
                <a-input v-model="editForm.phoneNumber"/>
            </a-form-model-item>
            <a-form-model-item label="公开手机数字" v-if="editForm.is_alive == ALIVE">
                <a-select ref="select"
                          v-model:value="editForm.openPhone" >
                    <a-select-option  :value="0">全公开</a-select-option>
                    <a-select-option :value="1">公开后四位</a-select-option>
                    <a-select-option :value="2" selected>不公开</a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item label="身份证号" prop="identityId" v-if="editForm.is_alive == ALIVE">
                <a-input v-model="editForm.identityId"/>
            </a-form-model-item>
            <a-form-model-item label="公开身份证号"  v-if="editForm.is_alive == ALIVE">
                <a-select ref="select"
                        v-model:value="editForm.openIdentityId" >
                    <a-select-option  :value="0">全公开</a-select-option>
                    <a-select-option :value="1">公开后四位</a-select-option>
                    <a-select-option :value="2" selected>不公开</a-select-option>
                </a-select>
            </a-form-model-item>
            <template v-if="editForm.is_alive == 0">
                <a-form-model-item label="安息地">
                    <a-input v-model="editForm.restplace"/>
                </a-form-model-item>
                <a-form-model-item label="逝世日期">
                    <CustomDatePicker v-model="editForm.deathDate" :dateType.sync="editForm.deathDateType"/>
                </a-form-model-item>
            </template>
            <a-form-model-item label="介绍">
                <a-input v-model="editForm.description" type="textarea"/>
            </a-form-model-item>
<!--            <a-form-model-item label="影像资料">
                {{ selectedAlbum.name }}
                <a-button size="small" type="primary" @click="bindAlbum">绑定</a-button>
            </a-form-model-item>-->
            <a-form-model-item label="文档资料">
                <p v-for="(d,i) in editForm.documents" style="display: flex; align-items: center; ">
                    {{ d.name }}
                    <van-button round type="danger" style="width: 24px; margin-left: 6px"
                                @click="editForm.documents.splice(i, 1)" size="mini">
                        <van-icon slot="icon" class-prefix="iconfont" name="delete_big"
                                  color="#fff"
                                  size="10px"></van-icon>
                    </van-button>
                </p>
                <a-button @click="uploadFileEl.click()" size="small" type="primary" :loading="uploading">上传文档</a-button>
                <input type="file" accept=".doc,.docx,.ppt,.pptx,.pdf" ref="uploadFileEl"
                       @change="fileInputChange($event)" hidden></input>
            </a-form-model-item>
        </a-form-model>



        <mu-dialog :open="showSelectAlbum" :fullscreen="isCurrentClient('mobile')">
            <mu-load-more
                    :loading="loadingAlbum" @load="loadAlbum" :loaded-all="albumPage.$pageEnd">
                <mu-list>
                    <mu-list-item avatar button v-for="album in albumList" :key="album.id" @click="selectAlbum(album)">
                        <mu-list-item-action>
                            <img class="uploadable-album-thumbnail" :src="getViewThumbnailUrl(album.thumbnail)">
                            <!--<div class="uploadable-album-thumbnail" :style="{'background-image' : getViewThumbnailUrl(album.thumbnail)}"></div>-->
                        </mu-list-item-action>
                        <mu-list-item-content>
                            <mu-list-item-title>
                                <div class="uploadable-album-name">{{ album.name }}</div>
                            </mu-list-item-title>
                            <mu-list-item-sub-title>
                                <div class="uploadable-album-name">
                                    {{ album.photoCount ? album.photoCount + ' ' : '0 ' }}张
                                </div>
                            </mu-list-item-sub-title>
                        </mu-list-item-content>
                    </mu-list-item>
                </mu-list>
            </mu-load-more>
            <a-button slot="actions" @click="showSelectAlbum = false">关闭</a-button>
        </mu-dialog>

<!--        <div style="text-align: right;">
            <a-button @click="$emit('close')">关闭</a-button>
            <a-button type="primary" @click="editSubmit">确认</a-button>
        </div>-->

<!--        <van-popup v-model="showDatePicker" position="bottom">
            <van-datetime-picker
                    v-model="currentDate.value"
                    :min-date="minDate"
                    :max-date="maxDate" title="选择年月"
                    type="date"/>
        </van-popup>-->
    </div>
</template>
<script setup>
    import {isCurrentClient, toast} from "../../common/env";
    import {computed, ref, watchEffect} from "vue";
    import { rules } from '../../common/constants'
    import { lunars, convert } from '../../common/lunar'
    import {Dialog} from "vant";
    import {watch, onMounted} from "vue";
    import Utils from "../../common/utils";
    import {generateUUID} from "../../common/common";
    import MemberLocalStorage from '../../common/member_localstorage'
    import CustomDatePicker from './CustomDatePicker'
    import bftsUrls from "../../common/global_variable";
    import Service from "../../service/service";
    import ObjService from "../../service/obj_service";
    import ImageService from "../../service/image_service";


    const service = new Service()
    const objService = new ObjService()
    const imageService = new ImageService();

    const memberLocalStorage = new MemberLocalStorage()
    const emit = defineEmits(['success','error', 'close'])

    const ALIVE = 1;
    const DEATH = 0;

    const editForm = ref( {
        gender: 1,
        is_alive: ALIVE,
        birthDateType: 'DATE',
        deathDateType: 'DATE',
        birthdate: '',
        deathDate: '',
        openPhone: 2,
        openIdentityId: 2
    })

    //根据生日推算死亡日期
    watch(()=> editForm.value.birthdate, (value)=>{
        if (!!!value){
            return
        }

        let birthdateYear
        if(editForm.value.birthDateType === 'DATE'){
            const date = new Date(value.replace("-", "/"))
            birthdateYear = date.getFullYear()
        }else{
            const year = value.match(/\d+/g)[0]
            birthdateYear = ~~year
        }


        if (editForm.value.deathDate !== ''){
            let deathDateYear = editForm.value.deathDate.match(/\d+/g)[0]
            deathDateYear = ~~deathDateYear

            if (birthdateYear <= deathDateYear) return;
        }

        if (birthdateYear + 100 >= 1949) {
            editForm.value.deathDateType = 'DATE'
            editForm.value.deathDate = `${birthdateYear + 100}-1-1`
        }else{
            editForm.value.deathDateType = 'DYNASTY'
            editForm.value.deathDate = convert(birthdateYear + 100)
        }

    }, {deep: true})


    const props = defineProps(['id','type', 'treeId', 'memberId', 'gender', 'isFirstMember'])

    const formEl = ref(null)

    const onlyRead = ref(false)

    if (!props.isFirstMember)
    {
        watchEffect( (value)=>{
            if (props.type === 4){
                return
            }
            onlyRead.value = true;

            if (props.type === 1){
                editForm.value.gender = 1
            }else if (props.type === 2){
                editForm.value.gender = 0
            }else if (props.type === 3){
                editForm.value.gender = props.gender ^ 1
            }
        })
    }

    let submitting = false;
    function submit() {
        if (submitting) return;

        submitting = true
        formEl.value.validate(valid => {
            if (valid){
                editSubmit()
            }else{
                emit("error")
                submitting = false
                return false;
            }
        })
    }

    //修改信息提交
    function editSubmit() {
        if (!props.isFirstMember){
            editForm.value['type'] = props.type
            editForm.value['targetId'] = props.memberId
        }

        editForm.value['treeId'] = props.treeId

        let params = new FormData();

        for (let key in editForm.value){
            if (key == "documents") {
                params.append(key, JSON.stringify(editForm.value[key]))
            } else {
                params.append(key, editForm.value[key])
            }
        }

        $.ajax({
            type: 'POST',
            url: g_runToolUrl + "&action=" + requestAction.value,
            data: params,
            dataType: "json",
            processData: false,
            contentType: false,
            complete: function(){
                submitting = false
            },
            success: function (data) {
                if (data.flag) {
                    emit('success', data.member)
                    memberLocalStorage.removeEditingMember(editForm.value.id)
                } else {

                    emit("error")
                    Dialog.alert({
                        message: data.errorMessage
                    })
                }
            }
        });
    }

    const requestAction = computed(()=>{
        if (props.isFirstMember){
            return 'insertStartMember'
        }else{
            return 'insertMember'
        }
    })

    let modified = false;
    function cancel(){

        if (!modified){
            emit('close')
            return
        }

        Dialog.confirm({
            title: '提示',
            message: '内容未保存，取消编辑将丢失内容, 是否取消?',
            cancelButtonText:'退出',
            confirmButtonText: '继续编辑',
            confirmButtonColor: '#4f7b92',
            cancelButtonColor: '#ee0a24'
        }).then(() => {
        }).catch(() => {
            memberLocalStorage.removeEditingMember(editForm.value.id)
            emit('close')
        })
    }


    /**
     * 上传资料
     */

    const uploading = ref(false)
    const uploadFileEl = ref(null)

    async function fileInputChange(e) {
        const file = e.target.files[0];
        uploadFileEl.value.value = "";

        let result;
        uploading.value = true
        try {
            result = await service.uploadFile(file);
            result = result.data
        } catch (e) {
            console.error(e)
        }

        if (!result || !result.success) {
            toast("上传失败");
            uploading.value = false;
            return;
        }
        const documentID = result.documentID;
        try {
            result = await objService.getDocumentInfo(documentID);
            result = result.data
        } catch (e) {
            console.error(e)
        }

        if (result.rows) {
            if (!editForm.value.hasOwnProperty('documents')){
                editForm.value['documents'] = []
            }
            editForm.value.documents.push({
                id: result.rows[0].documentID,
                name: file.name,
                memberId: editForm.id
            })
            toast("上传成功");
        } else {
            toast("上传失败");
        }

        uploading.value = false;
    }

    function getViewThumbnailUrl(id) {
        return bftsUrls.viewThumbnailURL + id
    }

    /**
     * 影像相关
     */
    function bindAlbum() {
        showSelectAlbum.value = true;
        loadAlbum()
    }

    let albumPage = ref({ //可上传相册分页参数
        page: 0,
        size: 20,
        total: 0, $pageEnd: false
    })
    const showSelectAlbum = ref(false)
    const albumList = ref([])
    const loadingAlbum = ref(false);
    const selectedAlbum = ref({})

    async function loadAlbum() {
        loadingAlbum.value = true;

        let result = await imageService.getPageAlbumsByPermission({
            page: albumPage.value.page + 1,
            size: albumPage.value.size
        })

        albumPage.value.page++
        loadingAlbum.value = false;

        albumList.value = result.rows
    }

    function selectAlbum(album) {
        selectedAlbum.value = album
        editForm.value.albumId = album.id
        showSelectAlbum.value = false
    }


    onMounted(()=>{
        if (!props.id){
            editForm.value['id'] = generateUUID()
            return
        }

        editForm.value = memberLocalStorage.getEditingMember(props.id)

    })

    watch(editForm, (value, oldValue) => {
        if (!value.name) return;

        value['treeId'] = props.treeId
        value['isFirstMember'] = props.isFirstMember

        if (!props.isFirstMember){
            value['type'] = props.type
            value['targetId'] = props.memberId
        }

        memberLocalStorage.saveEditingMember(value.id, value)
        modified = true
    }, {deep: true})

    defineExpose({submit, cancel})
</script>
<style lang="less">
  .ant-calendar-picker-container {
    z-index: 1000000000;
  }

  .mu-dialog .edit-member-root{
    padding: 1rem;
  }

  .ant-cascader-menu-item-expand-icon,.ant-select-arrow-icon{
    font-size: 13px !important;

   svg{
     width: 12px;
     height: 12px;
   }
  }

  .ant-cascader-menus{
    z-index: 10000000000000000000000 !important;
  }
</style>
