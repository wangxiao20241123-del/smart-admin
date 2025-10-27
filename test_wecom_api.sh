#!/bin/bash
#
# 企业微信 API 测试脚本 (使用 curl)
#
# @Author:    wangxiao
# @Date:      2025-10-14
# @Copyright  子午线高科智能科技 2025

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 企业微信配置
CORP_ID="ww7d5bca9c66c2e988"
CORP_SECRET="n4TgNHACnKUe8Q_vzcDFpDXtfQ-Go3mHmoG_S1mxPYM"
ACCESS_TOKEN=""

echo ""
echo "===================================================="
echo "企业微信 API 测试"
echo "===================================================="
echo "测试时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo "企业ID: $CORP_ID"
echo ""

# ============================================
# 1. 获取 Access Token
# ============================================
echo "===================================================="
echo "【1】获取 Access Token"
echo "===================================================="
echo "请求URL: https://qyapi.weixin.qq.com/cgi-bin/gettoken"
echo ""

TOKEN_RESPONSE=$(curl -s "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=$CORP_ID&corpsecret=$CORP_SECRET")
echo "$TOKEN_RESPONSE" | python3 -m json.tool

ERRCODE=$(echo "$TOKEN_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('errcode', -1))")

if [ "$ERRCODE" == "0" ]; then
    ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('access_token', ''))")
    EXPIRES_IN=$(echo "$TOKEN_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('expires_in', 0))")
    echo ""
    echo -e "${GREEN}✅ 成功获取 Access Token!${NC}"
    echo "Token: ${ACCESS_TOKEN:0:30}..."
    echo "过期时间: $EXPIRES_IN 秒"
else
    echo ""
    echo -e "${RED}❌ 获取失败! 错误码: $ERRCODE${NC}"
    exit 1
fi

# ============================================
# 2. 获取部门列表
# ============================================
echo ""
echo "===================================================="
echo "【2】获取部门列表"
echo "===================================================="
echo "请求URL: https://qyapi.weixin.qq.com/cgi-bin/department/list"
echo ""

DEPT_RESPONSE=$(curl -s "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=$ACCESS_TOKEN")
echo "$DEPT_RESPONSE" | python3 -m json.tool

ERRCODE=$(echo "$DEPT_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('errcode', -1))")

if [ "$ERRCODE" == "0" ]; then
    DEPT_COUNT=$(echo "$DEPT_RESPONSE" | python3 -c "import sys, json; print(len(json.load(sys.stdin).get('department', [])))")
    echo ""
    echo -e "${GREEN}✅ 成功获取部门列表! 部门数量: $DEPT_COUNT${NC}"
else
    echo ""
    echo -e "${RED}❌ 获取失败! 错误码: $ERRCODE${NC}"
fi

# ============================================
# 3. 获取成员列表
# ============================================
echo ""
echo "===================================================="
echo "【3】获取成员简单列表 (部门ID=1)"
echo "===================================================="
echo "请求URL: https://qyapi.weixin.qq.com/cgi-bin/user/simplelist"
echo ""

USER_RESPONSE=$(curl -s "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=$ACCESS_TOKEN&department_id=1")
echo "$USER_RESPONSE" | python3 -m json.tool

ERRCODE=$(echo "$USER_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('errcode', -1))")

if [ "$ERRCODE" == "0" ]; then
    USER_COUNT=$(echo "$USER_RESPONSE" | python3 -c "import sys, json; print(len(json.load(sys.stdin).get('userlist', [])))")
    echo ""
    echo -e "${GREEN}✅ 成功获取成员列表! 成员数量: $USER_COUNT${NC}"

    # 如果有成员,获取第一个成员的详情
    if [ "$USER_COUNT" -gt "0" ]; then
        FIRST_USER_ID=$(echo "$USER_RESPONSE" | python3 -c "import sys, json; users = json.load(sys.stdin).get('userlist', []); print(users[0].get('userid', '') if users else '')")

        if [ -n "$FIRST_USER_ID" ]; then
            echo ""
            echo "===================================================="
            echo "【4】获取成员详情 (UserID: $FIRST_USER_ID)"
            echo "===================================================="
            echo "请求URL: https://qyapi.weixin.qq.com/cgi-bin/user/get"
            echo ""

            USER_DETAIL=$(curl -s "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=$ACCESS_TOKEN&userid=$FIRST_USER_ID")
            echo "$USER_DETAIL" | python3 -m json.tool

            ERRCODE=$(echo "$USER_DETAIL" | python3 -c "import sys, json; print(json.load(sys.stdin).get('errcode', -1))")

            if [ "$ERRCODE" == "0" ]; then
                echo ""
                echo -e "${GREEN}✅ 成功获取成员详情!${NC}"
            else
                echo ""
                echo -e "${RED}❌ 获取失败! 错误码: $ERRCODE${NC}"
            fi
        fi
    fi
else
    ERRMSG=$(echo "$USER_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('errmsg', ''))")
    echo ""
    echo -e "${YELLOW}⚠️  获取失败! 错误码: $ERRCODE${NC}"
    echo -e "${YELLOW}错误信息: $ERRMSG${NC}"
    echo ""
    echo -e "${YELLOW}可能原因:${NC}"
    echo "  1. 该应用Secret没有通讯录权限 (错误码 60011)"
    echo "  2. 需要使用通讯录管理Secret或配置应用可见范围"
    echo "  3. 如果是会话存档Secret,可能仅有会话存档相关权限"
fi

# ============================================
# 总结
# ============================================
echo ""
echo "===================================================="
echo "测试完成!"
echo "===================================================="
echo ""
echo "API 调用总结:"
echo "  ✅ Access Token: 成功获取"
echo "  $([ "$ERRCODE" == "0" ] && echo '✅' || echo '⚠️ ') 部门列表: $([ "$ERRCODE" == "0" ] && echo '可访问' || echo '权限不足')"
echo "  $([ "$ERRCODE" == "0" ] && echo '✅' || echo '⚠️ ') 成员列表: $([ "$ERRCODE" == "0" ] && echo '可访问' || echo '权限不足 (错误码: 60011)')"
echo ""
echo "建议:"
echo "  - 如需测试通讯录API,请使用通讯录管理Secret"
echo "  - 当前Secret可能是会话存档专用Secret"
echo "  - 可以继续测试会话存档相关API"
echo ""
