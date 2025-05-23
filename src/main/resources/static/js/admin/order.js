layui.use(['element', 'jquery', 'layer', 'laytpl', 'laypage', 'form', 'table'], function() {
	var $ = layui.jquery,
		layer = layui.layer,
		laytpl = layui.laytpl,
		laypage = layui.laypage,
		form = layui.form,
		element = layui.element,
		table = layui.table;

	var orderTb=table.render({
		elem: '#order_tb',
		url: '/order/list',
		cols: [
			[{
				field: 'orderId',
				title: '订单编号',
				width: 120,
				fixed: 'left',
				unresize: true,
				sort: true
			}, {
				title: '收货人姓名',  // 修改为使用templet
				width: 120,
				templet: function(d){
					return d.address_info?.recipient || '';
				}
			}, {
				title: '收货地址',
				width: 220,
				templet: function(d){
					const addr = d.address_info;
					return addr ? `${addr.province}${addr.city}${addr.district}${addr.address}` : '';
				}
			}, {
				title: '邮政编码',  // 修改为使用templet
				width: 100,
				sort: true,
				templet: function(d){
					return d.address_info?.zipcode || '';
				}
			}, {
				title: '联系方式',  // 修改为使用templet
				width: 120,
				templet: function(d){
					return d.address_info?.phone || '';
				}
			}, {
				field: 'status',
				title: '订单状态',
				width: 90,
				templet: function(res) {
					if (res.status) {
						return '<span class="layui-badge layui-bg-blue">已发货</span>';
					}
					return '<span class="layui-badge">待发货</span>';
				}
			}, {
				field: 'createTime',
				title: '创建时间',
				width: 120
			}, {
				fixed: 'right',
				title: '操作',
				toolbar: '#order_tb_bar',
				width: 200
			}]
		],
		page: true,
		height: 450,
		parseData: function (res) {
			console.log('原始数据:', res);  // 检查原始响应数据
			console.log('第一条数据的address_info:', res.data?.[0]?.address_info);  // 检查第一条数据的address_info
			return {
				"code": res.code,
				"msg": res.message,
				"count": res.count,
				"data": res.data
			}
		}
	});


	//监听行工具事件
	table.on('tool(order_tb)', function(obj) {
		var data = obj.data;
		if (obj.event === 'del') {
			layer.confirm('真的删除行么', function(index) {
				$.ajax({
					url:'/order/'+data.orderId,
					type:'delete',
					dataType:'json',
					success:function (res) {
						if(res.code!=0){
							return layer.msg(res.message, {icon: 2});
						}
						return layer.msg("删除成功", {icon: 1,time: 1300},function () {
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
		} else if (obj.event === 'detail') {
			//渲染订单明细
			laytpl($("#order-item-tpl").html()).render(
				data.orderItems,
				function(html) {
					console.log(html)
					$("#order-items").html(html);
				}
			);
		} else if (obj.event === 'ship') {
			layer.confirm('确认发货吗？', function(index) {
				$.ajax({
					url: '/order/ship',
					type: 'POST',
					data: JSON.stringify({orderId: data.orderId}),
					contentType: 'application/json',
					success: function(res) {
						if (res.code != 0) {
							return layer.msg(res.message, {icon: 2});
						}
						layer.msg("发货成功", {icon: 1});
						obj.update({
							status: true
						});
					},
					error: function() {
						layer.msg("服务器错误", {icon: 2});
					}
				});
				layer.close(index);
			});
		}
	});

	//搜索
	var order_tb_this;
	form.on('submit(search_btn)', function(data) {
		if (order_tb_this != null) {
			order_tb_this.where = {};
		}
		orderTb.reload({
			url: '/order/search',
			where: data.field,
			page: {
				curr: 1
			},
			done: function() {
				order_tb_this = this;
			}
		});
		return false;
	});


});
