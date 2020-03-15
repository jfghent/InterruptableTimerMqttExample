/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jfghent.interruptabletask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static com.jfghent.interruptabletask.InterruptableTaskState.*;

/**
 *
 * @author jon
 */
public class InterruptableTask implements Runnable { 
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> publisher;
    private InterruptableTaskState state = CREATED;
    private String _TaskName = "";
    
    public InterruptableTask(String TaskName){
        _TaskName = TaskName;
        state = INITIALIZED;
    }
    
    public String GetName(){
        return _TaskName;
    }
    
    public InterruptableTaskState GetState(){
        return state;
    }

    final public void start(){
        publisher = executor.submit(this);
    }
    
    @Override
    final public void run() {
        state = RUNNING;
        content();
        onfinish();
    }
    
    final public void cancel(){
        state = CANCELED;
        executor.shutdownNow();
        oncancel();
        onfinish();
    }
    
    final public void pause(){
        if(RUNNING==state) 
        {
            state = PAUSED;
            publisher.cancel(true);
            onpause();
        }
    }
    
    public void resumetask(){
        if(PAUSED==state)
        {
            state = RUNNING;
            onresume();
            start();
        }    
    }
    
    public void content() {
        //main code to run when task is started for the 
        //first time OR is resumed from a paused state
    }
    
    public void oncancel(){
        //code that runs when canceled, just before onfinish()
    }
    
    public void onpause(){
        //code that runs after the run task has been stopped
    }
    
    public void onresume(){
        //code that runs when a paused task is resumed 
        //just before content() is run in start()
    }
    
    public void onfinish(){
        //code that runs when content() finishes or when 
        //canceled (after oncancel())
    }
    
}
