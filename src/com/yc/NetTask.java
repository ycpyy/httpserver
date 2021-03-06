package com.yc;

import com.yc.threadpool.Taskable;

import java.io.*;
import java.io.OutputStream;
import java.net.Socket;

//一个任务类完成与一个客户的处理.
public class NetTask implements Runnable, Taskable {
    private Socket s;
    private InputStream iis;
    private OutputStream oos;

    public NetTask(Socket s) {
        this.s=s;
    }

    @Override
    public  void doTask(){
        run();
    }

    @Override
    public void run() {
        // 取出流   socket
      try {
              this.iis = this.s.getInputStream();
              this.oos = this.s.getOutputStream();
              //request功能就是解析? 请求行，请示头域，请求实体...
              HttpServletRequest request = new HttpServletRequest(this.iis,this.s);
              request.parse();
              //为什么在response中要有一个request呢? 因为响应时要知道请求中请求的资源地址
              HttpServletResponse response = new HttpServletResponse(this.oos, request);

              //判断  request中资源到底是静态的还是动态的
             Processor processor=null;
             if( request.getRequestURI().endsWith(".action")){
                 //动态资源
             }else{
                 //静态资源
                 processor=new StaticProcessor();
             }
             processor.process(request,response);
            // Connection: keep-alive
            this.s.close();   // http协议. 无状态
        }catch( Exception ex){
            ex.printStackTrace();
        }

    }
}
