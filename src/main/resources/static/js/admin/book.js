layui.use(['table', 'form', 'jquery', 'layer', 'upload','element'], function() {
	let table = layui.table,
		$ = layui.jquery,
		layer = layui.layer,
		upload = layui.upload,
		element = layui.element,
		form = layui.form;

	let book_tb = table.render({
		elem: '#book_tb',
		url: '/book/list',
		cols: [
			[{
				field: 'bookId',
				title: 'ID',
				width: 110,
				fixed: 'left',
				sort: true
			}, {
				field: 'bookName',
				title: '书名',
				width: 120
			}, {
				field: 'categoryName',
				title: '分类',
				width: 80,
				templet: function (res) {
					return '<span class="layui-badge-rim">' + res.category.categoryName + '</span>';
				}
			}, {
				field: 'description',
				title: '简介',
				width: 120
			}, {
				field: 'isbn',
				title: 'ISBN',
				width: 120,
				templet: function (res) {
					return '<em>' + res.isbn + '</em>'
				}
			}, {
				field: 'press',
				title: '出版社',
				width: 120
			}, {
				field: 'author',
				title: '作者',
				width: 100
			}, {
				field: 'pubDate',
				title: '出版日期',
				width: 120
			}, {
				field: 'price',
				title: '价格',
				width: 80
			}, {
				field: 'stock',
				title: '库存',
				width: 80
			}, {
				field: 'createTime',
				title: '上架时间',
				width: 120
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


	//监听行工具事件
	table.on('tool(book_tb)', function (obj) {
		let data = obj.data;
		console.log(data)
		if (obj.event === 'del') {
			layer.confirm('确认删除行？', {
				icon: 3
			}, function (index) {
				$.ajax({
					url: '/book/' + data.bookId,
					type: 'DELETE',
					dataType: 'json',
					success: function (res) {
						if (res.code != 0) {
							return layer.msg("删除失败：" + res.message, {
								icon: 2
							});
						}
						return layer.msg("删除成功", {
							icon: 1,
							time: 1300
						}, function () {
							obj.del();
						});
					}
				});
				layer.close(index);
			});
		} else if (obj.event === 'edit') {
			layer.open({
				type: 1,
				title: '编辑书籍',
				content: $("#book_form_tmpl").html(),
				area: ['500px'],
				btn: ['更新'],
				yes: function (index1) {
					let new_data = form.val("book-form");
					console.log(new_data)
					$.ajax({
						url: '/book/update',
						type: 'put',
						data: JSON.stringify(new_data),
						dataType: 'json',
						contentType: 'application/json',
						success: function (res) {
							if (res.code != 0) {
								return layer.msg(res.message, {
									icon: 2
								});
							}
							return layer.msg("更新成功", {
								icon: 1,
								time: 1300
							}, function () {
								obj.update(new_data);
								layer.close(index1);
							});
						}
					});
				},
				success: function () {
					form.render(null, "book-form");
					$("#bookId").attr("disabled", true);
					$.getJSON("/category", function (res) {
						if (res.code != 0) {
							return;
						}
						$.each(res.data, function (index, item) {
							$("#categoryCode").append('<option value="' + item.categoryCode + '">' + item.categoryName +
								'</option> ');
						});
						//填充表单（编辑状态）
						form.val("book-form", data);
						form.render();
					});
				}
			});
		}
	});


	//搜索
	let book_tb_this
	form.on('submit(search_btn)', function (data) {
		console.log(data.field)
		if (book_tb_this != null) {
			book_tb_this.where = {};
		}
		book_tb.reload({
			url: '/book/search',
			where: data.field,
			page: {
				curr: 1
			},
			done: function () {
				book_tb_this = this;
			}
		});
		return false;
	})
})
