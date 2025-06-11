package com.example.library.controller; // 按需修改

import com.example.library.pojo.dto.ChatRequest;
import com.example.library.pojo.dto.ChatResponse;
import com.example.library.service.PromptService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * ChatController：处理 /api/chat 请求，将前端消息加上 system prompt 后调用外部 LLM，
 * 解析返回的 JSON，过滤 <think>...</think> 内容，只保留最后回答发送给前端。
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final PromptService promptService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${llm.api.url}")
    private String llmApiUrl;

    @Value("${llm.api.key}")
    private String llmApiKey;

    public ChatController(PromptService promptService, RestTemplate restTemplate) {
        this.promptService = promptService;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        if (llmApiKey == null || llmApiKey.isBlank()) {
            System.err.println("[WARN] 未注入 LLM API Key，调用将失败！");
        } else {
            System.out.println("[LLM配置] URL=" + llmApiUrl + ", keyLength=" + llmApiKey.length());
        }
    }

    /**
     * 接收前端历史消息，构建简化 payload（system + 最近 user），调用 LLM，解析并返回回答。
     * 请求体: { "messages": [ { "role":"user"|"assistant", "content":"..." }, ... ] }
     * 返回: { "content": "..." }
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
        // 打印接收到的前端消息，便于排查
        System.out.println("[Chat] 收到前端 messages: " + chatRequest.getMessages());

        // 提取最后一条 user 消息，避免过长历史干扰
        String lastUser = null;
        if (chatRequest.getMessages() != null) {
            List<ChatRequest.Message> list = chatRequest.getMessages();
            for (int i = list.size() - 1; i >= 0; i--) {
                ChatRequest.Message m = list.get(i);
                if ("user".equals(m.getRole()) && m.getContent() != null && !m.getContent().isBlank()) {
                    lastUser = m.getContent().trim();
                    break;
                }
            }
        }
        if (lastUser == null) {
            lastUser = ""; // 若没有 user，后续可返回提示
        }

        // 构建发送给 LLM 的 messages，仅 system + last user
        List<Map<String, String>> messagesForLLM = new ArrayList<>();
        messagesForLLM.add(Map.of("role", "system", "content", promptService.getSystemPrompt()));
        if (!lastUser.isBlank()) {
            messagesForLLM.add(Map.of("role", "user", "content", lastUser));
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "deepseek-r1");
        payload.put("messages", messagesForLLM);
        // 如需更多参数，可解注释：
        // payload.put("temperature", 0.7);
        // payload.put("max_tokens", 1024);

        System.out.println("[Chat] 发送给 LLM 的 payload: " + payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(llmApiKey);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        String answer = null;
        try {
            // 用 String 拿原始响应，方便打印和解析
            ResponseEntity<String> resp = restTemplate.postForEntity(llmApiUrl, requestEntity, String.class);
            int status = resp.getStatusCodeValue();
            String body = resp.getBody();
            System.out.println("[LLM 原始响应] 状态: " + status + ", Body: " + body);

            if (resp.getStatusCode().is2xxSuccessful() && body != null) {
                JsonNode root = objectMapper.readTree(body);
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    JsonNode contentNode = choices.get(0).path("message").path("content");
                    if (contentNode.isTextual()) {
                        String raw = contentNode.asText().trim();
                        // 过滤 <think>...</think> 部分，只保留后面的实际回答
                        String cleaned = raw;
                        String startTag = "<think>";
                        String endTag = "</think>";
                        // 若包含 endTag，就取其后部分；否则若包含 startTag，去掉 startTag起始到末尾
                        int idxEnd = raw.indexOf(endTag);
                        if (idxEnd >= 0) {
                            cleaned = raw.substring(idxEnd + endTag.length()).trim();
                        } else {
                            int idxStart = raw.indexOf(startTag);
                            if (idxStart >= 0) {
                                cleaned = raw.substring(idxStart + startTag.length()).trim();
                            }
                        }
                        answer = cleaned;
                    } else {
                        System.err.println("[LLM] content 字段非文本: " + contentNode);
                    }
                } else {
                    System.err.println("[LLM] 未找到 choices 或 choices 为空: " + choices);
                }
            } else {
                System.err.println("[LLM] 非 2xx 响应或 body 为空");
            }
        } catch (HttpClientErrorException | HttpServerErrorException httpEx) {
            System.err.println("[LLM] HTTP 错误: 状态=" + httpEx.getStatusCode()
                    + ", 响应体=" + httpEx.getResponseBodyAsString());
        } catch (ResourceAccessException timeoutEx) {
            System.err.println("[LLM] 访问超时: " + timeoutEx.getMessage());
        } catch (Exception e) {
            System.err.println("[LLM] 解析或调用出错:");
            e.printStackTrace();
        }

        if (answer == null || answer.isEmpty()) {
            answer = "抱歉，未能生成回答。";
        }
        ChatResponse cr = new ChatResponse();
        cr.setContent(answer);
        return cr;
    }

    /**
     * 初始化对话：返回 system prompt + welcome message
     */
    @GetMapping("/init")
    public List<ChatRequest.Message> initChat() {
        List<ChatRequest.Message> init = new ArrayList<>();
        ChatRequest.Message sys = new ChatRequest.Message();
        sys.setRole("system");
        sys.setContent(promptService.getSystemPrompt());
        ChatRequest.Message welcome = new ChatRequest.Message();
        welcome.setRole("assistant");
        welcome.setContent(promptService.getWelcomeMessage());
        init.add(sys);
        init.add(welcome);
        System.out.println("[Chat] init 返回初始消息: " + init);
        return init;
    }
}
