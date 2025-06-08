package com.example.library.service;

import com.example.library.controller.DeviceControlController.ControlRequest;
import com.example.library.pojo.model.*;
import com.example.library.repository.DeviceStatusRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

// MQTT 控制服务，负责发布命令并监听设备响应
@Service
public class MqttControlService implements MqttCallback {

    private static final String BROKER_URL = "tcp://149.88.73.107:1883";
    private static final String CLIENT_ID = "SpringBootMqttClient";

    private MqttClient client;

    @Autowired
    private DeviceStatusRepository statusRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    public MqttControlService() {
        try {
            // 初始化 MQTT 客户端
            client = new MqttClient(BROKER_URL, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.setCallback(this);
            client.connect(options);
            System.out.println("Connected to MQTT broker at " + BROKER_URL);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // 发布控制命令到设备的命令主题
    public void sendCommand(ControlRequest request) throws MqttException {
        String topic = "device/" + request.getdevice_id() + "/command";
        // 构建 JSON 负载
        String payload = "";
        try {
            payload = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(topic, message);
        System.out.println("Published command to topic " + topic + ": " + payload);
    }

    // 发布创建设备的命令到创建主题
    public void createDevice(String deviceId, String deviceType) throws MqttException {
        String topic = "device/register";
        // 构建 JSON 负载
        String payload = String.format(
                "{\"msg_id\": \"%s\", \"device_id\": \"%s\", \"device_type\": \"%s\"}",
                System.currentTimeMillis(), deviceId, deviceType);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(topic, message);
        System.out.println("Published create command to topic " + topic + ": " + payload);
    }

    // 发布删除设备的命令到删除主题
    public void deleteDevice(String deviceId, String deviceType) throws MqttException {
        String topic = "device/unregister";
        // 构建 JSON 负载
        String payload = String.format(
                "{\"msg_id\": \"%s\", \"device_id\": \"%s\", \"device_type\": \"%s\"}",
                System.currentTimeMillis(), deviceId, deviceType);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(topic, message);
        System.out.println("Published delete command to topic " + topic + ": " + payload);
    }

    // 在 bean 初始化后订阅响应主题
    @PostConstruct
    public void subscribeResponseTopic() {
        try {
            client.subscribe("device/+/response", 1);
            System.out.println("Subscribed to device/+/response topic");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // MQTT 回调：连接丢失
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("MQTT connection lost: " + cause.getMessage());
        // 可在此处重连逻辑
    }

    // 接收到消息时调用
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Received message. Topic: " + topic + ", Payload: " + new String(message.getPayload()));
        // 解析设备ID
        String[] parts = topic.split("/");
        if (parts.length >= 3) {
            String device_id = parts[1];
            // 解析 JSON 负载
            JsonNode json = objectMapper.readTree(message.getPayload());
            String msg_id = json.has("msg_id") ? json.get("msg_id").asText() : null;
            String status = json.has("status") ? json.get("status").asText() : null;
            int error_code = json.has("error_code") ? json.get("error_code").asInt() : 0;
            // 保存状态到数据库
            DeviceStatus deviceStatus = new DeviceStatus();
            deviceStatus.setdevice_id(device_id);
            deviceStatus.setmsg_id(msg_id);
            deviceStatus.setStatus(status);
            deviceStatus.seterror_code(error_code);
            deviceStatus.setTimestamp(LocalDateTime.now());
            statusRepository.save(deviceStatus);
            System.out.println("Saved device response to DB: " + deviceStatus);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 发布完成回调，可选实现
    }
}
