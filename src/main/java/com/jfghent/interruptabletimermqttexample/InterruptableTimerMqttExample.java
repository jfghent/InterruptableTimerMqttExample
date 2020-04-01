/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jfghent.interruptabletimermqttexample;

//import com.jfghent.interruptabletimermqtt.InterruptableTimerMqtt;
import com.jfghent.interruptabletasksequence.InterruptableTaskSequence;
import com.jfghent.interruptabletimermqtt.InterruptableTimerMqtt;
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

                    System.out.println("Elapsed Time");

                    if(topic.equals("home/test/cnx"))
                    {
                        seq.cancel();
                    }
                    else if (topic.equals("home/test/elapsed")){
                        Long elap = seq.GetRemaining(); 
                        System.out.println("elapsed time: " + elap.toString());
                        mc.publish("home/test/time", 
                                elap.toString().getBytes(),
                                2,
                                false);
                    }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Delivery Complete");
            }
        };
            
            
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
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
            seq = new InterruptableTaskSequence( ()-> { System.exit(0);});
            
            seq.add(new InterruptableTimerMqtt(
                    "test task 1",
                    8000, 
                    () -> {
                        seq.runNextTask();
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
                        seq.runNextTask();
                    },
                    mc,
                    "home/test/start",
                    "home/test/stop",
                    new MqttMessage("start2".getBytes()),//StartMsg, 
                    new MqttMessage("stop2".getBytes())));
            
            seq.run();
            
            System.out.println("Exiting Main");
            
            
        } else {
            System.out.println("Error: MqttClient not connected.");
        }
    }
}
