	//初始构建树变量
	var t_id = null;//
	var treeId = null;//树Id
	var click_id = null;//
	var depth = null;
	var t_name = null;
	var btnvalue = null;
	var height = 750;
	var width=(window.innerWidth
	|| document.documentElement.clientWidth
	|| document.body.clientWidth)+50;
	(width>1000)?width=1000:width=width;



	//安息地隐现控制
	//添加成员节点控制
	var restplaceform = $("#restplaceform");
	var deathform = $("#deathform");
	deathform.css("display","none");
    restplaceform.css("display","none");
    $("#not_alive").click(function(){
        restplaceform.css("display","block");
		deathform.css("display","block");
    });
    $("#is_alive").click(function(){
        restplaceform.css("display","none");
        restplaceform.val('');
		deathform.css("display","none");
		deathform.val('');
    });
    //新增起始成员控制
    var restplaceform_new = $('#restplaceform_new');
	var deathform_new = $('#deathform_new');
    restplaceform_new.css("display","none");
	deathform_new.css("display","none");
    $('#not_alive_new').click(function(){
    	restplaceform_new.css("display","block");
		deathform_new.css("display","block");
    });
    $('#is_alive_new').click(function(){
    	restplaceform_new.css("display","none");
    	restplaceform_new.val('');
		deathform_new.css("display","none");
		deathform_new.val('');
    });
    //编辑框内控制
    var restplaceform_edit = $('#restplaceform_edit');
	var deathform_edit = $('#deathform_edit');
    $('#edit-not_alive').click(function(){
    	restplaceform_edit.css("display","block");
		deathform_edit.css("display","block");
    });
    $('#edit-is_alive').click(function(){
    	restplaceform_edit.css("display","none");
    	restplaceform_edit.val('');
		deathform_edit.css("display","none");
		deathform_edit.val('');
    });
	
