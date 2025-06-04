package com.example.library.pojo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

/**
 * 设备状态实体类，用于保存设备响应的状态信息
 */
@Entity
public class DeviceStatus {

    /** 自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备唯一标识 */
    @Column(nullable = false)
    private String device_id;

    /** 消息 ID（与前端/后端通信消息一一对应） */
    @Column(nullable = false)
    private String msg_id;

    /** 设备执行结果状态，如 "success"/"error" */
    @Column(nullable = false)
    private String status;

    /** 错误码，0 表示成功，非零表示失败原因 */
    private int error_code;

    /** 记录生成时间戳 */
    private LocalDateTime timestamp;

    public DeviceStatus() {
        // 默认构造函数
    }

    // -------- Getter 和 Setter --------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getdevice_id() {
        return device_id;
    }

    public void setdevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getmsg_id() {
        return msg_id;
    }

    public void setmsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int geterror_code() {
        return error_code;
    }

    public void seterror_code(int error_code) {
        this.error_code = error_code;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // toString 方法

    @Override
    public String toString() {
        return "DeviceStatus{" +
                "id=" + id +
                ", device_id='" + device_id + '\'' +
                ", msg_id='" + msg_id + '\'' +
                ", status='" + status + '\'' +
                ", error_code=" + error_code +
                ", timestamp=" + timestamp +
                '}';
    }
}
