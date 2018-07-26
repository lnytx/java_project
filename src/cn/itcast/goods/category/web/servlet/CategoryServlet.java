package cn.itcast.goods.category.web.servlet;

import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

/*
 * 分类模块Web层
 * */
public class CategoryServlet extends BaseServlet
{
	private CategoryService categoryService = new CategoryService();
}
