package com.yc.threadpool;

/*
  SimpleThread:
 1. 初始化创建:
     构造方法: ->  runningFlag=false
     start()  ->  jvm 调用run()  ->  this.wait();
 2. 有任务过来:
     manager  -> vector取一个 SimpleThread对象
      调用setTask()
      调用 setRunningFlag( true)  -> this.notify() ->
        this.task不为空, 则调用task.doTask()  ->  任务运行完毕
       ->  setRunningFlag(false);  ->  wait()
 */
public class SimpleThread extends Thread{
   // private int count;   // 使用权重. 1 2 3 4 5 6   ->   1/1  1/2   1/3  1/4

    private Taskable task;  //任务
    private boolean runningFlag;   //此线程的运行状态

    public SimpleThread(){
        this.runningFlag=false;
        System.out.println(  "线程:"+ this.getName()+"实例化完成,进入创建态..." );
    }

    //获取线程的运行状态
    public boolean isRunning(){
        return runningFlag;
    }

    public synchronized void setRunningFlag(   boolean flag ){
        this.runningFlag=flag;
        if(   this.runningFlag ){
            this.notify();
            System.out.println(   this.getName()+"进入 active");
        }
    }

    public void setTask(   Taskable task){
        this.task=task;   //绑定任务给当前的线程
    }

    @Override   // 所以run要加一把锁
    public synchronized void run() {  // jvm调度到run    运行态
            while (true) {  // 设置为true是为了让线程池中的线程不会结束，销毁
                if (runningFlag == false) {
                    try {
                        //如果报出  java.lang.IllegalMonitorStateException 异常,表明:
                        // wait要释放对象锁，所以这个wait()所在的方法上要加一把锁.
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (this.task != null) {
                        this.task.doTask();   //执行任务,直到任务完成
                        //  break; return; 不能写这两个关键字，因为，这样的话，会将当前线程结束.
                        setRunningFlag(false);  //任务完成后，将运行态改为false.
                        System.out.println(  Thread.currentThread().getName()+"进入wait");
                        //其实任务完成后，最终的目标都是将当前的线程设置 wait.
                    }
                }
            }
    }
}
