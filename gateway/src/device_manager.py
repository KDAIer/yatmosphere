# gateway/src/device_manager.py
"""设备管理核心模块"""
import json
import logging
import time
from protocols import get_protocol_converter
import threading

class DeviceManager:
    """设备管理器，负责设备通信协议转换和状态维护"""
    def __init__(self, mqtt_client):
        self.mqtt = mqtt_client  # 保存MQTT客户端引用
        self.devices = {}
        # 动态获取协议转换器
        self.protocol_factory = get_protocol_converter
        
    def process_message(self, device_id, payload):
        msg = json.loads(payload)

        if device_id not in self.devices:
            logging.error(f"设备 {device_id} 不存在")
            # 发送mqtt response错误响应
            self.mqtt.publish(f"device/{device_id}/response", json.dumps({
                "msg_id": f"{int(time.time())}",
                "msg_type": "response",
                "status": "failed",
                "error_code": 404,  # 设备未注册
                "error_msg": f"device {device_id} no exist",
                "related_msg_id": msg.get('msg_id')
            }))
            return  

        device_type = msg.get('device_type')
        
        # 动态获取协议转换器
        converter = self.protocol_factory(device_type)
        
        # 协议转换
        device_cmd = converter.to_device_protocol(msg)
        self._send_to_device(device_id, device_cmd)
            
    def _handle_command(self, device_id: str, command: dict):
        """
        处理控制命令
        :param device_id: 目标设备ID
        :param command: 命令字典
        """
        # 协议转换（JSON -> 设备专用协议）
        device_cmd = self.converter.to_device_protocol(command)
        
        # 发送到物理设备（模拟实现）
        self._send_to_device(device_id, device_cmd)

    def _send_to_device(self, device_id: str, command: str):
        """
        实际设备通信方法（模拟实现）
        :param device_id: 设备ID
        :param command: 设备协议指令
        """
        logging.info(f"[{device_id}] 执行指令: {command}")
    
    def _create_device(self, device_id, device_type):
        """创建设备实例线程"""
        if device_id in self.devices:
            logging.warning(f"设备 {device_id} 已存在，不能重复创建")
            raise ValueError(f"设备 {device_id} 已存在，不能重复创建")
        
        # 根据设备类型动态导入对应的虚拟设备类
        if device_type == "light": # 灯光设备
            from virtual_devices.light import Light
            device = Light(device_id)
        elif device_type == "air_conditioner": # 空调设备
            from virtual_devices.aircon import AirConditioner
            device = AirConditioner(device_id)
        # 其他设备类型
        else:
            raise ValueError(f"未知设备类型: {device_type}")
        
        # 启动设备线程，设备主循环run函数
        thread = threading.Thread(target=device.run, daemon=True)
        thread.start()
        self.devices[device_id] = {
            "instance": device,
            "thread": thread
        }
        logging.info(f"设备 {device_id} ({device_type}) 已注册并启动")
    
    def _handle_registration(self, payload):
        """
        处理设备注册命令
        :param device_id: 设备ID
        :param dedevice_type: 设备类型
        :return: 执行是否成功
        """
        try:
            msg = json.loads(payload)
            
            # 动态创建设备
            self._create_device(
                device_id=msg['device_id'],
                device_type=msg['device_type'],
            )

            # 返回成功响应
            self.mqtt.publish("device/registered", json.dumps({
                "msg_id": f"{int(time.time())}",
                "msg_type": "registered",
                "device_id": msg['device_id'],
                "status": "success",
                "related_msg_id": msg.get('msg_id')
            }, ensure_ascii=False))
            
        except Exception as e:
            self.mqtt.publish("device/registered", json.dumps({
                "msg_id": f"{int(time.time())}",
                "msg_type": "registered",
                "status": "failed",
                "error_msg": str(e),
                "related_msg_id": msg.get('msg_id')
            }, ensure_ascii=False))

    def _handle_unregistration(self, payload):
        """
        处理设备删除命令
        :param device_id: 设备ID
        :param dedevice_type: 设备类型
        :return: 执行是否成功
        """
        try:
            msg = json.loads(payload)
            device_id = msg['device_id']
            if device_id in self.devices:
                # 停止设备
                device_info = self.devices[device_id]
                device = device_info["instance"] # 获取设备实例
                # 调用设备的stop方法停止设备
                if hasattr(device, "stop"):
                    device.stop()  
                # 等待线程结束
                device_info["thread"].join(timeout=3)
                del self.devices[device_id]
                self.mqtt.publish("device/unregistered", json.dumps({
                    "msg_id": f"{int(time.time())}",
                    "device_id": msg['device_id'],
                    "msg_type": "unregistered",
                    "status": "success",
                    "device_id": device_id
                }, ensure_ascii=False))
            else:
                raise ValueError("设备未注册")
        except Exception as e:
            self.mqtt.publish("device/unregistered", json.dumps({
                "msg_id": f"{int(time.time())}",
                "device_id": msg['device_id'],
                "msg_type": "unregistered",
                "status": "failed",
                "error_msg": str(e),
                "related_msg_id": msg.get('msg_id')
            }, ensure_ascii=False))
    
    