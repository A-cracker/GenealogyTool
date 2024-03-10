<template>
    <div class="find-root">

        <div id="app">
            <!--顶部导航栏-->
            <nav id="navbar navbar-default" class="navbar navbar-default navbar-static-top">
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
                            <li class="active"><a @click="RouterHelper.toFind(vueIns)">找亲</a></li>
                        </ul>
                        <ul class="nav navbar-nav navbar-right">
                            <li><a @click="RouterHelper.toInfo(vueIns)" style="margin-right: 50px;"><span class="iconfont icon-personalcenter" aria-hidden="true" style="margin-right: 4px;"></span>个人中心</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
            <div style="height: calc(100vh - 74px)">
                <div class="row" style="display:flex;justify-content: space-around;" id="content">
                    <div id="container-div">
                        <div class="panel panel-default" style="margin-bottom: 0;">
                            <div class="panel-heading text-center"><h3 style="padding: 5px;">找亲</h3></div>
                            <div class="panel-body">
                                <div class="row">
                                    <div class="col-xs-12 col-sm-12 col-md-5" style="padding: 5px 15px;">
                                        <select id="source" name="source" class="form-control selectpicker" data-live-search="true" data-size="5">
                                            <option value="" disabled selected style="display:none">请选择起始成员</option>
                                        </select>
                                    </div>
                                    <div class="col-xs-12 col-sm-12 col-md-5" style="padding: 5px 15px;">
                                        <select id="target" name="target" class="form-control selectpicker" data-live-search="true" data-size="5">
                                            <option value="" disabled selected style="display:none">请选择目标成员</option>
                                        </select>
                                    </div>
                                    <div class="col-xs-12 col-sm-2 col-md-2" style="text-align: center;padding: 5px 0;">
                                        <button id="generate" type="button" class="btn btn-default">
                                            <span class="iconfont icon-chakan" aria-hidden="true"></span>定位
                                        </button>
                                    </div>
                                </div>
                                <div id="graph" style="overflow: hidden;margin-top: 10px;">
                                    <p style="margin-left:10px;">两者相差代数为<span id="depth"></span></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="myModalLabel">个人信息</h4>
                    </div>
                    <div class="modal-body">
                        <table class="table table-striped table-bordered table-hover" style="text-align:center;vertical-align: middle;">
                            <tr class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">照片</td>
                                <td id="modal-img" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                            <tr class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">姓名</td>
                                <td id="modal-name" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                            <tr class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">性别</td>
                                <td id="modal-gender" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                            <tr class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">字辈</td>
                                <td id="modal-generation" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                            <tr class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">出生时间</td>
                                <td id="modal-birthdate" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                            <tr class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">出生地</td>
                                <td id="modal-birthplace" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                            <tr class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">介绍</td>
                                <td id="modal-description" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                            <tr class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">死亡时间</td>
                                <td id="modal-deathDate" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                            <tr id="modal-restplace-row" class="item">
                                <td class="col-md-5 col-sm-5 col-xs-5">安息地</td>
                                <td id="modal-restplace" class="col-md-5 col-sm-5 col-xs-5"></td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>


        <div class="modal fade" id="myModal1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="myModalLabel">个人信息</h4>
                    </div>
                    <div class="modal-body">
                        亲网中不存在这个人的个人信息
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
    import {getBaseUrl, getResourceUrl} from "../../common/env";
    import { getCurrentInstance } from "vue"
    import { RouterHelper } from "../../router/RouterHelper";

    const vueIns = getCurrentInstance().proxy

    var height = 400;
    var width = 960;
    var modalPlay = function (data) {
        $("#modal-name").text(data.name);
        $("#modal-gender").text(data.gender);
        if (data.description != "") {
            $("#modal-description").text(data.description);
        } else {
            $("#modal-description").text("暂未完善");
        }
        if (data.generation != "") {
            $("#modal-generation").text(data.generation);
        } else {
            $("#modal-generation").text("暂未完善");
        }
        if (data.birthplace != "") {
            $("#modal-birthplace").text(data.birthplace);
        } else {
            $("#modal-birthplace").text("暂未完善");
        }
        if (data.is_alive == 0) {
            if (data.restplace != "") {
                $("#modal-restplace").text(data.restplace);
            } else {
                $("#modal-restplace").text("暂未完善");
            }
        } else {
            $("#modal-restplace").text("暂未完善");
        }
        if (data.birthdate != "") {
            $("#modal-birthdate").text(data.birthdate);
        } else {
            $("#modal-birthdate").text("暂未完善");
        }
        if (data.deathDate != "") {
            $("#modal-deathDate").text(data.deathDate);
        } else {
            $("#modal-deathDate").text("暂未完善");
        }
        $("#modal-img").empty();
        if (data.img == "true") {
            var img = $("<img></img>").css("width", "100px").attr("src", getBaseUrl() + "&action=getMemberImg&id=" + data.id).attr("class", "img-thumbnail");
            $("#modal-img").append(img);
        } else {
            var img = $("<img></img>").css("width", "100px").attr("src", getResourceUrl() + "/images/avatar_default.png").attr("class", "img-thumbnail");
            $("#modal-img").append(img);
        }
    }
    $(document).ready(function () {
        $("#graph").css({'height': height});
        $.ajax({
            type: "GET",
            url: getBaseUrl(),
            data: "action=getMembers",
            dataType: "json",
            success: function (data) {
                length = data.length;
                for (var i = 0; i < length; i++) {
                    $("#source").append("<option value=\"" + data[i].id + "\" data-subtext=\"" + "(" + data[i].identityId + ")" + "\">" + data[i].name + "</option>");
                    $("#target").append("<option value=\"" + data[i].id + "\" data-subtext=\"" + "(" + data[i].identityId + ")" + "\">" + data[i].name + "</option>");
                    $('.selectpicker').selectpicker('refresh');
                    $('.dropdown-toggle').dropdown();
                }
            }, error: function (data) {
                alert("获取人员出错,请稍后再试");
            }
        });
        $("#generate").click(function () {
            var source = $("#source").val();
            var target = $("#target").val();
            if (source == null || source == "") {
                alert("请选择起始成员");
                return;
            }
            if (target == null || target == '') {
                alert("请选择目标成员");
                return;
            }
            $("svg").remove();
            d3.json( getBaseUrl() + "&action=getPath&source=" + source + "&target=" + target, function (error, data) {
                dPath.init(data, {
                    target: "#graph",
                    svg: {
                        width: width,
                        height: height
                    },
                    callbacks: {
                        nodeClick: function (id) {
                            if (id != "") {
                                $.ajax({
                                    type: "GET",
                                    url: getBaseUrl(),
                                    data: "action=getMemberInfo&id=" + id,
                                    dataType: "json",
                                    success: function (data) {
                                        modalPlay(data);
                                        $('#myModal').modal('show');
                                    }, error: function (data) {
                                        alert("请求出错,请稍后再试");
                                    }
                                });
                            } else {
                                $('#myModal1').modal('show');
                            }
                        }
                    }
                });
            });
        });
    });

</script>
<style lang="less">
  @import "../../css/dTree.css";
  @import "../../css/glorify.css";

  ul {
    max-height: 130px;
    overflow-y: auto;
    margin-top: 0 -15px;
  }

  .panel-heading {
    border-top-left-radius: unset;
    border-top-right-radius: unset;
  }

  .navbar {
    margin-bottom: 0px;
  }

  .panel {
    border-radius: unset;
  }

  @media screen and (max-width: 768px) {
    #container-div {
      width: 100%;
    }

    #content {
      margin: 0px;
    }

    .panel {
      border: unset;
      -webkit-box-shadow: unset;
      box-shadow: unset;
    }

    .navbar-brand {
      padding-left: 15px;
    }
  }

  @media screen and (min-width: 768px) {
    #container-div {
      width: 85%;
    }

    #content {
      margin: 20px 0 0 0;
    }

    .navbar-brand {
      padding-left: 50px;
      padding-right: 30px;
    }
  }

  foreignObject {
    height: 30px;
  }
</style>
