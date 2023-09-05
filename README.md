# 简易云盘后端代码

前端代码链接：https://github.com/yirenyixin/pan-vue

Springboot+nginx+mysql

这个项目只是练手项目，只实现了云盘部分核心功能。

实现功能：查询文件树，文件上传，文件下载，文件分享，新增文件夹，删除文件夹，定时器删除过时分享链接。文件上传、下载一次只能选择一个文件，分享只能一次选择一个文件或文件夹。



## 配置数据库

1. 找到配置文件application.properties


2. 输入数据库相关配置信息


   ```yaml
   # 配置端口号为8081 端口号可自行配置
   server.port=8081
   
   # 配置数据库
   # 配置驱动
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   # 若连接的是云数据库则将easypan改为云端ip  easypan为本地数据库名
   spring.datasource.url=jdbc:mysql://localhost:3306/easypan?serverTimezone=UTC
   # Mysql用户  
   spring.datasource.username=root
   # Mysql对应用户密码  
   spring.datasource.password=123456
   # 设置文件下载不能超过5000mb，可以自己改
   spring.servlet.multipart.max-file-size=5000MB
   spring.servlet.multipart.max-request-size=5000MB
   ```
## 配置nginx
1.找到nginx的下载路径进入到conf文件夹
2.打开conf文件夹下的nginx.conf，添加以下配置
````
     #下载代理
     location /download/ {
         alias "E:/easypan/";  # 文件存储路径，可以修改成自己的路径
         autoindex off;  # 关闭目录浏览
         add_header Content-Disposition 'attachment';  # 设置下载头
     } 
     #文件分享代理
     location /share/ {
        proxy_pass http://localhost:8080;  # 将请求代理到后端服务器
        # 可以添加其他反向代理配置，如反向代理缓存等
    }

````
## 其他配置

注册用户后生成相应的文件夹没有写，需要自己创建，保存位置的文件夹也要自己创建。
如选择E:/easypan/为云盘位置要创建，还有注册的两个用户，test1，test2，也要在E:/easypan/创建test1和test2文件夹。


postman查看文件树 http://localhost:8081/api/path/get

返回的数据类似
````
{
  "save_path": "file/",
  "space": 0,
  "children": [
    {
      "save_path": "file1/",
      "space": 1,
      "children": [
        {
          "save_path": "txt1.txt",
          "space": 3,
          "file_type": "txt"
        },
        {
          "save_path": "jpg1.jpg",
          "space": 3,
          "file_type": "jpg"
        }
      ]
    },
    {
      "save_path": "file2/",
      "space": 1,
      "children": [
        {
          "save_path": "txt2.txt",
          "space": 3,
          "file_type": "txt"
        }
      ]
    }
  ],
  "uname": "uname"
}

````
