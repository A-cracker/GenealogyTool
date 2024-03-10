<template>
    <div id="info-root">
        <div style="width: 100%;">
            <nav id="navbar navbar-default" class="navbar navbar-default navbar-static-top" style="margin-bottom: 0px!important;">
                <div class="container-fluid" id="rootContent">
                    <div class="navbar-header">
                        <!-- Collapsed Hamburger -->
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#app-navbar-collapse" aria-expanded="false">
                            <span class="sr-only">Toggle Navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>

                        <!-- Branding Image -->
                        <a class="navbar-brand" @click="RouterHelper.toIndex(vueIns)">
                            亲网
                        </a>
                    </div>

                    <div class="collapse navbar-collapse" id="app-navbar-collapse">
                        <!-- Left Side Of Navbar -->
                        <ul class="nav navbar-nav">
                            <li><a @click="RouterHelper.toHome(vueIns)">我的亲网</a></li>
                            <li><a @click="RouterHelper.toFind(vueIns)">找亲</a></li>
                        </ul>
                        <ul class="nav navbar-nav navbar-right">
                        </ul>
                    </div>
                </div>
            </nav>
            <div style="display: flex;padding: 10px;" id="info">

                <div class="content" style="flex-grow: 1;display: flex;flex-direction: column">
                    <div id="personal-info">
                        <div id="avatar" style="text-align: center;padding: 10px;margin-top: 20px;">
                            <img src="resource/images/avatar_default.png" style="object-fit: cover;width:90px;height:90px;border-radius: 50%;" onclick="uploadFile()"/>
                        </div>
                        <input id="avatar-file" type="file" name="avatar" accept="image/*" style="height: 0;display:none" onchange="handleImg()"/>
                        <div style="text-align: center;">
                            <div name="name"><h4 style="display: inline-block;margin-right: 5px;" id="userName">游客用户</h4></div>
                            <form id="editUser-form" enctype="multipart/form-data" style="margin: 0px;">
                                <div class="detail" style="display: flex;flex-wrap: wrap;justify-content: space-between">
                                    <div class="input-group item form-group">
                                        <span class="input-group-addon">出生地</span>
                                        <input type="text" name="birthplace" class="form-control" readonly="true" id="user-birthplace" placeholder="出生地">
                                    </div>
                                    <div class="input-group item form-group">
                                        <span class="input-group-addon">出生日期</span>
                                        <input type="date" name="birthdate" class="form-control" readonly="true" id="user-birthdate">
                                    </div>
                                    <div class="input-group item form-group">
                                        <span class="input-group-addon">现居住地</span>
                                        <input type="text" name="residence" class="form-control" readonly="true" id="user-residence" placeholder="现居住地">
                                    </div>
                                    <div class="input-group item form-group">
                                        <span class="input-group-addon">手机号</span>
                                        <input type="text" name="phoneNumber" class="form-control" readonly="true" id="user-phoneNumber" placeholder="手机号">
                                    </div>
                                    <div class="input-group item form-group">
                                        <span class="input-group-addon">字辈</span>
                                        <input type="text" name="generation" class="form-control" readonly="true" id="user-generation" placeholder="字辈">
                                    </div>
                                    <div class="input-group item form-group">
                                        <span class="input-group-addon">身份证号</span>
                                        <input type="text" name="identityId" class="form-control" readonly="true" id="user-identityId" placeholder="请输入身份证号">
                                    </div>
                                    <div class="input-group item form-group" style="width: 100%;">
                                        <span class="input-group-addon">介绍:</span>
                                        <textarea name="description" id="user-description" readonly="true" class="form-control" rows="5" maxlength="250" placeholder="请输入介绍"></textarea>
                                    </div>
                                </div>
                            </form>
                            <button type="button" class="btn-default btn" id="edit-info" style="background-color: #3b3b3b;color: #ffffffde;margin:0 0 10px 0;">修改个人信息</button>
                        </div>
                    </div>
                    <button type="button" class="btn" id="logout" style="margin:10px auto;display:none;">退出登录</button>
                </div>
            </div>
        </div>



        <!--添加亲网用户插入框-->
        <div class="modal fade" id="insertUser-Modal" tabindex="-1" role="dialog" aria-labelledby="optionModalLabel">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title" id="optionModalLabel">加入亲网</h4>
                    </div>
                    <div class="modal-body">
                        <form id="user-form" enctype="multipart/form-data">
                            <div class="form-group">
                                <label for="name" class="control-label">姓名<span class="hint">*</span>:</label>
                                <input type="text" class="form-control" id="name" name="name"  placeholder="请输入姓名" required>
                            </div>
                            <div class="form-group">
                                <label for="name" class="control-label">性别<span class="hint">*</span>:<span class="hint" style="font-size:10px;font-weight: lighter">（性别确认后不可更改）</span></label>
                                <label class="radio-inline">
                                    <input type="radio" class="man" name="gender" id="man" value="1" checked>男
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" class="woman" name="gender" id="woman" value="0">女
                                </label>
                            </div>
                            <div class="form-group">
                                <label for="name" class="control-label">出生日期<span class="hint">*</span>:</label>
                                <input type="date" class="form-control" id="birthdate" name="birthdate">
                            </div>
                            <div class="form-group">
                                <label for="name" class="control-label">身份证后四位<span class="hint">*</span>:<span class="hint" style="font-size:10px;font-weight: lighter">（必填，防重名）</span></label>
                                <input type="text" class="form-control" id="identityId" name="identityId" placeholder="请输入身份证后四位" required>
                            </div>
                            <div class="form-group">
                                <label for="name" class="control-label">出生地:</label>
                                <input type="text" class="form-control" id="birthplace" name="birthplace" placeholder="请输入出生地">
                            </div>
                            <div class="form-group">
                                <label for="name" class="control-label">现居住地:</label>
                                <input type="text" class="form-control" id="residence" name="residence" placeholder="请输入现居住地">
                            </div>
                            <div class="form-group">
                                <label for="name" class="control-label">手机号:</label>
                                <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" placeholder="请输入手机号">
                            </div>
                            <div class="form-group">
                                <label for="name" class="control-label">字辈:</label>
                                <input type="text" class="form-control" id="generation" name="generation" placeholder="请输入字辈">
                            </div>
                            <div class="form-group">
                                <label for="name" class="control-label">介绍:</label>
                                <textarea name="description" id="description" class="form-control" rows="5" maxlength="250" placeholder="请输入介绍"></textarea>
                            </div>
                            <div class="form-group">
                                <label for="img">上传照片</label>
                                <input id="img" name="img" type="file" class="file">
                            </div>
                            <div class="form-group">
                                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                                <button type="button" class="btn btn-primary" onclick="insertUser()">确认</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
    import { RouterHelper} from "../../router/RouterHelper";
    import { getCurrentInstance } from "vue"

    const vueIns = getCurrentInstance().proxy

    $(function () {
        $.ajax({
            type: "GET",
            url: g_runToolUrl + "&id=" + g_userId,
            data: "action=getUserDetail",
            dataType: "json",
            success: function (data) {
                if (!data.result) {
                    $(".content").html("");
                    document.getElementById("info").style.height = "calc(100% - 60px)";
                    $(".content").css("justify-content", "center");
                    var text = document.createElement("div");
                    var button = document.createElement("button");
                    text.className = "text-monospace";
                    text.classList.add('text-muted');
                    text.style.textAlign = "center";
                    text.style.fontSize = "15px";
                    text.innerHTML = "您还是游客身份";
                    button.setAttribute("type", "button");
                    button.setAttribute("class", "btn");
                    button.setAttribute("id", "insertUser");
                    button.style.margin = "0 auto";
                    button.style.display = "block";
                    button.setAttribute("onclick", "newUser_modalPlay()");
                    button.innerHTML = "成为亲网用户";
                    // graph.appendChild(text);
                    $(".content").append(text);
                    $(".content").append(button);
                    return
                }

                data = data.content;
                if (data.img == "true") {
                    $('#avatar').html("<img onclick='uploadFile()' style='height: 90px;width:90px;border-radius: 50%;object-fit: cover;' src='" + g_runToolUrl + "&action=showUserImg&id=" + g_userId + "'>");
                } else {
                    $('#avatar').html("<img onclick='uploadFile()' style='height: 90px;width:90px;border-radius: 50%;object-fit: cover;' src='" + resourceUrl + "/images/avatar_default.png'>");
                }
                if (data.birthdate != null) {
                    $("#user-birthdate").val(data.birthdate);
                }
                if (data.name != null) {
                    $("#userName").html(data.name);
                }
                if (data.birthplace != null) {
                    $("#user-birthplace").val(data.birthplace);
                }
                if (data.residence != null) {
                    $("#user-residence").val(data.birthplace);
                }
                if (data.description != null) {
                    $("#user-description").val(data.description);
                }
                if (data.phoneNumber != null) {
                    $("#user-phoneNumber").val(data.phoneNumber);
                }
                if (data.generation != null) {
                    $("#user-generation").val(data.generation);
                }
                if (data.identityId != null) {
                    $("#user-identityId").val(data.identityId);
                }
            }, error: function (data) {
                alert("获取亲网用户信息出错");
            }
        });

    })


    $("#edit-info").click(function (){
        if($("#user-birthplace").attr("readonly")=="readonly"){
            $(".form-control").removeAttr("readonly");
            $("#edit-info").text("保存");
        }else{
            if(window.confirm('是否修改个人信息?')) {
                userSubmit();
            }else{
                $(".form-control").attr("readonly","readonly");
                $("#edit-info").text("修改个人信息");
            }
        }
    })
    function uploadFile() {
        document.getElementById("avatar-file").click();
    }
    function handleImg(){
        if($("#avatar-file")[0].files[0] != undefined){
            var formData = new FormData();
            formData.append("img",$("#avatar-file")[0].files[0]);
            console.log($("#avatar-file")[0].files[0]);
            formData.append("id",g_userId);
            $.ajax({
                type: 'POST',
                url: g_runToolUrl+"&action=updateAvatar",
                data: formData,
                dataType:"json",
                processData: false,
                contentType: false,
                success : function(data) {
                    window.location.reload();
                }
            });
        }
    }
    function  userSubmit(){
        var editFormData=new FormData(document.getElementById("editUser-form"));
        var formIdentityId = editFormData.get("identityId").trim();
        var formPhoneNumber = editFormData.get("phoneNumber").trim();
        if(formIdentityId == ''){
            alert("您还未输入身份证号");
        }
        else if(!(/^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(formIdentityId))){
            alert("身份证后格式错误，请重新填写")
        }
        else if(!(/^\+?[0-9][0-9]*$/.test(formPhoneNumber)) && formPhoneNumber!=""){
            alert("电话号码格式不正确，请重新输入")
        }
        else{
            editFormData.append("editid", g_userId);
            editFormData.append("name",$("#userName").text())
            editFormData.append("is_alive",1);
            editFormData.append("restplace",null);
            $.ajax({
                type: 'POST',
                url: g_runToolUrl+"&action=editUser",
                data: editFormData,
                dataType:"json",
                processData: false,
                contentType: false,
                success : function(data) {
                    if(data == true){
                        $('#editUser-form')[0].reset();
                        window.location.reload();
                    }
                }
            });
        }
    }

    function newUser_modalPlay(){
        $('#insertUser-Modal').modal();
    }

    function setting(){
        $("#personal-info").css("display","none");
        $("#logout").css("display","block");
        document.getElementById("info").style.height = "calc(100% - 60px)";
        $("#tab1").removeClass("active");
        $("#tab2").addClass("active");
    }

    function showInfo(){
        $("#tab2").removeClass("active");
        $("#tab1").addClass("active");
        $("#personal-info").css("display","block");
        $("#logout").css("display","none");
    }

    function insertUser(){
        var userFormData=new FormData(document.getElementById("user-form"));
        var formName = userFormData.get("name").trim();
        var formIdentityId = userFormData.get("identityId").trim();
        var formPhoneNumber = userFormData.get("phoneNumber").trim();
        if(formName == ''){
            alert("您还未输入姓名");
        }
        else if(formIdentityId == ''){
            alert("您还未输入身份证号");
        }
        else if(!(/^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(formIdentityId))){
            alert("输入身份证格式错误，请重新填写")
        }
        else if(!(/^\+?[0-9][0-9]*$/.test(formPhoneNumber)) && formPhoneNumber!=""){
            alert("电话号码格式不正确，请重新输入")
        }
        else{
            $.ajax({
                type: 'POST',
                url: g_runToolUrl+"&action=insertUser",
                data: userFormData,
                dataType:"json",
                processData: false,
                contentType: false,
                success : function(data) {
                    if(data.flag == true){
                        //重置表单
                        $('#user-form')[0].reset();
                        window.location.reload();
                    }
                }
            });

        }
    }

