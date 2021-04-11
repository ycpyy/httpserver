package com.yc;

import java.net.Socket;

public class NetTask implements Runnable {
    private Socket s;

    public NetTask(Socket s) {
        this.s=s;
    }

    @Override
    public void run() {

    }
}
