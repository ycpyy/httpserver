package com.yc;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

//标准的  J2ee中定诉  request的模样
public class HttpServletRequest {
    private InputStream iis;
    private Socket socket;

    private String realPath;  //D:\apache-tomcat-9.0.43\webapps\test1\  System.getProperty("user.dir")
    private String requestURI; //   /test1/a.jsp
    private String requestURL; //   http://localhost:8080/test1/a.jsp
    private String queryString; //  name=zy&age=20

    private String method;    //方法
    private Map<String,String> headers=new ConcurrentHashMap<String, String>();  //头域
    private String uri;     //请求的资源地址
    private String protocol;   //协议的版本

    private Map<String,String[]> parameterMap=new ConcurrentHashMap<String,String[]>();

    public HttpServletRequest(  InputStream iis,Socket socket){
        this.iis=iis;
        this.socket=socket;
    }

    public void parse(){
        //1. 从iis中取出协议   String
        String protocolContent=readProtocolFromInputStream();
        //2. 解析
        parseProtocol(protocolContent);
        System.out.println("hello world");
    }
    // 解析: 请求行，请求头域，实体..参数  存信息到  headers及其它的域中.
    private void parseProtocol(String protocolContent) {
        if( protocolContent==null || "".equals(protocolContent)){
            return;   //TODO:  注意：此时，应通过response生成404响应
        }
        //字符串分隔符：对字符串自动以 空格，回车，换行来切割
        StringTokenizer st=new StringTokenizer( protocolContent,"\r\n");
        int index=0;  //标识第一行
        while( st.hasMoreElements() ) { //按行循环
            String line = st.nextToken();//取每一行
            if(index==0){  //如果是第一行，则解析第一行
                String[] first=line.split(" ");
                this.method = first[0];
                this.uri = first[1];
                this.protocol = first[2];

                //解析出realPath
                this.realPath=System.getProperty("user.dir")+ File.separator+"webapps"+File.separator+this.uri.split("/")[0];
                this.requestURI=this.uri.split("\\?")[0];
                if(  "HTTP/1.1".equals(this.protocol) || "HTTP/1.0".equals(this.protocol)){
                    this.requestURL="http://"+this.socket.getLocalSocketAddress()+this.requestURI;
                }
                if(this.uri.indexOf("?")>=0){
                    this.queryString=this.uri.split("\\?")[1];
                    String[] params=this.queryString.split("&");
                    for( int i=0;i<params.length;i++){
                        String [] pv=params[i].split("=");
                        if( pv[1].indexOf(",")>=0){
                            String[] values=pv[1].split(",");
                            this.parameterMap.put(pv[0],values);
                        }else{
                            this.parameterMap.put(pv[0],new String[]{pv[1]});
                        }
                    }
                }



            }else if( "".equals(line) ){
                if( "POST".equals(line) ){
                    //以下的数据都是请求实体部分的数据了，比如 post的参数
                    parseParams(st);
                }
                break;
            }else{
                String[] heads=line.split(":");
                headers.put(heads[0],heads[1]);
            }
            index++;
        }

    }

    //解析请求的实体参数
    private void parseParams(StringTokenizer st){
        while( st.hasMoreElements() ){
            String line=st.nextToken();
            String[] params=line.split("&");
            for( int i=0;i<params.length;i++){
                String[] pv=params[i].split("=");
                if( pv[1].indexOf(",")>=0 ){
                    String[] values=pv[1].split(",");
                    this.parameterMap.put(pv[0],values);
                }else{
                    this.parameterMap.put(pv[0],new String[]{pv[1]});
                }
            }
        }
    }

    //从iis中取出协议   String
    private String readProtocolFromInputStream() {
        String protocolContent=null;
       // byte[] bs=IoUtil.readFromInputStream(this.iis);
       // protocolContent=new String(  bs  );
       // System.out.println(  protocolContent);
        StringBuffer sb=new StringBuffer(  1024*30  );
        int length=-1;
        byte[] bs=new byte[1024*30];
        try {
            length = this.iis.read(bs);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        for( int i=0;i<length;i++){
            sb.append(  (char)bs[i] );
        }
        protocolContent=sb.toString();
         return protocolContent;
    }

    public String getRequestURI(){
        return  requestURI;
    }

    public String getRealPath(){
        return  realPath;
    }

    public String getHeader(String headerName){
        if(   headers!=null){
            return headers.get(  headerName);
        }
        return null;
    }

    public String getProtocol() {
        return protocol;
    }

    public InputStream getInputStream(){
        return this.iis;
    }

    public String getMethod(){
        return this.method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getUri() {
        return uri;
    }
}
