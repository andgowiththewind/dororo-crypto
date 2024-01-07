<template>
  <div class="VpM8CpARnT0eEtZGTip3">
    <div class="conditionalFormZone" v-cloak>
      <el-form @submit.native.prevent size="mini" :inline="true" :model="conditionVo.params" ref="conditionParamForm">
        <el-form-item label="目录" prop="folderPath">
          <el-input v-model="conditionVo.params.folderPath" style="min-width: 15vw;" placeholder="左侧目录树选取或者直接输入目录地址"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="cryptoStatus">
          <el-select v-model="conditionVo.params.cryptoStatus" placeholder="请选择加密状态">
            <el-option v-for="item in sysParams.cryptoStatusOptions" :key="item.code" :label="item.name" :value="item.code"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form @submit.native.prevent size="mini" :inline="true" :model="conditionVo.model" ref="conditionModelForm">
        <el-form-item label="文件名" prop="name">
          <el-input v-model="conditionVo.model.name" placeholder="文件名支持模糊查询"></el-input>
        </el-form-item>
        <el-form-item label="绝对路径" prop="absPath">
          <el-input v-model="conditionVo.model.absPath" placeholder="绝对路径支持模糊查询"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button-group>
            <el-button type="primary" icon="el-icon-arrow-left" style="width: 15vw;" @click="submitConditionPagingQuery">查询文件列表</el-button>
            <el-button type="primary" plain style="width: 5vw;" @click="resetConditionalForm">重置<i class="el-icon-arrow-right el-icon--right"></i></el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </div>
    <div class="pagingFormZone" v-cloak>
      <el-form @submit.native.prevent size="mini">
        <el-form-item label="">
          <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page.sync="conditionVo.page.pageNum"
              :page-size="conditionVo.page.pageSize"
              :total="conditionVo.page.total"
              layout="total, prev, pager, next, jumper">
          </el-pagination>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import * as methodConsts from "@/config/methodConsts";
import {devConsoleLog, getSysParam} from "@/utils/dororoUtils";
import {nanoid} from 'nanoid';


export default {
  // 请注意这里的.vue后缀需要去掉
  name: "ConditionalPagination",
  components: {},
  data() {
    return {
      sysParams: {
        cryptoStatusOptions: [],
      },
      conditionVo: {
        model: {},
        page: {pageNum: 1, pageSize: 10, total: 0},
        params: {
          folderPath: process.env.VUE_APP_DEV_FOLDER_PATH || '',
          cryptoStatus: 2,
        },
      },
      pagingVo: {},
    }
  },
  methods: {
    handleSizeChange(val) {
      this.conditionVo.page.pageSize = val;
    },
    handleCurrentChange(val) {
      this.conditionVo.page.pageNum = val;
    },
    resetConditionalForm() {
      this.$refs.conditionParamForm.resetFields();
      this.$refs.conditionModelForm.resetFields();
    },
    // 提交条件分页查询
    submitConditionPagingQuery() {
      // 整合全部参数
      let data = {model: {}, page: {}, params: {}};
      Object.assign(data.model, this.conditionVo.model);
      Object.assign(data.page, this.conditionVo.page);
      Object.assign(data.params, this.conditionVo.params);
      // 请求
    },
    bindingNodeClickEvent() {
      this.$bus.$on(methodConsts.TREE_NODE_CLICK_EVENT, (folderPath) => {
        this.$nextTick(() => {
          this.conditionVo.params.folderPath = folderPath;
        });
      });
    },
    sysParamsInit() {
      getSysParam('cryptoStatusOptions').then(res => this.sysParams.cryptoStatusOptions = res.data);
    },
  },
  watch: {
    'conditionVo.model': {
      handler: function (newVal, oldVal) {
        this.submitConditionPagingQuery();
      },
      deep: true,
    },
    'conditionVo.page.pageNum': {
      handler: function (newVal, oldVal) {
        this.submitConditionPagingQuery();
      },
      deep: true,
    },
    'conditionVo.page.pageSize': {
      handler: function (newVal, oldVal) {
        this.submitConditionPagingQuery();
      },
      deep: true,
    },
    'conditionVo.params.cryptoStatus': {
      handler: function (newVal, oldVal) {
        this.submitConditionPagingQuery();
      },
      deep: true,
    },
  },
  mounted() {
    this.bindingNodeClickEvent();
    this.sysParamsInit();
  },
}
</script>

<style scoped>
[v-cloak] {
  display: none !important;
}

.conditionalFormZone > form {
  display: inline-block;
}

.VpM8CpARnT0eEtZGTip3 {
  text-align: right;
}
</style>