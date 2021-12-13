package com.rdev.basaltminer;

import okhttp3.Response;

class YEPRunnable implements Runnable {
    Response resp;
    String sresp;

    @Override
    public void run() {}

    public YEPRunnable init(Response resp) {
        this.resp = resp;
        return this;
    }

    public YEPRunnable init(String resp) {
        this.sresp = resp;
        return this;
    }
}
