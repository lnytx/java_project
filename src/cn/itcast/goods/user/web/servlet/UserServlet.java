package cn.itcast.goods.user.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.UserException;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.servlet.BaseServlet;
import cn.itcast.vcode.servlet.VerifyCodeServlet;


/*
 * 用户模块业务层
 * */
public class UserServlet extends BaseServlet
{
	//UserServlet依赖的是UserService类,通过UserService来访问UserDao中的校验方法
	private UserService userService = new UserService();
	
	/*
	 * 退出功能
	 * */
	public String quit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
				req.getSession().invalidate();//会清空所有已定义的session
				return "f:/jsps/user/login.jsp";
			}
	/*
	 * 修改密码功能
	 * */
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
				/*1、封装表单数据到user中
				 * 2、从session中获取uid
				 * 3、使用uid和表单中的oldpass和newpass来调用service方法
				 * 		如果出现异常保存异常信息到request中，转发到pwd中
				 * 4、保存成功信息到request中
				 * 5、转发到msg.jsp中
				 * 
				 * */
				System.out.println("update");
				User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
				User user = (User) req.getSession().getAttribute("sessionUser");//sessionUser在下面的login方法有设置过
				System.out.println("修改密码user"+user);
				System.out.println("修改密码formUser"+formUser);
				//如果用户没有登录，返回到登录页面，显示错误信息
				if(user == null)
				{
					req.setAttribute("msg", "您还没有登录");
					return "f:/jsps/user/login.jsp";
				}
				try
				{
					userService.updatePassword(user.getUid(), formUser.getNewpass(), formUser.getLoginpass());
					req.setAttribute("msg", "修改密码成功");
					req.setAttribute("code", "success");
					return "f:/jsps/msg.jsp";
				} catch (UserException e)
				{
					req.setAttribute("msg", e.getMessage());//保存异常信息
					req.setAttribute("user", formUser);//为了回显
					return "f:/jsps/user/pwd.jsp";
				}
			}
	
	/*ajax用户是否注册
	 * */
	public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
				//System.out.println("ajaxValidateLoginname...");
				//从前台提交的表单中获取里面的loginname的值
				String loginname = req.getParameter("loginname");
				//使用UserService类中的方法进行检验
				boolean b = userService.ajaxValidateLoginname(loginname);
				//将结果发给客户端
				resp.getWriter().print(b);
				return null;//返回null表示不转发也不重定向
			}
	/*ajax Email是否注册
	 * */
	public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
				//从前台提交的表单中获取里面的Email的值
				String email = req.getParameter("email");
				//使用UserService类中的方法进行检验
				boolean b = userService.ajaxValidateEmail(email);
				//将结果发给客户端
				resp.getWriter().print(b);
				//System.out.println("Email");
				return null;//返回null表示不转发也不重定向
			}
	/*ajax验证码是否正确
	 * */
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
				/*
				 * 1、获取输入框中的验证码
				 * */
				String verifyCode = req.getParameter("verifyCode");
				/*
				 * 2、从session中获取图片上真实的验证码
				 * */
				String vcode = (String) req.getSession().getAttribute("vCode");
				/*3、进行验证码比较，忽略大小写，得到结果
				 * */
				boolean b = verifyCode.equalsIgnoreCase(vcode);
				/*
				 * 4、发给客户端
				 * */
				
				resp.getWriter().print(b);
				return null;
			}
	/*
	 * 注册功能
	 * */
	public String regist(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
				System.out.println("regist...");
				
				/*
				 * 1、封装表单数据到User对象
				 * */
				//将从前端表单发送过来的数据封装成map中，再将这个map对象封装到User对象中
				User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
				/*
				 * 2、校验之,如果校验失败，保存错误信息，返回到regist.jsp显示
				 * */
				Map<String,String> errors = validateRegist(formUser, req.getSession());
				if(errors.size() > 0)
				{
					//将原来的数据返回回去，否则前台一提交如果有问题的话，所有写入的数据都丢失了，又需要重写，很不方便
					req.setAttribute("form",formUser);
					//保存到req中
					req.setAttribute("errors", errors);
					return "f:/jsps/user/regist.jsp";
				}
				/*
				 * 3、使用service中的方法完成业务,并且传递session，用于验证码校验
				 * */
				userService.regist(formUser);
				/*
				 * 4、保存成功信息，转发到msg.jsp显示
				 * */
				req.setAttribute("code","success");
				req.setAttribute("msg", "注册成功，请马上到邮箱激活");
				//转发该页面，下面的f表示的是转发
				return "f:/jsps/msg.jsp";
			}
	/*
	 * 注册校验对整个表单的（后台校验）
	 * 对表单的字段进行逐个校验，如果有错误就使用当前字段名称为key,错误信息为value保存到map中，最后返回map
	 * */
	private Map<String,String> validateRegist(User formUser,HttpSession session)
	{
		//将校验到的错误信息送入map中，最后对map进行探索，如果map为空则表示检验难过
		Map<String,String> errors = new HashMap<String, String>();
		/*1、检验登录名
		 * */
		String loginname = formUser.getLoginname();
		if(loginname==null || loginname.trim().isEmpty())
		{
			errors.put("loginname", "用户名不能为空");
		}
		else if(loginname.length()<3 || loginname.length()>20)
		{
			errors.put("loginname", "用户名长度必须在3~20之间");
		}
		else if(!userService.ajaxValidateLoginname(loginname))
		{
			errors.put("loginname", "用户名已被注册");
		}
		/*1、检验登录密码
		 * */
		String loginpass = formUser.getLoginpass();
		if(loginpass==null || loginpass.trim().isEmpty())
		{
			errors.put("loginpass", "密码不能为空");
		}
		else if(loginpass.length()<3 || loginpass.length()>20)
		{
			errors.put("loginpass", "密码长度必须在3~20之间");
		}
		
		/*3、确认密码校验
		 * */
		String reloginpass = formUser.getReloginpass();
		if(reloginpass==null || reloginpass.trim().isEmpty())
		{
			errors.put("reloginpass", "确认密码不能为空");
		}
		else if(reloginpass.length()<3 || reloginpass.length()>20)
		{
			errors.put("reloginpass", "确认密码长度必须在3~20之间");
		}
		else if(!reloginpass.equals(loginpass))
		{
			errors.put("reloginpass", "两次输入不一致");
		}
		
		/*4、检验Email
		 * */
		String email = formUser.getEmail();
		if(email==null || email.trim().isEmpty())
		{
			errors.put("email", "Email不能为空");
		}
		//校验email格式的正则表达式，与js中的不一样，不需要前后的\并且里面的\要使用\\
		else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$"))
		{
			errors.put("email", "Email格式错误");
		}
		else if(!userService.ajaxValidateEmail(email))
		{
			errors.put("email", "Email已被注册");
		}
		/*4、检验验证码
		 * 通过session获取验证码
		 * */
		String verifycode = formUser.getVerifyCode();
		//这是从会话中拿到的验证码
		String vcode = (String) session.getAttribute("vCode");
		if(verifycode==null || verifycode.trim().isEmpty())
		{
			errors.put("verifycode", "验证码不能为空");
		}
		else if(!verifycode.equalsIgnoreCase(vcode))
		{
			//没有必要对验证码长度进行校验，因为前台已经有了，相当是少了一次对服务器的访问
			errors.put("verifycode", "验证码错误");
		}
		return errors;
	}
	
	/*
	 * 激活功能
	 * */
	public String activation(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
				//点击邮箱里的激活就能访问此 servlet的此方法
				System.out.println("activation()...");
				/*
				 * 1、获取参数激活码
				 * 2、用激活码调用service方法完成激活
				 * 	service方法可能抛出异常，把异常信息拿来，保存到request中，转发到msg.jsp显示
				 * 3、保存成功信息到request中，转到msg.jsp中显示
				 * 
				 */
				//获取请求中链接中的的验证码参数
				String code = req.getParameter("activationCode");
				try
				{
					userService.activation(code);
					//没异常的话就成功
					req.setAttribute("code", "success");//通知msg.jsp激活成功了
					req.setAttribute("msg", "恭喜，激活成功,请马上登录");
				} catch (UserException e)
				{
					//说明service抛出了异常
					req.setAttribute("msg", e.getMessage());
					req.setAttribute("code", "error");//通知msg.jsp显示错号
					e.printStackTrace();
				}
				return "f:/jsps/msg.jsp";
			}
	
	/*
	 * 登录功能
	 * */
	public String login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
			{
				/*
				 * 1、封装表单数据到User
				 * 2、校验表单数据
				 * 3、使用service查询，得到User
				 * 4、查看用户是否存在，如果不存在
				 * 		保存错误信息：用户名或密码错误
				 * 		保存表单数据：为了回显
				 * 		转发到login.jsp
				 * 5、如果用户存在，查看状态，如果状态为false
				 * 		保存错误信息：您没有激活
				 * 		保存表单数据：为了回显
				 * 		转发到login.jsp
				 * 6、登录成功
				 * 		保存当前查询出的user到session中
				 * 		保存当前用户的名称到cookie中，注意中文需要处理。cookie不支持中文
				 * */
				//1、封装表单数据到user
				System.out.println("login。。。");
				User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
				System.out.println("登录.."+formUser);
				//2、校验
				Map<String,String> errors = validateLogin(formUser,req.getSession());
				if(errors.size() > 0)
				{
					req.setAttribute("form", formUser);
					req.setAttribute("errors", errors);
					return "f:/jsps/user/login.jsp";
				}
				
				//3、调用userService.login()方法
				User user = userService.login(formUser);
				
				//4、开始判断用户名与密码
				if(user == null)
				{
					req.setAttribute("msg", "用户名或密码错误");
					req.setAttribute("user", formUser);//回显，保证输入的数据不丢失，需要在页面使用${user.loginname}
					return "f:/jsps/user/login.jsp";
				}
					else//用户不为空
					{
						if(!user.getStatus())//未激活
						{
							req.setAttribute("msg", "您还未激活");
							req.setAttribute("user", formUser);//回显，保证输入的数据不丢失，需要在页面使用${user.loginname}
							return "f:/jsps/user/login.jsp";
						}
						else//已激活
						{
							//保存数据里查询出来的用户user到session中,键名为sessionuser
							req.getSession().setAttribute("sessionUser", user);
							System.out.println("修改密码user"+user);
							//创建用户名的cookie，并编码，防止中文，cookie不支持中文
							String loginname = user.getLoginname();
							loginname = URLEncoder.encode(loginname,"utf-8");
							Cookie cookie = new Cookie("loginname",loginname);
							cookie.setMaxAge(10000 * 60 * 60 * 24 * 10);//保存cookie为10天
							//发送cookie
							resp.addCookie(cookie);//发送cookie,还可以设置cookie路径
							System.out.println(loginname+"已登录");
							return "r:/index.jsp";//重定向到主页
							
						}
					}
			}
	
	/*
	 * 登录校验方法，内容等你来完成
	 * */
	private Map<String,String> validateLogin(User formUser,HttpSession session)
	{
		//将校验到的错误信息送入map中，最后对map进行探索，如果map为空则表示检验难过
		Map<String,String> errors = new HashMap<String, String>();
		return errors;
	}
	
}
