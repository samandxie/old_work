layui.use(['element', 'jquery', 'layer', 'laytpl', 'laypage', 'form'], function () {
    let $ = layui.jquery,
        layer = layui.layer,
        laytpl = layui.laytpl,
        laypage = layui.laypage,
        form = layui.form,
        element = layui.element;


    //订单信息填写弹窗,并提交订单
    /**
     * 参数orderItemData的要求[{'cartId':'','bookId':'','price':'','quantity':''}]
     * @param {Object} orderItemData
     */
    window.order_submit_popup = function (orderItemData) {
        $.getJSON("/checkLogIn", function (res) {
            if(res.code!==0){
                return layer.msg(res.message, {icon: 2});
            }

            // 获取地址列表
            $.get('/address/list', function(addressRes) {
                if(!addressRes || !addressRes.data || addressRes.data.length === 0) {
                    return layer.msg('您还没有添加收货地址，请先添加', {icon: 2});
                }

                // 创建卡片式地址选择容器
                let content = `
                    <div style="padding: 20px;">
                        <h3 style="margin-bottom: 20px; font-size: 16px; font-weight: bold;">请选择收货地址</h3>
                        <div id="addressCardsContainer" style="max-height: 300px; overflow-y: auto;">
                `;

                // 添加地址卡片
                addressRes.data.forEach(address => {
                    content += `
                        <div class="address-card-select" 
                             data-id="${address.id}"
                             style="padding: 15px; margin-bottom: 10px; border: 1px solid #eee; border-radius: 4px; cursor: pointer; ${address.isDefault ? 'border-color: #1E88E5; background-color: #f5f9ff;' : ''}">
                            <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                                <span style="font-weight: bold;">${address.recipient}</span>
                                <span>${address.phone}</span>
                                ${address.isDefault ? '<span style="color: #1E88E5; font-size: 12px;">默认地址</span>' : ''}
                            </div>
                            <div style="color: #666; margin-bottom: 5px;">
                                ${address.province}${address.city}${address.district}${address.address}
                            </div>
                            <div style="font-size: 12px; color: #999;">
                                邮编: ${address.zipcode}
                            </div>
                        </div>
                    `;
                });

                content += `
                        </div>
                        <div style="margin-top: 20px; text-align: center;">
                            <button id="addNewAddressBtn" class="layui-btn layui-btn-primary" style="margin-right: 10px;">添加新地址</button>
                            <button id="confirmOrderBtn" class="layui-btn layui-btn-normal">确认订单</button>
                        </div>
                    </div>
                `;

                // 打开弹窗
                const index = layer.open({
                    type: 1,
                    title: '选择收货地址',
                    content: content,
                    area: ['600px', '500px'],
                    success: function(layero, index) {
                        // 地址卡片点击事件
                        $('.address-card-select').on('click', function() {
                            $('.address-card-select').css({
                                'border-color': '#eee',
                                'background-color': 'transparent'
                            });
                            $(this).css({
                                'border-color': '#1E88E5',
                                'background-color': '#f5f9ff'
                            });
                            layero.find('#confirmOrderBtn').data('addressId', $(this).data('id'));
                        });

                        // 默认选中第一个地址或默认地址
                        const defaultCard = addressRes.data.find(a => a.isDefault) || addressRes.data[0];
                        if(defaultCard) {
                            $(`.address-card-select[data-id="${defaultCard.id}"]`).trigger('click');
                        }

                        // 添加新地址按钮
                        $('#addNewAddressBtn').on('click', function() {
                            layer.close(index);
                            showAddAddressForm(orderItemData);
                        });

                        // 确认订单按钮
                        $('#confirmOrderBtn').on('click', function() {
                            const addressId = $(this).data('addressId');

                            // 构建订单数据
                            const orderData = {
                                addressId: addressId,
                                orderItems: orderItemData.map(item => ({
                                    bookId: item.bookId.toString(), // 确保bookId是字符串
                                    price: parseFloat(item.price).toFixed(2), // 保留两位小数
                                    quantity: parseInt(item.quantity)
                                })),
                                totalAmount: parseFloat(calculateTotal(orderItemData)).toFixed(2) // 总金额保留两位小数
                            };

                            console.log('准备提交订单，订单数据:', orderData);

                            $.ajax({
                                url: '/order/submit',
                                type: 'post',
                                contentType: 'application/json',
                                data: JSON.stringify(orderData),
                                success: function(orderRes) {
                                    console.log('订单提交成功，响应数据:', orderRes);
                                    if(orderRes.code !== 0) {
                                        return layer.msg(orderRes.message, {icon: 2});
                                    }
                                    // 订单提交成功后，发起支付请求
                                    console.log('准备发起支付请求，订单数据:', orderData);
                                    $.ajax({
                                        url: '/api/alipay/pay',
                                        type: 'post',
                                        contentType: 'application/json',
                                        data: JSON.stringify(orderData),
                                        // 移除 dataType: 'json'
                                        success: function(res) {
                                            console.log('支付请求成功，响应数据:', res);
                                            // 处理支付宝返回的表单
                                            const div = document.createElement('div');
                                            div.innerHTML = res;
                                            document.body.appendChild(div);
                                            const form = document.forms['punchout_form'];
                                            if (form) {
                                                form.submit();
                                            } else {
                                                console.error('未找到支付宝表单元素');
                                                layer.msg('支付表单加载失败，请稍后重试', {icon: 2});
                                            }
                                        },
                                        error: function(xhr) {
                                            console.log('支付请求出错，错误信息:', xhr);
                                            let msg = '支付请求失败';
                                            if (xhr.responseJSON && xhr.responseJSON.message) {
                                                msg = xhr.responseJSON.message;
                                            } else if (xhr.responseText) {
                                                try {
                                                    const res = JSON.parse(xhr.responseText);
                                                    msg = res.message || msg;
                                                } catch (e) {
                                                    msg = xhr.responseText;
                                                }
                                            }
                                            layer.msg(msg, {icon: 2});
                                        }
                                    });
                                },
                                error: function(xhr) {
                                    console.log('订单提交出错，错误信息:', xhr);
                                    let msg = '服务器错误';
                                    if (xhr.responseJSON && xhr.responseJSON.message) {
                                        msg = xhr.responseJSON.message;
                                    } else if (xhr.responseText) {
                                        try {
                                            const res = JSON.parse(xhr.responseText);
                                            msg = res.message || msg;
                                        } catch (e) {
                                            msg = xhr.responseText;
                                        }
                                    }
                                    layer.msg(msg, {icon: 2});
                                }
                            });

                            // 计算总金额函数
                            function calculateTotal(items) {
                                return items.reduce((total, item) => {
                                    return total + (item.price * item.quantity);
                                }, 0);
                            }
                        });
                    }
                });
            });
        });
    }





    /**
     * 计算总价格
     * @param {Object} price
     * @param {Object} quantity
     */
    window.calculate = function (price, quantity) {
        return Math.floor(parseFloat(price * 100 * quantity)) / 100;
    }

    // 新增：显示添加地址表单
function showAddAddressForm(orderItemData) {
    // 这里使用与用户中心相同的地址表单
    const formContent = `
        <form class="layui-form" lay-filter="addressForm" style="padding: 20px;">
            <div class="layui-form-item">
                <label class="layui-form-label">收货人</label>
                <div class="layui-input-block">
                    <input type="text" name="recipient" required lay-verify="required" placeholder="请输入收货人姓名" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">手机号码</label>
                <div class="layui-input-block">
                    <input type="text" name="phone" required lay-verify="required|phone" placeholder="请输入手机号码" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">省份</label>
                <div class="layui-input-block">
                    <select name="province" lay-verify="required" lay-filter="province" id="province">
                        <option value="">请选择省份</option>
                    </select>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">城市</label>
                <div class="layui-input-block">
                    <select name="city" lay-verify="required" lay-filter="city" id="city">
                        <option value="">请选择城市</option>
                    </select>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">区县</label>
                <div class="layui-input-block">
                    <select name="district" lay-verify="required" id="district">
                        <option value="">请选择区县</option>
                    </select>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">详细地址</label>
                <div class="layui-input-block">
                    <textarea name="address" required lay-verify="required" placeholder="请输入详细地址" class="layui-textarea"></textarea>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">邮政编码</label>
                <div class="layui-input-block">
                    <input type="text" name="zipcode" lay-verify="zip" placeholder="请输入邮政编码" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <div class="layui-input-block">
                    <input type="checkbox" name="isDefault" lay-skin="primary" title="设为默认地址">
                </div>
            </div>
            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button class="layui-btn" lay-submit lay-filter="addressSubmit">保存地址</button>
                    <button type="button" class="layui-btn layui-btn-primary" id="cancelAddAddress">取消</button>
                </div>
            </div>
        </form>
    `;

    const addAddressIndex = layer.open({
        type: 1,
        title: '添加新地址',
        content: formContent,
        area: ['600px', '600px'],
        success: function(layero, index) {
            // 初始化省份数据
            loadProvinces();

            // 省份选择变化事件
            form.on('select(province)', function(data){
                if(data.value) {
                    loadCities(data.value);
                    // 清空区县
                    document.getElementById('district').innerHTML = '<option value="">请选择区县</option>';
                    form.render('select');
                }
            });

            // 城市选择变化事件
            form.on('select(city)', function(data){
                if(data.value) {
                    loadDistricts(data.value);
                }
            });

            // 表单提交
            form.on('submit(addressSubmit)', function(data) {
                $.ajax({
                    url: '/address/add',
                    type: 'post',
                    contentType: 'application/json',
                    data: JSON.stringify(data.field),
                    dataType: 'json',
                    success: function(res) {
                        if(res.code === 0) {
                            layer.close(addAddressIndex);
                            layer.msg('地址添加成功', {icon: 1});
                            // 重新打开订单提交弹窗
                            order_submit_popup(orderItemData);
                        } else {
                            layer.msg(res.message, {icon: 2});
                        }
                    },
                    error: function() {
                        layer.msg('服务器错误，请稍后再试', {icon: 2});
                    }
                });
                return false;
            });

            // 取消按钮
            $('#cancelAddAddress').on('click', function() {
                layer.close(addAddressIndex);
                // 重新打开订单提交弹窗
                order_submit_popup(orderItemData);
            });
        }
    });
}

// 加载省份数据
function loadProvinces() {
    const AMAP_KEY = '295327d592345ddcc61310c4608475a2'; // 替换为实际的高德地图KEY
    fetch(`https://restapi.amap.com/v3/config/district?key=${AMAP_KEY}&keywords=中国&subdistrict=1`)
        .then(response => response.json())
        .then(data => {
            if(data.status === '1' && data.districts && data.districts[0].districts) {
                const provinceSelect = document.getElementById('province');
                provinceSelect.innerHTML = '<option value="">请选择省份</option>';

                data.districts[0].districts.forEach(province => {
                    const option = document.createElement('option');
                    option.value = province.name;
                    option.textContent = province.name;
                    provinceSelect.appendChild(option);
                });
                form.render('select');
            }
        })
        .catch(error => {
            console.error('获取省份数据失败:', error);
            layer.msg('获取省份数据失败', {icon: 2});
        });
}

// 加载城市数据
function loadCities(provinceName) {
    const AMAP_KEY = '295327d592345ddcc61310c4608475a2'; // 替换为实际的高德地图KEY
    fetch(`https://restapi.amap.com/v3/config/district?key=${AMAP_KEY}&keywords=${encodeURIComponent(provinceName)}&subdistrict=1`)
        .then(response => response.json())
        .then(data => {
            if(data.status === '1' && data.districts && data.districts[0].districts) {
                const citySelect = document.getElementById('city');
                citySelect.innerHTML = '<option value="">请选择城市</option>';

                data.districts[0].districts.forEach(city => {
                    const option = document.createElement('option');
                    option.value = city.name;
                    option.textContent = city.name;
                    citySelect.appendChild(option);
                });
                form.render('select');
            }
        })
        .catch(error => {
            console.error('获取城市数据失败:', error);
            layer.msg('获取城市数据失败', {icon: 2});
        });
}

// 加载区县数据
function loadDistricts(cityName) {
    const AMAP_KEY = '295327d592345ddcc61310c4608475a2'; // 替换为实际的高德地图KEY
    fetch(`https://restapi.amap.com/v3/config/district?key=${AMAP_KEY}&keywords=${encodeURIComponent(cityName)}&subdistrict=1`)
        .then(response => response.json())
        .then(data => {
            if(data.status === '1' && data.districts && data.districts[0].districts) {
                const districtSelect = document.getElementById('district');
                districtSelect.innerHTML = '<option value="">请选择区县</option>';

                data.districts[0].districts.forEach(district => {
                    const option = document.createElement('option');
                    option.value = district.name;
                    option.textContent = district.name;
                    districtSelect.appendChild(option);
                });
                form.render('select');
            }
        })
        .catch(error => {
            console.error('获取区县数据失败:', error);
            layer.msg('获取区县数据失败', {icon: 2});
        });
}

})

