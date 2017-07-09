# SSM #
手把手教你创建Spring mvc +spring+mybatis+shiro
## 前言 ##
 接触SSM框架两个多月，可谓是收获颇丰，从最基础的jsp+servlet+Javabean开始，了解到了最原始的mvc开发模式，到mybatis+spring+springmvc的模式，从最初的一个一个方法的封装，一个一个对象的自定义，到渐渐习惯了把所有的对象交给spring进行管理，属性交给spring进行注入。可以这么说，spring框架的产生确实改变了程序员的编程习惯，同时也给程序员带来了极大的便利。好了，言归正传，下面开始咱们的springmvc+spring+mybatis之旅。
## 第一章准备环境 ##
首先准备jar包，mybatis的jar包、spring的jar包、shiro的jar包，spring和shiro整合的jar包，以及spring和mybatis整合的jar包，相关jar包已经整合到项目中，可以进行clone下载：

- 相关jar包如下：
![](http://i.imgur.com/JsjMLad.png)

其次准备工程目录，本次工程整合没有继承maven进行库的管理，因此很多包需要自己手工进行建，下面是我的工程目录：
![](http://i.imgur.com/mXD9VC5.png)

包名解释：

com.qit.controller   

- springmvc控制器所在包

com.qit.converter    

- 数据转换器所在包
	
com.qit.dao			

-  数据持久层所在包，包括mybatis的mapper接口和mapperxml文件
 
com.qit.exception    

- 自定义的异常和异常解析器所在包

com.qit.interceptor  

- 在基于URL登陆拦截方式时候拦截器所在的包，后面用的shiro可能不会用到

com.qit.message      

- 公共消息包，主要是类中的静态变量作为常量使用

com.qit.pojo         

- pojo包，主要是针对数据库中表所定义的bean类，一般使用mybatis-generator自动生成

com.qit.pojo.custom  

- pojocustom包，自定义的pojo，基本上是继承pojo包中的类

com.qit.service      

- service服务包，主要负责业务部分，此处存放接口

com.qit.service.impl 

- service各个接口实现类，主要负责业务部分

com.qit.shiro        

- shiro的realm的定义以及一些炎症类的实现

com.qit.util         

- 工具类

com.qit.validation 

- hibernate的validator的相关分组类

### 第二章spring和mybatis进行整合 ###
1、通过myeclipse的mybatis generator插件生成相关的mapper接口和mapper的xml文件，具体generator插件安装请度娘。此处提供generatorConfig.xml是generator配置文件，在安装好插件之后，邮件单机此xml文件，选择generate mybatis artifacts，即可生成相应的mapper接口和mapperxml文件：
```<?xml version="1.0" encoding="UTF-8"?>  
    <!DOCTYPE generatorConfiguration  
    PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"  
    "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
    <generatorConfiguration>
	<!-数据库驱动 -->
	<classPathEntry location="E:/projectlib/mysql-connector-java-5.1.41-bin.jar" />
	<context id="DB2Tables" targetRuntime="MyBatis3">
		<commentGenerator>
			<property name="suppressDate" value="true" />
			<!-是否去除自动生成的注释 true：是 ： false:否 -->
			<property name="suppressAllComments" value="true" />
		</commentGenerator>
		<!--数据库链接URL，用户名、密码 -->
		<jdbcConnection driverClass="com.mysql.jdbc.Driver"
			connectionURL="jdbc:mysql://192.168.3.100/qit-shiro" userId="qit" password="qit3875">
		</jdbcConnection>
		<javaTypeResolver>
			<property name="forceBigDecimals" value="false" />
		</javaTypeResolver>
		<!-生成pojo -->
		<javaModelGenerator targetPackage="com.qit.pojo"
			targetProject="SSM">
			<property name="enableSubPackages" value="false" />
			<property name="trimStrings" value="true" />
		</javaModelGenerator>
		<!-生成映射文件的mapperXML文件 -->
		<sqlMapGenerator targetPackage="com.qit.dao"
			targetProject="SSM">
			<property name="enableSubPackages" value="false" />
		</sqlMapGenerator>
		<!-生成mapper接口，dao接口 -->
		<javaClientGenerator type="XMLMAPPER"
			targetPackage="com.qit.dao" targetProject="SSM">
			<property name="enableSubPackages" value="false" />
		</javaClientGenerator>
		<!-要生成哪些表 -->
		<table tableName="sys_user" domainObjectName="User"></table>
		<table tableName="sys_role" domainObjectName="Role"></table>
		<table tableName="sys_user_role" domainObjectName="User_Role"></table>
		<table tableName="sys_role_permission" domainObjectName="Role_Permission"></table>
		<table tableName="sys_permission" domainObjectName="Permission"></table>
	</context>
</generatorConfiguration>
````
上面是generatorconfig.xml文件，下面是如何在myeclipse中生成相应的dao文件，上述xml文档中有相关注释。
![](http://i.imgur.com/LERGRZ8.png)
2、配置spring和mybatis整合配置文件
````<?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:p="http://www.springframework.org/schema/p" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd 
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd 
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd  
        http://www.springframework.org/schema/tx 
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/mvc 
        http://www.springframework.org/schema/tx/spring-mvc.xsd">
	<!--获取数据库配置文件,数据库配置文件中包含数据库的地址，用户名和密码等相关信息 -->
	<context:property-placeholder location="classpath:db.properties" />
	<!--获取数据源 ，此处不唯一，可以使用c3p0也可以使用阿里巴巴的德鲁伊，也可以使用其他数据源-->
	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
		<property name="user" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />
		<property name="driverClass" value="${jdbc.driver}" />
		<property name="jdbcUrl" value="${jdbc.url}" />
	</bean>
	<!--配置sqlsessionfactory，sqlsession很重要，将上面配置的数据源注入，并且制定相应的mybatis配置文件位置 -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource"></property>
		<property name="configLocation" value="classpath:mybatis/mybatis.xml"></property>
	</bean>
	<!--设置mapper扫描 ，spring启动后会扫描相关的mapper接口和mapperxml文件，此处设置扫描模式-->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
		<property name="basePackage" value="com.qit.dao" />
	</bean>
  </beans>
````
上述xml文件为工程目录config/spring/spring-mybatis.xml中的内容，此处主要是配置spring和mybatis的整合功能，上述文档中注释可以自行查看

3、至此，spring和mybatis整合结束

### 第三章springmvc spring mybatis整合 ###
1、springmvc因为是属于spring的一部分，因此不需要和spring整合，但是要设置一些配置
   首先是springmvc的dispatcherservlet设置，dispatcherservlet相当于一个中枢控制器，所有的web请求会先到dispatcherservlet，然后dispatcherservlet会调用处理器映射器查找此URL对应的处理器handler，然后dispatcherservlet会调用处理器适配器进行执行此handler，最后dispatcherservlet会将处理器handler处理后返回的modelandview分发到视图解析器，由视图解析器进行视图渲染然后交给用户。
   dispatcherservlet的配置如下，记住此处配置在web工程的web.xml下面。
````<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" id="WebApp_ID" version="3.0">
  <display-name>SSM</display-name>
  <welcome-file-list>
    <welcome-file>index.jsp</welcome-file>
  </welcome-file-list>
  <!--springmvc前端控制器配置 -->
	<servlet>
		<servlet-name>springmvc-action</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:springmvc/spring-*.xml</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>springmvc-action</servlet-name>
		<url-pattern>*.action</url-pattern>
	</servlet-mapping>
	<!--post编码过滤器 -->
	<filter>
		<filter-name>encoding-filter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>encoding-filter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<!--shiro的filter -->
	<!--shiro过滤器，DelegatingFilterProxy通过代理模式将spring容器中的bean和filter关联起来 -->
	<filter>
		<filter-name>shiroFilter</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<!-- 设置为TRUE由Servlet容器进行filter生命周期的控制 Set whether to invoke the {@code 
			Filter.init} and {@code Filter.destroy} lifecycle methods on the target bean. 
			<p>Default is "false"; target beans usually rely on the Spring application 
			context for managing their lifecycle. Setting this flag to "true" means that 
			the servlet container will control the lifecycle of the target Filter, with 
			this proxy delegating the corresponding calls. -->
		<init-param>
			<param-name>targetFilterLifecycle</param-name>
			<param-value>true</param-value>
		</init-param>
		<!-- 设置spring容器的bean的ID，如果不设置则找与<filter-name></filter-name>中一致的bean -->
		<init-param>
			<param-name>targetBeanName</param-name>
			<param-value>shiroFilter</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>shiroFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<!--spring监听器 -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:spring/spring-*.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
</web-app>
````
主要看dispatcherservlet部分，其余可以暂时忽略。
2、springmvc的配置文件
````<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd 
        http://www.springframework.org/schema/aop 
        http://www.springframework.org/schema/aop/spring-aop.xsd 
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd  
        http://www.springframework.org/schema/tx 
        http://www.springframework.org/schema/tx/spring-tx.xsd 
        http://www.springframework.org/schema/mvc 
        http://www.springframework.org/schema/mvc/spring-mvc.xsd  ">
	<!--开启aop代理支持，主要针对shiro的注解模式  -->
	<aop:config proxy-target-class="true"></aop:config>
	
	<!--扫描controller注解 -->
	<context:component-scan base-package="com.qit.controller"></context:component-scan>
	<!--处理器映射器和处理器适配器，其中配置了转换器和校验器 -->
	<mvc:annotation-driven conversion-service="convertionFactory"
		validator="hibernateValidator"></mvc:annotation-driven>
	<bean id="convertionFactory"
		class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
		<property name="converters">
			<list>
				<bean class="com.qit.converter.String2DateConverter"></bean>
			</list>
		</property>
	</bean>
	<!--validate校验器 -->
	<bean name="hibernateValidator"
		class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
		<!--校验器 -->
		<property name="providerClass" value="org.hibernate.validator.HibernateValidator"></property>
		<!--校验器文件,提供校验器的相关信息 -->
		<property name="validationMessageSource" ref="messageSource"></property>
	</bean>
	<bean id="messageSource"
		class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
		<property name="basenames">
			<list>
				<value>classpath:validate</value>
			</list>
		</property>
		<!--资源文件编码格式 -->
		<property name="fileEncodings" value="UTF-8"></property>
		<!--对资源文件缓存时间 -->
		<property name="cacheSeconds" value="120"></property>
	</bean>

	<!--配置视图解析器 -->
	<bean
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/WEB-INF/jsp/"></property>
		<property name="suffix" value=".jsp"></property>
	</bean>
	<!--静态资源的解析 -->
	<!-- <mvc:default-servlet-handler /> -->

	<!--springmvc拦截器,如果上面的处理器适配器配置的是RequestMappingHandlerAdapter类型的那么就需要在上面 
		进行配置，此处不需要，顺序很重要，先认证后授权 -->
	<!-- <mvc:interceptors> <mvc:interceptor> <mvc:mapping path="/**"/> <bean 
		class="com.qit.interceptor.LoginInInterceptor"></bean> </mvc:interceptor> 
		<mvc:interceptor> <mvc:mapping path="/**"/> <bean class="com.qit.interceptor.AuthencatieInterceptor"></bean> 
		</mvc:interceptor> </mvc:interceptors> -->

	<!--全局异常处理器，只此一个，定义在多也没用 -->
	<bean class="com.qit.exception.CustomExceptionResolver"></bean>

	<!--文件上传解析器 -->
	<bean id="multipartResolver"
		class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
		<property name="maxUploadSize">
			<value>102400</value>
		</property>
	</bean>

</beans>
````
spring-mvc.xml主要是配置springmvc相关的设置，在tomcat启动时候会加载此文件，进行相应的设置
3、controller的编写
  浏览器来到一个请求，首先经过dispatcherservlet的调度，会指定相应的handler处理，那么handler是如何编写那，handler有多种编写方法，如果在spring-mvc。xml配置文件中配置的是非注解的处理器映射器和非注解的处理器适配器，那么程序员就需要编写类来实现controller或者handler接口，如果使用的是注解驱动的处理器映射器和适配器，那么就需要程序员在编写handler时候加上注解，比如浏览器要查询
![](http://i.imgur.com/y6uAw4b.png)

对应的handler该如何编写：
````
package com.qit.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qit.message.SessionAttributeDictionary;
import com.qit.exception.CustomException;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;
import com.qit.service.UserService;

//controller注解表示此类是controller控制类，负责处理springmvc的请求
@Controller
public class UserController {
	//此处注解autowired表示由spring自动的将bean注入到此属性中
	@Autowired
	private UserService userService;
	//Requestmapping指示了URL请求的路径是/login.action时候将会由此方法进行处理
	@RequestMapping("/login.action")
	public String login(HttpServletRequest request) throws Exception {
		// 如果登陆失败从request中获取认证异常信息，shiroLoginFailure就是shiro异常类的全限定名
		String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
		// 根据shiro返回的异常类路径判断，抛出指定异常信息
		if (exceptionClassName != null) {
			if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
				// 最终会抛给异常处理器
				throw new CustomException("账号不存在");
			} else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
				throw new CustomException("用户名/密码错误");
			} else if ("RandomCodeError".equals(exceptionClassName)) {
				throw new CustomException("验证码错误 ");
			} else {
				throw new Exception();// 最终在异常处理器生成未知错误
			}
		}
		// 此方法不处理登陆成功（认证成功），shiro认证成功会自动跳转到上一个请求路径
		// 登陆失败还到login页面
		return "login";
	}

	// 登陆成功后跳转到首页
	@RequestMapping("/first.action")
	public String first(Model model) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
			model.addAttribute(SessionAttributeDictionary.SESSION_USER, activeUser);
		}
		return "first";
	}

	@RequestMapping("/loginIndex.action")
	public String loginIndex(Model model) {

		return "login";
	}

	@RequestMapping("/queryUsers.action")
	@RequiresPermissions("user:query")
	public String queryUsers(Model model) {
		List<UserCustom> userCustomList = userService.selctUserListUserName("%a%");
		model.addAttribute("userList", userCustomList);
		return "userList";

	}

	@RequestMapping("/clearShiroEhcache.action")
	public String clearShiroEhcache() {
		userService.clearShiroEhcache();
		return "first";
	}
}

````
### 第四章spring进行管理service ###
 service部分两部分组成，service接口和service接口实现类
 以用户登陆为例.com.qit.service.UserService接口为：
````
package com.qit.service;

import java.util.List;

import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;

public interface UserService {

	ActiveUser authenticateUser(UserCustom userCustom) throws Exception;

	User findUserByUserCode(String userCode);

	List<Permission> findMenuListByUserId(String id) throws Exception;

	List<Permission> findPermissionListByUserId(String id) throws Exception;

	List<UserCustom> selctUserListUserName(String userName);

	void clearShiroEhcache();

}
````
com.qit.service.impl.UserServiceImpl接口实现类为：
````
package com.qit.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.qit.dao.PermissionCustomMapper;
import com.qit.dao.UserMapper;
import com.qit.exception.CustomException;
import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.UserExample;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;
import com.qit.shiro.CustomRealm;
import com.qit.service.UserService;

public class UserServiceImpl implements UserService {
	//定义dao包中的UserMapper接口并且自动注入相关的bean
	@Autowired
	private UserMapper userMapper;
	//定义dao包中的PermissionCustomMapper接口并自动注入
	@Autowired
	private PermissionCustomMapper permissionCustomMapper;
	//定义shiro包中的custom包接口，此处为了清除自动登录的缓存
	@Autowired
	private CustomRealm customRealm;
	/*
	* 方法名称: authenticateUser
	* 方法描述:
	* @see com.qit.service.impl.UserService#authenticateUser(com.qit.pojo.custom.UserCustom)
	*/
	@Override
	public ActiveUser authenticateUser(UserCustom userCustom) throws Exception {
		User user = this.findUserByUserCode(userCustom.getUsercode());
		if (user == null)
			throw new CustomException("账号不存在");
		if (userCustom.getPassword().equals(user.getPassword())) {
			ActiveUser activeUser = new ActiveUser();
			activeUser.setId(user.getId());
			activeUser.setUsercode(user.getUsercode());
			activeUser.setUsername(user.getUsername());
			List<Permission> permissionList = this.findPermissionListByUserId(user.getId());
			List<Permission> menuList = this.findMenuListByUserId(user.getId());
			activeUser.setMenuList(menuList);
			activeUser.setPermissionList(permissionList);
			return activeUser;
		} else {
			throw new CustomException("用户密码错误!");
		}

	}

	/*
	* 方法名称: findUserByUserCode
	* 方法描述:
	* @see com.qit.service.impl.UserService#findUserByUserCode(java.lang.String)
	*/
	@Override
	public User findUserByUserCode(String userCode) {
		UserExample example = new UserExample();
		com.qit.pojo.UserExample.Criteria criteria = example.createCriteria();
		criteria.andUsercodeEqualTo(userCode);
		List<User> userList = userMapper.selectByExample(example);
		if (userList != null && userList.size() == 1)
			return userList.get(0);
		return null;
	}

	/*
	* 方法名称: findMenuListByUserId
	* 方法描述:
	* @see com.qit.service.impl.UserService#findMenuListByUserId(java.lang.String)
	*/
	@Override
	public List<Permission> findMenuListByUserId(String id) throws Exception {
		return permissionCustomMapper.findMenuListByUserId(id);
	}

	/*
	* 方法名称: findPermissionListByUserId
	* 方法描述:
	* @see com.qit.service.impl.UserService#findPermissionListByUserId(java.lang.String)
	*/
	@Override
	public List<Permission> findPermissionListByUserId(String id) throws Exception {
		return permissionCustomMapper.findPermissionListByUserId(id);
	}

	public List<UserCustom> selctUserListUserName(String userName) {
		UserExample example = new UserExample();
		com.qit.pojo.UserExample.Criteria criteria = example.createCriteria();
		criteria.andUsernameLike(userName);
		List<User> userList = userMapper.selectByExample(example);
		List<UserCustom> userCustomList = new ArrayList<UserCustom>();
		for (User user : userList) {
			UserCustom userCustom = new UserCustom();
			BeanUtils.copyProperties(user, userCustom);
			userCustomList.add(userCustom);
		}
		return userCustomList;
	}

	@Override
	public void clearShiroEhcache() {
		
		customRealm.clearCached();
		
	}

}
````
可能看到这里有些童鞋蒙圈了，怎么回事一会这个一会那个。。。好了 我现在来捋一捋，上面看到我们先设置了web.xml文件定义了dispatcherservlet的路径，然后配置了springmvc的配置文件，之后我们定义controller注解类，然后我们定义service类。在这一连串过程中其实有一条线，就是springmvc负责url请求，负责自动的将请求映射到controller类中相应的方法里，然后由方法调用service实现类中的相应的方法进行实现，在service实现类中我们又调用了dao中相应的额接口中的方法，在mybatis中会自动为相应的接口生成代理的类，然后根据对应的xml文件进行数据操作。这就是一条完整的线。

### 第五章 spring配置，其他一些配置文件设置 ###
 1、我们前面用到的是springmvc,那么spring其实也是需要进行配置，比如我们定义了service接口和service实现类，就需要交给spring进行管理，还有就是相关的数据库信息，数据校验信息，日志信息等也需要进行配置：
spring主要是在web.xml中进行配置，上面在讲springmvc时候已经把web.xml文档贴出来了，那个就是全的，我再贴一次:
````
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" id="WebApp_ID" version="3.0">
  <display-name>SSM</display-name>
  <welcome-file-list>
    <welcome-file>index.jsp</welcome-file>
  </welcome-file-list>
  <!--springmvc前端控制器配置 -->
	<servlet>
		<servlet-name>springmvc-action</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:springmvc/spring-*.xml</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>springmvc-action</servlet-name>
		<url-pattern>*.action</url-pattern>
	</servlet-mapping>
	<!--post编码过滤器 -->
	<filter>
		<filter-name>encoding-filter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>encoding-filter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<!--shiro的filter -->
	<!--shiro过滤器，DelegatingFilterProxy通过代理模式将spring容器中的bean和filter关联起来 -->
	<filter>
		<filter-name>shiroFilter</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<!-- 设置为TRUE由Servlet容器进行filter生命周期的控制 Set whether to invoke the {@code 
			Filter.init} and {@code Filter.destroy} lifecycle methods on the target bean. 
			<p>Default is "false"; target beans usually rely on the Spring application 
			context for managing their lifecycle. Setting this flag to "true" means that 
			the servlet container will control the lifecycle of the target Filter, with 
			this proxy delegating the corresponding calls. -->
		<init-param>
			<param-name>targetFilterLifecycle</param-name>
			<param-value>true</param-value>
		</init-param>
		<!-- 设置spring容器的bean的ID，如果不设置则找与<filter-name></filter-name>中一致的bean -->
		<init-param>
			<param-name>targetBeanName</param-name>
			<param-value>shiroFilter</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>shiroFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<!--spring监听器 -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:spring/spring-*.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
</web-app>
````
log4j的配置文件:
````
log4j.rootLogger = DEBUG,stdout

log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern =[%-5p] %d{yyyy-MM-dd HH\:mm\:ss,SSS} method\:%l%n%m%n
````
db.properties配置文件：
````
jdbc.driver = com.mysql.jdbc.Driver
jdbc.url = jdbc:mysql://192.168.3.100:3306/qit-shiro?characterEncoding=UTF-8
jdbc.username = qit
jdbc.password = qit3875
````
基本上齐活了，如果现在运行此程序是完全没问题的，但是我们既然是一个系统，就需要进行用户的登陆认证和资源的授权，因此下面我们设置一下shiro。
### 第六章 shiro的配置 ###
1、首先在springmvc配置文件也就是spring-mvc.xml中设置开启aop代理支持，主要针对shiro的注解模式：
````
<!--开启aop代理支持，主要针对shiro的注解模式  -->
<aop:config proxy-target-class="true"></aop:config>
````
2、然后设置shiro的配置：
````
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd 
        http://www.springframework.org/schema/aop 
        http://www.springframework.org/schema/aop/spring-aop.xsd 
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd  
        http://www.springframework.org/schema/tx 
        http://www.springframework.org/schema/tx/spring-tx.xsd 
        http://www.springframework.org/schema/mvc 
        http://www.springframework.org/schema/mvc/spring-mvc.xsd  ">
	<!--web.xml对应的shirobean的配置 -->
	<!--过滤器 -->
	<bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
		<property name="securityManager" ref="securityManager"></property>
		<!--loginurl认证提交地址，如果没有认证将会请求此地址进行认证，请求此地址将会由formAuthenticationFilter进行表单认证 -->
		<property name="loginUrl" value="/login.action" />
		<!-- 认证成功统一跳转到first.action，建议不配置，shiro认证成功自动到上一个请求路径 -->
		<!-- <property name="successUrl" value="/first.action" /> -->
		<!-- 通过unauthorizedUrl指定没有权限操作时跳转页面 -->
		<property name="unauthorizedUrl" value="/refuse.jsp" />
		<!--过滤器链定义，从上向下执行，一般将/**放在最下面 -->
		<property name="filterChainDefinitions">
			<value>
				<!-- 对静态资源设置匿名访问 -->
				/image/** = anon
				/js/** = anon
				/styles/** = anon
				/loginIndex.action=anon
				/logout.action =logout
				<!--商品查询权限,拦截时候设置则会拦截，不设置就不会拦截 -->
				<!-- /item/queryItems.action=perms[item:query] /item/managerItem.action=perms[item:manager] -->
				/** = authc
				<!-- /** = authc 所有url都必须认证通过才可以访问 -->
				<!-- /** = anon所有url都可以匿名访问 -->
			</value>
		</property>
	</bean>
	<!--securitymanager配置 -->
	<bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
		<!--自定义realm -->
		<property name="realm" ref="customRealm"></property>
		<!--缓存管理器注入属性  -->
		<property name="cacheManager" ref="cacheManager"></property>
		<property name="sessionManager" ref="sessionManager"></property>
	</bean>
	<!--session管理器-->
	<bean id="sessionManager" class="org.apache.shiro.web.session.mgt.DefaultWebSessionManager">
		<!--session的失效时长，单位是毫秒  -->
		<property name="globalSessionTimeout" value="#{1 * 5 * 1000}"></property>
		<!--删除失效的session  -->
		<property name="deleteInvalidSessions" value="true"></property>
	</bean>
	<!--缓存管理器  -->
	<bean id="cacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
		<property name="cacheManagerConfigFile" value="classpath:shiro-ehcache.xml"></property>
	</bean>
	<!--定义凭证匹配器 -->
	<bean id="credentialsMatcher"
		class="org.apache.shiro.authc.credential.HashedCredentialsMatcher">
		<property name="hashAlgorithmName" value="md5"></property>
		<property name="hashIterations" value="1"></property>
	</bean>
	<!--自定义的realm -->
	<bean id="customRealm" class="com.qit.shiro.CustomRealm">
		<property name="credentialsMatcher" ref="credentialsMatcher"></property>
		
	</bean>
	<!--自定义表单验证  -->
	<bean id="cunstomFormAuthenticationFilter" class="com.qit.shiro.CustomFormAuthenticationFilter">
		<property name="usernameParam" value="username"></property>
		<property name="passwordParam" value="password"></property>
	</bean>
	<!--开启shiro的注解支持 -->
	<bean
		class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
		<property name="securityManager" ref="securityManager"></property>
	</bean>
</beans>
````
3、自定义realm,com.qit.shiro.CustomRealm
````
package com.qit.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.qit.exception.CustomException;
import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.custom.ActiveUser;
import com.qit.service.UserService;

public class CustomRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	// 用于授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 首先从凭证里获取到当前的用户名
		ActiveUser activeUser = (ActiveUser) principals.getPrimaryPrincipal();
		// 实际开发中会根据当前的用户名查找当前用户对应的角色和权限信息

		List<String> permissionList = new ArrayList<String>();
		List<Permission> permissionObjectList = null;
		try {
			permissionObjectList = userService.findPermissionListByUserId(activeUser.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (permissionObjectList != null) {
			for (Permission permission : permissionObjectList)
				permissionList.add(permission.getPercode());
		}
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addStringPermissions(permissionList);
		return simpleAuthorizationInfo;
	}

	// 用于认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 从token中取出用户身份信息
		String userCode = (String) token.getPrincipal();
		User user = userService.findUserByUserCode(userCode);
		if (user == null)
			return null;
		List<Permission> menuList = null;
		try {
			menuList = userService.findMenuListByUserId(user.getId());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ActiveUser activeUser = new ActiveUser();
		activeUser.setUsercode(user.getUsercode());
		activeUser.setUsername(user.getUsername());
		activeUser.setId(user.getId());
		activeUser.setMenuList(menuList);
		// 如果查询寻到，返回认证信息AuthenticationInfo
		SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(activeUser, user.getPassword(),
				ByteSource.Util.bytes(user.getSalt()), this.getName());
		// 如果查询不到
		return simpleAuthenticationInfo;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		super.setName("CustomRealm");
	}
	//清除缓存
	public void clearCached() {
		PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
		super.clearCache(principals);
	}

}

````
4、因为有验证码，所以需要重新写FormAuthenticationFilter验证过滤器
````
package com.qit.shiro;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import com.qit.message.SessionAttributeDictionary;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		// 进行验证码的校验，如果校验失败提示验证码错误，抛出异常
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession session = httpServletRequest.getSession();
		String validate_code = (String) session.getAttribute(SessionAttributeDictionary.VALIDATE_CODE);
		String reqValidateCode = httpServletRequest.getParameter(SessionAttributeDictionary.VALIDATE_CODE);
		if (validate_code != null && reqValidateCode != null) {
			if (!reqValidateCode.equalsIgnoreCase(validate_code)) {
				httpServletRequest.setAttribute("shiroLoginFailure", "RandomCodeError");
				//拒绝访问，不在验证账号和密码
				return true;
			}

		}
		return super.onAccessDenied(request, response);
	}

}
````
validate.jsp中已经将验证码写入到session中了，所以在用户输入验证码提交后，会首先被CustomFormAuthenticationFilter拦截，拦截后先判断验证码是否正确，如果不正确就拒绝访问，如果正确就继续允许访问，至此，我们的程序基本上是走通了
### 第六章测试 ###
登陆界面
![](http://i.imgur.com/RoIVLBG.png)

登陆后主页面

![](http://i.imgur.com/nZBNdsW.png)

### 总结 ###

个人认为学习技术还是需要领路人的，当然了，那些开发框架的大牛们另当别论，再次特别感谢传智播客的燕青老师，虽然他不认识我，但是看了他的视频绝对是长知识，而且每一步我都是一步一步跟着做的，很受用
花了一天的时间整理了这个文档，有喜欢的可以加我微信进行交流，如果你认为我的代码或者文档对你有帮助，请打赏0.9元（土豪不限），至少能够弥补我一天的辛苦。

![](http://i.imgur.com/8wQhOH0.png)

 




# SSM #
手把手教你创建Spring mvc +spring+mybatis+shiro
## 前言 ##
 接触SSM框架两个多月，可谓是收获颇丰，从最基础的jsp+servlet+Javabean开始，了解到了最原始的mvc开发模式，到mybatis+spring+springmvc的模式，从最初的一个一个方法的封装，一个一个对象的自定义，到渐渐习惯了把所有的对象交给spring进行管理，属性交给spring进行注入。可以这么说，spring框架的产生确实改变了程序员的编程习惯，同时也给程序员带来了极大的便利。好了，言归正传，下面开始咱们的springmvc+spring+mybatis之旅。
## 第一章准备环境 ##
首先准备jar包，mybatis的jar包、spring的jar包、shiro的jar包，spring和shiro整合的jar包，以及spring和mybatis整合的jar包，相关jar包已经整合到项目中，可以进行clone下载：

- 相关jar包如下：
![](http://i.imgur.com/JsjMLad.png)

其次准备工程目录，本次工程整合没有继承maven进行库的管理，因此很多包需要自己手工进行建，下面是我的工程目录：
![](http://i.imgur.com/mXD9VC5.png)

包名解释：

com.qit.controller   

- springmvc控制器所在包

com.qit.converter    

- 数据转换器所在包
	
com.qit.dao			

-  数据持久层所在包，包括mybatis的mapper接口和mapperxml文件
 
com.qit.exception    

- 自定义的异常和异常解析器所在包

com.qit.interceptor  

- 在基于URL登陆拦截方式时候拦截器所在的包，后面用的shiro可能不会用到

com.qit.message      

- 公共消息包，主要是类中的静态变量作为常量使用

com.qit.pojo         

- pojo包，主要是针对数据库中表所定义的bean类，一般使用mybatis-generator自动生成

com.qit.pojo.custom  

- pojocustom包，自定义的pojo，基本上是继承pojo包中的类

com.qit.service      

- service服务包，主要负责业务部分，此处存放接口

com.qit.service.impl 

- service各个接口实现类，主要负责业务部分

com.qit.shiro        

- shiro的realm的定义以及一些炎症类的实现

com.qit.util         

- 工具类

com.qit.validation 

- hibernate的validator的相关分组类

### 第二章spring和mybatis进行整合 ###
1、通过myeclipse的mybatis generator插件生成相关的mapper接口和mapper的xml文件，具体generator插件安装请度娘。此处提供generatorConfig.xml是generator配置文件，在安装好插件之后，邮件单机此xml文件，选择generate mybatis artifacts，即可生成相应的mapper接口和mapperxml文件：
```<?xml version="1.0" encoding="UTF-8"?>  
    <!DOCTYPE generatorConfiguration  
    PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"  
    "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
    <generatorConfiguration>
	<!-数据库驱动 -->
	<classPathEntry location="E:/projectlib/mysql-connector-java-5.1.41-bin.jar" />
	<context id="DB2Tables" targetRuntime="MyBatis3">
		<commentGenerator>
			<property name="suppressDate" value="true" />
			<!-是否去除自动生成的注释 true：是 ： false:否 -->
			<property name="suppressAllComments" value="true" />
		</commentGenerator>
		<!--数据库链接URL，用户名、密码 -->
		<jdbcConnection driverClass="com.mysql.jdbc.Driver"
			connectionURL="jdbc:mysql://192.168.3.100/qit-shiro" userId="qit" password="qit3875">
		</jdbcConnection>
		<javaTypeResolver>
			<property name="forceBigDecimals" value="false" />
		</javaTypeResolver>
		<!-生成pojo -->
		<javaModelGenerator targetPackage="com.qit.pojo"
			targetProject="SSM">
			<property name="enableSubPackages" value="false" />
			<property name="trimStrings" value="true" />
		</javaModelGenerator>
		<!-生成映射文件的mapperXML文件 -->
		<sqlMapGenerator targetPackage="com.qit.dao"
			targetProject="SSM">
			<property name="enableSubPackages" value="false" />
		</sqlMapGenerator>
		<!-生成mapper接口，dao接口 -->
		<javaClientGenerator type="XMLMAPPER"
			targetPackage="com.qit.dao" targetProject="SSM">
			<property name="enableSubPackages" value="false" />
		</javaClientGenerator>
		<!-要生成哪些表 -->
		<table tableName="sys_user" domainObjectName="User"></table>
		<table tableName="sys_role" domainObjectName="Role"></table>
		<table tableName="sys_user_role" domainObjectName="User_Role"></table>
		<table tableName="sys_role_permission" domainObjectName="Role_Permission"></table>
		<table tableName="sys_permission" domainObjectName="Permission"></table>
	</context>
</generatorConfiguration>
````
上面是generatorconfig.xml文件，下面是如何在myeclipse中生成相应的dao文件，上述xml文档中有相关注释。
![](http://i.imgur.com/LERGRZ8.png)
2、配置spring和mybatis整合配置文件
````<?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:p="http://www.springframework.org/schema/p" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd 
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd 
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd  
        http://www.springframework.org/schema/tx 
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/mvc 
        http://www.springframework.org/schema/tx/spring-mvc.xsd">
	<!--获取数据库配置文件,数据库配置文件中包含数据库的地址，用户名和密码等相关信息 -->
	<context:property-placeholder location="classpath:db.properties" />
	<!--获取数据源 ，此处不唯一，可以使用c3p0也可以使用阿里巴巴的德鲁伊，也可以使用其他数据源-->
	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
		<property name="user" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />
		<property name="driverClass" value="${jdbc.driver}" />
		<property name="jdbcUrl" value="${jdbc.url}" />
	</bean>
	<!--配置sqlsessionfactory，sqlsession很重要，将上面配置的数据源注入，并且制定相应的mybatis配置文件位置 -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource"></property>
		<property name="configLocation" value="classpath:mybatis/mybatis.xml"></property>
	</bean>
	<!--设置mapper扫描 ，spring启动后会扫描相关的mapper接口和mapperxml文件，此处设置扫描模式-->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
		<property name="basePackage" value="com.qit.dao" />
	</bean>
  </beans>
````
上述xml文件为工程目录config/spring/spring-mybatis.xml中的内容，此处主要是配置spring和mybatis的整合功能，上述文档中注释可以自行查看

3、至此，spring和mybatis整合结束

### 第三章springmvc spring mybatis整合 ###
1、springmvc因为是属于spring的一部分，因此不需要和spring整合，但是要设置一些配置
   首先是springmvc的dispatcherservlet设置，dispatcherservlet相当于一个中枢控制器，所有的web请求会先到dispatcherservlet，然后dispatcherservlet会调用处理器映射器查找此URL对应的处理器handler，然后dispatcherservlet会调用处理器适配器进行执行此handler，最后dispatcherservlet会将处理器handler处理后返回的modelandview分发到视图解析器，由视图解析器进行视图渲染然后交给用户。
   dispatcherservlet的配置如下，记住此处配置在web工程的web.xml下面。
````<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" id="WebApp_ID" version="3.0">
  <display-name>SSM</display-name>
  <welcome-file-list>
    <welcome-file>index.jsp</welcome-file>
  </welcome-file-list>
  <!--springmvc前端控制器配置 -->
	<servlet>
		<servlet-name>springmvc-action</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:springmvc/spring-*.xml</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>springmvc-action</servlet-name>
		<url-pattern>*.action</url-pattern>
	</servlet-mapping>
	<!--post编码过滤器 -->
	<filter>
		<filter-name>encoding-filter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>encoding-filter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<!--shiro的filter -->
	<!--shiro过滤器，DelegatingFilterProxy通过代理模式将spring容器中的bean和filter关联起来 -->
	<filter>
		<filter-name>shiroFilter</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<!-- 设置为TRUE由Servlet容器进行filter生命周期的控制 Set whether to invoke the {@code 
			Filter.init} and {@code Filter.destroy} lifecycle methods on the target bean. 
			<p>Default is "false"; target beans usually rely on the Spring application 
			context for managing their lifecycle. Setting this flag to "true" means that 
			the servlet container will control the lifecycle of the target Filter, with 
			this proxy delegating the corresponding calls. -->
		<init-param>
			<param-name>targetFilterLifecycle</param-name>
			<param-value>true</param-value>
		</init-param>
		<!-- 设置spring容器的bean的ID，如果不设置则找与<filter-name></filter-name>中一致的bean -->
		<init-param>
			<param-name>targetBeanName</param-name>
			<param-value>shiroFilter</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>shiroFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<!--spring监听器 -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:spring/spring-*.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
</web-app>
````
主要看dispatcherservlet部分，其余可以暂时忽略。
2、springmvc的配置文件
````<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd 
        http://www.springframework.org/schema/aop 
        http://www.springframework.org/schema/aop/spring-aop.xsd 
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd  
        http://www.springframework.org/schema/tx 
        http://www.springframework.org/schema/tx/spring-tx.xsd 
        http://www.springframework.org/schema/mvc 
        http://www.springframework.org/schema/mvc/spring-mvc.xsd  ">
	<!--开启aop代理支持，主要针对shiro的注解模式  -->
	<aop:config proxy-target-class="true"></aop:config>
	
	<!--扫描controller注解 -->
	<context:component-scan base-package="com.qit.controller"></context:component-scan>
	<!--处理器映射器和处理器适配器，其中配置了转换器和校验器 -->
	<mvc:annotation-driven conversion-service="convertionFactory"
		validator="hibernateValidator"></mvc:annotation-driven>
	<bean id="convertionFactory"
		class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
		<property name="converters">
			<list>
				<bean class="com.qit.converter.String2DateConverter"></bean>
			</list>
		</property>
	</bean>
	<!--validate校验器 -->
	<bean name="hibernateValidator"
		class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
		<!--校验器 -->
		<property name="providerClass" value="org.hibernate.validator.HibernateValidator"></property>
		<!--校验器文件,提供校验器的相关信息 -->
		<property name="validationMessageSource" ref="messageSource"></property>
	</bean>
	<bean id="messageSource"
		class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
		<property name="basenames">
			<list>
				<value>classpath:validate</value>
			</list>
		</property>
		<!--资源文件编码格式 -->
		<property name="fileEncodings" value="UTF-8"></property>
		<!--对资源文件缓存时间 -->
		<property name="cacheSeconds" value="120"></property>
	</bean>

	<!--配置视图解析器 -->
	<bean
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/WEB-INF/jsp/"></property>
		<property name="suffix" value=".jsp"></property>
	</bean>
	<!--静态资源的解析 -->
	<!-- <mvc:default-servlet-handler /> -->

	<!--springmvc拦截器,如果上面的处理器适配器配置的是RequestMappingHandlerAdapter类型的那么就需要在上面 
		进行配置，此处不需要，顺序很重要，先认证后授权 -->
	<!-- <mvc:interceptors> <mvc:interceptor> <mvc:mapping path="/**"/> <bean 
		class="com.qit.interceptor.LoginInInterceptor"></bean> </mvc:interceptor> 
		<mvc:interceptor> <mvc:mapping path="/**"/> <bean class="com.qit.interceptor.AuthencatieInterceptor"></bean> 
		</mvc:interceptor> </mvc:interceptors> -->

	<!--全局异常处理器，只此一个，定义在多也没用 -->
	<bean class="com.qit.exception.CustomExceptionResolver"></bean>

	<!--文件上传解析器 -->
	<bean id="multipartResolver"
		class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
		<property name="maxUploadSize">
			<value>102400</value>
		</property>
	</bean>

</beans>
````
spring-mvc.xml主要是配置springmvc相关的设置，在tomcat启动时候会加载此文件，进行相应的设置
3、controller的编写
  浏览器来到一个请求，首先经过dispatcherservlet的调度，会指定相应的handler处理，那么handler是如何编写那，handler有多种编写方法，如果在spring-mvc。xml配置文件中配置的是非注解的处理器映射器和非注解的处理器适配器，那么程序员就需要编写类来实现controller或者handler接口，如果使用的是注解驱动的处理器映射器和适配器，那么就需要程序员在编写handler时候加上注解，比如浏览器要查询
![](http://i.imgur.com/y6uAw4b.png)

对应的handler该如何编写：
````
package com.qit.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qit.message.SessionAttributeDictionary;
import com.qit.exception.CustomException;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;
import com.qit.service.UserService;

//controller注解表示此类是controller控制类，负责处理springmvc的请求
@Controller
public class UserController {
	//此处注解autowired表示由spring自动的将bean注入到此属性中
	@Autowired
	private UserService userService;
	//Requestmapping指示了URL请求的路径是/login.action时候将会由此方法进行处理
	@RequestMapping("/login.action")
	public String login(HttpServletRequest request) throws Exception {
		// 如果登陆失败从request中获取认证异常信息，shiroLoginFailure就是shiro异常类的全限定名
		String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
		// 根据shiro返回的异常类路径判断，抛出指定异常信息
		if (exceptionClassName != null) {
			if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
				// 最终会抛给异常处理器
				throw new CustomException("账号不存在");
			} else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
				throw new CustomException("用户名/密码错误");
			} else if ("RandomCodeError".equals(exceptionClassName)) {
				throw new CustomException("验证码错误 ");
			} else {
				throw new Exception();// 最终在异常处理器生成未知错误
			}
		}
		// 此方法不处理登陆成功（认证成功），shiro认证成功会自动跳转到上一个请求路径
		// 登陆失败还到login页面
		return "login";
	}

	// 登陆成功后跳转到首页
	@RequestMapping("/first.action")
	public String first(Model model) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
			model.addAttribute(SessionAttributeDictionary.SESSION_USER, activeUser);
		}
		return "first";
	}

	@RequestMapping("/loginIndex.action")
	public String loginIndex(Model model) {

		return "login";
	}

	@RequestMapping("/queryUsers.action")
	@RequiresPermissions("user:query")
	public String queryUsers(Model model) {
		List<UserCustom> userCustomList = userService.selctUserListUserName("%a%");
		model.addAttribute("userList", userCustomList);
		return "userList";

	}

	@RequestMapping("/clearShiroEhcache.action")
	public String clearShiroEhcache() {
		userService.clearShiroEhcache();
		return "first";
	}
}

