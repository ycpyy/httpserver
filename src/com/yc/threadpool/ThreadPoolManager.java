package com.yc.threadpool;

import java.util.Vector;

/*
线程池管理器:
   1. 初始线程池容器
   2. 执行任务
 */
public class ThreadPoolManager {
    //线程池容器
    private Vector<SimpleThread> vector;
    //最多的线程数
    private int maxThread;
    private static int coreCounts;

    static{
        //1. 获取系统核数
        coreCounts= Runtime.getRuntime().availableProcessors();
        System.out.println("系统核数:"+coreCounts);
        System.out.println("线程池开始初始化");
    }

    public ThreadPoolManager(){
        this(    coreCounts*2 );  //调用当前类的构造方法.
    }

    public ThreadPoolManager( int threadCount){
        vector=new Vector<SimpleThread>();
        //2. ，创建  SimpleThread对象，存到vector中
        for( int i=0;i<threadCount;i++){
            SimpleThread st=new SimpleThread(   );
            st.setName("线程"+(i+1));
            st.setDaemon(true);   //精灵线程
            vector.add(   st );
            st.start();
        }
        //3. 启动  Simplethread对象   start()   ->  进入到就绪  ->|||||| XXXX jvm来调用-> run()
        //       注意点: 新线程要进入wait()
    }

    public void process(  Taskable task   ){
        //1. task不能为空
        if(   task==null ){
            return;
        }
        //2. 从这个vector取出一个线程.  （  循环/随机/hash/weight  ->   TODO: ）
        SimpleThread st=getFreeSimpleThread();
        //3. 将task绑定到  simpleThread中
        st.setTask(task);
        //4. 设置这个simpleThread的状态为运行态
        st.setRunningFlag(true);
    }
    /*
    循环/随机/hash/weight 获取一个空闲的thread
     */
    private   SimpleThread getFreeSimpleThread(  ){
        int j=0;
        for( int i=0;i<vector.size();i++){
            SimpleThread stt=vector.get(i);
            j++;
            if(  stt.isRunning()==false   ){
                return stt;
            }
        }
        //线程数不够，则产生新线程，存到vector中.
        System.out.println("线程池没有空线程了，扩容 :"+ coreCounts+"个新线程");
        for(  int i=0;i<coreCounts;i++){
            SimpleThread st=new SimpleThread();
            st.setName("线程"+(i+1));
            st.setDaemon(true);   //精灵线程
            vector.add(   st );
            st.start();
        }
        //return getFreeSimpleThread();   //递归方案
        return vector.get(j);
    }


}
