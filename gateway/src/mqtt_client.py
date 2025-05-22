# gateway/src/mqtt_client.py
"""MQTT客户端封装模块"""
import paho.mqtt.client as mqtt
import logging

class MQTTClient:
    """MQTT客户端封装类，提供简洁的API接口"""
    
    def __init__(self, broker: str, port: int, client_id: str):
        """
        初始化MQTT客户端
        :param broker: MQTT代理服务器地址
        :param port: 服务器端口
        :param client_id: 客户端唯一标识
        """
        self.client = mqtt.Client(client_id=client_id)
        self.broker = broker
        self.port = port
        self.on_message = None  # 消息到达回调函数
        
        # 绑定标准回调方法
        self.client.on_connect = self._on_connect
        self.client.on_message = self._on_message

    def _on_connect(self, client, userdata, flags, rc):
        """连接结果回调（内部方法）"""
        if rc == 0:
            logging.info("成功连接到MQTT Broker")
        else:
            logging.error(f"连接失败，错误码: {rc}")

    def _on_message(self, client, userdata, msg):
        """原始消息到达回调（内部方法）"""
        if self.on_message:
            # 将二进制payload转换为字符串
            payload = msg.payload.decode('utf-8')
            # 调用外部注册的回调处理
            self.on_message(msg.topic, payload)

    def connect(self):
        """建立与MQTT Broker的连接"""
        self.client.connect(self.broker, self.port)

    def subscribe(self, topic: str):
        """
        订阅指定主题
        :param topic: 要订阅的MQTT主题，支持+通配符
        """
        self.client.subscribe(topic)
        logging.info(f"已订阅主题: {topic}")

    def publish(self, topic: str, payload: str):
        """
        发布消息到指定主题
        :param topic: 目标主题
        :param payload: 消息内容（字符串）
        """
        self.client.publish(topic, payload)
        logging.debug(f"消息已发布到 {topic}")

    def loop_forever(self):
        """启动阻塞式消息循环"""
        logging.info("启动MQTT消息循环...")
        self.client.loop_forever()