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
              <el-button type="danger" plain size="mini" @click="decryptOne(scope.row)">解密</el-button>
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
import {nanoid} from 'nanoid';
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
      // WS实例ID
      cryptoWebSocketId: null,
      // WS实例对象
      cryptoWebSocketVo: null,
      // 用于跟踪WebSocket连接状态的Promise,防止重复构建
      cryptoWebSocketPromise: null,
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
        // TODO
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
        // devConsoleLog('INSIGHT_TABLE_DATA_UPDATE', data);
      });
    },
    sysParamsInit() {
      getSysParam('encryptedPrefix').then(res => {
        if (!res.data) {
          Notification({title: '系统参数', message: '系统参数获取失败:已加密文件前缀', type: 'error', duration: 2000, position: 'bottom-right'});
          return;
        }
        this.sysParams = Object.assign({}, this.sysParams, {encryptedPrefix: res.data});
      });

      getSysParam('websocketUriPrefix').then(res => {
        if (!res.data) {
          Notification({title: '系统参数', message: '系统参数获取失败:WS前缀', type: 'error', duration: 2000, position: 'bottom-right'});
          return;
        }
        this.sysParams = Object.assign({}, this.sysParams, {websocketUriPrefix: res.data});
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
    bufferSizeOptionsInit() {
      let list = [];
      for (let i = 0; i < 10; i++) {
        list.push(i + 1);
      }
      list.push(20);
      list.push(30);
      list.push(40);
      list.push(50);
      list.map(item => {
        this.sysParams.bufferSizeOptions.push({label: item * 1024, value: item * 1024});
      });
    },
    // 构建WS,如果已存在且已连接,则无需重复构建
    buildCryptoWebSocket() {
      if (this.cryptoWebSocketVo != null && this.cryptoWebSocketVo != undefined && this.cryptoWebSocketVo.readyState === WebSocket.OPEN) {
        // devConsoleLog(`无需重复构建webSocket,当前-[${this.cryptoWebSocketId}]`);
        return true;
      }
      if (!this.sysParams.websocketUriPrefix) {
        console.error("无法构建webSocket,缺少服务器地址");
        return false;
      }
      if (this.cryptoWebSocketPromise) {
        devConsoleLog("有其他线程webSocket")
        return false;
      } else {
        // 说明没有其他线程正在构建WS
        let sessionId = nanoid(18);
        const url = `${this.sysParams.websocketUriPrefix}/cryptoWebSocket/${sessionId}`;
        // 构建Promise
        this.cryptoWebSocketPromise = new Promise((resolve, reject) => {
          try {
            devConsoleLog("正在新建webSocket...");
            // 此时是异步的
            this.cryptoWebSocketVo = new WebSocket(url);

            // 连接建立成功
            this.cryptoWebSocketVo.onopen = () => {
              devConsoleLog(`[${sessionId}]-webSocket Connected`);
              this.cryptoWebSocketId = sessionId;
              resolve(this.cryptoWebSocketVo);
            };

            // 连接建立失败
            this.cryptoWebSocketVo.onerror = (error) => {
              devConsoleLog(`[${sessionId}]-webSocket Connect Failed`);
              reject(error);
            };

            // 接收到消息
            this.cryptoWebSocketVo.onmessage = (event) => {
              // devConsoleLog(`[${sessionId}]-webSocket Received:`, event.data);
              // 处理消息分发到具体业务具体操作
              this.cryptoWebSocketMessageDispatch(event.data);
            };

            // 连接关闭
            this.cryptoWebSocketVo.onclose = () => {
              devConsoleLog(`[${sessionId}]-webSocket Closed`);
              this.cryptoWebSocketPromise = null; // 重置Promise
              this.cryptoWebSocketId = null; // 重置ID
            };
          } catch (e) {
            devConsoleLog("webSocket Build Failed", e);
            reject(e);
          }
        });

        // 处理Promise完成和异常
        this.cryptoWebSocketPromise.then(ws => {
          // devConsoleLog("webSocket Promise Resolved");
        }).catch(error => {
          devConsoleLog("webSocket Promise Rejected", error);
          this.cryptoWebSocketPromise = null; // 重置Promise
        });
      }
    },
    // 处理WS接收到的消息
    cryptoWebSocketMessageDispatch(messageStr) {
      // 1.0 字符串尝试转JSON
      let res = {};
      try {
        res = JSON.parse(messageStr);
      } catch (e) {
        devConsoleLog("`webSocket`接收字符串尝试转JSON发生错误", e);
        return false;
      }
      // devConsoleLog("WS接收字符串转JSON成功", res);
      // 2.0 判断JSON中是否包含约定的状态码`200`
      if (!(res && res.code && res.code === 200)) {
        // devConsoleLog("webSocket接收到的对象状态码!=200", res);
        return false;
      }
      // 3.1 [具体业务具体分发]-如果是`tableDataUpdate`类型
      if (res && res.type && res.type === 'tableDataUpdate') {
        this.wsTableDataUpdate(res.data);
      }
    },
    // 更新两个tableData中的数据
    wsTableDataUpdate(tableData) {
      // (1)预览表格
      // 约定字段`insightTableData`,更新`insightTableVo.tableData`
      let insightTableData = tableData.insightTableData;
      // 当前表格中展示的数据
      let currentData = this.insightTableVo.tableData;
      currentData.map(sourceItem => {
        // 当前表格中展示的数据行,如果在本次后端传过来的结果集能找到对应,则更新
        insightTableData.map(insightTableDataItem => {
          if (sourceItem.id === insightTableDataItem.id) {
            Object.assign(sourceItem, insightTableDataItem);
          }
        });
      });
      // (2)进度表格,约定字段`processTableData`,更新`processTableVo.tableData`
      let processTableData = tableData.processTableData;
      this.$bus.$emit(methodConsts.PROCESS_TABLE_DATA_UPDATE, processTableData);
    },
    // 启动定时任务
    scheduleStartUp(intervalMs) {
      // 每隔N毫秒请求一次tableData
      setInterval(() => {
        if (this.buildCryptoWebSocket()) {
          // 收集tableData的id
          let tableDataIdList = [];
          this.insightTableVo.tableData.map(item => {
            tableDataIdList.push(item.id);
          });
          this.cryptoWebSocketVo.send(JSON.stringify({type: 'tableDataUpdate', data: tableDataIdList}));
        }
      }, intervalMs);
    },
  },
  watch: {},
  mounted() {
    this.updateInsightTableDataBinding();
    this.folderPathUpdateBinding();
    this.sysParamsInit();
    this.bufferSizeOptionsInit();
    this.scheduleStartUp(2000);
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