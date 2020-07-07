package com.tcccode.blog.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.tcccode.blog.entity.Blog;
import com.tcccode.blog.exception.UpdateFailureException;
import com.jfinal.plugin.activerecord.Page;

/**
 * 本 tcccode 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * BlogService
 * 所有 sql 与业务逻辑写在 Service 中，不要放在 Model 中，更不
 * 要放在 Controller 中，养成好习惯，有利于大型项目的开发与维护
 */
public class BlogService {
	
	/**
	 * 所有的 dao 对象也放在 Service 中，并且声明为 private，避免 sql 满天飞
	 * sql 只放在业务层，或者放在外部 sql 模板，用模板引擎管理：
	 * 			http://www.jfinal.com/doc/5-13
	 */
	private Blog dao = new Blog().dao();
	//查找分页数据
	public Page<Blog> paginate(int pageNumber, int pageSize) {
		return dao.paginate(pageNumber, pageSize, "select *", "from blog order by id asc");
	}
	//根据主键查找数据
	public Blog findById(int id) {
		return dao.findById(id);
	}
	//删除数据
	public void deleteById(int id) {
		dao.deleteById(id);
	}
	//更新数据
	public void update(int id,String title,String content){
			if(!(new Blog()).findById(id)
					.setTitle(title)
					.setContent(content)
					.update()
			){
				throw new UpdateFailureException("更新博客失败!");
			}
	}
	public void updateBlog(int id,String title,String content){
		//第一种方案处理速度最快，但是需要手动写表名和列名，比较繁琐
		Db.update("blog",new Record().set("id",id).set("title",title).set("content",content));
		//第二种方案处理速度一般，但是编写起来容易
		//(new Blog()).findById(id).setTitle(title).setContent(content).update();
		//第三种方案
		//Db.update("blog",(new Blog()).findById(id).setTitle(title).setContent(content).toRecord());
	}
	/**在这里插入数据的写法，不需要写sql语句*/
	public void insertBlog(String title,String content){
		if (!(new Blog().setTitle(title)
				.setContent(content)
				.save())){
			throw new UpdateFailureException("插入数据失败!");
		}
	}

}
