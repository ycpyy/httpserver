package com.yc;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    //TODO: 日志的创建
    private static Logger logger= Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Map<String,String> xmlPros=initXml();
        try ( ServerSocket ss=new ServerSocket(  Integer.parseInt( xmlPros.get("port"))  );){
            logger.info(  ss.getInetAddress()+"正常启动，监听"+ss.getLocalPort()+"端口"  );
            while( true ){
                Socket s=ss.accept();
                logger.info(  s.getRemoteSocketAddress()+"联接到服务器"  );
                //TODO：考虑线程和普通线程
                Thread t=new Thread(     new NetTask(s)   );
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(   e.getMessage() );
        }
    }

    /**
     * 读取xml文件．　
     * @return
     */
    private static Map<String,String> initXml(){
        Map<String,String> xmlPros=new HashMap<String,String>();
        //TODO：读取ｘｍｌ
        //     dom, sax
        //   ***  dom4j   jdom
        return xmlPros;
    }
}
