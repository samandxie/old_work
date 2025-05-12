layui.use(['element', 'jquery', 'layer', 'laytpl','laypage','form','table'], function() {
	var $ = layui.jquery,
		layer = layui.layer,
		laytpl = layui.laytpl,
		laypage=layui.laypage,
		form=layui.form,
		element = layui.element,
		table = layui.table;

	table.render({
		elem: '#order_tb'
		,url:'/user_center/orders'
		,cols: [[
			{field:'orderId', title:'订单编号', width:120, fixed: 'left',unresize: true,  sort: true}
			,{field:'address_info', title:'收货人姓名', width:120, templet: function(res){
					return res.address_info ? res.address_info.recipient : '无';
				}}
			,{field:'address_info', title:'收货地址', width:200, templet: function(res){
					const addr = res.address_info;
					return addr ? `${addr.province}${addr.city}${addr.district}${addr.address}` : '无';
				}}
			,{field:'address_info', title:'邮政编码', width:100, templet: function(res){
					return res.address_info ? res.address_info.zipcode : '无';
				}}
			,{field:'address_info', title:'联系方式', width:120, templet: function(res){
					return res.address_info ? res.address_info.phone : '无';
				}}
			,{field:'status', title:'订单状态',width:90,templet: function(res){
					if(res.status){
						return '<span class="layui-badge layui-bg-blue">已发货</span>';
					}
					return '<span class="layui-badge">待发货</span>';
				}}
			,{field:'createTime', title:'创建时间', width:180,
				templet: function (res) {
					let date = new Date(res.createTime);
					let year = date.getFullYear();
					let month = String(date.getMonth() + 1).padStart(2, '0');
					let day = String(date.getDate()).padStart(2, '0');
					let hours = String(date.getHours()).padStart(2, '0');
					let minutes = String(date.getMinutes()).padStart(2, '0');
					let seconds = String(date.getSeconds()).padStart(2, '0');
					return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
				}}
			,{fixed: 'right', title:'操作', toolbar: '#order_tb_bar', width:160}
		]]
		,page: true
		,limit:5
		,limits:[5,15,30,60,100]
		,parseData(res){
			return {
				"code":res.code,
				"msg":res.message,
				"count":res.count,
				"data":res.data
			}
		}
	});


	//监听行工具事件
	table.on('tool(order_tb)', function(obj){
		var data = obj.data;
		if(obj.event === 'del'){
			layer.confirm('真的删除该订单记录么', function(index){
				$.ajax({
					url:'/user_center/orders/'+data.orderId,
					type:'delete',
					dataType:'json',
					success:function (res) {
						if(res.code!=0){
							return layer.msg(res.message, {icon: 2});
						}
						return layer.msg("删除成功", {icon: 1},function () {
							obj.del();
							$("#order-items").html("");
						});
					},
					error:function () {
						return layer.msg("服务器错误,请稍后再试", {icon: 2});
					}
				});
				layer.close(index);
			});
		} else if(obj.event === 'detail'){
			//渲染订单明细
			laytpl($("#order-item-tpl").html()).render(data.orderItems,function(html){
				$("#order-items").html(html);
			});
		}
	});


});