````
### 第四章spring进行管理service ###
 service部分两部分组成，service接口和service接口实现类
 以用户登陆为例.com.qit.service.UserService接口为：
````
package com.qit.service;

import java.util.List;

import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;

public interface UserService {

	ActiveUser authenticateUser(UserCustom userCustom) throws Exception;

	User findUserByUserCode(String userCode);

	List<Permission> findMenuListByUserId(String id) throws Exception;

	List<Permission> findPermissionListByUserId(String id) throws Exception;

	List<UserCustom> selctUserListUserName(String userName);

	void clearShiroEhcache();

}
````
com.qit.service.impl.UserServiceImpl接口实现类为：
````
package com.qit.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.qit.dao.PermissionCustomMapper;
import com.qit.dao.UserMapper;
import com.qit.exception.CustomException;
import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.UserExample;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;
import com.qit.shiro.CustomRealm;
import com.qit.service.UserService;

public class UserServiceImpl implements UserService {
	//定义dao包中的UserMapper接口并且自动注入相关的bean
	@Autowired
	private UserMapper userMapper;
	//定义dao包中的PermissionCustomMapper接口并自动注入
	@Autowired
	private PermissionCustomMapper permissionCustomMapper;
	//定义shiro包中的custom包接口，此处为了清除自动登录的缓存
	@Autowired
	private CustomRealm customRealm;
	/*
	* 方法名称: authenticateUser
	* 方法描述:
	* @see com.qit.service.impl.UserService#authenticateUser(com.qit.pojo.custom.UserCustom)
	*/
	@Override
	public ActiveUser authenticateUser(UserCustom userCustom) throws Exception {
		User user = this.findUserByUserCode(userCustom.getUsercode());
		if (user == null)
			throw new CustomException("账号不存在");
		if (userCustom.getPassword().equals(user.getPassword())) {
			ActiveUser activeUser = new ActiveUser();
			activeUser.setId(user.getId());
			activeUser.setUsercode(user.getUsercode());
			activeUser.setUsername(user.getUsername());
			List<Permission> permissionList = this.findPermissionListByUserId(user.getId());
			List<Permission> menuList = this.findMenuListByUserId(user.getId());
			activeUser.setMenuList(menuList);
			activeUser.setPermissionList(permissionList);
			return activeUser;
		} else {
			throw new CustomException("用户密码错误!");
		}

	}

