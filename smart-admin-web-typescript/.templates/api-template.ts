/*
 *  [模块名称]
 *
 * @Author:    wangxiao
 * @Date:      [日期]
 * @Copyright  SmartAdmin Team
 */
import { getRequest, postRequest } from '/@/lib/axios';
import { ResponseModel } from '/@/api/base-model/response-model';
import { PageResultModel } from '/@/api/base-model/page-result-model';
import {
  [ModuleName]QueryForm,
  [ModuleName]AddForm,
  [ModuleName]UpdateForm,
  [ModuleName]VO,
} from './[module-name]-model';

export const [moduleName]Api = {
  /**
   * 分页查询 @author wangxiao
   */
  queryPage: (param: [ModuleName]QueryForm) => {
    return postRequest<ResponseModel<PageResultModel<[ModuleName]VO>>>('/[module-path]/queryPage', param);
  },

  /**
   * 新增 @author wangxiao
   */
  add: (param: [ModuleName]AddForm) => {
    return postRequest<ResponseModel<void>>('/[module-path]/add', param);
  },

  /**
   * 更新 @author wangxiao
   */
  update: (param: [ModuleName]UpdateForm) => {
    return postRequest<ResponseModel<void>>('/[module-path]/update', param);
  },

  /**
   * 删除 @author wangxiao
   */
  delete: (id: number) => {
    return getRequest<ResponseModel<void>>(`/[module-path]/delete/${id}`);
  },

  /**
   * 详情 @author wangxiao
   */
  detail: (id: number) => {
    return getRequest<ResponseModel<[ModuleName]VO>>(`/[module-path]/detail/${id}`);
  },
};
