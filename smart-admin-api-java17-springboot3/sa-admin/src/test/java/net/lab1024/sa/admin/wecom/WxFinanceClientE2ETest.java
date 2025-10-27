/*
 * WxFinanceClient 端到端测试
 *
 * @Author:    wangxiao
 * @Date:      2025-10-08
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.wecom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.wecom.client.WxFinanceClient;
import net.lab1024.sa.admin.module.business.wecom.config.WxFinanceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * WxFinanceClient 端到端测试 - 真实调用 wx-finance 服务
 *
 * @author wangxiao
 */
@Slf4j
public class WxFinanceClientE2ETest {

    private WxFinanceClient wxFinanceClient;
    private RestClient restClient;

    private static final String API_URL = "http://localhost:8088";
    private static final String CORP_ID = "ww7d5bca9c66c2e988";
    private static final String SECRET = "n4TgNHACnKUe8Q_vzcDFpDXtfQ-Go3mHmoG_S1mxPYM";

    @BeforeEach
    public void setup() {
        // 初始化配置
        WxFinanceConfig config = new WxFinanceConfig();
        config.setApiUrl(API_URL);
        config.setConnectTimeout(10000);
        config.setReadTimeout(30000);
        config.setWriteTimeout(30000);

        // 初始化客户端
        wxFinanceClient = new WxFinanceClient();
        java.lang.reflect.Field configField;
        try {
            configField = WxFinanceClient.class.getDeclaredField("wxFinanceConfig");
            configField.setAccessible(true);
            configField.set(wxFinanceClient, config);
        } catch (Exception e) {
            throw new RuntimeException("配置注入失败", e);
        }

        restClient = RestClient.builder()
                .baseUrl(API_URL)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * 初始化SDK
     */
    private void initSdk() throws IOException {
        log.info("========== 步骤1: 初始化SDK ==========");

        JSONObject requestBody = new JSONObject();
        requestBody.put("corpid", CORP_ID);
        requestBody.put("secret", SECRET);

        String responseStr = restClient
                .post()
                .uri("/api/sdk/init")
                .body(requestBody.toJSONString())
                .retrieve()
                .body(String.class);

        JSONObject response = JSON.parseObject(responseStr);
        log.info("SDK初始化响应: {}", JSON.toJSONString(response, true));

        if (response.getInteger("code") != 0) {
            throw new IOException("SDK初始化失败: " + response.getString("message"));
        }

        log.info("SDK初始化成功 ✓");
    }

    /**
     * 测试完整流程：初始化 -> 拉取聊天 -> 下载媒体
     */
    @Test
    public void testCompleteFlow() throws IOException {
        log.info("\n\n========================================");
        log.info("开始端到端测试：下载企微媒体文件");
        log.info("========================================\n");

        // 步骤1: 初始化SDK
        initSdk();

        // 步骤2: 拉取聊天数据
        log.info("\n========== 步骤2: 拉取聊天数据 ==========");
        JSONObject chatData = wxFinanceClient.getChatData(0, 100);
        log.info("拉取到聊天数据");

        Integer errcode = chatData.getInteger("errcode");
        if (errcode != null && errcode != 0) {
            log.error("拉取聊天数据失败: errcode={}, errmsg={}", errcode, chatData.getString("errmsg"));
            return;
        }

        JSONArray chatMessages = chatData.getJSONArray("chatdata");
        if (chatMessages == null || chatMessages.isEmpty()) {
            log.warn("没有聊天数据");
            return;
        }

        log.info("共拉取到 {} 条消息", chatMessages.size());

        // 步骤3: 查找包含媒体的消息
        log.info("\n========== 步骤3: 查找包含媒体的消息 ==========");
        String sdkFileId = null;
        String msgType = null;

        for (int i = 0; i < chatMessages.size(); i++) {
            JSONObject msg = chatMessages.getJSONObject(i);
            String currentMsgType = msg.getString("msgtype");

            log.debug("消息 {}: seq={}, msgtype={}", i + 1, msg.getLong("seq"), currentMsgType);

            // 查找图片、语音、视频、文件消息
            if ("image".equals(currentMsgType) && msg.containsKey("image")) {
                JSONObject image = msg.getJSONObject("image");
                sdkFileId = image.getString("sdkfileid");
                msgType = "image";
                log.info("找到图片消息: sdkFileId={}, filesize={}, md5={}",
                        sdkFileId, image.getLong("filesize"), image.getString("md5sum"));
                break;
            } else if ("voice".equals(currentMsgType) && msg.containsKey("voice")) {
                JSONObject voice = msg.getJSONObject("voice");
                sdkFileId = voice.getString("sdkfileid");
                msgType = "voice";
                log.info("找到语音消息: sdkFileId={}", sdkFileId);
                break;
            } else if ("video".equals(currentMsgType) && msg.containsKey("video")) {
                JSONObject video = msg.getJSONObject("video");
                sdkFileId = video.getString("sdkfileid");
                msgType = "video";
                log.info("找到视频消息: sdkFileId={}", sdkFileId);
                break;
            }
        }

        if (sdkFileId == null) {
            log.warn("未找到包含媒体的消息（image/voice/video）");
            return;
        }

        // 步骤4: 下载媒体文件
        log.info("\n========== 步骤4: 下载媒体文件 ==========");
        log.info("开始下载: sdkFileId={}, msgType={}", sdkFileId, msgType);

        byte[] mediaData = wxFinanceClient.downloadMedia(sdkFileId);

        log.info("媒体文件下载成功 ✓");
        log.info("文件大小: {} 字节 ({} KB)", mediaData.length, mediaData.length / 1024);

        // 步骤5: 保存到本地文件（可选）
        String fileName = String.format("/tmp/wecom_media_%s.%s",
                System.currentTimeMillis(),
                getFileExtension(msgType));

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(mediaData);
            log.info("文件已保存到: {}", fileName);
        }

        log.info("\n========================================");
        log.info("端到端测试成功完成 ✓✓✓");
        log.info("========================================\n");
    }

