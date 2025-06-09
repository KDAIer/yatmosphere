import paho.mqtt.client as mqtt
from virtual_devices.virtual_device import Device
import json
import argparse
import time

# Yatmosphere Virtual Air Conditioner
# 继承于虚拟设备基类，需要实现抽象方法：handle_command, handle_state
class AirConditioner(Device):
    def __init__(self, device_id):
        super().__init__(device_id, 'air_conditioner')

        # data
        self.data = {
            'power': True,  # 开关状态(True:"on", False:"off")
            'temperature': 24,  # 温度(16-30)
            'mode': '制冷', # 模式(制冷, 制热, 除湿, 送风)
            'eco': True,  # 节能模式(True:"on", False:"off")
            'fanLevel': 0,  # 风速(1-5级)
            'timer': -1  # 定时器(分钟) (-1表示未设置) (获得定时器状态时，将该值设置为目标时间戳，每次返回状态时返回目标时间戳与当前时间的差值)
        }

    def handle_command(self, command):
        try:
            if command['action'] == 'set_power':
                if command['value'] not in ['on', 'off']:
                    self.handle_response(400, f"无效开关状态：{command['value']}", command)
                    return
                self.data['power'] = command['value'] == 'on'
                self.handle_state()

            elif command['action'] == 'set_temperature':
                if not (16 <= command['value'] <= 30):
                    self.handle_response(400, f"温度值必须在16到30之间：{command['value']}", command)
                    return
                self.data['temperature'] = command['value']
                self.handle_state()

            elif command['action'] == 'set_mode':
                mode = ['制冷', '制热', '除湿', '送风']
                if command['value'] in mode:
                    self.data['mode'] = command['value']
                    self.handle_state()
                else:
                    self.handle_response(400, f"无效模式：{command['value']}", command)
                    return
                
            elif command['action'] == 'set_eco':
                if command['value'] not in ['on', 'off']:
                    self.handle_response(400, f"无效节能模式状态：{command['value']}", command)
                    return
                self.data['eco'] = command['value'] == 'on'
                self.handle_state()

            elif command['action'] == 'set_fan_level':
                if 1 <= command['value'] <= 5:
                    self.data['fanLevel'] = command['value']
                    self.handle_state()
                else:
                    self.handle_response(400, f"预设置无效风速：{command['value']},风速必须在1到5之间", command)
                    return
                
            elif command['action'] == 'set_timer':
                # 设置定时器为目标时间戳
                if command['value'] >= 0:
                    timestamp = int(time.time()) + command['value'] * 60  # 转换为目标时间戳
                    self.data['timer'] = timestamp
                    self.handle_state()
                else:
                    self.handle_response(400, f"无效定时器值：{command['value']},必须为非负整数", command)
                    return
                self.handle_state()

            elif command['action'] == 'get_status':
                self.handle_state()

            else:
                self.handle_response(400, f"未知命令：{command['action']}", command)
                return

            self.handle_response(0, "success", command)

        except Exception as e:
            self.handle_response(500, str(e), command)

    def handle_state(self):
        """发送标准化状态消息"""
        # 计算定时器剩余时间
        if self.data['timer'] >= 0:
            remaining_time = self.data['timer'] - int(time.time())
        else:
            remaining_time = -1
        # 更新定时器状态
        if remaining_time <= 0:
            self.data['power'] = False
            self.data['timer'] = -1  # 重置定时器状态
        else:
            remaining_time //= 60

        # 构建状态消息
        status={
          "msg_id": int(time.time() * 1000),
          "timestamp": int(time.time()),
          "device_type": "air_conditioner",
          "device_id": self.device_id,
          "msg_type": "status",
          "power": "on" if self.data['power'] else "off",
          "temperature": self.data['temperature'],
          "mode": self.data['mode'],
          "eco": "on" if self.data['eco'] else "off",
          "fanLevel": self.data['fanLevel'],
          "timer": remaining_time  # 返回剩余时间(分钟)，-1表示未设置
        }

        # 发布状态消息
        self.client.publish(f"device/{self.device_id}/status", json.dumps(status))