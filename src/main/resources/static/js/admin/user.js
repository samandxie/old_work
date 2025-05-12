layui.use(['table', 'form', 'jquery', 'layer', 'element'], function() {
	let table = layui.table,
		$ = layui.jquery,
		layer = layui.layer,
		element =layui.element,
		form = layui.form;

	let user_tb = table.render({
		elem: '#user_tb',
		url: '/user/list',
		cols: [
			[{
				field: 'userId',
				title: 'ID',
				fixed: 'left',
				sort: true
			}, {
				field: 'userName',
				title: '用户名',
			}, {
				field: 'email',
				title: '邮箱',
				templet: function(res) {
					return '<em>' + res.email + '</em>';
				}
			}, {
				field: 'joinTime',
				title: '注册时间',
				templet: function (res) {
					let date = new Date(res.joinTime);
					let year = date.getFullYear();
					let month = String(date.getMonth() + 1).padStart(2, '0');
					let day = String(date.getDate()).padStart(2, '0');
					let hours = String(date.getHours()).padStart(2, '0');
					let minutes = String(date.getMinutes()).padStart(2, '0');
					let seconds = String(date.getSeconds()).padStart(2, '0');
					return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
				}
			}, {
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

	//表单验证
	form.verify({
		password: function (value, elem) {
			if (value.length < 6) {
				return '密码不能小于6位';
			}
		}
	})


	//监听行工具事件
	table.on('tool(user_tb)', function(obj) {
		var data = obj.data;
		if (obj.event === 'del') {
			layer.confirm('真的删除行么', {
				icon: 3
			}, function(index) {
				$.ajax({
					url: '/user/'+data.userId,
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
				title: '编辑用户',
				content: $("#user_form_tmpl").html(),
				area: ['400px'],
				btn: ['更新'],
				yes: function(index1) {
					let new_data=form.val("user-form");
					console.log(new_data);
					$.ajax({
						url: '/user/updateByAdmin',
						type: 'PUT',
						data: JSON.stringify(new_data),
						contentType: 'application/json',
						dataType: 'json',
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
					form.val("user-form", data);
				}
			});
		}
	});

	
	//搜索
	var user_tb_this;
	form.on('submit(search_btn)', function(data) {
		if (user_tb_this != null) {
			user_tb_this.where = {};
		}
		user_tb.reload({
			url: '/user/search',
			where: data.field,
			page: {
				curr: 1
			},
			done: function() {
				user_tb_this = this;
				console.log(data)
			}
		});
		return false;
	});

});
