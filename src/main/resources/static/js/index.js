layui.use(['element', 'jquery', 'layer', 'laytpl','laypage','form','carousel'], function() {
	var $ = layui.jquery,
		layer = layui.layer,
		laytpl = layui.laytpl,
		laypage=layui.laypage,
		element = layui.element,
		form = layui.form,
		carousel = layui.carousel;
		
	// 初始化轮播图
	carousel.render({
		elem: '#banner',
		width: '60%',
		height: '300px',
		interval: 3000
	});
	
	// 加载猜你喜欢
	function loadRecommendBook() {
		$.getJSON("/index/books/likes", {limit: 1}, function(res){
			if(res.code === 0 && res.count > 0){
				var dataArray = [res.data];
				laytpl($("#book-card-tpl").html()).render(dataArray, function(html){
					$("#likes-list").html(html);
				});
			}
		});
	}

	// 初始加载猜你喜欢
	loadRecommendBook();

	// 点击换一个按钮
	$("#change-book-btn").click(function(){
		loadRecommendBook();
	});

	//请求加载分类信息，渲染选项卡
	$.getJSON("/category",function(res){
		if(res.code!==0){
			$("#content").html(res.message);
			return ;
		}

		$.each(res.data,function(index,item){
			let str='<li c-code="'+item.categoryCode+'">'+item.categoryName+'</li>';
			$("#category_tag").append(str);
			console.log(item);
		});
		//请求全部书籍
		getBooksByPage('/index/books',{page:1,limit:10});
	});

	/**
	 * 监听切换分类选项卡的切换
	 */
	element.on('tab(categoryTabBrief)', function(data) {
		// 保存当前滚动位置
		var scrollPosition = $(window).scrollTop();
		
		if(!($(this).attr('lay-id')==='search')){
			//获取分类代码
			let code=$(this).attr("c-code");
			// 先设置透明度，然后加载内容
			$("#content").css('opacity', '0');
			getBooksByPage('/index/books',{page:1,limit:10,categoryCode:code}, scrollPosition);
		}else {
			$("#content").css('opacity', '0');
			$("#content").html('');
			$("#content").css('opacity', '1');
			$(window).scrollTop(scrollPosition);
		}
	});

	// 加载新书上架
	$.getJSON("/index/books/new", {limit: 5}, function(res){
		console.log(['新书上架', res])
		if(res.code === 0){
			laytpl($("#book-card-tpl").html()).render(res.data, function(html){
				$("#new-books-list").html(html);
			});
		}
	});





	//排行榜
	$.getJSON("/index/books/ranking",{limit: 5}, function(res){
		if(res.code === 0){
			var html = '<ul class="ranking-list">';
			$.each(res.data, function(index, item){
				html += '<li class="ranking-item">';
				html += '<a href="/index/books/details/'+item.bookId+'">';
				html += '<span class="rank-number">'+(index+1)+'</span>';
				html += '<span class="rank-name">'+item.bookName+'</span>';
				html += '</a>';
				html += '</li>';
			});
			html += '</ul>';
			$("#ranking-books-list").html(html);
		}
	});




	// 搜索提示功能
	var timer = null;
	$("#keyword-input").on('input', function(){
		var keyword = $(this).val().trim();
		if(keyword.length === 0) {
			$("#suggestions").remove();
			return;
		}
		
		// 防抖处理
		clearTimeout(timer);
		timer = setTimeout(function(){
			$.getJSON("/index/search/suggestions", {keyword: keyword}, function(res){
				if(res.code === 0 && res.data.length > 0){
					var inputWidth = $("#keyword-input").width();
					var html = '<div id="suggestions" class="layui-card" style="position: absolute; width: ' + inputWidth + 'px; top: 100%; left: 0; margin-top: 5px; display: none; z-index: 9999; background: #fff; box-shadow: 0 2px 4px rgba(0,0,0,.12);">';
					html += '<div class="layui-card-body" style="padding: 5px;">';
					$.each(res.data, function(index, item){
						html += '<div class="suggestion-item" style="padding: 5px; cursor: pointer; line-height: 1.5; border-bottom: 1px solid #f2f2f2;">' + item.bookName + '</div>';
					});
					html += '</div></div>';
					
					$("#suggestions").remove();
					$("#keyword-input").parent().css('position', 'relative').append(html);
					$("#suggestions").fadeIn();
					
					// 点击建议项
					$(".suggestion-item").on('click', function(){
						$("#keyword-input").val($(this).text());
						$("#suggestions").remove();
						$("#search-btn").click();
					});
				} else {
					$("#suggestions").remove();
				}
			});
		}, 300);
	});
	
	// 点击其他地方关闭建议框
	$(document).on('click', function(e){
		if(!$(e.target).closest('#suggestions').length && !$(e.target).is('#keyword-input')){
			$("#suggestions").remove();
		}
	});
	
	//搜索
	$("#search-btn").click(function(){
		element.tabChange('categoryTabBrief', 'search');
		let bookName=$("#keyword-input").val().trim();
		if(bookName.length==0){
			return;
		}
		getBooksByPage('/index/book/search',{page:1,limit:10,bookName:bookName});
	});

	/**
	 * 请求分页查询
	 * @param url
	 * @param param
	 * @param scrollPosition 滚动位置
	 */
	function getBooksByPage(url,param, scrollPosition) {
		param['page']=param.page||1;
		param['limit']=param.limit||10;
		$.getJSON(url,param,function (result) {
			if(result.code!=0){
				$("#content").html('<div style="text-align: center;font-size: 20px;">'+result.msg+'</div>');
				$("#content").css('opacity', '1');
				$(window).scrollTop(scrollPosition);
				return ;
			}
			if(result.data.length==0){
				$("#content").html('<div style="text-align: center;font-size: 20px;">暂时没有数据</div>');
				$("#content").css('opacity', '1');
				$(window).scrollTop(scrollPosition);
				return ;
			}
			laytpl($("#book-card-tpl").html()).render(result.data,function(html){
				$("#content").html(html);
				$("#content").css('opacity', '1');
				$(window).scrollTop(scrollPosition);
			});
			//调用分页
			laypage.render({
				elem: 'page-util'
				,count: result.count
				,curr: param.page||1
				,limit: param.limit||10
				,jump: function(obj,first){
					if(!first){
						param.page=obj.curr;
						param.limit=obj.limit;
						getBooksByPage(url, param, scrollPosition);
					}
				}
			});
		})
	}

	let isDragging = true;
	// 新增：定义systemMessage并添加到消息历史数组
	const systemMessage = { role:'system', content: '假设你是一个网上书店的ai助理，请直接回复用户的问题，并向推销书籍上引导' };
	let messageHistory = [systemMessage];
	$('#ai-chat-container').draggable({
		handle: '#ai-chat-icon',
		cursor: 'move',
		start: function() {
			isDragging = true;},
		stop: function() {
			// 确保拖动结束后一段时间内 isDragging 为 true
			setTimeout(() => isDragging = false, 200);
		}
	});
	// 添加拖放事件处理
	$('#ai-chat-input').on('dragover', function(e) {
		e.preventDefault();
		$(this).css('background', '#f0f0f0');
	}).on('dragleave', function(e) {
		e.preventDefault();
		$(this).css('background', '');
	}).on('drop', function(e) {
		e.preventDefault();
		$(this).css('background', '');
		const href = e.originalEvent.dataTransfer.getData('text/uri-list');
		if (href && href.includes('/index/books/details/')) {
			const htmlData = e.originalEvent.dataTransfer.getData('text/html');
			const parser = new DOMParser();
			const doc = parser.parseFromString(htmlData, 'text/html');
			const img = doc.querySelector('img');
			if (img) {
				const bookName = img.getAttribute('alt');
				$(this).val(`介绍一下《${bookName}》这本书`);
			}
		}
	});
	$('#ai-chat-icon').click(function(e) {
		// 确保点击时 isDragging 为 false
		if (!isDragging) {
			$('#ai-chat-box').toggle();
		}
	});
	$('#ai-chat-close').click(function() {
		$('#ai-chat-box').hide();
	});
	async function sendMessage() {
		const msg = $('#ai-chat-input').val().trim();
		if (!msg) return;
		$('#ai-chat-content').append(`<div style="text-align:right;margin-bottom:5px;">${msg}</div>`);
		$('#ai-chat-input').val('');
		// 新增：将用户消息添加到历史记录中
		messageHistory.push({ role: 'user', content: msg });
		const responseId = 'response-' + Date.now();
		$('#ai-chat-content').append(`<div id="${responseId}" style="color:#1E88E5;margin-bottom:5px;">AI助手：</div>`);
		$('#ai-chat-content').scrollTop($('#ai-chat-content')[0].scrollHeight);
		try {
			const response = await fetch('/api/deepseek-stream', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				// 新增：将消息历史数组发送给后端
				body: JSON.stringify({ messages: messageHistory })
			});
			const reader = response.body.getReader();
			const decoder = new TextDecoder();
			let fullResponse = '';
			let finalText = '';
			while (true) {
				const { done, value } = await reader.read();
				if (done) break;
				const text = decoder.decode(value);
				fullResponse += text;
				const lines = fullResponse.split('\n');
				fullResponse = lines.pop(); // 保存最后一个不完整的行
				for (const line of lines) {
					if (line.startsWith('data: ')) {
						const jsonStr = line.slice(6).trim();
						try {
							if (jsonStr === '[DONE]')
								break;
							const jsonData = JSON.parse(jsonStr);
							if (jsonData.choices && jsonData.choices[0].delta && jsonData.choices[0].delta.content) {
								const newText = jsonData.choices[0].delta.content;
								finalText += newText;
								// 逐字更新显示内容
								for (let i = 0; i < newText.length; i++) {
									const char = newText[i];
									$(`#${responseId}`).html(`AI助手：${finalText}`);
									await new Promise(resolve => setTimeout(resolve, 50)); // 控制每个字显示的间隔
								}
							}
						} catch (error) {
							console.error('JSON解析错误:', error);
						}
					}
				}
				$('#ai-chat-content').scrollTop($('#ai-chat-content')[0].scrollHeight);
			}
			// 新增：将AI回复添加到历史记录中
			messageHistory.push({ role: 'assistant', content: finalText });
		} catch (error) {
			$(`#${responseId}`).after(`
           <div style="color:red;margin-bottom:5px;">错误：</div>
           <div style="background:#f0f0f0;padding:8px;border-radius:4px;margin-bottom:10px;">
               无法连接到AI服务，请稍后再试。
           </div>
       `);
		}
	}
	$('#ai-chat-send').click(sendMessage);
	$('#ai-chat-input').keypress(function(e) {
		if (e.which === 13) sendMessage();
	});

});

