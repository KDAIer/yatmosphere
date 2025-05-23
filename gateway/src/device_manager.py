# gateway/src/device_manager.py
"""设备管理核心模块"""
import json
import logging
from protocols import get_protocol_converter

class DeviceManager:
    """设备管理器，负责设备通信协议转换和状态维护"""
    def __init__(self, mqtt_client):
        self.mqtt = mqtt_client  # 保存MQTT客户端引用
        self.devices = {}
        # 动态获取协议转换器
        self.protocol_factory = get_protocol_converter
        
    def process_message(self, device_id, payload):
        msg = json.loads(payload)
        device_type = msg.get('device_type')
        
        # 动态获取协议转换器
        converter = self.protocol_factory(device_type)
        
        # 协议转换
        device_cmd = converter.to_device_protocol(msg)
        self._send_to_device(device_id, device_cmd)
            
    def _handle_command(self, device_id: str, command: dict):
        """
        处理控制命令（核心业务逻辑）
        :param device_id: 目标设备ID
        :param command: 命令字典
        """
        # 协议转换（JSON -> 设备专用协议）
        device_cmd = self.converter.to_device_protocol(command)
        
        # 发送到物理设备（模拟实现）
        success = self._send_to_device(device_id, device_cmd)
        
        # 构造响应消息
        resp_msg = {
            "msg_id": command['msg_id'],
            "msg_type": "response",
            "status": "success" if success else "failed",
            "related_msg_id": command['msg_id']
        }
        
        # 发布到响应主题
        response_topic = f"device/{device_id}/response"
        self.mqtt.publish(response_topic, json.dumps(resp_msg))

    def _send_to_device(self, device_id: str, command: str) -> bool:
        """
        实际设备通信方法（模拟实现）
        :param device_id: 设备ID
        :param command: 设备协议指令
        :return: 执行是否成功
        """
        logging.info(f"[{device_id}] 执行指令: {command}")
        return True  # 假设设备始终成功
    
    def _handle_registration(self, payload):
        """
        实际设备通信方法（模拟实现）
        :param device_id: 设备ID
        :param command: 设备协议指令
        :return: 执行是否成功
        """
        try:
            msg = json.loads(payload)
            assert msg['msg_type'] == 'register'
            
            # 动态创建设备
            device = self._create_device(
                device_id=msg['device_id'],
                device_type=msg['device_type'],
                params=msg.get('params', {})
            )
            
            # 返回成功响应
            self.mqtt.publish("device/registered", json.dumps({
                "msg_id": f"resp_{time.time()}",
                "msg_type": "registered",
                "status": "success",
                "device_id": msg['device_id'],
                "control_topic": f"device/{msg['device_id']}/command"
            }))
            
        except Exception as e:
            self.mqtt.publish("device/registered", json.dumps({
                "status": "failed",
                "error_msg": str(e),
                "related_msg_id": msg.get('msg_id')
            }))
    
    def _create_device(self, device_id, device_type, params):
        """动态加载设备类"""
        if device_type == "light":
            from virtual_devices.light import Light
            device = Light(device_id, params)
        # 其他设备类型...
        else:
            raise ValueError(f"未知设备类型: {device_type}")
        
        self.devices[device_id] = device
        return device