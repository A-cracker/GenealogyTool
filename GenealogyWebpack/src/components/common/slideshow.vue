<template>
  <div class="showImg"><!--轮播图-->
    <img  @mouseover="changeInterval(true)"
          @mouseleave="changeInterval(false)"
          v-for="(item) in imgArr"
          :key="item.id"
          :src="item.url"
          alt="暂无图片"
          v-show="item.id===currentIndex"
    >

    <div class="banner-circle">
      <ul>
        <li @click="changeImg(item.id)"
            v-for="(item) in imgArr"
            :key="item.id"
            :class="item.id===currentIndex? 'active': '' "
        ></li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: "slideshow",
  data(){
    return {
      currentIndex :0,//当前所在图片下标
      timer:null,//定时轮询
      imgArr:[

      ]
    }
  },
  methods:{
    //开启定时器
    startInterval(){
      // 事件里定时器应该先清除在设置，防止多次点击直接生成多个定时器
      clearInterval(this.timer);
      this.timer = setInterval(()=>{
        this.currentIndex++;
        if(this.currentIndex > this.imgArr.length-1){
          this.currentIndex = 0
        }
      },3000)
    },

    // 点击控制圆点
    changeImg(index){
      this.currentIndex = index
    },
    //鼠标移入移出控制
    changeInterval(val){
      if(val){
        clearInterval(this.timer)
      }else{
        this.startInterval()
      }
    }
  },
//进入页面后启动定时轮询
  mounted(){
    this.startInterval()
  }
}
</script>

<style scoped>
/* 清除li前面的圆点 */
li {
  list-style-type: none;
}
.showImg{
  position: relative;
  width:100%;
  height: 250px;
  margin: 0px auto;
  overflow: hidden;
}
/* 轮播图片 */
.showImg img{
  width: 100%;
  height: 100%;
}

/* 控制圆点 */
.banner-circle{
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 20px;
}
.banner-circle ul{
  margin: 0 50px;
  height: 100%;
  text-align: right;
}
.banner-circle ul li{
  display: inline-block;
  width: 14px;
  height: 14px;
  margin: 0 5px;
  border-radius: 7px;
  background-color: rgba(125,125,125,.8);
  cursor: pointer;
}
.active{
  background-color: black !important;
}
</style>