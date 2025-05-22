import paho.mqtt.client as mqtt
from virtual_device import Device
import json
import argparse
import time


# 程序参数接收
parser = argparse.ArgumentParser(description='Yatmosphere Virtual Light')
parser.add_argument('--device_id', type=str, required=True, help='Device ID')


# Yatmosphere Virtual Light
# 继承于虚拟设备基类，需要实现抽象方法：handle_command
class Light(Device):
  def __init__(self, device_id):
    super().__init__(device_id, 'light')

    # data
    self.data = {
      'power': True,  # 灯的开关状态(True:"on", False:"off")
      'brightness': 100,  # 灯的亮度(0-100)
      'color':{
        'r': 255,  # 红色分量(0-255)
        'g': 255,  # 绿色分量(0-255)
        'b': 255   # 蓝色分量(0-255)
      }
    }
    
  def handle_command(self, command):
      try:
          # 保持原有逻辑，但调整数据格式
          if command['action'] == 'set_power':
              self.data['power'] = command['value'] == 'on'
              self._send_status()
              
          elif command['action'] == 'set_brightness':
              self.data['brightness'] = command['value']
              self._send_status()
              
          elif command['action'] == 'set_color':
              self.data['color'] = command['value']
              self._send_status()
              
          self.handle_response(0, "success", command)
          
      except Exception as e:
          self.handle_response(500, str(e), command)
  
  def _send_status(self):
      """发送标准化状态消息"""
      status = {
          "msg_id": int(time.time() * 1000),
          "timestamp": int(time.time()),
          "device_type": "light",
          "device_id": self.device_id,
          "msg_type": "status",
          "power": "on" if self.data['power'] else "off",
          "brightness": self.data['brightness'],
          "color": self.data['color']
      }
      self.client.publish(self.state_topic, json.dumps(status))