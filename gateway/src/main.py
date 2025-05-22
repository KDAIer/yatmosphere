def initialize():
    # 加载设备配置
    devices = load_config('config/devices.yaml')  
    
    # 初始化MQTT客户端
    mqtt_client = MQTTClient(
        broker_ip=os.getenv('MQTT_BROKER'),
        client_id=f"gateway_{uuid.uuid4()}"
    )
    mqtt_client.connect()
    
    # 扫描物理设备
    device_manager = DeviceManager()
    discovered_devices = device_manager.scan()  
    
    # 发布设备清单
    mqtt_client.publish(
        topic="yatmosphere/gateway/devices",
        payload=json.dumps({
            "msg_type": "discovery",
            "devices": discovered_devices
        }),
        qos=1
    )