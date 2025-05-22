# gateway/src/main.py
"""设备接口端主入口模块"""
import logging
from mqtt_client import MQTTClient
from device_manager import DeviceManager

# 配置日志格式
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

class Gateway:
    """网关核心类，负责协调各模块工作"""
    
    def __init__(self, config):
        """
        初始化网关组件
        :param config: 配置字典，包含MQTT连接信息等
        """
        # 初始化MQTT客户端（消息传输核心）
        self.mqtt_client = MQTTClient(
            broker=config['mqtt']['broker'],
            port=config['mqtt']['port'],
            client_id="gateway_001"  # 网关设备唯一标识
        )
        
        # 初始化设备管理器（业务逻辑核心）
        self.device_mgr = DeviceManager(self.mqtt_client)
        
        # 绑定消息回调处理
        self.mqtt_client.on_message = self.handle_mqtt_message
        
    def handle_mqtt_message(self, topic: str, payload: str):
        """
        MQTT消息统一入口处理
        :param topic: MQTT主题，格式为 device/<device_id>/<action>
        :param payload: 消息内容(JSON字符串)
        """
        # 提取设备ID（主题第二段）
        # 示例：device/light_001/command -> light_001
        device_id = topic.split('/')[1]
        
        try:
            # 将消息路由到设备管理器
            self.device_mgr.process_message(device_id, payload)
        except Exception as e:
            logging.error(f"设备{device_id}消息处理异常: {str(e)}")

    def start(self):
        """启动网关服务"""
        # 建立MQTT连接
        self.mqtt_client.connect()
        
        # 订阅所有设备命令主题
        # device/+/command 匹配任意设备ID的命令
        self.mqtt_client.subscribe("device/+/command")
        
        # 启动消息循环（阻塞式）
        self.mqtt_client.loop_forever()

if __name__ == "__main__":
    # 临时配置（实际应从配置文件读取）
    config = {
        'mqtt': {
            'broker': 'localhost',  # MQTT服务器地址
            'port': 1883            # 默认非加密端口
        }
    }
    
    # 启动网关实例
    gateway = Gateway(config)
    logging.info("Yatmosphere 设备接口端启动...")
    gateway.start()