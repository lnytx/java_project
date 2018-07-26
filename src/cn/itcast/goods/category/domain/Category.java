package cn.itcast.goods.category.domain;

import java.util.List;


/*分类模块的实体类
 * */
public class Category
{
	private String cid;//主键
	private String cname;//分类名称
	//private String pid;//父分类的id，pid是外键，其所对应的表还是Category所以这里需要修改一睛
	private Category parent;//父分类,相当于在人的类中还有一个属性朋友，但朋友的类型还是人
	private String desc;//分类描述
	private List<Category> children;//子分类
	public String getCid()
	{
		return cid;
	}
	public void setCid(String cid)
	{
		this.cid = cid;
	}
	public String getCname()
	{
		return cname;
	}
	public void setCname(String cname)
	{
		this.cname = cname;
	}
	public Category getParent()
	{
		return parent;
	}
	public void setParent(Category parent)
	{
		this.parent = parent;
	}
	public String getDesc()
	{
		return desc;
	}
	public void setDesc(String desc)
	{
		this.desc = desc;
	}
	public List<Category> getChildren()
	{
		return children;
	}
	public void setChildren(List<Category> children)
	{
		this.children = children;
	}
	@Override
	public String toString()
	{
		return "Category [cid=" + cid + ", cname=" + cname + ", parent="
				+ parent + ", desc=" + desc + ", children=" + children + "]";
	}
	
}
