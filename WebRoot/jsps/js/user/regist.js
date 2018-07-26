$(function()//加载前面时就会执行里的方法
{
	/*
	 * 1、找到不同浏览器上显示的多个"用户名不能为空"这个验证，循环遍历之，调用一个方法是否显示这个提示
	 * 2、可以将有内容的显示出来，没有内容的则不显示
	 * */
	$(".classError").each(function()
	{
		showError(this);//遍历每个元素，使用每个元素来调用showError方法
		
	});
	
	/*
	 * 2、切换注册按钮的图片
	 * 
	 * */
	$("#submitBtn").hover
	(
		function()
		{
			$("#submitBtn").attr("src","/goods/images/regist2.jpg");
		},
		function()
		{
			$("#submitBtn").attr("src","/goods/images/regist1.jpg");
		}
	);
	
	/*
	 * 3、输入框得到焦点就隐藏错误信息
	 * */
	$(".inputClass").focus(
			function()
			{
				//打印当前元素的id
				//alert($(this).attr("id"));
				//找到当前元素下面的label,因为label的id就是当前inputClass名称后面加上Error
				var labelId = $(this).attr("id")+"Error";
				//把label的内容清空
				$("#"+labelId).text("");
				//使用下面的showError函数隐藏掉没有内容的label标签
				showError($("#"+labelId));
			});
	
	/*
	 * 4、输入框失去焦点就隐藏错误信息
	 * */
	

		$(".inputClass").blur(
			function() 
			{
				var id = $(this).attr("id");//此处的id是regist.jsp中的inputClasss标签的id
				//因为下面的函数名我们是使用validate在id名来定义的，所以使用id并进行处理（id的第一个字母大写）就能得到下面的函数名了
				var funName = "validate"+id.substring(0,1).toUpperCase()+id.substring(1)+"()";
				//执行函数，下面的eval可以将字符串alert当成javascript代码执行，也就是在前台页面输出3
				//eval("alert(1+2)");
				//执行函数
				eval(funName);
			});
		/*
		 * 5、表单提交时进行校验(这是前台的校验)
		 * */
		$("#registForm").submit(function()
				{
					var bool = true;//表示未通过，当校验不成功时此方法返回false就可能拦截提交
					if(!validateLoginname())
						{
							bool = false;
						};
					if(!validateLoginpass())
						{
							bool = false;
						};
					if(!validateReloginpass())
					{
							bool = false;
					};
					if(!validateEmail())
					{
							bool = false;
					};
					if(!validateVerifyCode())
					{
						bool = false;
					};
					return bool;
				});
});

/*
 * 登录名校验方法
 * */
function validateLoginname()
{
	var id = "loginname";
	var value = $("#"+id).val();//获取输入框内容
	/*
	 * 1、非空校验
	 * */
	if(!value)
		{
			/*
			 * 获取对应的label
			 * 添加错误信息
			 * 显示label
			 * */
			//获取label的id
			$("#"+id+"Error").text("用户名不能为空");
			//再调用showError方法将有text的标签的内容显示出来
			showError($("#"+id+"Error"));
			return false;//返回false表示校验失败
		}
	
	/*
	 * 2、长度校验
	 * */
	if(value.length<3||value.length>20)
	{
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 * */
		//获取label的id
		$("#"+id+"Error").text("用户名长度必须在3到20之间");
		//再调用showError方法将有text的标签的内容显示出来
		showError($("#"+id+"Error"));
		return false;
	}
	
	/*
	 * 3、是否注册校验，需要访问服务器，使用ajax方法
	 * */
	$.ajax({
		url:"/goods/UserServlet",//要请求的servlet
		data:{method:"ajaxValidateLoginname",loginname:value},//这里传了两个参数，第一个是ajaxValidateLoginname，他是一个方法，第二个是loginname,他的值就是页面上的value值
		type:"POST",
		dataType:"json",
		async:false,//是否异步，如果是异步那么函数就不会等服务器返回，我们这个函数就直接向下执行了。这里为false的话就会一直在这里执行
		cache:false,
		success:function(result)
		{
			//从服务器拿到了结果
			if(!result)
				{
					$("#"+id+"Error").text("用户名已注册");
					showError($("#"+id+"Error"));
					return false;
				}
		}
	});
	//如果从头到尾都没错就返回true
	return true;
}
/*
 * 登录密码校验方法
 * */
function validateLoginpass()
{
	var id = "loginpass";
	var value = $("#"+id).val();//获取输入框内容
	/*
	 * 1、非空校验
	 * */
	if(!value)
		{
			/*
			 * 获取对应的label
			 * 添加错误信息
			 * 显示label
			 * */
			//获取label的id
			$("#"+id+"Error").text("密码不能为空");
			//再调用showError方法将有text的标签的内容显示出来
			showError($("#"+id+"Error"));
			return false;
		}
	
	/*
	 * 2、长度校验
	 * */
	if(value.length<2||value.length>20)
	{
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 * */
		//获取label的id
		$("#"+id+"Error").text("密码长度必须在3到20之间");
		//再调用showError方法将有text的标签的内容显示出来
		showError($("#"+id+"Error"));
		return false;
	}

	//如果从头到尾都没错就返回true
	return true;
}
/*
 * 确认密码校验方法
 * */
