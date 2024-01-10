<template>
  <div>
    <div class="process-show-btn" @click="showProcessDrawer">
      <el-tooltip effect="dark" content="查看进度" placement="top">
        <span><i class="fa fa-table"></i></span>
      </el-tooltip>
    </div>

    <!--弹出层-->
    <el-drawer
        :size="'60%'"
        :with-header="false"
        title="processDrawerAssembly"
        :visible.sync="processDrawerVo.show"
        :direction="processDrawerVo.direction">
      <div class="process-drawer-body-zone">
        <!--进度表格-->
        <div class="process-table-title-zone">
          <el-alert title="正在加解密文件总览" center type="success" :closable="false" style="height: 30px;"></el-alert>
        </div>
        <el-table
            size="mini"
            :fit="true"
            :stripe="true"
            :border="true"
            :row-style="rowStyleFn"
            :cell-style="cellStyleFn"
            :header-cell-style="headerCellStyleFn"
            :max-height="900"
            :data="processTableData"
            style="width: 100%;min-height:200px;">
          <el-table-column label="文件名" width="200" prop="name" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column label="进度" prop="percentage">
            <template v-slot="scope">
              <span v-if="!scope.row.percentage" style="opacity: 25%">/</span>
              <el-progress v-if="scope.row.percentage" :percentage="scope.row.percentage||0"></el-progress>
            </template>
          </el-table-column>
        </el-table>
        <!--统计数据-->
      </div>
    </el-drawer>

  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import * as methodConsts from '@/config/methodConsts';
import {rowStyleFn, cellStyleFn, headerCellStyleFn} from "@/api/processTableApi";


export default {
  // 请注意这里的.vue后缀需要去掉
  name: "ProcessTable",
  components: {},
  data() {
    return {
      rowStyleFn: rowStyleFn,
      cellStyleFn: cellStyleFn,
      headerCellStyleFn: headerCellStyleFn,
      processDrawerVo: {
        show: false,
        direction: 'btt',
      },
      processTableData: [],
    }
  },
  methods: {
    showProcessDrawer() {
      this.processDrawerVo.show = true;
    },
    processTableDataUpdateBinding() {
      this.$bus.$on(methodConsts.PROCESS_TABLE_DATA_UPDATE, (data) => {
        this.processTableData = data;
      });
    },
  },
  watch: {},
  mounted() {
    this.processTableDataUpdateBinding();
  },
}
</script>

<style scoped>
.process-show-btn {
  opacity: 25%;
  color: #0B61A4;
  position: fixed;
  left: 10px;
  bottom: 10px;
  font-size: 70px;
}

.process-show-btn:hover {
  opacity: 100%;
  color: rgb(0, 133, 125);
}
</style>