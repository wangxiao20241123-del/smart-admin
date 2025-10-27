/*
 * 企业微信会话存档拉取测试（通过 Docker SDK HTTP 服务）
 *
 * @Author:    wangxiao
 * @Date:      2025-10-07
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.wecom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 企业微信会话存档拉取测试（通过 Docker SDK HTTP 服务）
 *
 * @author wangxiao
 */
public class WxCpChatDataHttpTest {

    // Docker SDK 服务地址
    private static final String SDK_SERVICE_URL = "http://localhost:8888";

    // 企业ID
    private static final String CORP_ID = "ww7d5bca9c66c2e988";

    // 应用Secret
    private static final String CORP_SECRET = "n4TgNHACnKUe8Q_vzcDFpDXtfQ-Go3mHmoG_S1mxPYM";

    // HTTP 客户端
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * 测试健康检查 @author wangxiao
     */
    @Test
    public void testHealthCheck() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/health"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("\n【健康检查】");
            System.out.println("响应状态: " + response.statusCode());
            System.out.println("响应内容: " + JSON.toJSONString(JSON.parseObject(response.body()), true));

        } catch (Exception e) {
            System.err.println("健康检查失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试初始化 SDK @author wangxiao
     */
    @Test
    public void testInitSdk() {
        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("corpId", CORP_ID);
            requestBody.put("secret", CORP_SECRET);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/init"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("\n【SDK 初始化】");
            System.out.println("响应状态: " + response.statusCode());
            System.out.println("响应内容: " + JSON.toJSONString(JSON.parseObject(response.body()), true));

        } catch (Exception e) {
            System.err.println("SDK 初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试拉取聊天记录 @author wangxiao
     */
    @Test
    public void testGetChatData() {
        try {
            // 1. 先初始化 SDK
            System.out.println("\n步骤1: 初始化 SDK");
            JSONObject initBody = new JSONObject();
            initBody.put("corpId", CORP_ID);
            initBody.put("secret", CORP_SECRET);

            HttpRequest initRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/init"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(initBody.toJSONString()))
                    .build();

            HttpResponse<String> initResponse = httpClient.send(initRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("初始化响应: " + JSON.toJSONString(JSON.parseObject(initResponse.body()), true));

            // 2. 拉取聊天记录
            System.out.println("\n步骤2: 拉取聊天记录");
            JSONObject chatDataBody = new JSONObject();
            chatDataBody.put("seq", 0);
            chatDataBody.put("limit", 1000);
            chatDataBody.put("proxy", "");
            chatDataBody.put("passwd", "");
            chatDataBody.put("timeout", 10);

            HttpRequest chatRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/get_chat_data"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(chatDataBody.toJSONString()))
                    .build();

            HttpResponse<String> chatResponse = httpClient.send(chatRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("\n【拉取聊天记录】");
            System.out.println("响应状态: " + chatResponse.statusCode());
            System.out.println("响应内容: " + JSON.toJSONString(JSON.parseObject(chatResponse.body()), true));

        } catch (Exception e) {
            System.err.println("拉取聊天记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试解密消息 @author wangxiao
     */
    @Test
    public void testDecryptMessage() {
        try {
            // 1. 先初始化 SDK
            System.out.println("\n步骤1: 初始化 SDK");
            JSONObject initBody = new JSONObject();
            initBody.put("corpId", CORP_ID);
            initBody.put("secret", CORP_SECRET);

            HttpRequest initRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/init"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(initBody.toJSONString()))
                    .build();

            HttpResponse<String> initResponse = httpClient.send(initRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("初始化响应: " + JSON.toJSONString(JSON.parseObject(initResponse.body()), true));

            // 2. 拉取聊天记录
            System.out.println("\n步骤2: 拉取聊天记录");
            JSONObject chatDataBody = new JSONObject();
            chatDataBody.put("seq", 0);
            chatDataBody.put("limit", 10);
            chatDataBody.put("timeout", 10);

            HttpRequest chatRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/get_chat_data"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(chatDataBody.toJSONString()))
                    .build();

            HttpResponse<String> chatResponse = httpClient.send(chatRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject chatResult = JSON.parseObject(chatResponse.body());

            System.out.println("聊天记录响应: " + JSON.toJSONString(chatResult, true));

            // 3. 如果有数据,尝试解密第一条消息
            if (chatResult.getBoolean("success") && chatResult.containsKey("data")) {
                JSONObject data = chatResult.getJSONObject("data");

                if (data.containsKey("chatdata") && !data.getJSONArray("chatdata").isEmpty()) {
                    JSONObject firstMessage = data.getJSONArray("chatdata").getJSONObject(0);

                    String encryptKey = firstMessage.getString("encrypt_random_key");
                    String encryptMsg = firstMessage.getString("encrypt_chat_msg");

                    System.out.println("\n步骤3: 解密第一条消息");
                    JSONObject decryptBody = new JSONObject();
                    decryptBody.put("encryptKey", encryptKey);
                    decryptBody.put("encryptMsg", encryptMsg);

                    HttpRequest decryptRequest = HttpRequest.newBuilder()
                            .uri(URI.create(SDK_SERVICE_URL + "/decrypt"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(decryptBody.toJSONString()))
                            .build();

                    HttpResponse<String> decryptResponse = httpClient.send(decryptRequest, HttpResponse.BodyHandlers.ofString());

                    System.out.println("\n【解密结果】");
                    System.out.println("响应状态: " + decryptResponse.statusCode());
                    System.out.println("响应内容: " + JSON.toJSONString(JSON.parseObject(decryptResponse.body()), true));
                } else {
                    System.out.println("\n没有聊天数据可供解密");
                }
            }

        } catch (Exception e) {
            System.err.println("解密消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 完整流程测试 @author wangxiao
     */
    @Test
    public void testCompleteFlow() {
        try {
            System.out.println("========== 企业微信会话存档完整测试流程 ==========\n");

            // 1. 健康检查
            System.out.println("【步骤1】健康检查");
            HttpRequest healthRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/health"))
                    .GET()
                    .build();

            HttpResponse<String> healthResponse = httpClient.send(healthRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("健康状态: " + JSON.toJSONString(JSON.parseObject(healthResponse.body()), true));

            // 2. 初始化 SDK
            System.out.println("\n【步骤2】初始化 SDK");
            JSONObject initBody = new JSONObject();
            initBody.put("corpId", CORP_ID);
            initBody.put("secret", CORP_SECRET);

            HttpRequest initRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/init"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(initBody.toJSONString()))
                    .build();

            HttpResponse<String> initResponse = httpClient.send(initRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject initResult = JSON.parseObject(initResponse.body());
            System.out.println("初始化结果: " + JSON.toJSONString(initResult, true));

            if (!initResult.getBoolean("success")) {
                System.err.println("SDK 初始化失败,终止测试");
                return;
            }

            // 3. 拉取聊天记录
            System.out.println("\n【步骤3】拉取聊天记录");
            JSONObject chatDataBody = new JSONObject();
            chatDataBody.put("seq", 0);
            chatDataBody.put("limit", 1000);
            chatDataBody.put("timeout", 10);

            HttpRequest chatRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SDK_SERVICE_URL + "/get_chat_data"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(chatDataBody.toJSONString()))
                    .build();

            HttpResponse<String> chatResponse = httpClient.send(chatRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject chatResult = JSON.parseObject(chatResponse.body());

            System.out.println("拉取结果: " + JSON.toJSONString(chatResult, true));

            // 4. 统计和输出
            if (chatResult.getBoolean("success") && chatResult.containsKey("data")) {
                JSONObject data = chatResult.getJSONObject("data");

                System.out.println("\n【步骤4】数据统计");
                System.out.println("错误码: " + data.getInteger("errcode"));
                System.out.println("错误信息: " + data.getString("errmsg"));

                if (data.containsKey("chatdata")) {
                    int count = data.getJSONArray("chatdata").size();
                    System.out.println("拉取到聊天记录数: " + count);

                    if (count > 0) {
                        System.out.println("\n【步骤5】消息列表:");
                        for (int i = 0; i < Math.min(count, 5); i++) {
                            JSONObject msg = data.getJSONArray("chatdata").getJSONObject(i);
                            System.out.println((i + 1) + ". seq=" + msg.getLong("seq") +
                                             ", msgid=" + msg.getString("msgid") +
                                             ", publickey_ver=" + msg.getInteger("publickey_ver"));
                        }

                        if (count > 5) {
                            System.out.println("... 还有 " + (count - 5) + " 条消息");
                        }
                    }
                }
            }

            System.out.println("\n========== 测试完成 ==========");

        } catch (Exception e) {
            System.err.println("完整流程测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
