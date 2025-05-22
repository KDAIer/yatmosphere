## 命令处理
def on_message(client, userdata, msg):
    topic = msg.topic
    payload = json.loads(msg.payload)
    
    if "command" in topic:
        device_id = topic.split('/')[2]
        # 交给协议转换模块处理
        protocol = get_protocol(device_id)  
        result = protocol.execute(payload)
        
        # 发布响应
        response_topic = topic.replace('command', 'response')
        self.publish(response_topic, {
            "status": "success" if result else "failed",
            "related_msg_id": payload["msg_id"]
        })
        
        # 状态同步
        if result:
            current_state = protocol.get_state()
            self.publish(
                topic.replace('command', 'state'),
                current_state
            )