<template>
  <el-form ref="genInfoForm" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item prop="tplCategory">
          <span slot="label">鐢熸垚妯℃澘</span>
          <el-select v-model="info.tplCategory" @change="tplSelectChange">
            <el-option label="鍗曡〃锛堝鍒犳敼鏌ワ級" value="crud" />
            <el-option label="鏍戣〃锛堝鍒犳敼鏌ワ級" value="tree" />
            <el-option label="涓诲瓙琛紙澧炲垹鏀规煡锛? value="sub" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="tplWebType">
          <span slot="label">鍓嶇绫诲瀷</span>
          <el-select v-model="info.tplWebType">
            <el-option label="Vue2 Element UI 妯＄増" value="element-ui" />
            <el-option label="Vue3 Element Plus 妯＄増" value="element-plus" />
            <el-option label="Vue3 Element Plus TypeScript 妯＄増" value="element-plus-typescript" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="packageName">
          <span slot="label">
            鐢熸垚鍖呰矾寰?
            <el-tooltip content="鐢熸垚鍦ㄥ摢涓猨ava鍖呬笅锛屼緥濡?com.eapple.system" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.packageName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="moduleName">
          <span slot="label">
            鐢熸垚妯″潡鍚?
            <el-tooltip content="鍙悊瑙ｄ负瀛愮郴缁熷悕锛屼緥濡?system" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.moduleName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="businessName">
          <span slot="label">
            鐢熸垚涓氬姟鍚?
            <el-tooltip content="鍙悊瑙ｄ负鍔熻兘鑻辨枃鍚嶏紝渚嬪 user" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.businessName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="functionName">
          <span slot="label">
            鐢熸垚鍔熻兘鍚?
            <el-tooltip content="鐢ㄤ綔绫绘弿杩帮紝渚嬪 鐢ㄦ埛" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.functionName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="formColNum">
          <span slot="label">
            琛ㄥ崟甯冨眬
            <el-tooltip content="閫夋嫨琛ㄥ崟鐨勬爡鏍煎竷灞€鏂瑰紡" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.formColNum">
            <el-option label="鍗曞垪" :value="1" />
            <el-option label="鍙屽垪" :value="2" />
            <el-option label="涓夊垪" :value="3" />
          </el-select>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="genView">
          <span slot="label">鎵╁睍鍔熻兘</span>
          <el-checkbox v-model="info.view">鐢熸垚璇︽儏椤?/el-checkbox>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="genType">
          <span slot="label">
            鐢熸垚浠ｇ爜鏂瑰紡
            <el-tooltip content="榛樿涓簔ip鍘嬬缉鍖呬笅杞斤紝涔熷彲浠ヨ嚜瀹氫箟鐢熸垚璺緞" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-radio v-model="info.genType" label="0">zip鍘嬬缉鍖?/el-radio>
          <el-radio v-model="info.genType" label="1">鑷畾涔夎矾寰?/el-radio>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            涓婄骇鑿滃崟
            <el-tooltip content="鍒嗛厤鍒版寚瀹氳彍鍗曚笅锛屼緥濡?绯荤粺绠＄悊" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <treeselect
            :append-to-body="true"
            v-model="info.parentMenuId"
            :options="menus"
            :normalizer="normalizer"
            :show-count="true"
            placeholder="璇烽€夋嫨绯荤粺鑿滃崟"
          />
        </el-form-item>
      </el-col>

      <el-col :span="24" v-if="info.genType == '1'">
        <el-form-item prop="genPath">
          <span slot="label">
            鑷畾涔夎矾寰?
            <el-tooltip content="濉啓纾佺洏缁濆璺緞锛岃嫢涓嶅～鍐欙紝鍒欑敓鎴愬埌褰撳墠Web椤圭洰涓? placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.genPath">
            <el-dropdown slot="append">
              <el-button type="primary">
                鏈€杩戣矾寰勫揩閫熼€夋嫨
                <i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="info.genPath = '/'">鎭㈠榛樿鐨勭敓鎴愬熀纭€璺緞</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row v-show="info.tplCategory == 'tree'">
      <h4 class="form-header">鍏朵粬淇℃伅</h4>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            鏍戠紪鐮佸瓧娈?
            <el-tooltip content="鏍戞樉绀虹殑缂栫爜瀛楁鍚嶏紝 濡傦細dept_id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.treeCode" placeholder="璇烽€夋嫨">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '锛? + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            鏍戠埗缂栫爜瀛楁
            <el-tooltip content="鏍戞樉绀虹殑鐖剁紪鐮佸瓧娈靛悕锛?濡傦細parent_Id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.treeParentCode" placeholder="璇烽€夋嫨">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '锛? + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            鏍戝悕绉板瓧娈?
            <el-tooltip content="鏍戣妭鐐圭殑鏄剧ず鍚嶇О瀛楁鍚嶏紝 濡傦細dept_name" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.treeName" placeholder="璇烽€夋嫨">
            <el-option
              v-for="(column, index) in info.columns"
              :key="index"
              :label="column.columnName + '锛? + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-show="info.tplCategory == 'sub'">
      <h4 class="form-header">鍏宠仈淇℃伅</h4>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            鍏宠仈瀛愯〃鐨勮〃鍚?
            <el-tooltip content="鍏宠仈瀛愯〃鐨勮〃鍚嶏紝 濡傦細sys_user" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.subTableName" placeholder="璇烽€夋嫨" @change="subSelectChange">
            <el-option
              v-for="(table, index) in tables"
              :key="index"
              :label="table.tableName + '锛? + table.tableComment"
              :value="table.tableName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <span slot="label">
            瀛愯〃鍏宠仈鐨勫閿悕
            <el-tooltip content="瀛愯〃鍏宠仈鐨勫閿悕锛?濡傦細user_id" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-select v-model="info.subTableFkName" placeholder="璇烽€夋嫨">
            <el-option
              v-for="(column, index) in subColumns"
              :key="index"
              :label="column.columnName + '锛? + column.columnComment"
              :value="column.columnName"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"