	/*
	* 方法名称: findUserByUserCode
	* 方法描述:
	* @see com.qit.service.impl.UserService#findUserByUserCode(java.lang.String)
	*/
	@Override
	public User findUserByUserCode(String userCode) {
		UserExample example = new UserExample();
		com.qit.pojo.UserExample.Criteria criteria = example.createCriteria();
		criteria.andUsercodeEqualTo(userCode);
		List<User> userList = userMapper.selectByExample(example);
		if (userList != null && userList.size() == 1)
			return userList.get(0);
		return null;
	}

	/*
	* 方法名称: findMenuListByUserId
	* 方法描述:
	* @see com.qit.service.impl.UserService#findMenuListByUserId(java.lang.String)
	*/
	@Override
	public List<Permission> findMenuListByUserId(String id) throws Exception {
		return permissionCustomMapper.findMenuListByUserId(id);
	}

	/*
	* 方法名称: findPermissionListByUserId
	* 方法描述:
	* @see com.qit.service.impl.UserService#findPermissionListByUserId(java.lang.String)
	*/
	@Override
	public List<Permission> findPermissionListByUserId(String id) throws Exception {
		return permissionCustomMapper.findPermissionListByUserId(id);
	}

	public List<UserCustom> selctUserListUserName(String userName) {
		UserExample example = new UserExample();
		com.qit.pojo.UserExample.Criteria criteria = example.createCriteria();
		criteria.andUsernameLike(userName);
		List<User> userList = userMapper.selectByExample(example);
		List<UserCustom> userCustomList = new ArrayList<UserCustom>();
		for (User user : userList) {
			UserCustom userCustom = new UserCustom();
			BeanUtils.copyProperties(user, userCustom);
			userCustomList.add(userCustom);
		}
		return userCustomList;
	}

