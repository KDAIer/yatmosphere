package com.example.library.controller;

import com.example.library.service.MqttControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 控制器：处理前端发送的设备控制命令请求
 * 支持 GET 方式接收查询参数：msgId, deviceId, deviceType, action, value
 */
@RestController
@RequestMapping("/api/control")
public class DeviceControlController {

    @Autowired
    private MqttControlService mqttControlService;

    /**
     * GET 请求版本：前端通过 URL 查询参数传递控制命令
     * 示例 URL: /api/control/device?
     * msgId=1623456789000&
     * deviceId=light1&
     * deviceType=light&
     * action=set_power&
     * value=on
     *
     * @param msgId      消息唯一 ID（时间戳等）
     * @param deviceId   设备 ID
     * @param deviceType 设备类型（如 "light" 或 "aircon"）
     * @param action     控制动作（如 "set_power", "set_temperature" 等）
     * @param value      动作对应的值（字符串或数字，取决于 action）
     * @return 成功或失败提示
     */
    @GetMapping("/device")
    public ResponseEntity<String> controlDeviceByGet(
            @RequestParam("msgId") Long msgId,
            @RequestParam("deviceId") String deviceId,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("action") String action,
            @RequestParam(value = "value", required = false) String value) {
        try {
            // 将查询参数封装为 ControlRequest 对象
            ControlRequest request = new ControlRequest();
            request.setMsgId(msgId);
            request.setDeviceId(deviceId);
            request.setDeviceType(deviceType);
            request.setAction(action);
            // value 参数是 String，如果设备需要数值类型，可在 MqttControlService 内转换
            request.setValue(value);
            mqttControlService.sendCommand(request);
            return ResponseEntity.ok("Command sent to device via GET");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send command: " + e.getMessage());
        }
    }

    /**
     * POST 方法
     */
    @PostMapping("/device")
    public ResponseEntity<String> controlDeviceByPost(@RequestBody ControlRequest request) {
        try {
            mqttControlService.sendCommand(request);
            return ResponseEntity.ok("Command sent to device via POST");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send command: " + e.getMessage());
        }
    }

    /**
     * 用于接收前端请求的 DTO 类，包含 msgId、deviceId、deviceType、action 以及可选的 value
     */
    public static class ControlRequest {
        private Long msgId;
        private String deviceId;
        private String deviceType;
        private String action;
        private Object value;

        public ControlRequest() {
            // 默认构造函数
        }

        public Long getMsgId() {
            return msgId;
        }

        public void setMsgId(Long msgId) {
            this.msgId = msgId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
