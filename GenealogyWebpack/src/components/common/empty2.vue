<template>
    <div id="emptyInfo" :style="{'padding-top': top, 'background': bg}">
       <img :style="{'width': iconSize }" :src="require('../../images/empty2.svg')"/>
        <p>{{ message }}</p>
    </div>
</template>
<script>
    export default {
        name: 'empty2',
        props: {
            msg: String,
            iconSize: {
                type: String,
                default: '90px'
            },
            top: {
                type: String,
                default: '10%'
            },
            bg: {
                type: String,
                default: 'transparent'
            }
        },
        data() {
            return {
                message: this.msg || '无数据',
            };
        },
    }
</script>
<style lang="less">

    #emptyInfo {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        pointer-events: none;

        p{
            color: #707070;
            text-align: center;
            font-size: 0.9rem;
        }
    }
</style>
