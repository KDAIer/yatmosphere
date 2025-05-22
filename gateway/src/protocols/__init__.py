# gateway/src/protocols/__init__.py
"""协议转换核心模块"""
import json
from typing import Dict, Any

class ProtocolConverter:
    """协议转换抽象基类"""
    def to_device_protocol(self, mqtt_msg: Dict) -> Any:
        """将MQTT协议转为设备专用协议"""
        raise NotImplementedError
        
    def to_mqtt_protocol(self, device_data: Any) -> Dict:
        """将设备协议转为MQTT标准协议"""
        raise NotImplementedError

class LightProtocol(ProtocolConverter):
    """智能灯协议转换实现"""
    
    def to_device_protocol(self, mqtt_msg: Dict) -> bytes:
        """
        转换MQTT命令为设备指令
        示例设备指令格式(模拟)：
        [HEAD][CMD][LEN][DATA][CRC]
        """
        cmd_map = {
            'set_power': 0x01,
            'set_brightness': 0x02,
            'set_color': 0x03
        }
        
        # 构造二进制指令
        cmd_code = cmd_map[mqtt_msg['action']]
        if cmd_code == 0x01:  # 开关指令
            data = bytes([1 if mqtt_msg['value'] == 'on' else 0])
        elif cmd_code == 0x02:  # 亮度指令
            data = bytes([min(100, max(0, mqtt_msg['value']))])
        else:  # 颜色指令
            color = mqtt_msg['value']
            data = bytes([color['r'], color['g'], color['b']])
            
        # 模拟协议帧（实际应根据设备文档实现）
        return bytes([0xAA, cmd_code, len(data)]) + data + bytes([0xFF])
    
    def to_mqtt_protocol(self, device_data: bytes) -> Dict:
        """转换设备数据为MQTT消息"""
        # 示例实现（实际需要解析二进制数据）
        return {
            'power': 'on' if device_data[0] else 'off',
            'brightness': device_data[1],
            'color': {
                'r': device_data[2],
                'g': device_data[3],
                'b': device_data[4]
            }
        }

# 协议工厂
def get_protocol_converter(device_type: str) -> ProtocolConverter:
    """根据设备类型返回对应的协议转换器"""
    converters = {
        'light': LightProtocol(),
        # 可扩展其他设备类型
    }
    return converters.get(device_type, ProtocolConverter())