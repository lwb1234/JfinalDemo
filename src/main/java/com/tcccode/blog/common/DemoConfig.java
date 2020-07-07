package com.tcccode.blog.common;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.tcccode.blog.entity._MappingKit;
import com.tcccode.blog.index.BlogController;
import com.tcccode.blog.index.IndexController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

import java.io.File;

/**
 * 本 tcccode 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * API引导式配置
 */
public class DemoConfig extends JFinalConfig {

	//微信配置
	/*public ApiConfig getApiConfig(){
		ApiConfig apiConfig = new ApiConfig();
		apiConfig.setToken(PropKit.get("token"));
		apiConfig.setAppId(PropKit.get("appId"));
		apiConfig.setAppSecret(PropKit.get("appSecret"));
		apiConfig.setEncryptMessage(PropKit.getBoolean("encryptMessage",false));
		apiConfig.setEncodingAesKey(PropKit.get("encodingAesKey"));
		return apiConfig;
	}*/
	/**
	 * 启动入口，运行此 main 方法可以启动项目，此 main 方法可以放置在任意的 Class 类定义中，不一定要放于此
	 * 
	 * 使用本方法启动过第一次以后，会在开发工具的 debug、run configuration 中自动生成
	 * 一条启动配置项，可对该自动生成的配置再继续添加更多的配置项，例如 Program arguments
	 * 可配置为：src/main/webapp 80 / 5
	 * 
	 */
	public static void main(String[] args) {
	    //             src/main/webapp指的是web.xml的位置
		JFinal.start("src/main/webapp", 80, "/", 5);
	}
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("jdbc.properties").append("environment.properties").append("weixin_config.properties");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setEncoding("utf-8");
		//设置视图渲染器
		//me.setViewType(ViewType.FREE_MARKER);
        me.setBaseUploadPath(PathKit.getWebRootPath() + File.separator + "upload");
		// 支持 Controller、Interceptor 之中使用 @Inject 注入业务层，并且自动实现 AOP
		me.setInjectDependency(true);
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", IndexController.class, "/index");	// 第三个参数为该Controller的视图存放路径
		me.add("/blog", BlogController.class);			// 第三个参数省略时默认与第一个参数值相同，在此即为 "/blog"
	}
	
	public void configEngine(Engine me) {
	    me.setDevMode(PropKit.getBoolean("devMode",false));
		me.addSharedFunction("/common/_layout.html");
		me.addSharedFunction("/common/_paginate.html");
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim(),PropKit.get("driverClass"));
		me.add(druidPlugin);
		//添加sql统计插件
		druidPlugin.addFilter(new StatFilter());
		//添加日志统计插件
		druidPlugin.addFilter(new Slf4jLogFilter());
		//添加alibaba的数据库拦截器插件
		druidPlugin.addFilter(new WallFilter());

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		// 所有映射在 MappingKit 中自动化搞定
		_MappingKit.mapping(arp);
		//关闭框架自带的sql显示
		arp.setShowSql(false);
		//设置数据库类型
		arp.setDialect(new MysqlDialect());
		me.add(arp);
	}
	
	public static DruidPlugin createDruidPlugin() {
		return new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("base"));
	}

	/**如果使用FreeMarker作为模版，可以直接在下面进行设置*/
	/*public void afterJFinalStart(){
        Configuration configuration = FreeMarkerRender.getConfiguration();
        configuration.setWhitespaceStripping(true);
        configuration.setClassicCompatible(true);
        configuration.setLocale(Locale.CANADA);
        configuration.setTemplateUpdateDelayMilliseconds(2000);
    }*/
}
