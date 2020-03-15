/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jfghent.interruptabletimermqttexample;

//import com.jfghent.interruptabletimermqtt.InterruptableTimerMqtt;
import com.jfghent.interruptabletimermqtt.InterruptableTimerMqtt;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author jon
 */
public class InterruptableTimerMqttExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MqttClient mc = null;
        
        try {
            //Create a simple MQTT Client
            mc = new MqttClient("tcp://192.168.1.46:1885", "InterruptableTimerMqttExample");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            
            mc.connect(options);
            String payload = "this is the payload";
            mc.publish("home/test/topic", payload.getBytes(), 0, true);
        } catch (MqttException ex) {
            Logger.getLogger(InterruptableTimerMqttExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Create an InterruptableTimerMqtt task
        MqttMessage StartMsg = new MqttMessage("start".getBytes());
        MqttMessage StopMsg = new MqttMessage("stop".getBytes());
        InterruptableTimerMqtt itm = new InterruptableTimerMqtt(
                "test task", 
                0, 
                mc, 
                "home/test/start", 
                "home/test/stop", 
                new MqttMessage("start".getBytes()),//StartMsg, 
                new MqttMessage("stop".getBytes()));
        
        //Start
    }
    
}
