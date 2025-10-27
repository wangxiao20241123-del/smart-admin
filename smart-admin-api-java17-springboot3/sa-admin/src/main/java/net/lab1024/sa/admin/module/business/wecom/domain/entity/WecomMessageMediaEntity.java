/*
 * 企微消息媒体文件实体类
 *
 * @Author:    wangxiao
 * @Date:      2025-10-08
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.wecom.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企微消息媒体文件实体类
 *
 * @author wangxiao
 */
@Data
@TableName("t_wecom_message_media")
public class WecomMessageMediaEntity {

    /**
     * 媒体ID
     */
    @TableId(type = IdType.AUTO)
    private Long mediaId;

    /**
     * 消息ID(关联t_wecom_message)
     */
    private Long messageId;

    /**
     * 文件存储ID(关联t_file)
     */
    private Long fileId;

    /**
     * 企微SDK文件ID
     */
    private String sdkFileId;

    /**
     * 媒体类型(image/video/voice/file)
     */
    private String mediaType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 下载状态(0-待下载 1-下载中 2-已完成 3-失败)
     */
    private Integer downloadStatus;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 下载时间
     */
    private LocalDateTime downloadTime;
}
