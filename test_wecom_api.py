#!/usr/bin/env python3
"""
企业微信 API 测试脚本

@Author:    wangxiao
@Date:      2025-10-14
@Copyright  子午线高科智能科技 2025
"""

import requests
import json
from datetime import datetime

# 企业微信配置
CORP_ID = "ww7d5bca9c66c2e988"
CORP_SECRET = "n4TgNHACnKUe8Q_vzcDFpDXtfQ-Go3mHmoG_S1mxPYM"

class WecomAPITester:
    def __init__(self, corp_id, corp_secret):
        self.corp_id = corp_id
        self.corp_secret = corp_secret
        self.access_token = None
        self.base_url = "https://qyapi.weixin.qq.com"

    def get_access_token(self):
        """
        获取 Access Token
        @author wangxiao
        """
        url = f"{self.base_url}/cgi-bin/gettoken"
        params = {
            "corpid": self.corp_id,
            "corpsecret": self.corp_secret
        }

        print("\n" + "="*60)
        print("【1】获取 Access Token")
        print("="*60)
        print(f"请求URL: {url}")
        print(f"请求参数: {json.dumps(params, ensure_ascii=False, indent=2)}")

        try:
            response = requests.get(url, params=params, timeout=10)
            result = response.json()

            print(f"\n响应状态码: {response.status_code}")
            print(f"响应数据:\n{json.dumps(result, ensure_ascii=False, indent=2)}")

            if result.get('errcode') == 0:
                self.access_token = result.get('access_token')
                print(f"\n✅ 成功获取 Access Token!")
                print(f"Token: {self.access_token[:20]}...")
                print(f"过期时间: {result.get('expires_in')}秒")
                return True
            else:
                print(f"\n❌ 获取失败!")
                print(f"错误码: {result.get('errcode')}")
                print(f"错误信息: {result.get('errmsg')}")
                return False

        except Exception as e:
            print(f"\n❌ 请求异常: {str(e)}")
            return False

    def get_department_list(self):
        """
        获取部门列表
        @author wangxiao
        """
        if not self.access_token:
            print("\n❌ 请先获取 Access Token")
            return None

        url = f"{self.base_url}/cgi-bin/department/list"
        params = {
            "access_token": self.access_token
        }

        print("\n" + "="*60)
        print("【2】获取部门列表")
        print("="*60)
        print(f"请求URL: {url}")

        try:
            response = requests.get(url, params=params, timeout=10)
            result = response.json()

            print(f"\n响应状态码: {response.status_code}")
            print(f"响应数据:\n{json.dumps(result, ensure_ascii=False, indent=2)}")

            if result.get('errcode') == 0:
                departments = result.get('department', [])
                print(f"\n✅ 成功获取部门列表!")
                print(f"部门数量: {len(departments)}")
                for dept in departments:
                    print(f"  - ID: {dept.get('id')}, 名称: {dept.get('name')}, 父部门: {dept.get('parentid')}")
                return departments
            else:
                print(f"\n❌ 获取失败!")
                print(f"错误码: {result.get('errcode')}")
                print(f"错误信息: {result.get('errmsg')}")
                return None

        except Exception as e:
            print(f"\n❌ 请求异常: {str(e)}")
            return None

    def get_user_list(self, department_id=1):
        """
        获取部门成员列表
        @author wangxiao
        """
        if not self.access_token:
            print("\n❌ 请先获取 Access Token")
            return None

        url = f"{self.base_url}/cgi-bin/user/simplelist"
        params = {
            "access_token": self.access_token,
            "department_id": department_id
        }

        print("\n" + "="*60)
        print(f"【3】获取部门成员列表 (部门ID: {department_id})")
        print("="*60)
        print(f"请求URL: {url}")

        try:
            response = requests.get(url, params=params, timeout=10)
            result = response.json()

            print(f"\n响应状态码: {response.status_code}")
            print(f"响应数据:\n{json.dumps(result, ensure_ascii=False, indent=2)}")

            if result.get('errcode') == 0:
                users = result.get('userlist', [])
                print(f"\n✅ 成功获取成员列表!")
                print(f"成员数量: {len(users)}")
                for user in users[:5]:  # 只显示前5个
                    print(f"  - UserID: {user.get('userid')}, 姓名: {user.get('name')}, 部门: {user.get('department')}")
                if len(users) > 5:
                    print(f"  ... 还有 {len(users) - 5} 个成员")
                return users
            else:
                print(f"\n❌ 获取失败!")
                print(f"错误码: {result.get('errcode')}")
                print(f"错误信息: {result.get('errmsg')}")
                return None

        except Exception as e:
            print(f"\n❌ 请求异常: {str(e)}")
            return None

    def get_user_detail(self, userid):
        """
        获取成员详情
        @author wangxiao
        """
        if not self.access_token:
            print("\n❌ 请先获取 Access Token")
            return None

        url = f"{self.base_url}/cgi-bin/user/get"
        params = {
            "access_token": self.access_token,
            "userid": userid
        }

        print("\n" + "="*60)
        print(f"【4】获取成员详情 (UserID: {userid})")
        print("="*60)
        print(f"请求URL: {url}")

        try:
            response = requests.get(url, params=params, timeout=10)
            result = response.json()

            print(f"\n响应状态码: {response.status_code}")
            print(f"响应数据:\n{json.dumps(result, ensure_ascii=False, indent=2)}")

            if result.get('errcode') == 0:
                print(f"\n✅ 成功获取成员详情!")
                print(f"UserID: {result.get('userid')}")
                print(f"姓名: {result.get('name')}")
                print(f"别名: {result.get('alias')}")
                print(f"手机: {result.get('mobile')}")
                print(f"职位: {result.get('position')}")
                print(f"性别: {result.get('gender')}")
                print(f"头像: {result.get('avatar')}")
                print(f"部门: {result.get('department')}")
                print(f"状态: {result.get('status')}")
                return result
            else:
                print(f"\n❌ 获取失败!")
                print(f"错误码: {result.get('errcode')}")
                print(f"错误信息: {result.get('errmsg')}")
                return None

        except Exception as e:
            print(f"\n❌ 请求异常: {str(e)}")
            return None


def main():
    """
    主函数
    @author wangxiao
    """
    print("\n" + "="*60)
    print("企业微信 API 测试")
    print("="*60)
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"企业ID: {CORP_ID}")

    # 创建测试实例
    tester = WecomAPITester(CORP_ID, CORP_SECRET)

    # 1. 获取 Access Token
    if not tester.get_access_token():
        print("\n❌ 无法继续测试，请检查企业ID和Secret是否正确")
        return

    # 2. 获取部门列表
    departments = tester.get_department_list()

    # 3. 获取成员列表
    users = tester.get_user_list(department_id=1)

    # 4. 获取第一个成员的详情
    if users and len(users) > 0:
        first_user = users[0]
        tester.get_user_detail(first_user.get('userid'))

    print("\n" + "="*60)
    print("测试完成!")
    print("="*60)


if __name__ == "__main__":
    main()
