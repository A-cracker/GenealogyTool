//判断是否能连接到服务器
import $ from 'jquery'
export default function isNetworkOk() {

    //1.判断是否连接局域网或者wifi
    if(navigator.onLine){
        console.log('网络正常！');
    }else{
        console.log('网络中断！');
        console.log('十秒后重新请求传输文件块');
        return false;
    }

    //2.判断服务端能否访问，
    $.ajax({
        url:"/isNetworkOk",//需要测试的后台网址
        type: 'POST',
        complete: function(response) {
            if(response.status != 200) {
               return false;
            }
        }
    });
    return true;
}
