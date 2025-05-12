package com.yu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private final RestTemplate restTemplate;
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String API_KEY = "sk-597ec64bf18f400d92b09158df93d464"; // 建议移到配置文件中

    public ApiController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/deepseek-proxy")
    public ResponseEntity<Map<String, Object>> proxyToDeepSeek(@RequestBody Map<String, Object> request) {
        // 参数校验
        if (request == null ||!request.containsKey("messages")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "请求参数不能为空"));
        }

        // 准备请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        // 构建DeepSeek API请求体，直接使用前端传递的消息列表
        Map<String, Object> deepSeekRequest = new HashMap<>();
        deepSeekRequest.put("model", "deepseek-reasoner");
        deepSeekRequest.put("messages", request.get("messages"));
        deepSeekRequest.put("temperature", 0.7);

        // 发送请求到DeepSeek API
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(deepSeekRequest, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    DEEPSEEK_API_URL,
                    entity,
                    Map.class
            );

            // 处理并返回响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String aiResponse = extractAiResponse(response.getBody());
                if (aiResponse == null || aiResponse.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "DeepSeek API返回了空响应"));
                }
                return ResponseEntity.ok(Map.of("response", aiResponse));
            } else {
                // 记录详细的错误信息
                String errorMsg = "DeepSeek API请求失败，状态码: " + response.getStatusCode();
                if (response.getBody() != null) {
                    errorMsg += "，响应: " + response.getBody();
                }
                return ResponseEntity.status(response.getStatusCode())
                        .body(Map.of("error", errorMsg));
            }
        } catch (Exception e) {
            // 记录完整的异常堆栈
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "服务暂时不可用",
                            "details", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    @PostMapping("/deepseek-stream")
    public ResponseEntity<StreamingResponseBody> streamToDeepSeek(@RequestBody Map<String, Object> request) {
        // 参数校验
        if (request == null ||!request.containsKey("messages")) {
            return ResponseEntity.badRequest().build();
        }

        // 配置RestTemplate支持流式响应
        RestTemplate streamingTemplate = new RestTemplate();
        streamingTemplate.setRequestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory());

        // 准备请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Accept", "text/event-stream");

        // 构建请求体，直接使用前端传递的消息列表
        Map<String, Object> deepSeekRequest = new HashMap<>();
        deepSeekRequest.put("model", "deepseek-chat");
        deepSeekRequest.put("stream", true);
        deepSeekRequest.put("messages", request.get("messages"));

        // 创建流式响应
        StreamingResponseBody responseBody = outputStream -> {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(deepSeekRequest, headers);
            try {
                streamingTemplate.execute(DEEPSEEK_API_URL, HttpMethod.POST,
                        requestCallback -> {
                            requestCallback.getHeaders().putAll(headers);
                            requestCallback.getBody().write(new ObjectMapper().writeValueAsBytes(deepSeekRequest));
                        },
                        responseExtractor -> {
                            try (InputStream inputStream = responseExtractor.getBody()) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                    outputStream.flush();
                                }
                            }
                            return null;
                        });
            } catch (Exception e) {
                logger.error("Error streaming response from DeepSeek API", e);
                throw e;
            } finally {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    logger.error("Error closing output stream", e);
                }
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(responseBody);
    }

    private String extractAiResponse(Map<String, Object> apiResponse) {
        // 从DeepSeek API响应中提取AI回复内容
        if (apiResponse.containsKey("choices") &&
                ((java.util.List<?>) apiResponse.get("choices")).size() > 0) {
            Map<?, ?> firstChoice = (Map<?, ?>) ((java.util.List<?>) apiResponse.get("choices")).get(0);
            if (firstChoice.containsKey("message")) {
                Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                return (String) message.get("content");
            }
        }
        return "未能获取有效回复";
    }
}