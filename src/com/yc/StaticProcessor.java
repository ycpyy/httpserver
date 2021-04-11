package com.yc;

public class StaticProcessor implements Processor{


    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) {
        //静态资源处理是直接调用   HttpServletRequest  HttpServletResponse
        response.sendRedirect();
    }
}
