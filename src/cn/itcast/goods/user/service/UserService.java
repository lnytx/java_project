package cn.itcast.goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.domain.User;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;


/*
 * 用户模块业务层
 * */
public class UserService
{
	private UserDao userDao = new UserDao();
	
	
	/*
	 * 修改密码功能
	 * */
	public void updatePassword(String uid,String newpass,String oldPass) throws UserException
	{
		/*1、校验老密码
		 * */
		try
		{
			boolean bool = userDao.findByUidAndPassword(uid, oldPass);
			if(!bool)//如果老密码错误
			{
				throw new UserException("老密码错误");
			}
			/*修改密码
			 * */
			userDao.updatePassword(uid,newpass);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	/*
	 * 登录功能
	 * */
	public User login(User user)
	{
		try
		{
			System.out.println("-----"+user.getLoginname());
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * 激活功能
	 * */
	public void activation (String code) throws UserException
	{
		/*
		 * 1、通过激活码查询用户
		 * 2、如果User为null，说明是无效的激活码，给出异常信息（无效激活码）
		 * 3、查看用户状态是否为true，如果为true，抛出异常（该用户已激活，不要二次激活）
		 * */
		try
		{
			User user = userDao.findByCode(code);
			if(user == null)
				throw new UserException("无效激活码");//创建一个自定义异常类对象
			if(user.getStatus())
				throw new UserException("您已经激活过了，不要重复激活");
			userDao.updateStatus(user.getUid(), true);//修改状态
		}
		catch(SQLException e)
		{
			throw new RuntimeException(e);
		}
		
	}
	/*
	 * 用户名注册校验
	 * */
	public boolean ajaxValidateLoginname(String loginname)
	{
		try
		{
			return userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	/*
	 * 邮箱注册校验
	 * */
	public boolean ajaxValidateEmail(String email)
	{
		try
		{
			return userDao.ajaxValidateEmail(email);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * 用户注册功能
	 * */
	public void regist(User user)
	{
		/*
		 * 1、数据的补齐，因为前台有些数据是没有的
		 * */
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		/*
		 * 2、向数据库插入
		 * */
		try
		{
			userDao.add(user);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		/*
		 * 3、注册后发邮件给用户
		 */
		//将邮件的配置文件加载到prop中
		Properties prop = new Properties();
		try
		{
			//使用类加载器来加载这个配置文件，因为这个文件是在\goods\WEB-INF\classes文件中，所以可以使用类加载器
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1)
		{
			throw new RuntimeException(e1);
		}
		
		// 登录邮件服务器，得到session
		String host = prop.getProperty("host");//服务器名
		String name = prop.getProperty("username");//登录名
		String pass = prop.getProperty("password");//登录密码
		Session session = MailUtils.createSession(host, name, pass);
		//创建Mail对象
		String from = prop.getProperty("from");
		String to = user.getEmail();
		String subject = prop.getProperty("subject");//主题
		
		/* MessageFormat.format这个方法可以将模式里面的{0}用后面的一个参数代替，如果还有{1},{2}还可以在user.getActivationCode()这个参数后面加上其他的代替参数
		 * 例如，MessageFormat.format("您好{0},你{1}"，"张三","很好");返回的就是“张三你很好”
		 */
		String content = MessageFormat.format(prop.getProperty("content"), user.getActivationCode());//内容
		Mail mail = new Mail(from,to,subject,content);
		System.out.println(mail.getContent());
		//发送邮件
		try
		{
			MailUtils.send(session, mail);
		} catch (MessagingException e)
		{
			throw new RuntimeException(e);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

}
