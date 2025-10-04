/*
 * SmartAdmin增强ESLint配置
 *
 * @Description: 基于项目代码规范的严格ESLint规则
 * @Author: 1024创新实验室
 * @Date: 2025-10-04
 */
module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  parser: 'vue-eslint-parser',
  parserOptions: {
    ecmaVersion: 12,
    sourceType: 'module',
    parser: '@typescript-eslint/parser',
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    'plugin:vue/base',
    'plugin:@typescript-eslint/recommended',
  ],
  globals: {
    defineProps: 'readonly',
    defineEmits: 'readonly',
    defineExpose: 'readonly',
    withDefaults: 'readonly',
  },
  plugins: ['vue', '@typescript-eslint'],
  rules: {
    // ==================== 基础规则 ====================
    'no-unused-vars': 'off',  // 关闭,使用TS规则
    '@typescript-eslint/no-unused-vars': [
      'error',
      {
        varsIgnorePattern: '.*',
        args: 'none',
      },
    ],
    'space-before-function-paren': 'off',
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',

    // ==================== Vue规则 ====================
    'vue/attributes-order': 'off',
    'vue/one-component-per-file': 'off',
    'vue/html-closing-bracket-newline': 'off',
    'vue/max-attributes-per-line': 'off',
    'vue/multiline-html-element-content-newline': 'off',
    'vue/singleline-html-element-content-newline': 'off',
    'vue/attribute-hyphenation': 'off',
    'vue/require-default-prop': 'off',

    // 组件命名规范 (允许index作为组件名)
    'vue/multi-word-component-names': [
      'error',
      {
        ignores: ['index'],
      },
    ],

    // 自闭合标签规范
    'vue/html-self-closing': [
      'error',
      {
        html: {
          void: 'always',
          normal: 'never',
          component: 'always',
        },
        svg: 'always',
        math: 'always',
      },
    ],

    // script-setup变量使用检测
    'vue/script-setup-uses-vars': 'error',

    // ==================== SmartAdmin自定义规则 ====================

    // 命名规范
    '@typescript-eslint/naming-convention': [
      'error',
      {
        // 接口命名: Model/VO/Form/DTO后缀
        selector: 'interface',
        format: ['PascalCase'],
        custom: {
          regex: '(Model|VO|Form|DTO|Query|Add|Update|Detail|List|Page)$',
          match: false,  // 不强制后缀,但建议使用
        },
      },
      {
        // 枚举命名: ENUM后缀且全大写
        selector: 'variable',
        modifiers: ['const'],
        format: ['UPPER_CASE'],
        custom: {
          regex: '_ENUM$',
          match: false,  // 不强制ENUM后缀,但建议使用
        },
      },
      {
        // API对象命名: Api后缀
        selector: 'variable',
        filter: {
          regex: 'Api$',
          match: true,
        },
        format: ['camelCase'],
      },
    ],

    // TypeScript规则
    '@typescript-eslint/explicit-module-boundary-types': 'off',
    '@typescript-eslint/no-explicit-any': 'warn',  // 警告any使用
    '@typescript-eslint/no-non-null-assertion': 'warn',

    // 禁止var,强制let/const
    'no-var': 'error',
    'prefer-const': 'error',

    // 代码质量
    'eqeqeq': ['error', 'always'],  // 强制使用===
    'no-eval': 'error',  // 禁用eval
    'no-implied-eval': 'error',
    'no-magic-numbers': [
      'warn',
      {
        ignore: [0, 1, -1],  // 允许0,1,-1
        ignoreArrayIndexes: true,
        ignoreDefaultValues: true,
      },
    ],  // 警告魔法数字
  },
};
