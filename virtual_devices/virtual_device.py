import paho.mqtt.client as mqtt
from virtual_devices.const import Yatmosphere_MQTT_BROKER, Yatmosphere_MQTT_PORT
import abc
import json
import argparse
import time

# 程序参数接收
parser = argparse.ArgumentParser(description='Yatmosphere Virtual Device')

# 虚拟设备基类
class Device(metaclass=abc.ABCMeta):
  def __init__(self, device_id, device_type):
    # MQTT 客户端
    self.client = mqtt.Client()
    self.setClient()
    self.client.connect(Yatmosphere_MQTT_BROKER, Yatmosphere_MQTT_PORT, 60)

    # 设备 ID 和类型
    self.device_id = device_id
    self.device_type = device_type

    # 设备主题
    self.state_topic = f'device/{self.device_id}/status'
    self.command_topic = f'device/{self.device_id}/command'
    self.response_topic = f'device/{self.device_id}/response'

    # 设备数据
    self.data = {}

    # 设备加载
    self._running = True

  def run(self):
    # 设备主循环
    while self._running:
        self.client.loop_forever()
        # 用虚函数为个别设备添加定期上报等逻辑 TODO

  # 删除设备
  def stop(self):
      self._running = False
      self.client.disconnect()

  def setClient(self):
    self.client.on_connect = self.on_connect
    self.client.on_message = self.on_message

  def on_connect(self, client, userdata, flags, rc):
    print(f"{self.device_id}已连接到MQTT broker，返回码: {rc}")
    # 订阅命令主题
    self.client.subscribe(self.command_topic)
    print(f"{self.device_id}订阅的话题：{self.command_topic}")

  def on_message(self, client, userdata, msg):
    topic = msg.topic
    payload = json.loads(msg.payload.decode())
    print(f"{self.device_id}收到来自话题({topic})的消息：{payload}")

    # 检查设备类型和命令是否匹配
    if self.device_type == payload['device_type'] and payload['device_id'] == self.device_id:
      # 处理命令
      # if payload['msg_type'] == 'command':
      self.handle_command(payload)
    else:
      self.handle_response(400, "设备类型或ID不匹配", payload)
      return

  @abc.abstractmethod
  def handle_command(self, command):
    pass

  @abc.abstractmethod
  def handle_state(self, state):
    pass

  def handle_response(self, error_code, error_message, payload=None):
    status = "success" if error_code == 0 else "error"
    # 处理响应
    response = {
      "msg_id": int(time.time() * 1000),
      'timestamp': int(time.time()),
      'msg_type': 'response',
      'status': status,
      'related_msg_id': payload['msg_id'],
    }
    if error_code != 0:
      response['error_code'] = error_code
      response['error_message'] = error_message
    self.client.publish(self.response_topic, json.dumps(response))
    print(f"{self.device_id}已发布响应：{response}")