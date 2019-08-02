# clientone
springboot shiro CAS buji-pac4j 整合客户端，通过访问CAS server实现单点登录（Single Sign On）

+ 复制此客户端后发布(clienttwo)，通过CAS认证后可以相互访问不用再次登录.
+ CAS server v5.2.6  源码请访问[这里](https://github.com/youngLake/cas5.2.6)

## 配置说明：
+ 首先运行cas-server，保证其运行。
(可以用上面的改造好的，也可以用原生的cas-server。
只要等成功登陆cas-server即可)
+ 在application.yml配置 `cas.server.url` 例如：http://test.test.com:8081/demo
和 `cas.project.url` 例如 http://test.test.com:8080 
(test.test.com 在hosts文件映射的localhost)
+ 运行clietone 并访问 test.test.com:8080/clientone/logon 会自动跳转到
cas-server的登录页，登录成功后，会显示 `resources/templates/index.html` 页面的内容