<template>
  <div class="app-container ai-log-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">AI Audit Log</span>
        <h1>AI日志</h1>
        <p>集中跟踪平台中的智能问答、通知生成和教学建议调用记录，帮助管理员核查模型使用情况、内容安全状态与风险标记。</p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>调用总数</span>
          <strong>{{ total }}</strong>
        </div>
        <div class="stat-card">
          <span>可用结果</span>
          <strong>{{ usableCount }}</strong>
        </div>
      </div>
    </section>

    <section class="toolbar-panel">
      <el-form :inline="true" :model="queryParams" size="small" v-show="showSearch" class="query-form">
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
    </section>

    <el-table v-loading="loading" :data="aiLogList" class="content-table">
      <el-table-column label="业务类型" prop="businessType" width="160" />
      <el-table-column label="用户" prop="userName" width="120" />
      <el-table-column label="角色" prop="roleType" width="100" />
      <el-table-column label="模型" prop="modelName" width="120" />
      <el-table-column label="状态" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusTagType(scope.row.status)">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="风险" prop="riskFlag" width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="scope.row.riskFlag === 'Y' ? 'danger' : 'success'">
            {{ scope.row.riskFlag === 'Y' ? '有风险' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
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
  computed: {
    usableCount() {
      return this.aiLogList.filter(item => item.status !== 'failed').length
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
    },
    statusTagType(status) {
      if (status === 'success') return 'success'
      if (status === 'mock') return 'warning'
      if (status === 'failed') return 'danger'
      return 'info'
    }
  }
}
</script>

<style scoped>
.ai-log-page {
  position: relative;
  padding-top: 6px;
  overflow: hidden;
}

.ai-log-page::before,
.ai-log-page::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(8px);
  pointer-events: none;
}

.ai-log-page::before {
  top: -50px;
  right: 6%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(38, 232, 183, 0.18), transparent 68%);
}

.ai-log-page::after {
  left: -70px;
  bottom: 12%;
  width: 240px;
  height: 240px;
  background: radial-gradient(circle, rgba(91, 187, 255, 0.14), transparent 70%);
}

.hero-panel {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
  padding: 28px;
  border: 1px solid rgba(103, 224, 214, 0.18);
  border-radius: 26px;
  background:
    radial-gradient(circle at top right, rgba(63, 229, 190, 0.2), transparent 28%),
    radial-gradient(circle at bottom left, rgba(72, 153, 255, 0.16), transparent 24%),
    linear-gradient(135deg, rgba(21, 34, 46, 0.96), rgba(29, 73, 82, 0.92));
  box-shadow:
    0 24px 44px rgba(15, 35, 46, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

.hero-copy {
  max-width: 760px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(32, 224, 182, 0.14);
  color: #8ff6dc;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
  box-shadow: 0 10px 20px rgba(12, 34, 40, 0.2);
}

.hero-copy h1 {
  margin: 16px 0 12px;
  color: #ffffff;
  font-size: 34px;
}

.hero-copy p {
  margin: 0;
  color: rgba(232, 246, 246, 0.78);
  line-height: 1.9;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  min-width: 250px;
}

.stat-card {
  padding: 18px;
  border: 1px solid rgba(129, 224, 224, 0.14);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  box-shadow:
    0 16px 28px rgba(11, 32, 40, 0.16),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.stat-card span {
  color: rgba(222, 240, 243, 0.72);
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 10px;
  color: #ffffff;
  font-size: 28px;
}

.toolbar-panel {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  padding: 18px 20px;
  border: 1px solid rgba(157, 232, 233, 0.42);
  border-radius: 22px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow:
    0 22px 40px rgba(41, 130, 141, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px) saturate(140%);
  -webkit-backdrop-filter: blur(18px) saturate(140%);
}

.query-form {
  flex: 1;
}

.content-table {
  position: relative;
  z-index: 1;
  margin-bottom: 10px;
}

::v-deep .el-form-item__label {
  color: #456270;
  font-weight: 600;
}

::v-deep .el-input__inner,
::v-deep .el-select .el-input__inner {
  border-color: rgba(134, 214, 222, 0.42);
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(238, 249, 255, 0.94));
  color: #355161;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.82);
}

::v-deep .el-input__inner:focus {
  border-color: #25ddbf;
  box-shadow:
    0 0 0 4px rgba(37, 221, 191, 0.12),
    0 12px 22px rgba(39, 182, 194, 0.12);
}

::v-deep .el-button--primary {
  border: none;
  background: linear-gradient(135deg, #12e0a9 0%, #10c7c4 52%, #2a98ff 100%);
  box-shadow: 0 16px 30px rgba(20, 175, 183, 0.22);
}

::v-deep .el-button--primary:hover,
::v-deep .el-button--primary:focus {
  filter: saturate(108%);
  box-shadow: 0 18px 34px rgba(20, 175, 183, 0.28);
}

::v-deep .el-table {
  overflow: hidden;
  border-radius: 24px;
  border: 1px solid rgba(106, 216, 218, 0.18);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 22px 38px rgba(41, 130, 141, 0.08);
}

::v-deep .el-table th {
  background: linear-gradient(180deg, rgba(235, 251, 255, 0.96), rgba(229, 255, 249, 0.92));
  color: #34505f;
}

::v-deep .el-table tr {
  background-color: rgba(255, 255, 255, 0.9);
}

::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: rgba(230, 255, 249, 0.7);
}

::v-deep .el-pagination .btn-next,
::v-deep .el-pagination .btn-prev,
::v-deep .el-pagination .el-pager li {
  border-radius: 12px;
}

@media (max-width: 900px) {
  .hero-panel {
    flex-direction: column;
  }

  .hero-stats {
    min-width: 0;
  }

  .toolbar-panel {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
