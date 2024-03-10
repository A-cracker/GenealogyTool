<template>
    <div class="edit-member-root">
        <a-form-model ref="formEl" :rules="rules" :model="editForm" :label-col="{span: 6}" :wrapper-col="{span: 17}">
            <a-form-model-item label="家谱名称" prop="name">
                <a-input v-model="editForm.name"/>
            </a-form-model-item>
            <a-form-model-item label="卷宗" prop="dossier">
                <a-input v-model="editForm.dossier"/>
            </a-form-model-item>
            <a-form-model-item label="房支" prop="branch">
                <a-input v-model="editForm.branch"/>
            </a-form-model-item>
            <a-form-model-item label="派系" prop="faction">
                <a-input v-model="editForm.faction"/>
            </a-form-model-item>
            <a-form-model-item label="普籍地" prop="originPlace">
                <a-select v-model="editForm.originPlace" show-search option-filter-prop="children">
                    <van-icon slot="suffixIcon" class-prefix="iconfont" name="arrow_down"
                              color="#4f7b92"
                              size="1rem"></van-icon>
                    <a-select-option :value="d" v-for="d in places">
                        {{ d }}
                    </a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item label="家谱介绍" prop="description">
                <a-textarea v-model="editForm.description"/>
            </a-form-model-item>

        </a-form-model>
    </div>
</template>
<script setup>
    import {ref, watchEffect} from "vue";
    import {getBaseUrl, toast} from "../../common/env";

    const rules = {
        name: [
            { required: true, message: '请填写家谱名', trigger: 'blur' },
            { min: 1, max: 40, message: '长度1到40', trigger: 'blur' },
        ],
        dossier: [
            {max: 100, message:"最大长度100"}
        ],
        branch: [
            {max: 100, message:"最大长度100"}
        ],
        faction: [
            {max: 100, message:"最大长度100"}
        ],
        originPlace: [
            {max: 100, message:"最大长度100"}
        ],
        description: [
            {max: 255, message:"最大长度255"}
        ],
    }

    const places = ['北京市','浙江省','天津市','安徽省','上海市','福建省','重庆市','江西省','山东省','河南省','湖北省','湖南省','广东省','海南省','山西省','青海省','江苏省','辽宁省','吉林省','台湾省','河北省','贵州省','四川省','云南省','陕西省','甘肃省','黑龙江省','香港特别行政区','澳门特别行政区','广西壮族自治区','宁夏回族自治区','新疆维吾尔自治区','内蒙古自治区','西藏自治区']


    const emit = defineEmits(['success', 'close'])

    const editForm = ref( {} )

    const props = defineProps(['pedigree'])

    watchEffect(()=>{
        if (props.pedigree)
            editForm.value = props.pedigree
    })


    const formEl = ref(null)
    function submit(isInsert = true) {
        formEl.value.validate(valid => {
            if (valid){
                editSubmit(isInsert)
            }else{
                return false;
            }
        })
    }
    //修改信息提交
    function editSubmit(isInsert) {
        let params = new FormData();

        for (let key in editForm.value){
            params.append(key, editForm.value[key])
        }

        $.ajax({
            type: "POST",
            url: getBaseUrl() + "&action=" + (isInsert ? "insertPedigree":"editPedigree"),
            data: params,
            dataType: "json",
            processData: false,
            contentType: false,
            success: function (data) {
               emit('success', editForm.value)
                toast("已保存!")
            }, error: function (data) {
                alert("添加家谱出错，稍后再试");
                console.log(data);
            }
        });
    }

    defineExpose({submit})
</script>
<style lang="less">
  .ant-calendar-picker-container {
    z-index: 1000000000;
  }

  .mu-dialog .edit-member-root{
    padding: 1rem;
  }
</style>
