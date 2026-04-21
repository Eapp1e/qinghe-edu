<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="琛ㄥ悕绉? prop="tableName">
        <el-input
          v-model="queryParams.tableName"
          placeholder="璇疯緭鍏ヨ〃鍚嶇О"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="琛ㄦ弿杩? prop="tableComment">
        <el-input
          v-model="queryParams.tableComment"
          placeholder="璇疯緭鍏ヨ〃鎻忚堪"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="鍒涘缓鏃堕棿">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="寮€濮嬫棩鏈?
          end-placeholder="缁撴潫鏃ユ湡"
        ></el-date-picker>
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
          icon="el-icon-download"
          size="mini"
          :disabled="multiple"
          @click="handleGenTable"
          v-hasPermi="['tool:gen:code']"
        >鐢熸垚</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="openCreateTable"
          v-hasRole="['admin']"
        >鍒涘缓</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload"
          size="mini"
          @click="openImportTable"
          v-hasPermi="['tool:gen:import']"
        >瀵煎叆</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleEditTable"
          v-hasPermi="['tool:gen:edit']"
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
          v-hasPermi="['tool:gen:remove']"
        >鍒犻櫎</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="tables" v-loading="loading" :data="tableList" @selection-change="handleSelectionChange" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column type="selection" align="center" width="55"></el-table-column>
      <el-table-column label="搴忓彿" type="index" width="50" align="center">
        <template slot-scope="scope">
          <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="琛ㄥ悕绉? align="center" prop="tableName" :show-overflow-tooltip="true" width="140" />
      <el-table-column label="琛ㄦ弿杩? align="center" prop="tableComment" :show-overflow-tooltip="true" width="140" />
      <el-table-column label="瀹炰綋" align="center" prop="className" :show-overflow-tooltip="true" width="140" />
      <el-table-column label="鍒涘缓鏃堕棿" align="center" prop="createTime" sortable="custom" :sort-orders="['descending', 'ascending']" width="160" />
      <el-table-column label="鏇存柊鏃堕棿" align="center" prop="updateTime" sortable="custom" :sort-orders="['descending', 'ascending']" width="160" />
      <el-table-column label="鎿嶄綔" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
            v-hasPermi="['tool:gen:preview']"
          >棰勮</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleEditTable(scope.row)"
            v-hasPermi="['tool:gen:edit']"
          >缂栬緫</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['tool:gen:remove']"
          >鍒犻櫎</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-refresh"
            @click="handleSynchDb(scope.row)"
            v-hasPermi="['tool:gen:edit']"
          >鍚屾</el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-download"
            @click="handleGenTable(scope.row)"
            v-hasPermi="['tool:gen:code']"
          >鐢熸垚浠ｇ爜</el-button>
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
    <!-- 棰勮鐣岄潰 -->
    <el-dialog :title="preview.title" :visible.sync="preview.open" width="80%" top="5vh" append-to-body class="scrollbar">
      <el-tabs v-model="preview.activeName">
        <el-tab-pane
          v-for="(value, key) in preview.data"
          :label="key.substring(key.lastIndexOf('/')+1,key.indexOf('.vm'))"
          :name="key.substring(key.lastIndexOf('/')+1,key.indexOf('.vm'))"
          :key="key"
        >
          <el-link :underline="false" icon="el-icon-document-copy" v-clipboard:copy="value" v-clipboard:success="clipboardSuccess" style="float:right">澶嶅埗</el-link>
          <pre><code class="hljs" v-html="highlightedCode(value, key)"></code></pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <import-table ref="import" @ok="handleQuery" />
    <create-table ref="create" @ok="handleQuery" />
  </div>
</template>

<script>
import { listTable, previewTable, delTable, genCode, synchDb } from "@/api/tool/gen"
import importTable from "./importTable"
import createTable from "./createTable"
import hljs from "highlight.js/lib/highlight"
import "highlight.js/styles/github-gist.css"
hljs.registerLanguage("java", require("highlight.js/lib/languages/java"))
hljs.registerLanguage("xml", require("highlight.js/lib/languages/xml"))
hljs.registerLanguage("html", require("highlight.js/lib/languages/xml"))
hljs.registerLanguage("vue", require("highlight.js/lib/languages/xml"))
hljs.registerLanguage("javascript", require("highlight.js/lib/languages/javascript"))
hljs.registerLanguage("typescript", require("highlight.js/lib/languages/typescript"))
hljs.registerLanguage("sql", require("highlight.js/lib/languages/sql"))

