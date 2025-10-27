/*
 * 企微消息媒体文件Dao
 *
 * @Author:    wangxiao
 * @Date:      2025-10-08
 * @Copyright  子午线高科智能科技 2025
 */
package net.lab1024.sa.admin.module.business.wecom.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.wecom.domain.entity.WecomMessageMediaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企微消息媒体文件Dao
 *
 * @author wangxiao
 */
@Mapper
public interface WecomMessageMediaDao extends BaseMapper<WecomMessageMediaEntity> {

    /**
     * 查询待下载的媒体文件 @author wangxiao
     *
     * @param limit 限制数量
     * @return 待下载的媒体文件列表
     */
    List<WecomMessageMediaEntity> queryPendingDownload(@Param("limit") Integer limit);

    /**
     * 更新下载状态 @author wangxiao
     *
     * @param mediaId 媒体ID
     * @param downloadStatus 下载状态
     * @param fileId 文件ID
     * @param failReason 失败原因
     * @return 更新行数
     */
    int updateDownloadStatus(@Param("mediaId") Long mediaId,
                             @Param("downloadStatus") Integer downloadStatus,
                             @Param("fileId") Long fileId,
                             @Param("failReason") String failReason);
}
