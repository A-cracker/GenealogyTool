import https from './https';
//跳转到锚点
function goAnchor(selector) {
    
    // var anchor = this.$el.querySelector(selector);//获取元素
    // console.info("anchor",anchor)
    // if(anchor) {
    //     //前面已经设置延迟获取了
    //     setTimeout(()=>{//页面没有渲染完成时是无法滚动的，因此设置延迟
    //         //anchor.scrollIntoView(); //js的内置方法，可滚动视图位置至元素位置            
    //         var targetOffset=$(selector).offset().top; 
    //         console.info("targetOffset",anchor,targetOffset)
    //         $('html,body').animate({
    //             scrollTop: targetOffset
    //         },
    //         1000);
    //     },500);
    // }

    // console.info("selector",selector)
    let anchor=this.GetQueryString(selector);
    // console.info("anchor",anchor)

    if(anchor){
        setTimeout(() => {//延迟获取，页面没有渲染完成时id不知道，id是由后台数据拼接的
          let anchor_dom = this.$el.querySelector("#"+anchor);//获取元素
          // let anchor = this.$refs.anchor;//获取元素
        //   console.info("anchor_dom",anchor_dom)
          if(anchor_dom){
            anchor_dom.scrollIntoView(); //js的内置方法，可滚动视图位置至元素位置
          }
        }, 500);  
    }
};

//获取参数
function GetQueryString(name){
    //var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var href = window.location.href;
    console.log()
    var r = getSearchString(name,href);
    if (r != null) return decodeURI(r);
    return null;
}

//获取锚点(通过params传参数的方式，锚点为最后一个参数)
function getAnchorFromParams(){
    let href=window.location.href;
    //通过/来获取锚点在href中的index
    let index=href.lastIndexOf("/");
    let anchor=href.substring(index+1,href.length);
    return anchor;
}

function gotoAnchor(){
    let anchor=this.getAnchorFromParams();
    if(anchor.substring(0,3)!="%23"){
        setTimeout(() => {//延迟获取，页面没有渲染完成时id不知道，id是由后台数据拼接的
          let anchor_dom = this.$el.querySelector("#"+anchor);//获取元素
          // let anchor = this.$refs.anchor;//获取元素
        //   console.info("anchor_dom",anchor_dom)
          if(anchor_dom){
            anchor_dom.scrollIntoView(); //js的内置方法，可滚动视图位置至元素位置
          }
        }, 500);  
    }
}

function getSearchString(key, Url) {
    var str = Url;
    str = str.substring(1, str.length);    
    if(str.indexOf("?")!=-1){
        var arr = str.split("?")[1].split("&");// 获取？后的参数内容，并以&分隔字符串，获得类似name=xiaoli这样的元素数组
        var obj = new Object();
        // 将每一个数组元素以=分隔并赋给obj对象
        for (var i = 0; i < arr.length; i++) {
            var tmp_arr = arr[i].split("=");
            obj[decodeURIComponent(tmp_arr[0])] = decodeURIComponent(tmp_arr[1]);
        }
        return obj[key];
    }
}

//格式化时间，用法{{formatDate(item.commentTime,"yyyy-MM-dd hh:mm:ss")}}
function formatDate(time,fmt) {  
    var date=new Date(time.replace(/-/g, '/'));
    // console.log(date.getMonth()+1);
    var o = { 
    "M+" : date.getMonth()+1+"月",                 //月份
    "d+" : date.getDate()+"日",                    //日 
    "h+" : date.getHours(),                   //小时 
    "m+" : date.getMinutes(),                 //分 
    "s+" : date.getSeconds(),                 //秒 
    "q+" : Math.floor((date.getMonth()+3)/3), //季度 
    "S"  : date.getMilliseconds()             //毫秒 
    }; 
    // console.log(o);
    if(/(y+)/.test(fmt)) {
            fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"年").substr(4 - RegExp.$1.length)); 
    }
    // console.log(RegExp.$1)
    // console.log(RegExp.$1.length)
    // console.log(fmt)
    for(var k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
            let temp = "00"+ o[k];
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (temp.substr(temp.length-(""+ o[k]).length)));
            // console.log(RegExp.$1)
            // console.log(RegExp.$1.length)
            // console.log(fmt)
        }
    }
    fmt = fmt.replace(/-/g, '')
    return fmt;
}

//如果字符串长度过长，则截取字符串前len个字符
function textCut(str,len){
    if(str.length > len){
      return str.slice(0,len)+"...";
    }else{
      return str;
    }
}

// 跳到锚点对应的路由处
function jump2link(url){
    // console.log(url)
    this.$router.push({path:url})
}

//获取外网ip的方法
function getIp(url,callback){
  var httpRequest = new XMLHttpRequest();
  httpRequest.open('GET', url, true);
  httpRequest.send();
  httpRequest.onreadystatechange = function () {
      if (httpRequest.readyState == 4 && httpRequest.status == 200) {
          var json = httpRequest.responseText;
          callback(json);
      }
  };
}

//获取外网ip
function getNetworkIp(){
  var netWorkip = null;
  https.doGet("https://api.ipify.org/?format=json",{}).then((res) => {
    console.log(res.data)
    console.log(res.data.ip)
    // netWorkip = res.data.ip;
  })
  
  // setTimeout(() => {//延迟获取
  //   getIp("https://api.ipify.org/?format=json",function(data){
      
  //   });
  
  // },500) 
  return netWorkip;
}

function shiftSeconds2Hours(seconds){
  let hour=parseInt(seconds/3600);
  let minute=parseInt((seconds%3600)/60);
  let second=Math.floor(seconds-3600*hour-60*minute);
  
  let result=hour+":"+minute+":"+(second<10?'0':'')+second;
  return result;
}

function shiftSeconds2Minutes(seconds){
  let minute=parseInt(seconds/60);
  let second=Math.floor(seconds-60*minute);
  let result=minute+":"+(second<10?'0':'')+second;
  return result;
}

function shiftHours2Seconds(hourString){
  let elements=hourString.split(':');
  let result=parseInt(elements[0])*3600+parseInt(elements[1])*60+parseInt(elements[2]);
  return result;
}
/**
 * 生成唯一标识符UUID
 */
function generateUUID() {
  var d = new Date().getTime();
  if (window.performance && typeof window.performance.now === "function") {
      d += performance.now(); //use high-precision timer if available
  }
  var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      var r = (d + Math.random() * 16) % 16 | 0;
      d = Math.floor(d / 16);
      return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
  });
  return uuid;
}


export {
    goAnchor,
    GetQueryString,
    formatDate,
    textCut,
    jump2link,
    gotoAnchor,
    getAnchorFromParams,
    getNetworkIp,
    shiftSeconds2Hours,
    shiftSeconds2Minutes,
    shiftHours2Seconds,
    generateUUID
}