export default {
  components: { Treeselect },
  props: {
    info: {
      type: Object,
      default: null
    },
    tables: {
      type: Array,
      default: null
    },
    menus: {
      type: Array,
      default: []
    }
  },
  data() {
    return {
      subColumns: [],
      rules: {
        tplCategory: [
          { required: true, message: "璇烽€夋嫨鐢熸垚妯℃澘", trigger: "blur" }
        ],
        packageName: [
          { required: true, message: "璇疯緭鍏ョ敓鎴愬寘璺緞", trigger: "blur" }
        ],
        moduleName: [
          { required: true, message: "璇疯緭鍏ョ敓鎴愭ā鍧楀悕", trigger: "blur" }
        ],
        businessName: [
          { required: true, message: "璇疯緭鍏ョ敓鎴愪笟鍔″悕", trigger: "blur" }
        ],
        functionName: [
          { required: true, message: "璇疯緭鍏ョ敓鎴愬姛鑳藉悕", trigger: "blur" }
        ]
      }
    }
  },
  watch: {
    'info.subTableName': function(val) {
      this.setSubTableColumns(val)
    },
    'info.tplWebType': function(val) {
      if (val === '') {
        this.info.tplWebType = "element-ui"
      }
    }
  },
  methods: {
    /** 杞崲鑿滃崟鏁版嵁缁撴瀯 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children
      }
    },
    /** 閫夋嫨瀛愯〃鍚嶈Е鍙?*/
    subSelectChange(value) {
      this.info.subTableFkName = ''
    },
    /** 閫夋嫨鐢熸垚妯℃澘瑙﹀彂 */
    tplSelectChange(value) {
      if(value !== 'sub') {
        this.info.subTableName = ''
        this.info.subTableFkName = ''
      }
    },
    /** 璁剧疆鍏宠仈澶栭敭 */
    setSubTableColumns(value) {
      for (var item in this.tables) {
        const name = this.tables[item].tableName
        if (value === name) {
          this.subColumns = this.tables[item].columns
          break
        }
      }
    }
  }
}
</script>
