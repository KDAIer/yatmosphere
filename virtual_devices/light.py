import paho.mqtt.client as mqtt
from virtual_devices.virtual_device import Device
import json
import argparse
import time

# Yatmosphere Virtual Light
# 继承于虚拟设备基类，需要实现抽象方法：handle_command, handle_state
class Light(Device):
  def __init__(self, device_id):
    super().__init__(device_id, 'light')

    # data
    self.data = {
      'power': True,  # 灯的开关状态(True:"on", False:"off")
      'brightness': 100,  # 灯的亮度(0-100)
      'colorTemp': '暖光' # 灯的色温(暖光, 自然, 冷光)
    }
    
  def handle_command(self, command):
      try:
          # 保持原有逻辑，但调整数据格式
          if command['action'] == 'set_power':
              self.data['power'] = command['value'] == 'on'
              self.handle_state()
              
          elif command['action'] == 'set_brightness':
              self.data['brightness'] = command['value']
              self.handle_state()
              
          elif command['action'] == 'set_color_temp':
              color_temps = ['暖光', '自然', '冷光']
              if command['value'] in color_temps:
                  self.data['colorTemp'] = command['value']
                  self.handle_state()
              else:
                  self.handle_response(400, f"无效色温：{command['value']}", command)
                  return

          elif command['action'] == 'get_status':
              self.handle_state()

          else:
              self.handle_response(400, f"未知命令{command['action']}", command)
              return
              
          self.handle_response(0, "success", command)
          
      except Exception as e:
          self.handle_response(500, str(e), command)
  
  def handle_state(self):
      """发送标准化状态消息"""
      status = {
          "msg_id": int(time.time() * 1000),
          "timestamp": int(time.time()),
          "device_type": "light",
          "device_id": self.device_id,
          "msg_type": "status",
          "power": "on" if self.data['power'] else "off",
          "brightness": self.data['brightness'],
          "colorTemp": self.data['colorTemp']
      }
      self.client.publish(self.state_topic, json.dumps(status))