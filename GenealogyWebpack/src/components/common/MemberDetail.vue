<template>
  <div class="member-root"  :class="{'mobile': isCurrentClient('mobile') }">
      <van-icon v-if="isCurrentClient('pc')" @click="$emit('close')" class="close-member-btn" class-prefix="iconfont" name="close" color="#4f7b92"
                size="2rem"></van-icon>
      <div id="member-header">
<!--          <van-image width="100" height="100" :src="memberHeaderUrlComp"></van-image>-->
          <span id="member-name">{{ memberDetail.name }}</span>
      </div>
      <p class="member-info-item multiple-item">
          <span>
              <span class="member-info-label">性别:</span>
              <span class="member-info-content">{{ memberDetail.gender ? '男':'女' }}</span>
          </span>
          <span>
              <span class="member-info-label">字辈:</span>
              <span class="member-info-content" :class="{ 'not-content': !memberDetail.generation }">{{ formatInfo(memberDetail.generation) }}</span>
          </span>

          <span>
              <span class="member-info-label">代数:</span>
              <span class="member-info-content" >第{{ toChinesNum(depth) }}代</span>
          </span>
      </p>
<!--      <p class="member-info-item">
      </p>-->
      <p class="member-info-item multiple-item">
           <span >
              <span class="member-info-label">是否在世:</span>
              <span class="member-info-content" >{{ memberDetail.is_alive ? '是':'否' }}</span>
          </span>

          <span>
              <span class="member-info-label">出生日期:</span>
              <span class="member-info-content" :class="{ 'not-content': !memberDetail.birthdate }" v-html="dateFormat(memberDetail.birthdateType ,memberDetail.birthdate)"></span>
          </span>
      </p>
      <p class="member-info-item">
          <span class="member-info-label">出生地:</span>
          <span class="member-info-content" :class="{ 'not-content': !memberDetail.birthplace }">{{ formatInfo(memberDetail.birthplace) }}</span>
      </p>
      <p class="member-info-item" v-if="memberDetail.is_alive">
          <span class="member-info-label">现居住地:</span>
          <span class="member-info-content" :class="{ 'not-content': !memberDetail.residence }">{{ formatInfo(memberDetail.residence) }}</span>
      </p>
      <p class="member-info-item" v-if="memberDetail.is_alive && memberDetail.openPhone != 2">
          <span class="member-info-label">手机号:</span>
          <span class="member-info-content" :class="{ 'not-content': !memberDetail.residence }">{{ formatInfo(memberDetail.phoneNumber) }}</span>
      </p>
      <p class="member-info-item" v-if="memberDetail.is_alive && memberDetail.openIdentity != 2">
          <span class="member-info-label">身份证号:</span>
          <span class="member-info-content" :class="{ 'not-content': !memberDetail.identityId }">{{ formatInfo(memberDetail.identityId) }}</span>
      </p>
      <p class="member-info-item" v-show="!memberDetail.is_alive">
          <span class="member-info-label">逝世日期:</span>
          <span class="member-info-content" :class="{ 'not-content': !memberDetail.deathDate }" v-html="dateFormat(memberDetail.deathDateType ,memberDetail.deathDate) "></span>
      </p>
      <p class="member-info-item" v-show="!memberDetail.is_alive">
          <span class="member-info-label">安息地:</span>
          <span class="member-info-content" :class="{ 'not-content': !memberDetail.restplace }">{{ formatInfo(memberDetail.restplace) }}</span>
      </p>
      <p class="member-info-item">
          <span class="member-info-label">介绍:</span>
          <span class="member-info-content" :class="{ 'not-content': !memberDetail.description }">{{ formatInfo(memberDetail.description) }}</span>
      </p>
<!--      <p class="member-info-item" style="display: flex; flex-wrap: wrap">
          <span class="member-info-label">影像资料:</span>&nbsp;
          <span class="member-info-content not-content" v-if="!memberDetail.albumId ">无</span>
          <van-image v-for="(p, i) in photos.slice(0,showCount)" width="70" height="70" @click="preview(i)"
                     style="margin-right: 4px" :src="getPhotoSrc(p)"></van-image>
          <span id="open-album" v-show="memberDetail.albumId" @click="openAlbumTool">
              <span>打开相册</span>
          </span>
      </p>-->
      <p class="member-info-item" style="display: flex; flex-wrap: wrap">
          <span class="member-info-label">文档资料:</span>&nbsp;
          <span class="member-info-content not-content" v-if="!memberDetail.documents || !memberDetail.documents.length">无</span>
          <a-button  style="margin-right: 4px" class="member-info-content" v-for="d in memberDetail.documents" @click="previewDoc(d)">{{d.name}}</a-button>
      </p>
      <div id="member-info-btns">
          <div style="text-align: right;">
<!--              <a-button v-show="isCurrentClient('mobile')" @click="$emit('close')">关闭</a-button>-->
              <a-button v-show="showEditBtn" type="primary" @click="$emit('edit', memberDetail.id)">编辑</a-button>
              <a-button type="primary" @click="share" :loading="sharing">分享到微信</a-button>
              <a-button v-if="memberDetail.is_alive === 0" type="primary" @click="$emit('pray')">祭祀</a-button>
          </div>
      </div>

  </div>
</template>
<script setup>
    import {onMounted, defineProps, computed, ref, watch,watchEffect} from "vue"
    import {getBandId, getBaseUrl, getUserId, isCurrentClient, toast} from "../../common/env";
