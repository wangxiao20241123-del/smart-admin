/*
 * 原型API检查和智能降级工具
 *
 * @Author:    wangxiao
 * @Date:      2025-10-07
 * @Copyright  子午线高科智能科技 2025
 */

/**
 * API检查缓存
 * 避免重复检查同一个API
 */
const apiExistsCache = new Map<string, boolean>();

/**
 * OpenAPI文档缓存
 * 避免重复请求API文档
 */
let apiDocCache: Set<string> | null = null;

/**
 * 从OpenAPI文档获取所有可用的API路径 @author wangxiao
 *
 * @returns Promise<Set<string>> 所有可用的API路径集合
 */
async function fetchApiDocs(): Promise<Set<string>> {
  if (apiDocCache) {
    return apiDocCache;
  }

  try {
    const response = await fetch(`${import.meta.env.VITE_APP_API_URL || ''}/v3/api-docs`);
    if (!response.ok) {
      console.warn('⚠️ [API文档] 获取失败，假定所有API不存在');
      apiDocCache = new Set();
      return apiDocCache;
    }

    const apiDoc = await response.json();
    const paths = new Set<string>(Object.keys(apiDoc.paths || {}));

    apiDocCache = paths;
    console.info(`📚 [API文档] 成功加载，共 ${paths.size} 个API`);

    return paths;
  } catch (error) {
    console.warn('⚠️ [API文档] 获取异常，假定所有API不存在', error);
    apiDocCache = new Set();
    return apiDocCache;
  }
}

/**
 * 检查后端API是否存在 @author wangxiao
 *
 * @param apiPath API路径，如 '/business/wecom/chatRecord/querySessionPage'
 * @returns Promise<boolean> API是否存在
 */
export async function checkApiExists(apiPath: string): Promise<boolean> {
  // 检查缓存
  if (apiExistsCache.has(apiPath)) {
    return apiExistsCache.get(apiPath)!;
  }

  // 获取API文档
  const availableApis = await fetchApiDocs();

  // 检查API是否在文档中
  const exists = availableApis.has(apiPath);

  // 缓存结果
  apiExistsCache.set(apiPath, exists);

  if (!exists) {
    console.info(`🔍 [API检查] ${apiPath} - 不存在`);
  } else {
    console.info(`✅ [API检查] ${apiPath} - 存在`);
  }

  return exists;
}

/**
 * 带智能降级的API调用 @author wangxiao
 *
 * 流程：
 * 1. 检查API是否存在（带缓存）
 * 2. 如果不存在，直接返回Mock数据
 * 3. 如果存在，尝试调用真实API
 * 4. 如果调用失败，降级到Mock数据
 *
 * @param apiPath API路径
 * @param apiCall 真实API调用函数
 * @param mockDataGenerator Mock数据生成函数
 * @returns Promise<T> API响应或Mock数据
 */
export async function callApiWithFallback<T>(
  apiPath: string,
  apiCall: () => Promise<T>,
  mockDataGenerator: () => T | Promise<T>
): Promise<T> {
  // 1. 检查API是否存在
  const exists = await checkApiExists(apiPath);

  if (!exists) {
    // 2. API不存在，直接使用Mock
    console.warn(`🎨 [原型模式] API不存在，使用Mock数据: ${apiPath}`);
    return typeof mockDataGenerator === 'function'
      ? await Promise.resolve(mockDataGenerator())
      : mockDataGenerator;
  }

  try {
    // 3. API存在，尝试调用
    console.info(`📡 [API调用] ${apiPath}`);
    return await apiCall();
  } catch (error) {
    // 4. 调用失败，降级到Mock
    console.warn(`⚠️ [API降级] 调用失败，使用Mock数据: ${apiPath}`, error);
    return typeof mockDataGenerator === 'function'
      ? await Promise.resolve(mockDataGenerator())
      : mockDataGenerator;
  }
}

/**
 * 清除API存在性缓存 @author wangxiao
 *
 * 用于开发环境热更新后重新检查
 *
 * @param apiPath 可选，指定清除某个API的缓存，不传则清除全部
 */
export function clearApiCache(apiPath?: string): void {
  if (apiPath) {
    apiExistsCache.delete(apiPath);
    console.info(`🔄 [缓存清除] ${apiPath}`);
  } else {
    apiExistsCache.clear();
    apiDocCache = null; // 同时清除文档缓存
    console.info(`🔄 [缓存清除] 全部API缓存已清除`);
  }
}