</script>
<style lang="less">
  @import "../../css/glorify.css";

  #info-root {
    height: 100vh;
  }

  .input-group-addon {
    color: #555;
    background-color: #3b3b3b14;
  }

  @media screen and (max-width: 450px) {
    .navbar-brand {
      padding-left: 15px;
    }
  }

  @media screen and (min-width: 450px) {
    .navbar-brand {
      padding-left: 50px;
    }
  }

  .tab, .content {
    box-shadow: 5px 5px 30px #3b3b3b1a;
    border-radius: 10px;
  }

  .tab {
    margin-right: 10px;
    max-width: 100px;
  }

  .item {
    padding: 5px 10px;
  }

  .nav-pills > li.active > a, .nav-pills > li.active > a:focus, .nav-pills > li.active > a:hover {
    background-color: #3b3b3beb;
  }

  .form-control[disabled], .form-control[readonly], fieldset[disabled] .form-control {
    background-color: #4442420d;
  }

  @media screen and (max-width: 768px) {
    .item {
      width: 100%;
    }

    .detail {
      padding: 10px;
    }

    .navbar-brand {
      padding-left: 15px;
    }
  }

  @media screen and (min-width: 768px) {
    .item {
      width: calc(calc(100% / 2) - 0px);
      padding: 8px 30px;
    }

    #edit-info {
      margin-top: 80px;
    }

    .tab {
      width: 200px;
    }

    #info {
      height: calc(100% - 60px);
    }

    .detail {
      padding: 25px 100px 10px 100px;
    }

    .navbar-brand {
      padding-left: 50px;
      padding-right: 30px;
    }
  }
</style>
