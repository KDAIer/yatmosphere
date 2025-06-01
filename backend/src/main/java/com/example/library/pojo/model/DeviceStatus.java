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
    private String deviceId;

    /** 消息 ID（与前端/后端通信消息一一对应） */
    @Column(nullable = false)
    private String msgId;

    /** 设备执行结果状态，如 "success"/"error" */
    @Column(nullable = false)
    private String status;

    /** 错误码，0 表示成功，非零表示失败原因 */
    private int errorCode;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
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
                ", deviceId='" + deviceId + '\'' +
                ", msgId='" + msgId + '\'' +
                ", status='" + status + '\'' +
                ", errorCode=" + errorCode +
                ", timestamp=" + timestamp +
                '}';
    }
}
