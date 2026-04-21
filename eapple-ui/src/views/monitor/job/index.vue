<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="浠诲姟鍚嶇О" prop="jobName">
        <el-input
          v-model="queryParams.jobName"
          placeholder="璇疯緭鍏ヤ换鍔″悕绉?
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="浠诲姟缁勫悕" prop="jobGroup">
        <el-select v-model="queryParams.jobGroup" placeholder="璇烽€夋嫨浠诲姟缁勫悕" clearable>
          <el-option
            v-for="dict in dict.type.sys_job_group"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="浠诲姟鐘舵€? prop="status">
        <el-select v-model="queryParams.status" placeholder="璇烽€夋嫨浠诲姟鐘舵€? clearable>
          <el-option
            v-for="dict in dict.type.sys_job_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">鎼滅储</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">閲嶇疆</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['monitor:job:add']"
        >鏂板</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['monitor:job:edit']"
        >淇敼</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['monitor:job:remove']"
        >鍒犻櫎</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['monitor:job:export']"
        >瀵煎嚭</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-s-operation"
          size="mini"
          @click="handleJobLog"
          v-hasPermi="['monitor:job:query']"
        >鏃ュ織</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jobList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="浠诲姟缂栧彿" width="100" align="center" prop="jobId" />
      <el-table-column label="浠诲姟鍚嶇О" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <a class="link-type" style="cursor:pointer" @click="handleView(scope.row)">{{ scope.row.jobName }}</a>
        </template>
      </el-table-column>
      <el-table-column label="浠诲姟缁勫悕" align="center" prop="jobGroup">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_job_group" :value="scope.row.jobGroup"/>
        </template>
      </el-table-column>
      <el-table-column label="璋冪敤鐩爣瀛楃涓? align="center" prop="invokeTarget" :show-overflow-tooltip="true" />
      <el-table-column label="cron鎵ц琛ㄨ揪寮? align="center" prop="cronExpression" :show-overflow-tooltip="true" />
      <el-table-column label="鐘舵€? align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="鎿嶄綔" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['monitor:job:edit']"
          >淇敼</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['monitor:job:remove']"
          >鍒犻櫎</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)" v-hasPermi="['monitor:job:changeStatus', 'monitor:job:query']">
            <el-button size="mini" type="text" icon="el-icon-d-arrow-right">鏇村</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="handleRun" icon="el-icon-caret-right"
                v-hasPermi="['monitor:job:changeStatus']">鎵ц涓€娆?/el-dropdown-item>
              <el-dropdown-item command="handleJobLog" icon="el-icon-s-operation"
                v-hasPermi="['monitor:job:query']">璋冨害鏃ュ織</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 娣诲姞鎴栦慨鏀瑰畾鏃朵换鍔″璇濇 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="浠诲姟鍚嶇О" prop="jobName">
              <el-input v-model="form.jobName" placeholder="璇疯緭鍏ヤ换鍔″悕绉? />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="浠诲姟鍒嗙粍" prop="jobGroup">
              <el-select v-model="form.jobGroup" placeholder="璇烽€夋嫨浠诲姟鍒嗙粍">
                <el-option
                  v-for="dict in dict.type.sys_job_group"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="invokeTarget">
              <span slot="label">
                璋冪敤鏂规硶
                <el-tooltip placement="top">
                  <div slot="content">
                    Bean璋冪敤绀轰緥锛歳yTask.ryParams('ry')
                    <br />Class绫昏皟鐢ㄧず渚嬶細com.eapple.quartz.task.EappleTask.demo('param')
                    <br />鍙傛暟璇存槑锛氭敮鎸佸瓧绗︿覆锛屽竷灏旂被鍨嬶紝闀挎暣鍨嬶紝娴偣鍨嬶紝鏁村瀷
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.invokeTarget" placeholder="璇疯緭鍏ヨ皟鐢ㄧ洰鏍囧瓧绗︿覆" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="cron琛ㄨ揪寮? prop="cronExpression">
              <el-input v-model="form.cronExpression" placeholder="璇疯緭鍏ron鎵ц琛ㄨ揪寮?>
                <template slot="append">
                  <el-button type="primary" @click="handleShowCron">
                    鐢熸垚琛ㄨ揪寮?
                    <i class="el-icon-time el-icon--right"></i>
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.jobId !== undefined">
            <el-form-item label="鐘舵€?>
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_job_status"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="鎵ц绛栫暐" prop="misfirePolicy">
              <el-radio-group v-model="form.misfirePolicy" size="small">
                <el-radio-button label="1">绔嬪嵆鎵ц</el-radio-button>
                <el-radio-button label="2">鎵ц涓€娆?/el-radio-button>
                <el-radio-button label="3">鏀惧純鎵ц</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="鏄惁骞跺彂" prop="concurrent">
              <el-radio-group v-model="form.concurrent" size="small">
                <el-radio-button label="0">鍏佽</el-radio-button>
                <el-radio-button label="1">绂佹</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">纭?瀹?/el-button>
        <el-button @click="cancel">鍙?娑?/el-button>
      </div>
    </el-dialog>

    <el-dialog title="Cron琛ㄨ揪寮忕敓鎴愬櫒" :visible.sync="openCron" append-to-body destroy-on-close class="scrollbar">
      <crontab @hide="openCron=false" @fill="crontabFill" :expression="expression"></crontab>
    </el-dialog>

    <!-- 浠诲姟鏃ュ織璇︾粏 -->
    <job-detail :visible.sync="openView" :row="form" type="job" />
  </div>
