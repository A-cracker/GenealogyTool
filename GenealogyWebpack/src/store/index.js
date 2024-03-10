import Vue from 'vue'
import Vuex from 'vuex'
import mutations from './mutations'

Vue.use(Vuex)

const state = {
    CURRENT: {
        organizationId: '',
        userId: '',
        userName: '',
    },
    WINDOW:{
        width:0,
        height:0,
    },
    cacheBase64:[],
    clientType:'PC',
    refreshAlbum:false,
    UploadingTaskStatus:false,
    uploadFiles:[],
    uploadShowList:null,
    DownloadingTaskStatus:false,
    isAdmin: true,
}

export default new Vuex.Store({
	state,
	mutations,
})