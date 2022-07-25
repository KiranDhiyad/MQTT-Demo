package com.example.mqttdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

public class MainActivity extends AppCompatActivity {

    Button button, button1, button2;
    public String ServerUri = "tcp://*Server*:*Port*";
    public String USERNAME = "USERNAME", PASSWORD = "PASSWORD";
    public String Publisher = "1111111111_1", Topic = "1111111111";
    MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MQTTConnect();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribeToTopic();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishMessage("Demo Message.");

            }
        });
    }


    private void MQTTConnect() {

        String clientId = MqttClient.generateClientId();
        System.out.println(clientId);
        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), ServerUri, clientId, Ack.AUTO_ACK);

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(USERNAME);
        mqttConnectOptions.setPassword(PASSWORD.toCharArray());


        try {

            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Successfully Connected To Sever.", Toast.LENGTH_SHORT).show();
                    System.out.println("Connection SuccessFul.");

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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        mqttAndroidClient.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(MainActivity.this, "Connection Lost", Toast.LENGTH_SHORT).show();
                System.out.println("Connection Lost");

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Message: " + topic + " : " + new String(message.getPayload()));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Delivery Complete" + token);
            }
        });
    }

    public void publishMessage(@NonNull String MQTTCommand) {

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

        } catch (Exception e) {
            Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}