	@Override
	public void clearShiroEhcache() {
		
		customRealm.clearCached();
		
	}

}
````
可能看到这里有些童鞋蒙圈了，怎么回事一会这个一会那个。。。好了 我现在来捋一捋，上面看到我们先设置了web.xml文件定义了dispatcherservlet的路径，然后配置了springmvc的配置文件，之后我们定义controller注解类，然后我们定义service类。在这一连串过程中其实有一条线，就是springmvc负责url请求，负责自动的将请求映射到controller类中相应的方法里，然后由方法调用service实现类中的相应的方法进行实现，在service实现类中我们又调用了dao中相应的额接口中的方法，在mybatis中会自动为相应的接口生成代理的类，然后根据对应的xml文件进行数据操作。这就是一条完整的线。

### 第五章 spring配置，其他一些配置文件设置 ###
 1、我们前面用到的是springmvc,那么spring其实也是需要进行配置，比如我们定义了service接口和service实现类，就需要交给spring进行管理，还有就是相关的数据库信息，数据校验信息，日志信息等也需要进行配置：
spring主要是在web.xml中进行配置，上面在讲springmvc时候已经把web.xml文档贴出来了，那个就是全的，我再贴一次:
````
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" id="WebApp_ID" version="3.0">
  <display-name>SSM</display-name>
  <welcome-file-list>
    <welcome-file>index.jsp</welcome-file>
  </welcome-file-list>
  <!--springmvc前端控制器配置 -->
	<servlet>
		<servlet-name>springmvc-action</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:springmvc/spring-*.xml</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>springmvc-action</servlet-name>
		<url-pattern>*.action</url-pattern>
	</servlet-mapping>
	<!--post编码过滤器 -->
	<filter>
		<filter-name>encoding-filter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>encoding-filter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<!--shiro的filter -->
	<!--shiro过滤器，DelegatingFilterProxy通过代理模式将spring容器中的bean和filter关联起来 -->
	<filter>
		<filter-name>shiroFilter</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<!-- 设置为TRUE由Servlet容器进行filter生命周期的控制 Set whether to invoke the {@code 
			Filter.init} and {@code Filter.destroy} lifecycle methods on the target bean. 
			<p>Default is "false"; target beans usually rely on the Spring application 
			context for managing their lifecycle. Setting this flag to "true" means that 
			the servlet container will control the lifecycle of the target Filter, with 
			this proxy delegating the corresponding calls. -->
		<init-param>
			<param-name>targetFilterLifecycle</param-name>
			<param-value>true</param-value>
		</init-param>
		<!-- 设置spring容器的bean的ID，如果不设置则找与<filter-name></filter-name>中一致的bean -->
		<init-param>
			<param-name>targetBeanName</param-name>
			<param-value>shiroFilter</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>shiroFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<!--spring监听器 -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:spring/spring-*.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
