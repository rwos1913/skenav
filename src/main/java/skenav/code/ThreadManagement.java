package skenav.code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManagement {
static ExecutorService pool;
    public void createThreadPool(){
        pool = Executors.newFixedThreadPool(5);
        System.out.println("thread pool created");

    }
    public void executeThread(String filename, String uploaddirectory, String hlsfilename){
        Runnable r = new VideoEncoder(filename, uploaddirectory, hlsfilename);
        pool.execute(r);
    }

}
