/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jfghent.interruptabletimermqttexample;

//import com.jfghent.interruptabletimermqtt.InterruptableTimerMqtt;
import com.jfghent.interruptabletasksequence.InterruptableTaskSequence;
import com.jfghent.interruptabletimer.InterfaceVoid;
import com.jfghent.interruptabletimermqtt.InterruptableTimerMqtt;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author jon
 */
public class InterruptableTimerMqttExample {
    static InterruptableTaskSequence seq = null;
    
    static MqttClient mc = null;
    static InterruptableTimerMqtt itm = null;
    
    static MqttCallback mcb = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection Lost");
                try {
                    if(mc!=null) mc.connect();
                } catch (MqttException ex) {
                    Logger.getLogger(InterruptableTimerMqttExample.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                System.out.println("Message received: " + msg);
//                if("stopit".equals(msg))
//                {
//                    System.out.println("Cancelling...");
//                    if(itm != null)
//                    { 
//                        itm.cancel();
//                    }    
//                }else if ("howlong".equals(msg))
//                {
                    System.out.println("Elapsed Time");
                    //if(itm != null)
                    //{ 
                   
                    if(topic.equals("home/test/cnx"))
                    {
                        itm.cancel();
                    }
                    else if (topic.equals("home/test/elapsed")){
                        Long elap = itm.GetElapsed();
                        System.out.println("elapsed time: " + elap.toString());
                        mc.publish("home/test/time", 
                                elap.toString().getBytes(),
                                2,
                                false);
                    }
//                }
                
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                System.out.println("Delivery Complete");
            }
        };
            
            
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        Thread t1 = new Thread(itm);
//        mcb = new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//                System.out.println("Connection Lost");
//                try {
//                    if(mc!=null) mc.connect();
//                } catch (MqttException ex) {
//                    Logger.getLogger(InterruptableTimerMqttExample.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                String msg = new String(message.getPayload());
//                System.out.println("Message received: " + msg);
////                if("stopit".equals(msg))
////                {
////                    System.out.println("Cancelling...");
////                    if(itm != null)
////                    { 
////                        itm.cancel();
////                    }    
////                }else if ("howlong".equals(msg))
////                {
//                    System.out.println("Elapsed Time");
//                    if(itm != null)
//                    { 
//                        Long elap = itm.GetElapsed();
//                        System.out.println("elapsed time: " + elap.toString());
//                        mc.publish("home/test/time", 
//                                elap.toString().getBytes(),
//                                0,
//                                false);
//                    }
////                }
//                
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                System.out.println("Delivery Complete");
//            }
//        };
             
        try {
            //Create a simple MQTT Client
            mc = new MqttClient("tcp://localhost","InterruptableTimerMqttExample"); //("tcp://192.168.1.46:1885", "InterruptableTimerMqttExample");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(false);
            options.setCleanSession(true);
                     
            mc.connect(options);
            mc.setCallback(mcb);
            
            String payload = "online";
            mc.publish("home/test/topic", payload.getBytes(), 0, false);
            mc.subscribe("home/test/cnx");
            mc.subscribe("home/test/elapsed");
        } catch (MqttException ex) {
            Logger.getLogger(InterruptableTimerMqttExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(mc.isConnected())
        {
            //Create an InterruptableTimerMqtt task
//            itm = new InterruptableTimerMqtt(
//                    "test task 1",
//                    8000, 
//                    () -> {
//                        System.exit(0);
//                    },
//                    mc,
//                    "home/test/start",
//                    "home/test/stop",
//                    new MqttMessage("start1".getBytes()),//StartMsg, 
//                    new MqttMessage("stop1".getBytes()));
//
//            //Start
//            itm.start();
//                //t1.start();
//                //while(true);
//                //mosquitto_sub -h 192.168.1.46 -p 1885 -t home/test/#
//                //mc.publish("/home/test/topic", "exiting main".getBytes(), 0, false);
            seq = new InterruptableTaskSequence( ()-> { System.exit(0);});
            
            seq.add(new InterruptableTimerMqtt(
                    "test task 1",
                    8000, 
                    () -> {
                        System.exit(0);
                    },
                    mc,
                    "home/test/start",
                    "home/test/stop",
                    new MqttMessage("start1".getBytes()),//StartMsg, 
                    new MqttMessage("stop1".getBytes())));
                    
            seq.add(new InterruptableTimerMqtt(
                    "test task 2",
                    3000, 
                    () -> {
                        System.exit(0);
                    },
                    mc,
                    "home/test/start",
                    "home/test/stop",
                    new MqttMessage("start2".getBytes()),//StartMsg, 
                    new MqttMessage("stop2".getBytes())));
                    
            System.out.println("Exiting Main");
            
            
        } else {
            System.out.println("Error: MqttClient not connected.");
        }
    }
    
    
}
