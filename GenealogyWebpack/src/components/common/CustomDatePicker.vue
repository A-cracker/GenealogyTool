<template>
    <div id="custom-picker-root">
<!--
        <mu-date-input v-if="isCurrentClient('pc')" v-model="currentDate" format="YYYY-MM-DD"
                       value-format="YYYY-MM-DD" class="date-custom-class" solo
                       container="dialog" landscape></mu-date-input>
-->

        <span @click="selectDate()" class="ant-input">{{ currentDate }}</span>
        <van-popup v-model="showDatePicker" position="bottom">
           <div class="date-type">
               <span>日期类型:</span>
               <a-radio-group :value="type"  button-style="solid" @change="typeChange">
                   <a-radio-button value="DATE">
                       年月日
                   </a-radio-button>
                   <a-radio-button value="DYNASTY">
                       朝代
                   </a-radio-button>
               </a-radio-group>
               <span></span>
           </div>
            <div class="van-picker__toolbar" v-show="type == 'DYNASTY'">
                <button type="button" @click="showDatePicker = false" class="van-picker__cancel">取消</button>
                <div class="van-ellipsis van-picker__title">选择朝代</div>
                <button type="button" @click="cascaderConfirm" class="van-picker__confirm">确认</button>
            </div>
            <van-datetime-picker
                    v-if="type == 'DATE'"
                    v-model="vanCurrentDate"
                    type="date"
                    title="选择年月日"
                    :min-date="minDate"
                    :max-date="maxDate"
                    :formatter="vanDateFormatter"
                    @confirm="selectDateConfirm"
                    @cancel="showDatePicker = false"
            />
            <van-cascader
                    v-else
                    v-model="cascaderValue"
                    active-color="#4f7b92"
                    :options="lunars"
                    :field-names="{text:'l', value: 'v', children: 'c' }"
                    :closeable="false"
                    :show-header="false"
                    @finish="onFinish"
            />
        </van-popup>
        <mu-dialog  width="530" :open.sync="showPcDateSelect">
            <div class="date-type">
                <span>日期类型:</span>
                <a-radio-group :value="type"  button-style="solid" @change="typeChange">
                    <a-radio-button value="DATE">
                        年月日
                    </a-radio-button>
                    <a-radio-button value="DYNASTY">
                        朝代
                    </a-radio-button>
                </a-radio-group>
                <span></span>
            </div>
            <mu-date-picker v-if="type == 'DATE'" landscape  :date.sync="muCurrentDate"></mu-date-picker>
            <van-cascader
                    v-else
                    active-color="#4f7b92"
                    v-model="cascaderValue"
                    :options="lunars"
                    :field-names="{text:'l', value: 'v', children: 'c' }"
                    :closeable="false"
                    :show-header="false"
                    @finish="onFinish"></van-cascader>
<!--            <a-cascader v-else v-model:value="cascaderValue" :field-names="{label:'l', value: 'v', children: 'c' }"
                        :options="lunars" placeholder="Please select" />-->
            <mu-button slot="actions" flat @click="showPcDateSelect = false">取消</mu-button>
            <mu-button slot="actions" flat color="primary" @click="pcSelectedConfirm" >确定</mu-button>
        </mu-dialog>
    </div>
</template>
<script>
    import {minDate, maxDate, vanDateFormatter} from '../../common/constants'
    import {isCurrentClient} from "../../common/env";
    import { lunars } from '../../common/lunar'

    export default {
        name: 'custom-date-picker',
        model: {
            prop: 'value',
            event: 'change'
        },
        props: {
            value: String,
            dateType: String
        },
        data() {
            return {
                showDatePicker: false,
                currentDate: '',
                vanCurrentDate: null,
                muCurrentDate: null,
                type: this.dateType,
                cascaderValue:'',
                selectedOptions:[],
                showPcDateSelect: false,
                minDate, maxDate, lunars
            }
        },
        watch: {
            value: {
                handler(newVal) {
                    if (newVal){
                        this.currentDate = newVal;
                    }
                    // this.currentDate = newVal ? new Date(newVal.replace("-", "/")) : new Date(2000, 0, 1)

                    if (this.dateType == 'DYNASTY' && newVal){
                        this.cascaderValue = newVal.match(/\d+-?\d+/g)[0]
                    }
                },
                // 代表在wacth立即先去执行handler方法
                immediate: true
            },
            dateType:{
                handler(newVal){
                    this.type = newVal

                    if (newVal == 'DYNASTY' && this.value){
                        this.cascaderValue = this.value.match(/\d+-?\d+/g)[0]
                    }
                },
                immediate: true
            }
        },
        methods: {
            selectDate: function () {
                if (isCurrentClient('pc')){
                    this.showPcDateSelect = true

                    if (this.type == 'DATE'){
                        this.muCurrentDate = this.currentDate ? new Date(this.currentDate.replace("-", "/"))
                            : new Date(2000, 0, 1)
                    }

                    return
                }


                this.showDatePicker = true

                this.vanCurrentDate = this.currentDate ?
                    new Date(this.currentDate.replace("-", "/")) : new Date(2000, 0, 1)

            },
            selectDateConfirm: function (value) {
                this.showDatePicker = false;
                this.currentDate = `${value.getFullYear()}-${value.getMonth() + 1}-${value.getDate()}`;
                this.$emit("change", this.currentDate)
            },
            cascaderConfirm(){

                this.showDatePicker = false;
                if (this.selectedOptions.length == 3){
                    this.currentDate = this.selectedOptions[0].l + '-' + this.selectedOptions[2].l
                }else{
                    this.currentDate = this.selectedOptions[1].l
                }
                this.$emit("change", this.currentDate)
            },
            pcSelectedConfirm(){
                this.showPcDateSelect = false;
                if (this.type == "DATE"){

                  this.selectDateConfirm(this.muCurrentDate)
                  this.showPcDateSelect = false;
                  return
              }

              this.cascaderConfirm()
            },
            typeChange(e){
                this.type = e.target.value
                this.$emit("update:dateType", this.type)

                if(this.type == 'DATE'){
                    this.vanCurrentDate = new Date(2000, 0, 1)
                    this.muCurrentDate =  new Date(2000, 0, 1)
                }
            },
            onFinish({ selectedOptions }) {
                // this.show = false;
                this.selectedOptions = selectedOptions

            },
            isCurrentClient, vanDateFormatter
        }

    }

</script>
<style lang="less">
  #custom-picker-root {

    .date-custom-class input {
      box-sizing: border-box;
      margin: 0;
      font-variant: tabular-nums;
      list-style: none;
      font-feature-settings: 'tnum';
      position: relative;
      display: inline-block;
      width: 100%;
      height: 32px;
      padding: 4px 11px;
      color: rgba(0, 0, 0, 0.65);
      font-size: 14px;
      line-height: 1.5;
      background-color: #fff;
      background-image: none;
      border: 1px solid #d9d9d9;
      border-radius: 4px;
      transition: all 0.3s;
    }

    .mu-input {
      width: 100%;
    }
  }

  .date-type{
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px;

    >span{
      margin-right: 10px;
      font-size: 1rem;
      flex: 1;
      text-align: right;
    }
  }
</style>
