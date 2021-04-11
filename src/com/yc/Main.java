package com.yc;


import com.yc.threadpool.ThreadPoolManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Main {
    //TODO: 日志的创建
    private static Logger logger= Logger.getLogger(Main.class.getName());
    private static ThreadPoolManager tpm;

    static{
        //System.out.println(    System.getProperties() );
        //System.out.println(  "\n\n"+System.getProperty("user.dir") );
        //System.out.println( File.separator  );
        String userDir=System.getProperty("user.dir")+ File.separator+"conf"+File.separator+"log4j.properties";
        //System.out.println(   userDir);
        // PropertyConfigurator 是log4j框架提供的配置类
        PropertyConfigurator.configure( userDir);
    }

    public static void main(String[] args) {
        Map<String,String> xmlPros=initXml();
        if( "true".equalsIgnoreCase(xmlPros.get("threadpool"))){
            tpm=new ThreadPoolManager();
        }
        //jdk1.8的新的try...catch 释放资源的方法
        try ( ServerSocket ss=new ServerSocket(  Integer.parseInt( xmlPros.get("port"))  );){
            logger.info(  ss.getInetAddress()+"正常启动，监听"+ss.getLocalPort()+"端口"  );
            while( true ){
                Socket s=ss.accept();
                logger.info(  s.getRemoteSocketAddress()+"联接到服务器"  );
                if("true".equalsIgnoreCase(xmlPros.get("threadpool"))){
                    tpm.process(new NetTask(s));
                }else{
                    Thread t=new Thread(new NetTask(s));
                    t.setDaemon(true);
                    t.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(   e.getMessage() );
        }
    }

    /**
     * 1. 读取xml文件．　:
     *    原始: dom, sax
     *    框架: dom4j,  jdom
     *    解析出server.xml的port, threadpool,  shutdown_port存入map
     * 2. 项目路径:
     * @return
     */
    private static Map<String,String> initXml(){
        Map<String,String> xmlPros=new HashMap<String,String>();
        //TODO：读取ｘｍｌ
        //     dom, sax
        //   ***  dom4j   jdom
        //  J2EE中自带的xml解析器  javax.xml.parsers.DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 通过DocumentBulderFoctory创建XML解释器
        try {
            DocumentBuilder bulider = factory.newDocumentBuilder(); // 通过解释器创建一个可以加载并生成XML的DocumentBuilder
            //TODO: 发布后是否会有路径问题
            Document doc = bulider.parse("conf/server.xml"); // 通过DocumentBuilder加载并生成一颗XML树.Document对象的实例
            // javascript中的dom的解析方式
            NodeList nl = doc.getElementsByTagName("Server"); // 通过Document可以遍历这颗树.并读取相应节点中的内容
            for (int i = 0; i < nl.getLength(); i++) {
                Element node = (Element) nl.item(i);
                String value=node.getAttribute("shutdown");
                if( "SHUTDOWN".equals(value)){
                    String port=node.getAttribute("port");
                    xmlPros.put("shutdown_port",port);
                }
                // node是server节点, 取它下面的一个connector节点
                NodeList nls=node.getElementsByTagName("Connector");
                for (int j = 0; j < nls.getLength(); j++) {
                    Element node2 = (Element) nls.item(j);
                    String port="9090";
                    String threadpool="false";
                    if(   node2.getAttribute("port")!=null){
                         port=node2.getAttribute("port");
                    }
                    if( node2.getAttribute("threadpool")!=null){
                        threadpool= node2.getAttribute("threadpool"  );
                    }
                    xmlPros.put("port",port);
                    xmlPros.put(  "threadpool", threadpool  );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(   e.getMessage() );
        }
        return xmlPros;
    }
}