</template>

<script>
import { listJob, getJob, delJob, addJob, updateJob, runJob, changeJobStatus } from "@/api/monitor/job"
import JobDetail from './detail'
import Crontab from '@/components/Crontab'

export default {
  components: { Crontab, JobDetail },
  name: "Job",
  dicts: ['sys_job_group', 'sys_job_status'],
  data() {
    return {
      // 閬僵灞?
      loading: true,
      // 閫変腑鏁扮粍
      ids: [],
      // 闈炲崟涓鐢?
      single: true,
      // 闈炲涓鐢?
      multiple: true,
      // 鏄剧ず鎼滅储鏉′欢
      showSearch: true,
      // 鎬绘潯鏁?
      total: 0,
      // 瀹氭椂浠诲姟琛ㄦ牸鏁版嵁
      jobList: [],
      // 寮瑰嚭灞傛爣棰?
      title: "",
      // 鏄惁鏄剧ず寮瑰嚭灞?
      open: false,
      // 鏄惁鏄剧ず璇︾粏寮瑰嚭灞?
      openView: false,
      // 鏄惁鏄剧ずCron琛ㄨ揪寮忓脊鍑哄眰
      openCron: false,
      // 浼犲叆鐨勮〃杈惧紡
      expression: "",
      // 鏌ヨ鍙傛暟
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        jobName: undefined,
        jobGroup: undefined,
        status: undefined
      },
      // 琛ㄥ崟鍙傛暟
      form: {},
      // 琛ㄥ崟鏍￠獙
      rules: {
        jobName: [
          { required: true, message: "浠诲姟鍚嶇О涓嶈兘涓虹┖", trigger: "blur" }
        ],
        invokeTarget: [
          { required: true, message: "璋冪敤鐩爣瀛楃涓蹭笉鑳戒负绌?, trigger: "blur" }
        ],
        cronExpression: [
          { required: true, message: "cron鎵ц琛ㄨ揪寮忎笉鑳戒负绌?, trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 鏌ヨ瀹氭椂浠诲姟鍒楄〃 */
    getList() {
      this.loading = true
      listJob(this.queryParams).then(response => {
        this.jobList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 浠诲姟缁勫悕瀛楀吀缈昏瘧
    jobGroupFormat(row, column) {
      return this.selectDictLabel(this.dict.type.sys_job_group, row.jobGroup)
    },
    // 鍙栨秷鎸夐挳
    cancel() {
      this.open = false
      this.reset()
    },
    // 琛ㄥ崟閲嶇疆
    reset() {
      this.form = {
        jobId: undefined,
        jobName: undefined,
        jobGroup: undefined,
        invokeTarget: undefined,
        cronExpression: undefined,
        misfirePolicy: 1,
        concurrent: 1,
        status: "0"
      }
      this.resetForm("form")
    },
    /** 鎼滅储鎸夐挳鎿嶄綔 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 閲嶇疆鎸夐挳鎿嶄綔 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 澶氶€夋閫変腑鏁版嵁
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.jobId)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    // 鏇村鎿嶄綔瑙﹀彂
    handleCommand(command, row) {
      switch (command) {
        case "handleRun":
          this.handleRun(row)
          break
        case "handleView":
          this.handleView(row)
          break
        case "handleJobLog":
          this.handleJobLog(row)
          break
        default:
          break
      }
    },
    // 浠诲姟鐘舵€佷慨鏀?
    handleStatusChange(row) {
      let text = row.status === "0" ? "鍚敤" : "鍋滅敤"
      this.$modal.confirm('纭瑕?' + text + '""' + row.jobName + '"浠诲姟鍚楋紵').then(function() {
        return changeJobStatus(row.jobId, row.status)
      }).then(() => {
        this.$modal.msgSuccess(text + "鎴愬姛")
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0"
      })
    },
    /* 绔嬪嵆鎵ц涓€娆?*/
    handleRun(row) {
      this.$modal.confirm('纭瑕佺珛鍗虫墽琛屼竴娆?' + row.jobName + '"浠诲姟鍚楋紵').then(function() {
        return runJob(row.jobId, row.jobGroup)
      }).then(() => {
        this.$modal.msgSuccess("鎵ц鎴愬姛")
      }).catch(() => {})
    },
    /** 浠诲姟璇︾粏淇℃伅 */
    handleView(row) {
      getJob(row.jobId).then(response => {
        this.form = response.data
        this.openView = true
      })
    },
    /** cron琛ㄨ揪寮忔寜閽搷浣?*/
    handleShowCron() {
      this.expression = this.form.cronExpression
      this.openCron = true
    },
    /** 纭畾鍚庡洖浼犲€?*/
    crontabFill(value) {
      this.form.cronExpression = value
    },
    /** 浠诲姟鏃ュ織鍒楄〃鏌ヨ */
    handleJobLog(row) {
      const jobId = row.jobId || 0
      this.$router.push('/monitor/job-log/index/' + jobId)
    },
    /** 鏂板鎸夐挳鎿嶄綔 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "娣诲姞浠诲姟"
    },
    /** 淇敼鎸夐挳鎿嶄綔 */
    handleUpdate(row) {
      this.reset()
      const jobId = row.jobId || this.ids
      getJob(jobId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "淇敼浠诲姟"
      })
    },
    /** 鎻愪氦鎸夐挳 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.jobId != undefined) {
            updateJob(this.form).then(() => {
              this.$modal.msgSuccess("淇敼鎴愬姛")
              this.open = false
              this.getList()
            })
          } else {
            addJob(this.form).then(() => {
              this.$modal.msgSuccess("鏂板鎴愬姛")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 鍒犻櫎鎸夐挳鎿嶄綔 */
    handleDelete(row) {
      const jobIds = row.jobId || this.ids
      this.$modal.confirm('鏄惁纭鍒犻櫎瀹氭椂浠诲姟缂栧彿涓?' + jobIds + '"鐨勬暟鎹」锛?).then(function() {
        return delJob(jobIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("鍒犻櫎鎴愬姛")
      }).catch(() => {})
    },
    /** 瀵煎嚭鎸夐挳鎿嶄綔 */
    handleExport() {
      this.download('monitor/job/export', {
        ...this.queryParams
      }, `job_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
