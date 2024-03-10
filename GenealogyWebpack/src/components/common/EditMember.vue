<template>
    <div class="edit-member-root" >
        <a-form-model ref="formEl" :rules="rules" :model="editForm" :label-col="{span: 6}" :wrapper-col="{span: 17}">
            <a-form-model-item label="姓名(必填)" prop="name">
                <a-input v-model.trim="editForm.name"/>
            </a-form-model-item>
            <a-form-model-item label="性别">
                <a-radio-group v-model="editForm.gender" >
                    <a-radio :value="1">
                        男
                    </a-radio>
                    <a-radio :value="0">
                        女
                    </a-radio>
                </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="字辈" prop="generation">
                <a-input v-model="editForm.generation"/>
            </a-form-model-item>
            <a-form-model-item label="是否在世">
                <a-radio-group v-model="editForm.is_alive">
                    <a-radio :value="ALIVE">
                        在世
                    </a-radio>
                    <a-radio :value="DEATH">
                        已逝
                    </a-radio>
                </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="出生日期">
                <CustomDatePicker key="birthdate" v-model="editForm.birthdate" :dateType.sync="editForm.birthDateType"></CustomDatePicker>
            </a-form-model-item>
            <a-form-model-item label="出生地" prop="birthplace">
                <a-input v-model="editForm.birthplace"/>
            </a-form-model-item>

            <a-form-model-item label="现居住地" prop="residence" v-if="editForm.is_alive == ALIVE">
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
            <a-form-model-item label="公开身份证号" v-if="editForm.is_alive == ALIVE">
                <a-select ref="select"
                        v-model:value="editForm.openIdentity" >
                    <a-select-option  :value="0">全公开</a-select-option>
                    <a-select-option :value="1">公开后四位</a-select-option>
                    <a-select-option :value="2" selected>不公开</a-select-option>
                </a-select>
            </a-form-model-item>
            <template v-if="editForm.is_alive == DEATH">
                <a-form-model-item label="安息地" prop="restplace">
                    <a-input v-model="editForm.restplace"/>
                </a-form-model-item>
                <a-form-model-item label="逝世日期">
                    <CustomDatePicker key="deathdate" v-model="editForm.deathDate" :dateType.sync="editForm.deathDateType"></CustomDatePicker>
                </a-form-model-item>
            </template>
            <a-form-model-item label="介绍" prop="description">
                <a-input v-model="editForm.description" type="textarea" rows="5"/>
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

        <mu-dialog title="选择相册" dialog-class="my-dialog select-album" :open="showSelectAlbum" :fullscreen="isCurrentClient('mobile')" scrollable>
            <mu-load-more
                    :loading="loadingAlbum" @load="loadAlbum" :loaded-all="albumPage.$pageEnd">
                <mu-list>
                    <mu-list-item avatar button v-for="album in albumList" :key="album.id" @click="selectAlbum(album)">
                        <mu-list-item-action>
                            <van-image  width="56" height="56" :src="getViewThumbnailUrl(album.thumbnail)"></van-image>
<!--                            <img class="uploadable-album-thumbnail" :src="getViewThumbnailUrl(album.thumbnail)">-->
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
            <a-button slot="actions" @click="refreshAlbum">刷新</a-button>
            <a-button slot="actions" @click="showSelectAlbum = false">关闭</a-button>
        </mu-dialog>


    </div>
