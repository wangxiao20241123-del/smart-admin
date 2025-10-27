/*
 * 企微消息媒体文件服务类
 *
 * @Author:    wangxiao
 * @Date:      2025-10-08
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.wecom.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.wecom.client.WxFinanceClient;
import net.lab1024.sa.admin.module.business.wecom.dao.WecomMessageMediaDao;
import net.lab1024.sa.admin.module.business.wecom.domain.entity.WecomMessageMediaEntity;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.file.constant.FileFolderTypeEnum;
import net.lab1024.sa.base.module.support.file.domain.vo.FileUploadVO;
import net.lab1024.sa.base.module.support.file.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 企微消息媒体文件服务类
 *
 * @author wangxiao
 */
@Slf4j
@Service
public class WecomMessageMediaService {

    @Resource
    private WecomMessageMediaDao wecomMessageMediaDao;

    @Resource
    private WxFinanceClient wxFinanceClient;

    @Resource
    private FileService fileService;

    /**
     * 下载单个媒体文件 @author wangxiao
     *
     * @param mediaId 媒体ID
     * @param requestUser 请求用户
     * @return 下载结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> downloadMedia(Long mediaId, RequestUser requestUser) {
        log.info("开始下载媒体文件: mediaId={}", mediaId);

        // 1. 查询媒体记录
        WecomMessageMediaEntity mediaEntity = wecomMessageMediaDao.selectById(mediaId);
        if (mediaEntity == null) {
            return ResponseDTO.userErrorParam("媒体文件不存在");
        }

        if (mediaEntity.getDownloadStatus() == 2) {
            return ResponseDTO.userErrorParam("媒体文件已下载");
        }

        // 2. 更新状态为"下载中"
        wecomMessageMediaDao.updateDownloadStatus(mediaId, 1, null, null);

        try {
            // 3. 调用wx-finance下载并解密媒体文件
            byte[] mediaData = wxFinanceClient.downloadMedia(mediaEntity.getSdkFileId());
            log.info("媒体文件下载成功: mediaId={}, size={}字节", mediaId, mediaData.length);

            // 4. 转换为MultipartFile
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    mediaData,
                    mediaEntity.getFileName(),
                    mediaEntity.getMediaType()
            );

            // 5. 上传到SmartAdmin文件系统
            ResponseDTO<FileUploadVO> uploadResult = fileService.fileUpload(
                    multipartFile,
                    FileFolderTypeEnum.WECOM_CHAT.getValue(),
                    requestUser
            );

            if (!uploadResult.getOk()) {
                wecomMessageMediaDao.updateDownloadStatus(mediaId, 3, null, uploadResult.getMsg());
                return ResponseDTO.error(uploadResult);
            }

            // 6. 更新状态为"已完成"
            Long fileId = uploadResult.getData().getFileId();
            wecomMessageMediaDao.updateDownloadStatus(mediaId, 2, fileId, null);

            log.info("媒体文件保存成功: mediaId={}, fileId={}", mediaId, fileId);
            return ResponseDTO.ok("媒体文件下载成功");

        } catch (IOException e) {
            log.error("媒体文件下载失败: mediaId={}", mediaId, e);
            wecomMessageMediaDao.updateDownloadStatus(mediaId, 3, null, e.getMessage());
            return ResponseDTO.userErrorParam("媒体文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 批量下载待处理的媒体文件 @author wangxiao
     *
     * @param limit 限制数量
     * @param requestUser 请求用户
     * @return 下载结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> batchDownloadMedia(Integer limit, RequestUser requestUser) {
        log.info("开始批量下载媒体文件: limit={}", limit);

        // 1. 查询待下载的媒体文件
        List<WecomMessageMediaEntity> pendingList = wecomMessageMediaDao.queryPendingDownload(limit);
        if (pendingList.isEmpty()) {
            return ResponseDTO.ok("没有待下载的媒体文件");
        }

        // 2. 逐个下载
        int successCount = 0;
        int failCount = 0;
        for (WecomMessageMediaEntity media : pendingList) {
            ResponseDTO<String> result = downloadMedia(media.getMediaId(), requestUser);
            if (result.getOk()) {
                successCount++;
            } else {
                failCount++;
            }
        }

        log.info("批量下载完成: 成功={}, 失败={}", successCount, failCount);
        return ResponseDTO.ok(String.format("下载完成: 成功%d个, 失败%d个", successCount, failCount));
    }

    /**
     * ByteArray转MultipartFile工具类 @author wangxiao
     */
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String contentType;

        public ByteArrayMultipartFile(byte[] content, String name, String contentType) {
            this.content = content;
            this.name = name;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return name;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("transferTo not supported");
        }
    }
}
