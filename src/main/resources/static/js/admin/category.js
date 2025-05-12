layui.use(['table', 'form', 'jquery', 'layer','element'], function() {
	var table = layui.table,
		$ = layui.jquery,
		layer = layui.layer,
		element =layui.element,
		form = layui.form;

	var category_tb = table.render({
		elem: '#category_tb',
		url: '/category',
		cols: [
			[{
				field: 'categoryCode',
				title: '分类Code',
				// width: 120,
				fixed: 'left',
				sort: true
			}, {
				field: 'categoryName',
				title: '分类名',
				// width: 120
			},{
				fixed: 'right',
				title: '操作',
				toolbar: '#barDemo',
				width: 150
			}]
		],
		page: true,
		height: 500,
		parseData: function (res) {
			console.log(res)
			return {
				"code": res.code,
				"msg": res.message,
				"count": res.count,
				"data": res.data
			}
		}
	});

	


	//监听行工具事件
	table.on('tool(category_tb)', function(obj) {
		var data = obj.data;
		if (obj.event === 'del') {
			layer.confirm('真的删除行么', {
				icon: 3
			}, function(index) {
				$.ajax({
					url: '/category/' + data.categoryCode,
					type: 'DELETE',
					dataType: 'json',
					success: function(res) {
						if (res.code != 0) {
							return layer.msg("删除失败：" + res.message, {
								icon: 2
							});
						}
						return layer.msg("删除成功", {
							icon: 1,
							time: 1300
						}, function() {
							obj.del();
						});
					}
				});
				layer.close(index);
			});
		} else if (obj.event === 'edit') {
			layer.open({
				type: 1,
				title: '编辑分类',
				content: $("#category_form_tmpl").html(),
				area: ['350px'],
				btn: ['更新'],
				yes: function(index1) {
					let new_data=form.val("category-update-form");
					console.log(new_data);
					$.ajax({
						url: '/category',
						type: 'put',
						data: JSON.stringify(new_data),
						dataType: 'json',
						contentType: 'application/json',
						success: function(res) {
							if (res.code != 0) {
								return layer.msg(res.message, {
									icon: 2
								});
							}
							return layer.msg("更新成功", {
								icon: 1,
								time: 1300
							}, function() {
								obj.update(new_data);
								layer.close(index1);
							});
						}
					});
				},
				success: function() {
					//填充表单（编辑状态）
					form.val("category-update-form", data);
				}
			});
		}
	});

	//添加
	form.on('submit(add_btn)', function(data) {
		var data=data.field;
		console.log(data);
		$.post("/category",data,function (res) {
			console.log(res);
			if (res.code != 0) {
				return layer.msg(res.message, {
					icon: 2
				});
			}
			return layer.msg("更新成功", {
				icon: 1,
				time: 1300
			}, function() {
				category_tb.reload();
			});
		})
		return false;
	});

});
