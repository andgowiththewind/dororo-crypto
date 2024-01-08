<template>
  <div>
    <el-table
        size="mini"
        :fit="true"
        :stripe="true"
        :border="true"
        :row-style="rowStyleFn"
        :cell-style="cellStyleFn"
        :header-cell-style="headerCellStyleFn"
        :max-height="900"
        :data="insightTableVo.tableData"
        style="width: 100%;min-height:200px;">
      <el-table-column label="文件名" width="222" prop="name" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="扩展名" width="60" prop="extName" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="大小" width="100" prop="readableFileSize" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="操作" width="120" prop="status">
        <template v-slot="scope">
          <div v-if="scope.row.status===null||scope.row.status===undefined">
            <el-button size="mini" type="info" disabled>无法获取状态</el-button>
          </div>
          <div v-if="scope.row.status===-1">
            <el-button size="mini" type="info" plain disabled>文件不存在</el-button>
          </div>
          <div v-if="scope.row.status===0">
            <div v-if="hadBeenEncrypted(scope.row.name)">
              <el-button type="success" plain size="mini" @click="decryptOne(scope.row)">解密</el-button>
            </div>
            <div v-if="!hadBeenEncrypted(scope.row.name)">
              <el-button type="success" plain size="mini" @click="encryptOne(scope.row)">加密</el-button>
            </div>
          </div>
          <div v-if="scope.row.status===10">
            <el-button size="mini" type="warning" plain>队列中</el-button>
          </div>
          <div v-if="scope.row.status>=20 && scope.row.status<=40">
            <el-button size="mini" type="warning" plain disabled>正在处理</el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="最近消息" width="250" prop="message" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="进度" width="350" prop="percentage">
        <template v-slot="scope">
          <div v-if="scope.row.status>=20 && scope.row.status<=40">
            <el-progress v-if="scope.row.percentage" :percentage="scope.row.percentage||0"></el-progress>
          </div>
          <span v-if="!scope.row.percentage" style="opacity: 25%">/</span>
        </template>
      </el-table-column>
      <el-table-column prop="absPath" label="文件路径" :show-overflow-tooltip="true"></el-table-column>
    </el-table>
    <!--功能按钮-->
    <div class="business-btn-zone">
      <el-form @submit.native.prevent :inline="true" size="small">
        <el-form-item label="">
          <el-input v-model="userPassword" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="">
          <el-button size="mini" type="warning" plain @click="checkSecretKey"><span><i class="fa fa-key"></i>&nbsp;检查密码格式</span></el-button>
        </el-form-item>
        <el-form-item label="">
          <el-select v-model="bufferSize" clearable placeholder="缓冲区" style="width: 100px;">
            <el-option v-for="item in sysParams.bufferSizeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="">
          <el-button-group>
            <el-button size="mini" type="primary" @click="encryptAll" :style="{minWidth:'25vw'}"><span><i class="fa fa-lock"></i>&nbsp;目录下全部加密</span></el-button>
            <el-button size="mini" type="danger" plain @click="decryptAll"><span>全部解密&nbsp;<i class="fa fa-wrench"></i></span></el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import * as methodConsts from '@/config/methodConsts';
import {devConsoleLog, getSysParam} from "@/utils/dororoUtils";
import {checkSecretKeyFn, cryptoSubmitFn, rowStyleFn, cellStyleFn, headerCellStyleFn} from "@/api/insightTableApi";


export default {
  // 请注意这里的.vue后缀需要去掉
  name: "InsightTable",
  components: {},
  data() {
    return {
      rowStyleFn: rowStyleFn,
      cellStyleFn: cellStyleFn,
      headerCellStyleFn: headerCellStyleFn,
      sysParams: {
        bufferSizeOptions: [],
      },
      insightTableVo: {
        tableData: [],
      },
      folderPathCopy: process.env.VUE_APP_DEV_FOLDER_PATH || '',
      userPassword: process.env.VUE_APP_TEST_KEY || '',
      bufferSize: 1024,
    }
  },
  methods: {
    decryptOne(row) {
      let params = {pathList: [row.absPath], pathType: 'file', askEncrypt: false};
      this.cryptoSubmit(params);
    },
    encryptOne(row) {
      let params = {pathList: [row.absPath], pathType: 'file', askEncrypt: true};
      this.cryptoSubmit(params);
    },
    encryptAll() {
      let params = {pathList: [this.folderPathCopy], pathType: 'directory', askEncrypt: true};
      this.cryptoSubmit(params);
    },
    decryptAll() {
      let params = {pathList: [this.folderPathCopy], pathType: 'directory', askEncrypt: false};
      this.cryptoSubmit(params);
    },
    // 统一提交加解密请求
    cryptoSubmit(cryptoReqVo) {
      let defaultParams = {"pathList": [], "PathType": 'file', "askEncrypt": true, userPassword: this.userPassword, bufferSize: this.bufferSize};
      Object.assign(defaultParams, cryptoReqVo);
      cryptoSubmitFn(defaultParams).then(res => {
        // devConsoleLog('cryptoSubmitFn', res);
      });
    },
    checkSecretKey() {
      checkSecretKeyFn({userPassword: this.userPassword}).then(res => {
        let success = res && res.code && res.code === 200;
        let type = success ? 'success' : 'error';
        Notification({title: '密码检查', message: res.msg, type: type, duration: 2000, position: 'bottom-right'});
      });
    },
    updateInsightTableDataBinding() {
      this.$bus.$on(methodConsts.INSIGHT_TABLE_DATA_UPDATE, (data) => {
        this.insightTableVo.tableData = data;
        devConsoleLog('INSIGHT_TABLE_DATA_UPDATE', data);
      });
    },
    sysParamsInit() {
      getSysParam('encryptedPrefix').then(res => {
        this.sysParams = Object.assign({}, this.sysParams, {encryptedPrefix: res.data});
      });
    },
    hadBeenEncrypted(blossomName) {
      return blossomName && blossomName.startsWith(this.sysParams.encryptedPrefix);
    },
    folderPathUpdateBinding() {
      this.$bus.$on(methodConsts.FOLDER_PATH_UPDATE, (folderPath) => {
        this.folderPathCopy = folderPath;
      });
    },
  },
  watch: {},
  mounted() {
    this.updateInsightTableDataBinding();
    this.folderPathUpdateBinding();
    this.sysParamsInit();
  },
}
</script>

<style scoped>
.business-btn-zone {
  text-align: center;
  margin-top: 3px;
}

.business-btn-zone > form:nth-child(1) > div:nth-child(2) {
  margin-left: -8px;
}

.business-btn-zone > form:nth-child(1) > div:nth-child(3) {
  margin-left: -8px;
}
</style>