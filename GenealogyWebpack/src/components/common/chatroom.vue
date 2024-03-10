<template>
    <div>
        <van-datetime-picker
                v-model="currentDate"
                type="date"
                title="选择年月日"
                :min-date="minDate"
                :max-date="maxDate"
                :formatter="formatter"
        />
    </div>
</template>
<script setup>
    import {ref} from 'vue'
    const minDate=  ref(new Date(1800, 0, 1))
        const maxDate =  ref(new Date(2100, 10, 1))
       const currentDate =  ref(new Date("2022-01-12".replace("-","/")))
    function formatter(type, val) {
        if (type === 'year') {
            return `${val}年`;
        } else if (type === 'month') {
            return `${val}月`;
        }
        return `${val}日`;
    }

</script>
<style lang="scss">
   #chatroom{
       background: white;
       display: flex;
       flex-direction: column;
       z-index: 100;

       #title{
           background: #fafafa;
       }

       iframe{
           flex: 1;
       }
   }

</style>
