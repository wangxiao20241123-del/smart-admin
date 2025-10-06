<!--
  * 学员跟进 - 沉睡唤醒(占位页面)
  *
  * @Author:    wangxiao
  * @Date:      2025-10-06
  * @Copyright  子午线高科智能科技 2025
-->
<template>
  <a-card :bordered="false">
    <a-result status="info" title="沉睡唤醒功能">
      <template #subTitle>
        此功能正在开发中，敬请期待...
      </template>
      <template #extra>
        <p style="color: #666; margin-bottom: 20px;">
          功能说明：用于唤醒长期未互动的沉睡学员，通过AI分析生成个性化唤醒话术和策略。
        </p>
        <a-button type="primary" @click="goBack">返回</a-button>
      </template>
    </a-result>
  </a-card>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

/**
 * 返回上一页 @author wangxiao
 */
function goBack() {
  router.back();
}
</script>

<style lang="less" scoped>
</style>
