import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import cn.itcast.jdbc.TxQueryRunner;


public class TestDb
{
@Test
	
	/*
	 * 校验用户是否注册
	 * */
	public void ajaxValidateLoginname() throws SQLException
	{
		 QueryRunner qr = new TxQueryRunner();
		String sql = "select count(1) from t_user";
		Number number = (Number) qr.query(sql, new ScalarHandler());
		System.out.println(number);
	}
@Test
public void test()
{
	String pig = "{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}{12}{13}{14}{15}{16}";  
	  
	Object[] array = new Object[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q"};  
	  
	String value = MessageFormat.format(pig, array);  
	  
	System.out.println(value);  
}

}
