<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" size="small" v-show="showSearch">
      <el-form-item label="业务类型">
        <el-input v-model="queryParams.businessType" placeholder="如 homework_answer" clearable @keyup.enter.native="getList" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable placeholder="请选择状态">
          <el-option label="success" value="success" />
          <el-option label="mock" value="mock" />
          <el-option label="failed" value="failed" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" icon="el-icon-search" @click="getList">搜索</el-button>
        <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />

    <el-table v-loading="loading" :data="aiLogList">
      <el-table-column label="业务类型" prop="businessType" width="160" />
      <el-table-column label="用户" prop="userName" width="120" />
      <el-table-column label="角色" prop="roleType" width="100" />
      <el-table-column label="模型" prop="modelName" width="120" />
      <el-table-column label="状态" prop="status" width="100" />
      <el-table-column label="风险" prop="riskFlag" width="100" />
      <el-table-column label="提示词" prop="promptContent" min-width="240" show-overflow-tooltip />
      <el-table-column label="返回内容" prop="responseContent" min-width="260" show-overflow-tooltip />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listAiLog } from '@/api/edu/aiLog'

export default {
  name: 'EduAiLog',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      aiLogList: [],
      queryParams: { pageNum: 1, pageSize: 10, businessType: undefined, status: undefined }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAiLog(this.queryParams).then(res => {
        this.aiLogList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, businessType: undefined, status: undefined }
      this.getList()
    }
  }
}
</script>