</web-app>
````
log4j的配置文件:
````
log4j.rootLogger = DEBUG,stdout

log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern =[%-5p] %d{yyyy-MM-dd HH\:mm\:ss,SSS} method\:%l%n%m%n
````
db.properties配置文件：
````
jdbc.driver = com.mysql.jdbc.Driver
jdbc.url = jdbc:mysql://192.168.3.100:3306/qit-shiro?characterEncoding=UTF-8
jdbc.username = qit
jdbc.password = qit3875
````
基本上齐活了，如果现在运行此程序是完全没问题的，但是我们既然是一个系统，就需要进行用户的登陆认证和资源的授权，因此下面我们设置一下shiro。
### 第六章 shiro的配置 ###
1、首先在springmvc配置文件也就是spring-mvc.xml中设置开启aop代理支持，主要针对shiro的注解模式：
````
<!--开启aop代理支持，主要针对shiro的注解模式  -->
<aop:config proxy-target-class="true"></aop:config>
````
2、然后设置shiro的配置：
````
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd 
        http://www.springframework.org/schema/aop 
        http://www.springframework.org/schema/aop/spring-aop.xsd 
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd  
        http://www.springframework.org/schema/tx 
        http://www.springframework.org/schema/tx/spring-tx.xsd 
        http://www.springframework.org/schema/mvc 
        http://www.springframework.org/schema/mvc/spring-mvc.xsd  ">
	<!--web.xml对应的shirobean的配置 -->
	<!--过滤器 -->
	<bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
		<property name="securityManager" ref="securityManager"></property>
		<!--loginurl认证提交地址，如果没有认证将会请求此地址进行认证，请求此地址将会由formAuthenticationFilter进行表单认证 -->
		<property name="loginUrl" value="/login.action" />
		<!-- 认证成功统一跳转到first.action，建议不配置，shiro认证成功自动到上一个请求路径 -->
		<!-- <property name="successUrl" value="/first.action" /> -->
		<!-- 通过unauthorizedUrl指定没有权限操作时跳转页面 -->
		<property name="unauthorizedUrl" value="/refuse.jsp" />
		<!--过滤器链定义，从上向下执行，一般将/**放在最下面 -->
		<property name="filterChainDefinitions">
			<value>
				<!-- 对静态资源设置匿名访问 -->
				/image/** = anon
				/js/** = anon
				/styles/** = anon
				/loginIndex.action=anon
				/logout.action =logout
				<!--商品查询权限,拦截时候设置则会拦截，不设置就不会拦截 -->
				<!-- /item/queryItems.action=perms[item:query] /item/managerItem.action=perms[item:manager] -->
				/** = authc
				<!-- /** = authc 所有url都必须认证通过才可以访问 -->
				<!-- /** = anon所有url都可以匿名访问 -->
			</value>
		</property>
	</bean>
	<!--securitymanager配置 -->
	<bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
		<!--自定义realm -->
		<property name="realm" ref="customRealm"></property>
		<!--缓存管理器注入属性  -->
		<property name="cacheManager" ref="cacheManager"></property>
		<property name="sessionManager" ref="sessionManager"></property>
	</bean>
	<!--session管理器-->
	<bean id="sessionManager" class="org.apache.shiro.web.session.mgt.DefaultWebSessionManager">
		<!--session的失效时长，单位是毫秒  -->
		<property name="globalSessionTimeout" value="#{1 * 5 * 1000}"></property>
		<!--删除失效的session  -->
		<property name="deleteInvalidSessions" value="true"></property>
	</bean>
	<!--缓存管理器  -->
	<bean id="cacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
		<property name="cacheManagerConfigFile" value="classpath:shiro-ehcache.xml"></property>
	</bean>
	<!--定义凭证匹配器 -->
	<bean id="credentialsMatcher"
		class="org.apache.shiro.authc.credential.HashedCredentialsMatcher">
		<property name="hashAlgorithmName" value="md5"></property>
		<property name="hashIterations" value="1"></property>
	</bean>
	<!--自定义的realm -->
	<bean id="customRealm" class="com.qit.shiro.CustomRealm">
		<property name="credentialsMatcher" ref="credentialsMatcher"></property>
		
	</bean>
	<!--自定义表单验证  -->
	<bean id="cunstomFormAuthenticationFilter" class="com.qit.shiro.CustomFormAuthenticationFilter">
		<property name="usernameParam" value="username"></property>
		<property name="passwordParam" value="password"></property>
	</bean>
	<!--开启shiro的注解支持 -->
	<bean
		class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
		<property name="securityManager" ref="securityManager"></property>
	</bean>
</beans>
````
3、自定义realm,com.qit.shiro.CustomRealm
````
package com.qit.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.qit.exception.CustomException;
import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.custom.ActiveUser;
import com.qit.service.UserService;

public class CustomRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	// 用于授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 首先从凭证里获取到当前的用户名
		ActiveUser activeUser = (ActiveUser) principals.getPrimaryPrincipal();
		// 实际开发中会根据当前的用户名查找当前用户对应的角色和权限信息

		List<String> permissionList = new ArrayList<String>();
		List<Permission> permissionObjectList = null;
		try {
			permissionObjectList = userService.findPermissionListByUserId(activeUser.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (permissionObjectList != null) {
			for (Permission permission : permissionObjectList)
				permissionList.add(permission.getPercode());
		}
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addStringPermissions(permissionList);
		return simpleAuthorizationInfo;
	}

	// 用于认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 从token中取出用户身份信息
		String userCode = (String) token.getPrincipal();
		User user = userService.findUserByUserCode(userCode);
		if (user == null)
			return null;
		List<Permission> menuList = null;
		try {
			menuList = userService.findMenuListByUserId(user.getId());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ActiveUser activeUser = new ActiveUser();
		activeUser.setUsercode(user.getUsercode());
		activeUser.setUsername(user.getUsername());
		activeUser.setId(user.getId());
		activeUser.setMenuList(menuList);
		// 如果查询寻到，返回认证信息AuthenticationInfo
		SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(activeUser, user.getPassword(),
				ByteSource.Util.bytes(user.getSalt()), this.getName());
		// 如果查询不到
		return simpleAuthenticationInfo;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		super.setName("CustomRealm");
	}
	//清除缓存
	public void clearCached() {
		PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
		super.clearCache(principals);
	}

}

````
4、因为有验证码，所以需要重新写FormAuthenticationFilter验证过滤器
````
package com.qit.shiro;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import com.qit.message.SessionAttributeDictionary;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		// 进行验证码的校验，如果校验失败提示验证码错误，抛出异常
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession session = httpServletRequest.getSession();
		String validate_code = (String) session.getAttribute(SessionAttributeDictionary.VALIDATE_CODE);
		String reqValidateCode = httpServletRequest.getParameter(SessionAttributeDictionary.VALIDATE_CODE);
		if (validate_code != null && reqValidateCode != null) {
			if (!reqValidateCode.equalsIgnoreCase(validate_code)) {
				httpServletRequest.setAttribute("shiroLoginFailure", "RandomCodeError");
				//拒绝访问，不在验证账号和密码
				return true;
			}

		}
		return super.onAccessDenied(request, response);
	}

}
````
validate.jsp中已经将验证码写入到session中了，所以在用户输入验证码提交后，会首先被CustomFormAuthenticationFilter拦截，拦截后先判断验证码是否正确，如果不正确就拒绝访问，如果正确就继续允许访问，至此，我们的程序基本上是走通了
### 第六章测试 ###
登陆界面
![](http://i.imgur.com/RoIVLBG.png)

登陆后主页面

![](http://i.imgur.com/nZBNdsW.png)

### 总结 ###

个人认为学习技术还是需要领路人的，当然了，那些开发框架的大牛们另当别论，再次特别感谢传智播客的燕青老师，虽然他不认识我，但是看了他的视频绝对是长知识，而且每一步我都是一步一步跟着做的，很受用
花了一天的时间整理了这个文档，有喜欢的可以加我微信进行交流，如果你认为我的代码或者文档对你有帮助，请打赏0.9元（土豪不限），至少能够弥补我一天的辛苦。
![](http://i.imgur.com/EXkkvbm.png)

 




