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
    # 处理命令
    try:
     # 解析命令
     if command['action'] == 'set_power':
        self.data['power'] = (command['power'] == 'on') # 设置灯的开关状态
        self.handle_response(0, "成功", command)
        self.handle_state(self.data)
     elif command['action'] == 'set_brightness': # 设置灯的亮度
        self.data['brightness'] = int(command['brightness']) 
        self.handle_response(0, "成功", command)
        self.handle_state(self.data)
     elif command['action'] == 'set_color': # 设置灯的颜色
        self.data['color']['r'] = int(command['color']['r']) 
        self.data['color']['g'] = int(command['color']['g'])  
        self.data['color']['b'] = int(command['color']['b'])
        self.handle_response(0, "成功", command)
        self.handle_state(self.data)
     else:
        self.handle_response(400, "未知命令", command)
    except Exception as e:
      print(f"处理命令时发生错误: {e}")
      self.handle_response(500, "处理命令时发生错误", command)