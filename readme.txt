第一个版本的目标:
1. 在浏览器中输入  http://localhost:9090/kaw/index.html
   就能显示 网站的信息
2. server容器
3. jsp容器


1. 日志配置
    按天生成日志文件到 logs 目录
2. 服务器的启动代码:
    a) 读取  conf下的server.xml中的端口配置.
       技术:  xml解析:
                    原生: dom, sax
                    框架: jdom, dom4j
    b) 启动服务器   创建ServerSocket( 端口),
          服务器 accept()
    c) ａｃｃｅｐｔ（）一个ｓｏｃｋｅｔ后，启动一个线程来处理．　