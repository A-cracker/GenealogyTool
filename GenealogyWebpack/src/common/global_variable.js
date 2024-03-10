import {getBandId,getAccessToken} from './env'
import {getWtbUrl} from "./constants";
const context = require('../../build/context');

let BFTSURL;

// if(context.environment() === 'production'){
    BFTSURL ="https://www.wetoband.com/bigfile"
// }else{
//     BFTSURL = "http://127.0.0.1:9090"
// }

const viewMongoFileURL=BFTSURL+"/viewMongoFile?gid="+getBandId()+"&contentType=video/mp4&accessToken="+getAccessToken()+"&fileObjectId="
const viewThumbnailURL=BFTSURL+"/viewThumbnail?gid="+getBandId()+"&accessToken="+getAccessToken()+"&fileObjectId="
const viewHDThumbnailURL=BFTSURL+"/viewHDThumbnail?gid="+getBandId()+"&accessToken="+getAccessToken()+"&fileObjectId="
const viewCompressMongoFileURL=BFTSURL+"/viewCompressMongoFile?gid="+getBandId()+"&contentType=video/mp4&accessToken="+getAccessToken()+"&fileObjectId="
const gid="1500000001"

export default{
    BFTSURL,
    viewMongoFileURL,
    viewThumbnailURL,
    viewHDThumbnailURL,
    viewCompressMongoFileURL,
    gid
}