</template>
<script setup>
    import { minDate, maxDate, vanDateFormatter } from '../../common/constants'
    import {ref, watch} from "vue";
    import {isCurrentClient, toast} from "../../common/env";

    import {rules} from '../../common/constants'
    import Service from '../../service/service'
    import ObjService from '../../service/obj_service'
    import ImageService from '../../service/image_service'
    import Utils from "../../common/utils";
    import bftsUrls from "../../common/global_variable"
    import MemberLocalStorage from '../../common/member_localstorage'
    import {Dialog} from "vant";
    import CustomDatePicker from './CustomDatePicker'
    import {convert} from "../../common/lunar";

    const memberLocalStorage = new MemberLocalStorage()
    const service = new Service()
    const objService = new ObjService()
    const imageService = new ImageService();

    const emit = defineEmits(['success', 'close'])
    const props = defineProps(['treeId'])

    const ALIVE = 1;
    const DEATH = 0;

    const editForm = ref({
        gender: 1,
        is_alive: ALIVE,
        birthdate: '',
        birthDateType: '',
        deathDate: '',
        deathDateType: ''
    })

    //根据生日推算死亡日期
    watch(()=> editForm.value.birthdate, (value)=>{
        if (!value) return;

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


    function init(id, loading, loadFromStorage = false) {
        // editForm.value = {}
        modified = false;

        if (loadFromStorage){
            loading && (loading.value = false)
            editForm.value = memberLocalStorage.getEditingMember(id)

            return
        }

        $.ajax({
            type: "GET",
            url: g_runToolUrl,
            data: "action=getMemberInfo&id=" + id,
            dataType: "json",
            success: async function (data) {
                editForm.value = data
                loading.value = false

                editForm.value.gender = data.gender == '女' ? 0 : 1;

                if (editForm.value.albumId) {
                    const rs = await imageService.getAlbumById(editForm.value.albumId)
                    selectAlbum(rs)
                }
            }
        });
    }

    let modified = false;
    function cancel(){
        return new Promise( resolve=> {
            if (!modified){
                emit('close')
                resolve(true)
                return
            }



            Dialog.confirm({
                title: '提示',
                message: '内容未保存，取消编辑将丢失内容, 是否取消?',
                cancelButtonText:'取消编辑',
                confirmButtonText: '保存并退出',
                confirmButtonColor: '#4f7b92',
                cancelButtonColor: '#ee0a24'
            }).then(() => {
                resolve(false)
                submit()
            }).catch(() => {
                resolve(true)
                memberLocalStorage.removeEditingMember(editForm.value.id)
                emit('close')
            })
        })
    }

    const formEl = ref(null)
    let submitting = false;
    function submit() {
        if (submitting) return;

        submitting = true
        formEl.value.validate(valid => {
            if (valid) {
                editSubmit()
            } else {
                submitting = false
                return false;
            }
        })
    }

    //修改信息提交
    function editSubmit() {
        let params = new FormData();

        for (let key in editForm.value) {
            if (key == "documents") {
                params.append(key, JSON.stringify(editForm.value[key]))
            } else {
                params.append(key, editForm.value[key])
            }
        }

        $.ajax({
            type: 'POST',
            url: g_runToolUrl + "&action=editMember",
            data: params,
            dataType: "json",
            processData: false,
            contentType: false,
            complete: function(){
                submitting = false
            },
            success: function (data) {
                if (data) {
                    console.log(data)
                    emit('success')
                    selectedAlbum.value = {}
                    memberLocalStorage.removeEditingMember( editForm.value.id)
                }
            }
        });
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
        // albumPage.value =

        if (albumList.value.length == 0){
            loadAlbum()
        }
    }

    let albumPage = ref({ //可上传相册分页参数
        page: 0,
        size: 13,
        total: 0,
        $pageEnd: false
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

        albumPage.value.total = result.total
        albumPage.value.page++
        loadingAlbum.value = false;


        albumList.value = albumList.value.concat(result.rows)

        albumPage.value.$pageEnd = (albumList.value.length >= result.total)
    }

    function refreshAlbum(){
        albumPage.value = { //可上传相册分页参数
            page: 0,
            size: 13,
            total: 0,
            $pageEnd: false
        }
        albumList.value = []

        loadAlbum()
    }

    function selectAlbum(album) {
        selectedAlbum.value = album
        editForm.value.albumId = album.id
        showSelectAlbum.value = false
    }

    watch(editForm, (value, oldValue) => {
        if (!value.id || oldValue.id != value.id) return;

        value['treeId'] = props.treeId
        memberLocalStorage.saveEditingMember(value.id, value)
        modified = true
    }, {deep: true})

    defineExpose({init, submit, cancel})
</script>
<style lang="less">
  .edit-member-root{
      .ant-form-item{
        margin-bottom: 4px;
      }
  }
  .ant-calendar-picker-container {
    z-index: 1000000000;
  }

  .mu-dialog .edit-member-root {
    padding: 1rem;
  }

  .uploadable-album-content {
    flex: 1;
    padding-bottom: 8px;
    overflow: auto;
  }

  .uploadable-album-thumbnail {
    width: 56px;
    height: 56px;
    border-radius: 4px;
  }

  .uploadable-album-name {
    padding-left: 12px;
  }

  .mu-list > li {
    margin-bottom: 4px;
  }

  .van-overlay{
    z-index: 1000000000 !important;
  }

  .van-dialog{
    z-index: 10000000001 !important;
  }


  .select-album{
    .mu-dialog-body{
      max-height: 600px !important;
    }
  }
</style>
