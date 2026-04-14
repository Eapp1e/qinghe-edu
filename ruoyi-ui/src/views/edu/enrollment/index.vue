<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" size="small" v-show="showSearch">
      <el-form-item label="学生姓名">
        <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter.native="getList" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable placeholder="请选择状态">
          <el-option label="已报名" value="0" />
          <el-option label="已完成" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" icon="el-icon-search" @click="getList">搜索</el-button>
        <el-button size="mini" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />

    <el-table v-loading="loading" :data="enrollmentList">
      <el-table-column label="课程" prop="courseName" min-width="150" />
      <el-table-column label="学生" prop="studentName" width="120" />
      <el-table-column label="家长" prop="parentName" width="120" />
      <el-table-column label="教师" prop="teacherName" width="120" />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'warning'">{{ scope.row.status === '1' ? '已完成' : '已报名' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学习记录" prop="learningRecord" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button v-hasPermi="['edu:enrollment:edit']" size="mini" type="text" @click="handleUpdate(scope.row)">维护</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="维护学习记录" :visible.sync="open" width="620px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">已报名</el-radio>
            <el-radio label="1">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学习记录">
          <el-input v-model="form.learningRecord" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="互动总结">
          <el-input v-model="form.interactionSummary" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listEnrollment, updateEnrollment } from '@/api/edu/enrollment'

export default {
  name: 'EduEnrollment',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      open: false,
      enrollmentList: [],
      queryParams: { pageNum: 1, pageSize: 10, studentName: undefined, status: undefined },
      form: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listEnrollment(this.queryParams).then(res => {
        this.enrollmentList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, studentName: undefined, status: undefined }
      this.getList()
    },
    handleUpdate(row) {
      this.form = { ...row }
      this.open = true
    },
    submitForm() {
      updateEnrollment(this.form).then(() => {
        this.$modal.msgSuccess('保存成功')
        this.open = false
        this.getList()
      })
    }
  }
}
</script>