    /**
     * 根据消息类型获取文件扩展名
     */
    private String getFileExtension(String msgType) {
        return switch (msgType) {
            case "image" -> "jpg";
            case "voice" -> "amr";
            case "video" -> "mp4";
            default -> "bin";
        };
    }

    /**
     * 测试直接下载（使用真实的 sdkFileId）
     */
    @Test
    public void testDownloadWithKnownSdkFileId() throws IOException {
        log.info("\n========== 测试直接下载媒体文件 ==========");

        // 初始化SDK
        initSdk();

        // 使用真实的 sdkFileId
        String sdkFileId = "CoMEKjEqdTNXMmsvMis3VGlnRjNpZU0ySllXYURSaHhYMmhyeGRObzZMVndsT0hNajQvMzhQMytkdTB4Z2ExZmpyN3FmaE1jVG1QN2JhMEVsdUtiOFhTU2JtRE92VnQ1U0twVWtGUzBKQm9sQ2hjcndoY0c1dSs5dzdzakVqWW9JM2ppbG1MK09tRndySzNNZDY4Rml0RTgyVEYvNlVoNjVTNHhUUUkwVVFMMkpPMzFQNW9Dbm1TNjVPYk94MHc2TS9taXFaUGZFUkFhOUpUd29zclhZZ00wS1d5c09MWFNWZE1jdXNNZWMyU0txTHBOS3A3N3BKbThIcmxxVld2L0k1WjhteFlpbzhFbWZ3V0NReVVtb3FaUEc0cUJ5ZjFlcFRUKzF4NkhqSzhYRWkrY2YxbERQdi95ZG9TL0Y1UDRZbVNGWmswQ1hycTVOZWVtL0tpMENYbVRxQXVTMGluUEM2UWM2clB0YnA3Mjl0VDBzcVlCdWM1N1l4NHdBOE54VkRMdUpVQkdtWDc4RFhWa2J0YkN5N3ZFTFBXKy9KSWFSbWcxVjY1dU9JKzBBTlU4OVJMOStIbmloYWVaRUtSRVhsYkJQQVp0R0lua3dJY01GekVPZDF1M2xXTlkvS2NFUTQyVFd3aWpkWlF6aGsxQzVwVVNDenV5OHhXZmNBdHJiYmhYenkSOE5EZGZOemc0TVRNd01ESTFNVEF5TWpJME1GOHhORGd4TmpRNE56UXpYekUzTlRrMU5EWXhNREE9GiBmMTZlMzI3Njg4YmE5MmQzNGYyNDY1OTY0NGIxNzhjMg==";

        log.info("开始下载: sdkFileId={}", sdkFileId.substring(0, 50) + "...");
        byte[] mediaData = wxFinanceClient.downloadMedia(sdkFileId);

        log.info("下载成功，文件大小: {} 字节 ({} KB)", mediaData.length, mediaData.length / 1024);

        // 保存文件验证
        String fileName = String.format("/tmp/wecom_media_java_%s.png", System.currentTimeMillis());
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(mediaData);
            log.info("文件已保存到: {}", fileName);
        }
    }
}