import {useShareToWechat} from "./Share";
import ImageService from "../../service/image_service"
import ObjService from "../../service/obj_service"
import bftsUrls from "../../common/global_variable"
    import {ImagePreview} from "vant";
    import {getPhotoStorageAndSharingUrl, getRunSystemToolUrl} from "../../common/constants";

    import * as Constants from "../../common/constants";
    import Utils from "../../common/utils";

    const imageService = new ImageService();
    const objService = new ObjService();

    const props = defineProps({
        memberDetail:Object,
        treeId:Number,
        depth: {
            type: Number,
            default: 1
        }
    })

    const emit = defineEmits(['addNode' , 'close', 'pray'])

    function formatInfo(value){
        return value ? value : '未填';
    }

    function dateFormat(type, value){
        if (!value){
            return formatInfo(value)
        }

        if (type === 'DATE'){
            return value;
        }

        const leftBracketsIndex = value.indexOf('(');

        if (leftBracketsIndex < 0){
            return value
        }

        const dynasty = value.substring(0, leftBracketsIndex)
        const year = value.substring(leftBracketsIndex)

        return `${dynasty}<span class="detail-year">${year}</span>`
    }

    const showEditBtn = computed(()=>{
        return getUserId() != 28;
    })

    const photos = ref([]);

    const showCount = computed(()=>{
        return isCurrentClient('pc') ? 4 : 3;
    });

    function openAlbumTool(){
        const param = { albumId: props.memberDetail.albumId};

        const url = getPhotoStorageAndSharingUrl(getBandId()) + "&param=" + encodeURI(JSON.stringify(param));

       Utils.openUrl(url)
    }

        watchEffect(async ()=>{
        if (props.memberDetail.albumId){
            const rs = await imageService.getAllPhotoByAlbumId(props.memberDetail.albumId)
            photos.value = rs.rows
        }else{
            photos.value = []
        }
    });

    function preview(index){
        ImagePreview( {
            images: photos.value.flatMap((p)=> bftsUrls.viewHDThumbnailURL + p.mongoObjId),
            startPosition: index,
            closeable: true,
        })
    }

    async function previewDoc(d){
        const doc = await objService.getViewObj(d.id);

        objService.getDocumentVersionInfo(doc.objID, (data) => {
            const paramString = encodeURI(encodeURI('{"storageID":"' + data.storageID + '","documentID":"' + data.documentID + '","extension":"' + data.extension + '"}'));
            let url = getRunSystemToolUrl(Constants.docReviewToolID) + "&param=" + paramString + "&gid=" + data.documentID;
            Utils.openUrl(url)
        }, () => {
            toast('打开失败');
        })
    }

    function getPhotoSrc(photo){
        return bftsUrls.viewThumbnailURL + photo.mongoObjId
    }

    let sharing = ref(false)
    function share(){
        useShareToWechat(props.memberDetail.name,"我分享了家谱给你,快来看看", {memberId: props.memberDetail.id, treeId: props.treeId}, sharing,window.share)
    }

    /**
     * 数字转成汉字
     * @params num === 要转换的数字
     * @return 汉字
     * */
    function toChinesNum(num) {
        let changeNum = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九']
        let unit = ['', '十', '百', '千', '万']
        num = parseInt(num)
        let getWan = (temp) => {
            let strArr = temp.toString().split('').reverse()
            let newNum = ''
            let newArr = []
            strArr.forEach((item, index) => {
                newArr.unshift(item === '0' ? changeNum[item] : changeNum[item] + unit[index])
            })
            let numArr = []
            newArr.forEach((m, n) => {
                if (m !== '零') numArr.push(n)
            })
            if (newArr.length > 1) {
                newArr.forEach((m, n) => {
                    if (newArr[newArr.length - 1] === '零') {
                        if (n <= numArr[numArr.length - 1]) {
                            newNum += m
                        }
                    } else {
                        newNum += m
                    }
                })
            } else {
                newNum = newArr[0]
            }

            return newNum
        }
        let overWan = Math.floor(num / 10000)
        let noWan = num % 10000
        if (noWan.toString().length < 4) {
            noWan = '0' + noWan
        }
        return overWan ? getWan(overWan) + '万' + getWan(noWan) : getWan(num)
    }

</script>
<style lang="less">
  .mu-dialog .member-root{
    padding: 1rem;
  }

  #member-header{
    display: flex;
    margin-bottom: 1rem;

    #member-name{
      align-self: flex-end;
      font-size: 2rem;
      font-weight: bold;
      color: hsla(201, 30%, 27%, 1);
      font-family: cursive;
    }
  }

  .member-info-item{
    padding: 8px 0;
    border-bottom: 1px dashed #cdcdcd;
    margin-bottom: 4px;
    font-weight: bold;
    color: hsla(201, 30%, 27%, 1);

    .member-info-label,.not-content{
      font-size: 0.85rem;
      color: grey;
    }

    .member-info-content{
      font-family: cursive;
      font-size: 1.2rem;
    }

    &.multiple-item{
      display: flex;

      >span{
        flex:auto;
      }
    }
  }

  #member-info-btns{
    >span{
      font-size: 1rem;
      font-weight: bold;
      color: hsla(201, 30%, 27%, 1);
    }

    margin-top: 1rem;
  }
  .close-member-btn {
    position: absolute;
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

  .van-image-preview {
    z-index: 200141250 !important;
  }
  .van-image-preview__overlay{

    z-index: 200141249 !important;
  }

  #open-album{
    width: 70px;
    height: 70px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    background: #4f7b92;
    color: white;
    border-radius: 6px;
  }

  .mobile{
     padding-bottom: 3rem;

    #member-info-btns{
      position: fixed;
      bottom: 0;
      width: 100vw;
      padding: 6px;
      background: white;
      left: 0;
      margin: 0;
      height: 62px;
    }

    .member-info-item:last-child{
         margin-bottom: 3rem;
       }
  }

  .detail-year{
    color: #e12e2e;
  }
</style>
