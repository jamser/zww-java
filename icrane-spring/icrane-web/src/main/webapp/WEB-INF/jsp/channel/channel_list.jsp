<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="../common/include.jsp"%>
<base href="<%=basePath%>" />
<%@ include file="../common/js.jsp"%>
<%@ include file="../common/css.jsp"%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<nav class="breadcrumb"><i class="Hui-iconfont">&#xe67f;</i> 首页 <span class="c-gray en">&gt;</span> 渠道管理<span class="c-gray en">&gt;</span> 渠道列表 <a class="btn btn-success radius r" style="line-height:1.6em;margin-top:3px" href="${path}/channelManage/searchList" title="刷新" ><i class="Hui-iconfont">&#xe68f;</i></a></nav>
<form id="frm" action="channelManage/searchList" method="post" >
<div class="page-container">
	
	<!-- 筛选功能 div -text-c -->
	<div class="text-c"> 
	    <span>
			<select class="input-text" id="registerChannel" value="${registerChannel}" name="registerChannel" style="width:150px">
				<option value="">渠道</option>
				<c:forEach items="${memberList}" var="memberList">
					<c:choose>
						<c:when test="${memberList.registerChannel==registerChannel}">
							<option value="${memberList.registerChannel}"  selected>${memberList.registerChannel }</option>
						</c:when>
						<c:when test="${empty memberList.registerChannel}">
							<option value="2" >无渠道号</option>
						</c:when>
						<c:otherwise>
							<option value="${memberList.registerChannel}">${memberList.registerChannel }</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			<input type="text" name="memberid" id="memberid" value="${memberid}" placeholder="输入用户id" style="width:120px" class="input-text">
			<input type="text" name="name" id="name" value="${name}" placeholder="输入用户名" style="width:120px" class="input-text">
			<input type="text" name="registerDate" id="registerDate"
				   value="${registerDate}"
				   onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" placeholder="输入开始时间" style="width:120px"  class="input-text Wdate">
			<input type="text" name="endDate" id="endDate"
				   value="${endDate}"
				   onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" placeholder="输入结束时间" style="width:120px"  class="input-text Wdate">
			<select class="input-text" id="lastLoginFrom" name="lastLoginFrom" style="width:120px">
					<option value="">设备</option>
					<option value="android" <c:if test="${lastLoginFrom=='android'}">selected="selected"</c:if>>android</option>
					<option value="ios" <c:if test="${lastLoginFrom=='ios'}">selected="selected"</c:if>>ios</option>
			</select>
			<!-- <input type="submit" value="查询数据 "  name="button" class="button">&nbsp;&nbsp; -->
			<button name="" id="" class="btn btn-success" type="submit"><i class="Hui-iconfont">&#xe665;</i> 搜索</button>
		</span>
		<%--<span>
		    <button name="output" id="output" class="btn btn-success" type="submit">导出数据</button>
	    </span>--%>
		<span>
		    <button name="deduct" id="deduct" class="btn btn-success" type="submit">扣量</button>
	    </span>
	</div>
	
	<div class="cl pd-5 bg-1 bk-gray mt-20"> <span class="l"><!-- <a href="javascript:;" onclick="datadel()" class="btn btn-danger radius"><i class="Hui-iconfont">&#xe6e2;</i> 批量删除</a> --> </span>
		<span class="l">注册用户量：<strong>${pageBean.totalCount }</strong> 条</span>
		<span class="r">共有数据：<strong>${pageBean.totalCount }</strong> 条</span>
	</div>
	<div class="mt-20">
		<div id="DataTables_Table_0_wrapper" class="dataTables_wrapper no-footer">
		<table class="table table-border table-bordered table-bg table-hover table-sort table-responsive">
			<thead>
				<tr class="text-c">
					<th width="50" >登录渠道号</th>
					<th width="50" >注册渠道号</th>
					<th width="40" >id</th>
					<th width="60">用户id</th>
					<th width="80">昵称</th>
					<th width="80">电话</th>
					<th width="50">性别</th>
					<th width="50">机型</th>
					<th width="80">注册时间</th>
					<th width="80">最近登录时间</th>
					<th width="50">是否在线</th>
					<th width="45">设备</th>
					<th width="50">抓取记录</th>
					<th width="50">充值记录</th>
					<th width="60">
						<input  type="checkbox"  id="all" name="check" value="">
						<span >扣量</span>
					</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${pageBean.list}" var="member"> 
					<tr class="text-c">
						<td>
							<c:if test="${empty member.loginChannel}">未知</c:if>
							<c:if test="${not empty member.loginChannel}">${member.loginChannel}</c:if>
						</td>
						<td>
							<c:if test="${empty member.registerChannel}">未知</c:if>
							<c:if test="${not empty member.registerChannel}">${member.registerChannel}</c:if>
						</td>
						<td>${member.id}</td>
						<td>${member.memberID }</td>
						<td>${member.name }</td>
						<td>${member.mobile}</td>
						<td>
							<c:if test="${member.gender =='n'}">保密</c:if>
							<c:if test="${member.gender =='m'}">男</c:if>
							<c:if test="${member.gender =='f'}">女</c:if>
						</td>
						<td>
							<c:if test="${empty member.phoneModel}">未知</c:if>
							<c:if test="${not empty member.phoneModel}">${member.phoneModel}</c:if>

						</td>
						<td><fmt:formatDate value='${member.registerDate }' pattern='yy-MM-dd HH:mm'/></td>
						<td><fmt:formatDate value='${member.lastLoginDate }' pattern='yy-MM-dd HH:mm'/></td>
						<td class="td-status"><span class="label label-success radius">${member.onlineFlg==true?"在线":"离线" }</span></td>
						<td>
							<c:if test="${member.lastLoginFrom =='android'}">android</c:if>
							<c:if test="${member.lastLoginFrom ==null}">ios</c:if>
							<c:if test="${member.lastLoginFrom =='iOS'}">ios</c:if>
						</td>
						<td>
							<a class="btn btn-primary radius" data-title="用户抓取记录查看" data-href="javascript:;" onclick="charge_query('用户抓取记录','channelManage/listCatch','${member.id}','','')" href="javascript:;">查看</a>
						</td>
						<td>
							<a class="btn btn-primary radius" data-title="用户充值记录查看" data-href="javascript:;" onclick="charge_query('用户充值记录','channelManage/listCharge','${member.id}','','')" href="javascript:;">查看</a>
						</td>
						<td><input type="checkbox" value="${member.id}" name="check"></td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			    <tr>
					<td colspan="18">
					<jsp:include page= "/WEB-INF/jsp/common/pagination.jsp" flush= "true">
					<jsp:param name= "page_url" value= "channelManage/searchList"/>
					</jsp:include>
					</td>
			    </tr>
			</tfoot>
		</table>
			<!-- <div class="dataTables_info" id="DataTables_Table_0_info" role="status" aria-live="polite">显示 1 到 1 ，共 1 条</div> --> 
			<!-- <div class="dataTables_paginate paging_simple_numbers" id="DataTables_Table_0_paginate"><a class="paginate_button previous disabled" aria-controls="DataTables_Table_0" data-dt-idx="0" tabindex="0" id="DataTables_Table_0_previous">上一页</a><span><a class="paginate_button current" aria-controls="DataTables_Table_0" data-dt-idx="1" tabindex="0">1</a></span><a class="paginate_button next disabled" aria-controls="DataTables_Table_0" data-dt-idx="2" tabindex="0" id="DataTables_Table_0_next">下一页</a></div> -->
		</div>
	</div>
