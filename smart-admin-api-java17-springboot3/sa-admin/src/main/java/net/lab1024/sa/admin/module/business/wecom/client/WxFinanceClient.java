/*
 * WxFinance HTTP客户端
 *
 * @Author:    wangxiao
 * @Date:      2025-10-08
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.wecom.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.wecom.config.WxFinanceConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * WxFinance HTTP客户端 - 调用wx-finance服务下载并解密媒体文件
 * 使用 Spring Boot 3 推荐的 RestClient API
 *
 * @author wangxiao
 */
@Slf4j
@Component
public class WxFinanceClient {

    @Resource
    private WxFinanceConfig wxFinanceConfig;

    private volatile RestClient restClient;

    /**
     * 获取RestClient实例 (懒加载+双重检查锁)
     *
     * @return RestClient
     * @author wangxiao
     */
    private RestClient getRestClient() {
        if (restClient == null) {
            synchronized (this) {
                if (restClient == null) {
                    restClient = RestClient.builder()
                            .baseUrl(wxFinanceConfig.getApiUrl())
                            .defaultHeader("Content-Type", "application/json")
                            .build();
                }
            }
        }
        return restClient;
    }

    /**
     * 下载并解密媒体文件（支持分片下载）
     *
     * @param sdkFileId 企微SDK文件ID
     * @return 解密后的文件字节数组
     * @author wangxiao
     */
    public byte[] downloadMedia(String sdkFileId) throws IOException {
        log.info("开始下载企微媒体文件: sdkFileId={}", sdkFileId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String indexBuf = ""; // 首次为空
        boolean finished = false;
        int chunkCount = 0;

        try {
            while (!finished) {
                chunkCount++;
                log.debug("下载分片 {}: sdkFileId={}, indexBuf={}", chunkCount, sdkFileId, indexBuf);

                // 构建请求JSON
                JSONObject requestBody = new JSONObject();
                requestBody.put("sdkFileId", sdkFileId);
                requestBody.put("indexBuf", indexBuf);
                requestBody.put("timeout", wxFinanceConfig.getReadTimeout() / 1000); // 转为秒

                String responseStr = getRestClient()
                        .post()
                        .uri("/api/media/data")
                        .body(requestBody.toJSONString())
                        .retrieve()
                        .body(String.class);

                if (responseStr == null) {
                    throw new IOException("响应体为空");
                }

                JSONObject response = JSON.parseObject(responseStr);

                // 检查响应码
                Integer code = response.getInteger("code");
                if (code == null || code != 0) {
                    String message = response.getString("message");
                    throw new IOException("下载媒体文件失败: " + message);
                }

                // 解析数据
                JSONObject data = response.getJSONObject("data");
                if (data == null) {
                    throw new IOException("响应data字段为空");
                }

                // 获取base64编码的数据并解码
                String base64Data = data.getString("data");
                if (base64Data != null && !base64Data.isEmpty()) {
                    byte[] chunkData = Base64.getDecoder().decode(base64Data);
                    outputStream.write(chunkData);
                    log.debug("分片 {} 下载成功，大小: {} 字节", chunkCount, chunkData.length);
                }

                // 检查是否完成
                Boolean finishFlag = data.getBoolean("finish");
                finished = Boolean.TRUE.equals(finishFlag);

                // 获取下一次的indexBuf
                if (!finished) {
                    String outIndexBuf = data.getString("outIndexBuf");
                    if (outIndexBuf == null || outIndexBuf.isEmpty()) {
                        log.warn("未完成但outIndexBuf为空，终止下载");
                        break;
                    }
                    indexBuf = outIndexBuf;
                }
            }

            byte[] mediaData = outputStream.toByteArray();
            log.info("媒体文件下载完成: sdkFileId={}, 分片数={}, 总大小={}字节",
                    sdkFileId, chunkCount, mediaData.length);
            return mediaData;

        } catch (Exception e) {
            log.error("下载媒体文件失败: sdkFileId={}", sdkFileId, e);
            throw new IOException("下载媒体文件失败: " + e.getMessage(), e);
        } finally {
            outputStream.close();
        }
    }

    /**
     * 获取聊天数据（已解密）
     *
     * @param seq 消息序列号，首次使用0
     * @param limit 单次拉取的消息条数，最多1000条
     * @return 解密后的聊天数据
     * @author wangxiao
     */
    public JSONObject getChatData(long seq, int limit) throws IOException {
        log.info("开始拉取聊天数据: seq={}, limit={}", seq, limit);

        JSONObject requestBody = new JSONObject();
        requestBody.put("seq", seq);
        requestBody.put("limit", limit);
        requestBody.put("timeout", wxFinanceConfig.getReadTimeout() / 1000);

        try {
            String responseStr = getRestClient()
                    .post()
                    .uri("/api/chat/data")
                    .body(requestBody.toJSONString())
                    .retrieve()
                    .body(String.class);

            if (responseStr == null) {
                throw new IOException("响应体为空");
            }

            JSONObject response = JSON.parseObject(responseStr);

            Integer code = response.getInteger("code");
            if (code == null || code != 0) {
                String message = response.getString("message");
                throw new IOException("拉取聊天数据失败: " + message);
            }

            log.info("聊天数据拉取成功");
            return response.getJSONObject("data");

        } catch (Exception e) {
            log.error("拉取聊天数据失败: seq={}", seq, e);
            throw new IOException("拉取聊天数据失败: " + e.getMessage(), e);
        }
    }
}
