
export const  RouterHelper = {
    toInfo(vueIns){
        this.to(vueIns, {path: "info"})
    },

    toFind(vueIns){
        this.to(vueIns, {path: "find"})
    },

     toIndex(vueIns){
         // this.to(vueIns, {path: "index"})
         vueIns.$router.replace({path: "index"})
     },

    toHome(vueIns, treeId, memberId){
        this.to(vueIns, {path: "home2", query: {treeId: treeId, memberId:memberId}})
    },

    to(vueIns, params){
        vueIns.$router.push(params)
    }
}
