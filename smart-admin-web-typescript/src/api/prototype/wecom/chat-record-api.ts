/*
 * 企业微信聊天记录 API
 *
 * @Author:    wangxiao
 * @Date:      2025-01-07
 * @Copyright  子午线高科智能科技 2025
 */

import { getRequest, postRequest } from '/@/lib/axios';
import { ResponseModel } from '/@/api/model/response-model';
import { PageResultModel } from '/@/api/model/page-model';
import { ChatRecordQueryForm, ChatRecordVO, ChatRecordDetailVO, ChatRecordExportForm } from './chat-record-model';

/**
 * 聊天记录API
 */
export const chatRecordApi = {
  /**
   * 分页查询聊天记录 @author wangxiao
   */
  queryPage: (param: ChatRecordQueryForm) => {
    return postRequest<ResponseModel<PageResultModel<ChatRecordVO>>>('/business/wecom/chat-record/query-page', param);
  },

  /**
   * 查询聊天记录详情 @author wangxiao
   */
  getDetail: (id: number) => {
    return getRequest<ResponseModel<ChatRecordDetailVO>>(`/business/wecom/chat-record/detail/${id}`);
  },

  /**
   * 导出聊天记录 @author wangxiao
   */
  exportRecords: (param: ChatRecordExportForm) => {
    return postRequest<Blob>('/business/wecom/chat-record/export', param, { responseType: 'blob' });
  },
};
