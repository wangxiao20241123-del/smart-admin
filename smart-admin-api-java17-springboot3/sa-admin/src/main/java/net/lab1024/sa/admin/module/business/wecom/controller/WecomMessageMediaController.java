/*
 * 企微消息媒体文件Controller
 *
 * @Author:    wangxiao
 * @Date:      2025-10-08
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.wecom.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.business.wecom.service.WecomMessageMediaService;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企微消息媒体文件Controller
 *
 * @author wangxiao
 */
@RestController
@Tag(name = "企微消息媒体文件")
public class WecomMessageMediaController {

    @Resource
    private WecomMessageMediaService wecomMessageMediaService;

    /**
     * 下载单个媒体文件 @author wangxiao
     */
    @Operation(summary = "下载单个媒体文件 @author wangxiao")
    @GetMapping("/wecom/message/media/download/{mediaId}")
    @SaCheckPermission("wecom:message:media:download")
    public ResponseDTO<String> downloadMedia(@PathVariable Long mediaId) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        return wecomMessageMediaService.downloadMedia(mediaId, requestUser);
    }

    /**
     * 批量下载待处理的媒体文件 @author wangxiao
     */
    @Operation(summary = "批量下载待处理的媒体文件 @author wangxiao")
    @GetMapping("/wecom/message/media/batch-download")
    @SaCheckPermission("wecom:message:media:download")
    public ResponseDTO<String> batchDownloadMedia(
            @RequestParam(defaultValue = "10") Integer limit) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        return wecomMessageMediaService.batchDownloadMedia(limit, requestUser);
    }
}