</div>

</form>
<script type="text/javascript">
    //全选，全不选
    $("#all").click(function(){
        if(this.checked){
            $("input[name='check']").prop("checked", true);
        }else{
            $("input[name='check']").prop("checked", false);
        }
    });


//查询充值·抓取记录
function charge_query(title,url,id,w,h){
	url = url+"?id="+id;
	var index = parent.layer.getFrameIndex(window.name);
	parent.layer.iframeAuto(index);
	layer_show(title,url,w,h);

}

//扣量
	$("#deduct").click(function () {
        var groupCheckbox=$("input[name='check']");
        var ids = '';
        for(var i=1;i<groupCheckbox.length;i++){
            if(groupCheckbox[i].checked){
                var val = groupCheckbox[i].value;
//                alert(val+"***");
                ids += val + ',';
            }
        }
        //扣量请求
        $.ajax({
            type: 'POST',
            url: 'channelManage/channelDeduct',
            dataType: 'json',
            data:{ids:ids},
            success: function(data){
                if(data==1){
                    layer.msg('扣量成功!',{icon:1,time:1000});
                    groupCheckbox.attr("checked", false);
                    window.location.reload();	//自动刷新窗口
                }else{
                    alert('扣量失败！')
                }
            }
        });

    });
/*添加*/
/* function article_add(title,url,w,h){
	var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
	layer.full(index);
} */
/*上传图片*/
//function doll_upload(title,url,id,w,h){
//	url = url+"?id="+id;
//	layer_show(title,url,w,h);
//}

//function dollImage_edit(title,url,name,id,w,h){
//	 url = url+"?name="+name+"&doll_id="+id;
//	/* layer_show(title,url,w,h); */
//	 window.location.href=url;
//}

/*编辑*/
//function member_edit(title,url,id,w,h){
//	 url = url+"?id="+id;
//	layer_show(title,url,w,h);
//}
/*删除*/
//function doll_del(obj,id){
//	layer.confirm('确认要删除吗？',function(index){
//		$.ajax({
//			type: 'POST',
//			url: 'dollManage/dollDel',
//			dataType: 'json',
//			data:{id:id},
//			success: function(data){
//				if(data==1){
//					$(obj).parents("tr").remove();
//					layer.msg('已删除!',{icon:1,time:1000});
//				}else{
//					alert('删除失败！')
//				}
//
//			},
//			error:function(data) {
//				alert('删除失败!');
//			},
//		});
//	});
//}

/*下架*/
//function article_stop(obj,id){
//	layer.confirm('确认要下架吗？',function(index){
//		$(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="article_start(this,id)" href="javascript:;" title="发布"><i class="Hui-iconfont">&#xe603;</i></a>');
//		$(obj).parents("tr").find(".td-status").html('<span class="label label-defaunt radius">已下架</span>');
//		$(obj).remove();
//		layer.msg('已下架!',{icon: 5,time:1000});
//	});
//}

/*资讯-发布*/
//function article_start(obj,id){
//	layer.confirm('确认要发布吗？',function(index){
//		$(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="article_stop(this,id)" href="javascript:;" title="下架"><i class="Hui-iconfont">&#xe6de;</i></a>');
//		$(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已发布</span>');
//		$(obj).remove();
//		layer.msg('已发布!',{icon: 6,time:1000});
//	});
//}
/*资讯-申请上线*/
//function article_shenqing(obj,id){
//	$(obj).parents("tr").find(".td-status").html('<span class="label label-default radius">待审核</span>');
//	$(obj).parents("tr").find(".td-manage").html("");
//	layer.msg('已提交申请，耐心等待审核!', {icon: 1,time:2000});
//}

/*头像图片编辑*/
//function picture_edit(title,url,id){
//	var index = layer.open({
//		type: 2,
//		title: title,
//		content: url
//	});
//	layer.full(index);
//}
</script> 
