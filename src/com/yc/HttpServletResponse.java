package com.yc;

import java.io.*;

/**
 * 功能:
 * 1. 根据request中的资源地址以流的方式读取资源
 * 2. 拼接http 的响应
 * 3. 以输出流输出
 */
public class HttpServletResponse {
    private OutputStream oos;
    private HttpServletRequest request;

    public HttpServletResponse(   OutputStream oos, HttpServletRequest request  ){
        this.oos=oos;
        this.request=request;
    }

    /** 拼接响应 **/
    public void sendRedirect(){
        String responseprotocol=null;//响应协议
        byte[] fileContent=null;
        String uri=request.getRequestURI();

        File f=new File( request.getRealPath(),uri);
        if( !f.exists() ){
            //
            File file404=new File( request.getRealPath(),"404.html");
            fileContent=readFile(file404);
            responseprotocol=gen404(file404,fileContent);
        }else{
            //
            fileContent=readFile(f);
            responseprotocol=gen200(f,fileContent);
        }

        try {
            this.oos.write(responseprotocol.getBytes());
            this.oos.flush();
            this.oos.write(fileContent);
            this.oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if( this.oos!=null){
                try {
                    this.oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public byte[] readFile(File file){
        byte[] bs=null;
        //
        InputStream iis=null;
        try {
            iis=new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bs=IoUtil.readFromInputStream(iis);
        return  bs;
    }

    public String gen200(File file,byte[] fileContent){
        String result=null;
        String uri=this.request.getRequestURI();
        int index=uri.lastIndexOf(".");
        if( index>=0){
            index+=+1;
        }
        String fileExtension=uri.substring(index);
        if("JPG".equalsIgnoreCase(fileExtension) || "JPEG".equalsIgnoreCase(fileExtension)){
            result="HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: image/jpeg\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else if("PNG".equalsIgnoreCase(fileExtension)){
            result="HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: image/png\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else if ("json".equalsIgnoreCase(fileExtension)){
            result="HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: application/json\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else if ("css".equalsIgnoreCase(fileExtension)){
            result="HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: text/css\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else if("js".equalsIgnoreCase(fileExtension)){
            result="HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: application/javascript\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else{
            result="HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: text/html;charset=UTF-8\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }
        return  result;
    }

    public String gen404(File file,byte[] fileContent){
        String result=null;
        result="HTTP/1.1 404\\r\\nAccept-Ranges: bytes\\r\\nContent-Type: text/html;charset=UTF-8\\r\\nContent-Length:"+fileContent.length+"\r\n\r\n";
        return  result;
    }

}
