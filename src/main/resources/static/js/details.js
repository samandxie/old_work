layui.use(['element', 'jquery', 'layer', 'laytpl','laypage','form','table'], function() {
	var $ = layui.jquery,
		layer = layui.layer,
		laytpl = layui.laytpl,
		laypage=layui.laypage,
		form=layui.form,
		element = layui.element,
		table = layui.table;
		
		//从url路径中获取书籍ID
		var str=window.location.pathname;
		var bookId=str.substring(str.lastIndexOf("/")+1);

		//立即购买提交按钮
		$("#buyNowBtn").click(function(){
			let data=[];
			let quantity=$("#quantity").val();
			if(quantity==0){
				return layer.msg('至少要一件才能购买哦',{icon:5});
			}
			let price=$("#price").text();
			data.push({
				'cartId':null,
				'bookId':bookId,
				'price':parseFloat(price),
				'quantity':quantity
			});
			console.log(data);
			order_submit_popup(data);
			
		});
		
		//添加购物车提交按钮
		$("#addCartBtn").click(function(){
			let quantity=$("#quantity").val();
			if(quantity==0){
				return layer.msg('至少要购买一件才能添加购物车哦',{icon:5});
			}
			let price=$("#price").text();
			let data={
				'bookId':bookId,
				'price':parseFloat(price),
				'quantity':quantity
			};
			$.post('/cart/list', data, function (res) {
				if (res.code != 0) {
					return layer.msg(res.message,{icon:2});
				}
				return layer.msg("添加成功,请前往购物车查看",{icon:1});
			});
		});
		
		//购买数量输入框监听
		window.check=function(obj,price){
			var qty=$(obj).val();
			if(qty<0||qty==''){
				layer.msg("输入必须大于或者等于0");
				$(obj).val(0);
				qty=0;
			}
			if(qty>10){
				layer.msg("每个用户限购10件");
				$(obj).val(10);
				qty=10;
			}
			$("#totalAccount").text("¥"+Math.floor(parseFloat(price*100 *qty))/100);
		}
});


function order_submit_popup(data) {
    // 获取用户地址列表
    $.get('/addresses/list', function(res) {
        if(!res || res.length === 0) {
            return layer.msg('您还没有添加收货地址，请先添加', {icon: 2});
        }
        
        layui.laytpl($('#oder-popup-tpl').html()).render({
            addresses: res
        }, function(html) {
            var index = layer.open({
                type: 1,
                title: '选择收货地址',
                content: html,
                area: ['600px', '300px'],
                success: function(layero, index) {
                    layui.form.render();
                    
                    // 表单提交处理
                    layui.form.on('submit(order_submit)', function(formData){
                        // 合并订单数据和地址ID
                        var orderData = {
                            bookId: data[0].bookId,
                            quantity: data[0].quantity,
                            price: data[0].price,
                            addressId: formData.field.addressId
                        };
                        
                        // 提交订单
                        $.post('/order/create', orderData, function(res) {
                            if(res.code != 0) {
                                return layer.msg(res.message, {icon: 2});
                            }
                            layer.close(index);
                            layer.msg('订单创建成功', {icon: 1});
                            // 可以跳转到订单详情页
                            // window.location.href = '/order/' + res.data.orderId;
                        });
                        return false;
                    });
                    
                    // 添加新地址按钮点击事件
                    $('#addNewAddress').click(function() {
                        layer.close(index);
                        // 这里可以调用添加新地址的表单
                        addNewAddressForm();
                    });
                }
            });
        });
    });
}

