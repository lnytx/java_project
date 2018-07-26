package cn.itcast.goods.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;


/*
 *分类持久层 
 * */
public class CategoryDao
{
	private QueryRunner qr = new QueryRunner();
	
	
	/*
	 * 理解：
	 * 首先是通过toCategory使用CommonUtils.toBean将SQL查询到的map数据封装到Category类中
	 * 然后通过toCategoryList方法返回一个列表，列表中的数据是Category
	 * 当然我们首先写的是List<Category> findAll()这个方法，然后一步一步完成toCategory与toCategoryList方法
	 * */
	private Category toCategory(Map<String,Object> map)
	{
		/*Map里有{cid:xx, cname:xx, pid:xx, desc:xx, orderBy:xx}
		 *Category结构{cid:xx, cname:xx, parent:xx(里面有cid=pid), desc:xx}
		 * */
		Category category = CommonUtils.toBean(map, Category.class);
		String pid = (String) map.get("pid");
		if(pid != null)//如果父分类id不为空
		{
			/*
			 * 使用一个父分类对象来装载pid
			 * 再把父分类设置给category;
			 * */
			Category parent = new Category();//创建父分类
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	
	/*
	 * 可以将多个Map,Map(List<map>)映射成多个Category(List<Category>)
	 * */
	private List<Category> toCategoryList(List<Map<String,Object>> mapList)
	{
		List<Category> categoryList = new ArrayList<Category>();//创建一个实例（里面的内容是Category类）
		for(Map<String,Object> map : mapList)
		{
			Category c = toCategory(map);//使用上面的方法将map转成Category类,如果有父分类的话就转成父分类
			categoryList.add(c);//将转后的类添加到categoryList中
		}
		return categoryList;
	}
	/*
	 * 返回所有分类
	 * */
	public List<Category> findAll() throws SQLException
	{
		//查询出所有的一级分类
		String sql = "select *from t_catetory where pid is nulll";
		//使用MapListHndler，把多行结果集封装到List<Map>中
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		List<Category> parents = toCategoryList(mapList);
		/*循环遍历所有的一级分类，为每一个一级分类加载它的二级分类
		 * */
		for(Category parent : parents)//parents类型是什么，前面的这个参数就是<>尖括号里的类型
		{
			//查询当前父分类的所有子分类
			List<Category> children = findByParent(parent.getCid());
			//将找到的子类挂到父类上
			parent.setChildren(children);
		}
		return parents;
	}
	/*
	 * 通过父分类查询子分类
	 * */
	public List<Category> findByParent(String pid) throws SQLException
	{
		String sql = "select * from t_category where pid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		toCategoryList(mapList);//使用上面的方法将mapList映射成Category对象(该对象用了list封装)
		return null;
	}
}
