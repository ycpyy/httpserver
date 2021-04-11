package com.yc;

/**
 * 资源处理接口
 */
public interface Processor {
    /**
     * 处理方法
     * @param request
     * @param response
     */
    public void process(HttpServletRequest request,HttpServletResponse response);

}