export default {
  name: "Gen",
  components: { importTable, createTable },
  data() {
    return {
      // 閬僵灞?
      loading: true,
      // 鍞竴鏍囪瘑绗?
      uniqueId: "",
      // 閫変腑鏁扮粍
      ids: [],
      // 閫変腑琛ㄦ暟缁?
      tableNames: [],
      // 闈炲崟涓鐢?
      single: true,
      // 闈炲涓鐢?
      multiple: true,
      // 鏄剧ず鎼滅储鏉′欢
      showSearch: true,
      // 鎬绘潯鏁?
      total: 0,
      // 琛ㄦ暟鎹?
      tableList: [],
      // 鏃ユ湡鑼冨洿
      dateRange: "",
      // 榛樿鎺掑簭
      defaultSort: { prop: "createTime", order: "descending" },
      // 鏌ヨ鍙傛暟
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tableName: undefined,
        tableComment: undefined
      },
      // 棰勮鍙傛暟
      preview: {
        open: false,
        title: "浠ｇ爜棰勮",
        data: {},
        activeName: "domain.java"
      }
    }
  },
  created() {
    this.queryParams.orderByColumn = this.defaultSort.prop
    this.queryParams.isAsc = this.defaultSort.order
    this.getList()
  },
  activated() {
    const time = this.$route.query.t
    if (time != null && time != this.uniqueId) {
      this.uniqueId = time
      this.queryParams.pageNum = Number(this.$route.query.pageNum)
      this.getList()
    }
  },
  methods: {
    /** 鏌ヨ琛ㄩ泦鍚?*/
    getList() {
      this.loading = true
      listTable(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.tableList = response.rows
          this.total = response.total
          this.loading = false
        }
      )
    },
    /** 鎼滅储鎸夐挳鎿嶄綔 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 鐢熸垚浠ｇ爜鎿嶄綔 */
    handleGenTable(row) {
      const tableNames = row.tableName || this.tableNames
      if (tableNames == "") {
        this.$modal.msgError("璇烽€夋嫨瑕佺敓鎴愮殑鏁版嵁")
        return
      }
      if(row.genType === "1") {
        genCode(row.tableName).then(() => {
          this.$modal.msgSuccess("鎴愬姛鐢熸垚鍒拌嚜瀹氫箟璺緞锛? + row.genPath)
        })
      } else {
        const zipName = Array.isArray(tableNames) ? "qinghe-code.zip" : tableNames + ".zip"
        this.$download.zip("/tool/gen/batchGenCode?tables=" + tableNames, zipName)
      }
    },
    /** 鍚屾鏁版嵁搴撴搷浣?*/
    handleSynchDb(row) {
      const tableName = row.tableName
      this.$modal.confirm('纭瑕佸己鍒跺悓姝?' + tableName + '"琛ㄧ粨鏋勫悧锛?).then(function() {
        return synchDb(tableName)
      }).then(() => {
        this.$modal.msgSuccess("鍚屾鎴愬姛")
      }).catch(() => {})
    },
    /** 鎵撳紑瀵煎叆琛ㄥ脊绐?*/
    openImportTable() {
      this.$refs.import.show()
    },
    /** 鎵撳紑鍒涘缓琛ㄥ脊绐?*/
    openCreateTable() {
      this.$refs.create.show()
    },
    /** 閲嶇疆鎸夐挳鎿嶄綔 */
    resetQuery() {
      this.dateRange = []
      this.resetForm("queryForm")
      this.queryParams.pageNum = 1
      this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order)
    },
    /** 棰勮鎸夐挳 */
    handlePreview(row) {
      previewTable(row.tableId).then(response => {
        this.preview.data = response.data
        this.preview.open = true
        this.preview.activeName = "domain.java"
      })
    },
    /** 楂樹寒鏄剧ず */
    highlightedCode(code, key) {
      const vmName = key.substring(key.lastIndexOf("/") + 1, key.indexOf(".vm"))
      var language = vmName.substring(vmName.indexOf(".") + 1, vmName.length)
      const result = hljs.highlight(language, code || "", true)
      return result.value || '&nbsp;'
    },
    /** 澶嶅埗浠ｇ爜鎴愬姛 */
    clipboardSuccess() {
      this.$modal.msgSuccess("澶嶅埗鎴愬姛")
    },
    // 澶氶€夋閫変腑鏁版嵁
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tableId)
      this.tableNames = selection.map(item => item.tableName)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 鎺掑簭瑙﹀彂浜嬩欢 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop
      this.queryParams.isAsc = column.order
      this.getList()
    },
    /** 淇敼鎸夐挳鎿嶄綔 */
    handleEditTable(row) {
      const tableId = row.tableId || this.ids[0]
      const tableName = row.tableName || this.tableNames[0]
      const params = { pageNum: this.queryParams.pageNum }
      this.$tab.openPage("淇敼[" + tableName + "]鐢熸垚閰嶇疆", '/tool/gen-edit/index/' + tableId, params)
    },
    /** 鍒犻櫎鎸夐挳鎿嶄綔 */
    handleDelete(row) {
      const tableIds = row.tableId || this.ids
      this.$modal.confirm('鏄惁纭鍒犻櫎琛ㄧ紪鍙蜂负"' + tableIds + '"鐨勬暟鎹」锛?).then(function() {
        return delTable(tableIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("鍒犻櫎鎴愬姛")
      }).catch(() => {})
    }
  }
}
</script>

