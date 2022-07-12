package com.example.mqttdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button button;
    public String ServerUri = "tcp://3.251.89.203:1883";
    public String MQTTIP = "3.251.89.203", MQTTPort = "1883";
    public String USERNAME = "WHYTE", PASSWORD = "Whyte@123";
    public String Publisher = "1111111111_1", Topic = "1111111111";
    MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MQTTConnect();
                } catch (MqttException e) {
                    System.out.println("not call");
                    e.printStackTrace();
                }
            }
        });
    }

    private void MQTTConnect() throws MqttException {

//        String clientId ="Android_111125";

        String clientId = MqttClient.generateClientId();
        System.out.println(clientId);
        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), ServerUri, clientId);

        System.out.println("1");
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(USERNAME);
        mqttConnectOptions.setPassword(PASSWORD.toCharArray());

        System.out.println("2");


        try {
            System.out.println("3");
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            System.out.println("4");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Successfully Connected To Sever.", Toast.LENGTH_SHORT).show();
                    System.out.println("Connection SuccessFul.");
                    subscribeToTopic();
                    publishMessage("AABB");

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Not Connected To Server.", Toast.LENGTH_SHORT).show();
                    System.out.println("5");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("6");
        }

    }

    private void subscribeToTopic() {

        try {
            mqttAndroidClient.subscribe(Publisher, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Subscribed Successful.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Subscribed Fail", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mqttAndroidClient.setCallback(new MqttCallback() {
            private String topic;
            private MqttMessage message;

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(MainActivity.this, "Connection Lost", Toast.LENGTH_SHORT).show();
                System.out.println("Connection Lost");

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Message2: " + topic + " : " + new String(message.getPayload()));

                if (topic.equals(Publisher)){
                    Toast.makeText(MainActivity.this, "MQTT Operation Successful.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Delivery Complete2"+token);
            }
        });
    }

    public void publishMessage(@NonNull String MQTTCommand){

        try {
            MqttMessage message = new MqttMessage();
            message.setQos(1);
            message.setPayload(MQTTCommand.getBytes());

            mqttAndroidClient.publish(Topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Message Published.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Publish Message Failed", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MqttException e) {
            Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}