function validateReloginpass()
{
	var id = "reloginpass";
	var value = $("#"+id).val();//获取输入框内容
	/*
	 * 1、非空校验
	 * */
	if(!value)
		{
			/*
			 * 获取对应的label
			 * 添加错误信息
			 * 显示label
			 * */
			//获取label的id
			$("#"+id+"Error").text("密码不能为空");
			//再调用showError方法将有text的标签的内容显示出来
			showError($("#"+id+"Error"));
			return false;
		}
	
	/*
	 * 2、两次输入是否一致的校验
	 * */
	if(value!=$("#loginpass").val())
	{
		/*
		 * 获取对应的label
		 * 添加错误信息
		 * 显示label
		 * */
		//获取label的id
		$("#"+id+"Error").text("两次输入密码不 一致");
		//再调用showError方法将有text的标签的内容显示出来
		showError($("#"+id+"Error"));
		return false;
	}
	return true;
}
/*
 * Email校验方法
 * */
function validateEmail()
{
	var id = "email";
	var value = $("#"+id).val();//获取输入框内容
	/*
	 * 1、非空校验
	 * */
	if(!value)
		{
			/*
			 * 获取对应的label
			 * 添加错误信息
			 * 显示label
			 * */
			//获取label的id
			$("#"+id+"Error").text("Emil不能为空");
			//再调用showError方法将有text的标签的内容显示出来
			showError($("#"+id+"Error"));
			return false;
		}
			 /*2、Email格式校验
			  * 
			  */
		if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value))
			{
				$("#"+id+"Error").text("错误的Email");
				//再调用showError方法将有text的标签的内容显示出来
				showError($("#"+id+"Error"));
				return false;
			}
		/*
		 * 3、是否注册校验，需要访问服务器，使用ajax方法
		 * */
		$.ajax({
			url:"/goods/UserServlet",//要请求的servlet
			data:{method:"ajaxValidateEmail",email:value},//这里传了两个参数，第一个是ajaxValidateLoginname，他是一个方法，第二个是loginname,他的值就是页面上的value值
			type:"POST",
			dataType:"json",
			async:false,//是否异步，如果是异步那么函数就不会等服务器返回，我们这个函数就直接向下执行了。这里为false的话就会一直在这里执行
			cache:false,
			success:function(result)
			{
				//从服务器拿到了结果
				if(!result)
					{
						$("#"+id+"Error").text("Email已注册");
						showError($("#"+id+"Error"));
						return false;
					}
				
			}
		});
		return true;
}
/*
 * 验证码校验方法
 * */
function validateVerifyCode()
{
	var id = "verifyCode";
	var value = $("#"+id).val();//获取输入框内容
	/*
	 * 1、非空校验
	 * */
	if(!value)
		{
			/*
			 * 获取对应的label
			 * 添加错误信息
			 * 显示label
			 * */
			//获取label的id
			$("#"+id+"Error").text("验证码不能为空");
			//再调用showError方法将有text的标签的内容显示出来
			showError($("#"+id+"Error"));
			return false;
		}
			 /*2、验证码长度校验
			  * 
			  */
		if(value.length < 3 || value.length > 20)
			{
				$("#"+id+"Error").text("验证码错误");
				//再调用showError方法将有text的标签的内容显示出来
				showError($("#"+id+"Error"));
				return false;
			}
		/*
		 * 验证码是否正确
		 * */
		$.ajax({
			url:"/goods/UserServlet",//要请求的servlet
			data:{method:"ajaxValidateVerifyCode",verifyCode:value},//这里传了两个参数，第一个是ajaxValidateLoginname，他是一个方法，第二个是loginname,他的值就是页面上的value值
			type:"POST",
			dataType:"json",
			async:false,//是否异步，如果是异步那么函数就不会等服务器返回，我们这个函数就直接向下执行了。这里为false的话就会一直在这里执行
			cache:false,
			success:function(result)
			{
				//从服务器拿到了结果
				if(!result)
					{
						$("#"+id+"Error").text("验证码错误");
						showError($("#"+id+"Error"));
						return false;
					}
			}
		});
		return true;
}



/*
 * 判断当前元素是否存在内容，如果存在则显示，不存在则不显示
 * */
function showError(ele)
{
	var text = ele.text();//获取元素内容
	if(!text)//如果没有内容
		{
			ele.css("display","none");//隐藏元素
		}
	else//如果有内容
		{
			ele.css("display","");//显示元素
		}
}

/*
 * 加一个方法，实现换一张验证码
 * */
function _hyz()
{
	/*
	 * 1、获取<img>元素
	 * 2、重新设置它的值
	 * 3、使用毫秒来添加参数
	 * */
	$("#imgVerifyCode").attr("src","/goods/VerifyCodeServlet1?a=" + new Date().getTime());
/*	  var img = document.getElementById("imgVerifyCode");
	  //需要给出一个参数，这个参数每次都不同，这样才能干掉浏览器缓存
	  img.src = "/tools/VerifyCodeServlet?a=" + new Date().getTime();*/
}

