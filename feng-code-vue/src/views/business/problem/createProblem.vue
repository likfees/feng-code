<template>
  <div id="fromBody">
    <a-form-model
      ref="elForm"
      :model="formData"
      :rules="rules"
      :label-col="{ span: 3, offset: 0 }"
      :wrapper-col="{ span: 21, offset: 0 }"
      layout="horizontal">
      <a-form-model-item label="题目标题" prop="title">
        <a-input v-model="formData.title" placeholder="请输入标题" :style="{width: '100%'}" allow-clear></a-input>
      </a-form-model-item>
      <a-form-model-item label="题目说明" prop="description">
        <a-textarea
          v-model="formData.description"
          placeholder="请输入题目说明"
          :auto-size="{minRows: 4, maxRows: 4}"
          :style="{width: '100%'}"
          allow-clear />
      </a-form-model-item>
      <a-form-model-item label="题目提示" prop="hint">
        <a-textarea
          v-model="formData.hint"
          placeholder="请输入题目提示"
          :auto-size="{minRows: 4, maxRows: 4}"
          :style="{width: '100%'}"
          allow-clear />
      </a-form-model-item>
      <a-form-model-item label="难度等级" prop="level">
        <a-radio-group v-model="formData.level">
          <a-radio-button
            v-for="(item, index) in levelOptions"
            :key="index"
            :value="item.value"
            :disabled="item.disabled">{{ item.label }}</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="排序" prop="sort">
        <a-input v-model="formData.sort" placeholder="请输入排序" allow-clear></a-input>
      </a-form-model-item>
      <a-form-model-item label="批阅方式" prop="isAuto">
        <a-radio-group v-model="formData.isAuto">
          <a-radio-button
            v-for="(item, index) in isAutoOptions"
            :key="index"
            :value="item.value"
            :disabled="item.disabled">{{ item.label }}
          </a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="标签" prop="tagId">
        <a-select v-model="formData.tagId" placeholder="请选择标签" allow-clear :style="{width: '100%'}">
          <a-select-option
            v-for="(item, index) in tagIdOptions"
            :key="index"
            :value="item.value"
            :disabled="item.disabled">{{ item.label }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="分类" prop="categoryId">
        <a-select v-model="formData.categoryId" placeholder="请选择分类" allow-clear :style="{width: '100%'}">
          <a-select-option
            v-for="(item, index) in categoryIdOptions"
            :key="index"
            :value="item.value"
            :disabled="item.disabled">{{ item.label }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="支持语言" prop="language">
        <a-checkbox-group v-model="formData.language">
          <a-checkbox
            v-for="(item, index) in languageOptions"
            :key="index"
            :value="item.value"
            :disabled="item.disabled">{{ item.label }}</a-checkbox>
        </a-checkbox-group>
      </a-form-model-item>
      <template v-for="(item, key) in formData.language" v-if="!languageWrite.includes(item)">
        <a-form-model-item :label="languageList[item] + ' 代码模板'" prop="templete" :key="item" >
          <!--     代码编辑器      -->
          <CodemirrorDemo :mode="languageList[item]" :ref="'edit_' + item"></CodemirrorDemo>
        </a-form-model-item>
        <a-form-model-item :label="languageList[item] + ' 函数名'" prop="" :key="key + 100">
          <a-input v-model="formData.methodNames[item]" placeholder="请输入代码模版中要执行的函数名" :style="{width: '100%'}" allow-clear>
          </a-input>
        </a-form-model-item>
        <!--    Python不显示参数类型选择框和按钮    -->
        <template v-for="(count, index) in paramTypeCount[item]" v-if="!['1', '5'].includes(item)">
          <a-form-model-item :label="languageList[item] + '第'+ count +'个参数类型'" prop="" :key="index + item">
            <a-input v-model="formData.paramTypes[item][count - 1]" placeholder="请输入代码模版中参数类型" :style="{width: '100%'}" allow-clear>
            </a-input>
          </a-form-model-item>
        </template>
        <a-form-model-item label="参数操作" :key="key + 300" v-if="item !== '1'">
          <a-button type="primary" icon="plus" @click="paramTypeCountChange(item, 1)">
            添加
          </a-button>
          <a-button type="primary" icon="plus" @click="paramTypeCountChange(item, 2)" style="margin-left: 20px">
            删除
          </a-button>
        </a-form-model-item>
      </template>
      <a-form-model-item label="测试用例" prop="testCase" v-if="formData.isAuto === '0'">
        <a-alert
          message="测试用例说明"
          type="info"
        >
          <template slot="description">
            例：一行为一条测试用例；格式为：参数1;参数2;参数...=结果1;结果2;结果...<br />
            param1;param2=result <br />
            20;50=80
          </template>
        </a-alert>
        <!--     代码编辑器      -->
        <CodemirrorDemo mode="TestCase" ref="testCase"></CodemirrorDemo>
      </a-form-model-item>
      <a-form-model-item :wrapper-col="{ span: 21, offset: 3 }">
        <a-space>
          <a-button type="primary" @click="submitForm">提交</a-button>
          <a-button @click="resetForm">重置</a-button>
          <a-button @click="$router.go(-1)">返回</a-button>
        </a-space>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<script>

import { listAllProblemCategory } from '@/api/business/problemCategory'
import { listAllTags } from '@/api/business/tags'
import { getDicts } from '@/api/system/dict/data'
import { getProblem, addProblem, updateProblem } from '@/api/business/problem'
import CodemirrorDemo from '@/components/Codemirror'

export default {
  name: 'CreateProblem',
  components: {
    CodemirrorDemo
  },
  props: [],
  data () {
    return {
      id: undefined,
      formData: {
        id: undefined,
        userId: undefined,
        title: '',
        description: '',
        hint: '', // 提示
        categoryId: undefined,
        tagId: undefined,
        level: '0',
        sort: 50,
        isAuto: '0',
        language: [], // 支持的语言
        testCase: '',
        codeTemplates: [], // 语言模版
        methodNames: [], // 语言模版方法名称
        paramTypes: [[], [], [], [], [], []] // 语言模版参数类型列表
      },
      rules: {
        title: [{
          required: true,
          message: '请输入标题',
          trigger: 'blur'
        }],
        description: [{
          required: true,
          message: '请输入题目说明',
          trigger: 'blur'
        }],
        hint: [{
          required: true,
          message: '请输入题目提示',
          trigger: 'blur'
        }],
        categoryId: [{
          required: true,
          message: '请选择分类',
          trigger: 'change'
        }],
        tagId: [{
          required: true,
          message: '请选择标签',
          trigger: 'change'
        }],
        level: [{
          required: true,
          message: '难度等级不能为空',
          trigger: 'change'
        }],
        sort: [{
          required: true,
          message: '请输入排序',
          trigger: 'blur'
        }],
        isAuto: [{
          required: true,
          message: '批阅方式不能为空',
          trigger: 'change'
        }],
        language: [{
          required: true,
          type: 'array',
          message: '请至少选择一个支持语言',
          trigger: 'change'
        }],
        methodNames: [{
          required: true,
          message: '请输入代码模版中要执行的函数名',
          trigger: 'blur'
        }],
        testCase: [{
          required: true,
          message: '请输入测试用例',
          trigger: 'blur'
        }]
      },
      categoryIdOptions: [],
      tagIdOptions: [],
      levelOptions: [],
      isAutoOptions: [], // 是否自动批阅
      languageOptions: [], // 语言选项
      languageTypeOptions: [], // 每个语言的参数类型
      languageList: {},
      languageWrite: ['4', '6'], // 4sql 5shell 不生成函数名
      paramTypeCount: {} // 每个语言类型的参数个数
    }
  },
  computed: {},
  watch: {
    'formData.language': {
      handler (newVal, oldVal) {
        // 求差集 得到取消选择的语言
        const diffLanguage = newVal.concat(oldVal).filter(v => !newVal.includes(v))
        if (diffLanguage.length !== 0) {
          diffLanguage.forEach(lkey => {
            // 删除对应的数据
            // this.formData.paramTypes[lkey] = []
            const t = this.formData.paramTypes[lkey] = []
            this.formData.paramTypes = Object.assign([], this.formData.paramTypes, t)
            this.formData.codeTemplates.splice(lkey, 1)
            this.formData.methodNames.splice(lkey, 1)
            this.paramTypeCount[lkey] = 0
          })
        }
      },
      deep: true
    },
    'formData.categoryId': {
      handler (newVal, oldVal) {
        this.languageOptions.forEach((item, index) => {
          this.formData.language = []
          // 分类选数据库时启用sql
          if (newVal === 2) {
            item.disabled = item.value !== '4'
            this.formData.language.push('4')
          } else if (newVal === 3) { // 分类选数据库Shell时启用shell
            item.disabled = item.value !== '6'
            this.formData.language.push('6')
          } else {
            item.disabled = item.value === '6' || item.value === '4'
          }
        })
      },
      deep: true
    }
  },
  created () {
    this.getProblemCategoryList()
    this.getProblemTagsList()
    this.getDictsList()
    this.id = this.$route.query.id
    if (this.id) {
      this.setProblemInfo(this.id)
    }
  },

  methods: {
    submitForm () {
            // 获取所有代码编辑器的内容
      this.formData.language.forEach(item => {
        // sql 和 shell
        if (['4', '6'].includes(item)) {
          return false
        }
        const ref = 'edit_' + item
        this.formData.codeTemplates[item] = this.$refs[ref][0].getCodeValue()
      })
      // 获取测试用例
      if (this.formData.isAuto === '0') {
        this.formData.testCase = this.$refs.testCase.getCodeValue()
      }
      // 表单验证并提交
      this.$refs['elForm'].validate(valid => {
        console.log('form', this.formData)
        console.log('form-json', JSON.stringify(this.formData))
        console.log('valid', valid)
        if (!valid) {
          return false
        }

        if (this.id) {
          updateProblem(this.formData).then(res => {
            if (res.code !== 200) {
              this.$message.error(res.msg)
              return false
            }
            this.$message.success(
              '编辑成功',
              3
            )
            this.$router.push({ path: '/study/problem', query: { reload: 'true' } })
          })
        } else {
          addProblem(this.formData).then(res => {
            console.log('res', res)
            this.$message.success(
              '添加成功',
              3
            )
            this.$router.push({ path: '/study/problem', query: { reload: 'true' } })
          })
        }
      })
    },
    resetForm () {
      this.$refs['elForm'].resetFields()
    },
    // 获取问题详情信息
    async setProblemInfo (id) {
      const { code, msg, data } = await getProblem(id)
      if (code !== 200) {
        this.$message.error(msg, 3)
        setTimeout(() => {
          this.$router.go(-1)
        }, 3000)
        return false
      }
      console.log('getProblemInfo', data)
      this.formData.id = data.id
      this.formData.title = data.title
      this.formData.userId = data.userId
      this.formData.description = data.description
      this.formData.categoryId = data.categoryId
      this.formData.tagId = data.tagId
      this.formData.level = data.level + ''
      this.formData.hint = data.hint
      this.formData.sort = data.sort
      this.formData.isAuto = data.isAuto + ''
      this.formData.testCase = JSON.parse(data.testCase)
      // 为了防止监听 categoryId 的修改导致 language 被覆盖
      await new Promise((resolve) => {
        setTimeout(resolve, 800)
      })
      this.formData.language = JSON.parse(data.language)
      this.formData.methodNames = JSON.parse(data.methodNames)
      this.formData.paramTypes = JSON.parse(data.paramTypes)
      this.paramTypeCount = this.formData.paramTypes.map(item => {
        return item.length
      })
      // 等待代码编辑器初始化
      this.formData.codeTemplates = JSON.parse(data.codeTemplates)
      await new Promise((resolve) => {
        setTimeout(resolve, 2000)
      })
      this.$refs.testCase.code = this.formData.testCase
      this.formData.codeTemplates.forEach((item, index) => {
        if (item) {
          this.$refs['edit_' + index][0].code = item
        }
      })
    },
    /**
     * 添加删除参数
     * @param languageType
     * @param type 1增 2减
     */
    paramTypeCountChange (languageType, type) {
      const count = this.paramTypeCount
      if (type === 1) {
        count[languageType]++
      } else if (type === 2) {
        count[languageType]--
        // 删除数组最后一个元素
        this.formData.paramTypes[languageType].pop()
      }
      this.paramTypeCount = Object.assign({}, this.paramTypeCount, count)
    },
    /**
     * 获取分类列表
     */
    getProblemCategoryList () {
      listAllProblemCategory().then(res => {
        if (res.code !== 200) {
          this.$message.error('获取题目分类列表失败')
          return false
        }
        res.data.forEach(item => {
          const data = {
            'label': item.title,
            'value': item.id
          }
          this.categoryIdOptions.push(data)
        })
      })
    },
    getProblemTagsList () {
      listAllTags().then(res => {
        if (res.code !== 200) {
          this.$message.error('获取题目标签列表失败')
          return false
        }
        res.data.forEach(item => {
          const data = {
            'label': item.title,
            'value': item.id
          }
          this.tagIdOptions.push(data)
        })
      })
    },
    getDictsList () {
      // 获取难度
      getDicts('difficulty_level').then(res => {
        if (res.code !== 200) {
          this.$message.error('获取题难度等级失败')
          return false
        }
        res.data.forEach(item => {
          const data = {
            'label': item.dictLabel,
            'value': item.dictValue
          }
          this.levelOptions.push(data)
        })
      })
      // 获取自动批阅字典
      getDicts('is_auto').then(res => {
        if (res.code !== 200) {
          this.$message.error('获取自动批阅字典失败')
          return false
        }
        res.data.forEach(item => {
          const data = {
            'label': item.dictLabel,
            'value': item.dictValue
          }
          this.isAutoOptions.push(data)
        })
      })
      // 获取语言类型字典
      getDicts('code_language').then(res => {
        if (res.code !== 200) {
          this.$message.error('获取语言类型失败')
          return false
        }
        res.data.forEach(item => {
          const data = {
            'label': item.dictLabel,
            'value': item.dictValue,
            'disabled': true
          }
          this.languageList[item.dictValue] = item.dictLabel
          this.paramTypeCount[item.dictValue] = 0
          this.languageOptions.push(data)
        })
      })
    }
  }
}

</script>
<style>

#fromBody{
  background-color: white;
  padding: 40px 80px 40px 0;
}
/* 编辑器高度 */
.CodeMirror {
  height: auto !important;
}
</style>
