<template>
  <div>
    <div class="process-show-btn" @click="showProcessDrawer">
      <el-tooltip effect="dark" content="查看进度" placement="top">
        <span><i class="fa fa-table"></i></span>
      </el-tooltip>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import * as methodConsts from '@/config/methodConsts';


export default {
  // 请注意这里的.vue后缀需要去掉
  name: "ProcessTable",
  components: {},
  data() {
    return {}
  },
  methods: {
    showProcessDrawer() {
      // 测试
      this.$bus.$emit(methodConsts.TEST_TEST_TEST, true);
    },
  },
  watch: {
    // 示例
    // 'searchParamVo.topPath': function (newValue, oldValue) {this.paginationVo.pageNum = 1;this.tableVo.tableData.length = 0;this.tableVo.tableData = [];},
  },
  mounted() {
    // this.init();//示例
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