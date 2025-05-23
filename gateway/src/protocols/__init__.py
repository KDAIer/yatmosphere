# gateway/src/protocols/__init__.py
"""协议转换模块"""
from typing import Dict

class ProtocolConverter:
    """协议转换基类（直接透传JSON）"""
    def to_device_protocol(self, mqtt_msg: Dict) -> Dict:
        """直接返回原始JSON数据"""
        return mqtt_msg
    
    def to_mqtt_protocol(self, device_data: Dict) -> Dict:
        """直接返回设备数据"""
        return device_data

class LightProtocol(ProtocolConverter):
    """灯光协议转换"""
    def to_device_protocol(self, mqtt_msg: Dict):
        # 校验字段
        if 'action' not in mqtt_msg:
            raise ValueError("Missing action field")
        return mqtt_msg

def get_protocol_converter(device_type: str) -> ProtocolConverter:
    converters = {
        'light': LightProtocol(),
    }
    return converters.get(device_type, ProtocolConverter())