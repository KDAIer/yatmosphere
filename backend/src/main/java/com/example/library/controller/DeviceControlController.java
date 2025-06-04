package com.example.library.controller;

import com.example.library.etc.Result;
import com.example.library.service.MqttControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 控制器：处理前端发送的设备控制命令请求
 * 返回值类型改为 Result<Object>，在内部封装状态码、提示信息和可选数据
 */
@RestController
@RequestMapping("/api/control")
public class DeviceControlController {

    @Autowired
    private MqttControlService mqttControlService;

    /**
     * GET 请求版本：前端通过 URL 查询参数传递控制命令
     * 示例 URL:
     * /api/control/device?
     * msg_id=1623456789000&
     * device_type=light&
     * device_id=AC001&
     * action=set_power&
     * value=off
     */
    @GetMapping("/device")
    public ResponseEntity<Result<Object>> controlDeviceByGet(
            @RequestParam("msg_id") Long msg_id,
            @RequestParam("device_id") String device_id,
            @RequestParam("device_type") String device_type,
            @RequestParam("action") String action,
            @RequestParam(value = "value", required = false) String value) {
        // 封装请求到 DTO
        ControlRequest request = new ControlRequest();
        request.setmsg_id(msg_id);
        request.setdevice_id(device_id);
        request.setdevice_type(device_type);
        request.setAction(action);
        request.setValue(value);

        try {
            // 发布 MQTT 命令
            mqttControlService.sendCommand(request);
            // 返回一个 Result 对象，表示成功，不携带额外 data
            return ResponseEntity.ok(Result.success(null));
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈，辅助调试
            // 返回一个失败的 Result，状态码 500，提示 e.getMessage()
            return ResponseEntity
                    .status(500)
                    .body(Result.failure(500, "Failed to send command: " + e.getMessage()));
        }
    }

    /**
     * POST 请求版本：接收 JSON 体
     */
    @PostMapping("/device")
    public ResponseEntity<Result<Object>> controlDeviceByPost(@RequestBody ControlRequest request) {
        try {
            mqttControlService.sendCommand(request);
            return ResponseEntity.ok(Result.success(null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body(Result.failure(500, "Failed to send command: " + e.getMessage()));
        }
    }

    /**
     * 用于接收前端请求的 DTO 类，包含 msg_id、device_id、device_type、action 以及可选的 value
     */
    public static class ControlRequest {
        private Long msg_id;
        private String device_id;
        private String device_type;
        private String action;
        private Object value;

        public ControlRequest() {
        }

        public Long getmsg_id() {
            return msg_id;
        }

        public void setmsg_id(Long msg_id) {
            this.msg_id = msg_id;
        }

        public String getdevice_id() {
            return device_id;
        }

        public void setdevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getdevice_type() {
            return device_type;
        }

        public void setdevice_type(String device_type) {
            this.device_type = device_type;
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

        @Override
        public String toString() {
            return "ControlRequest{" +
                    "msg_id=" + msg_id +
                    ", device_id='" + device_id + '\'' +
                    ", device_type='" + device_type + '\'' +
                    ", action='" + action + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}
