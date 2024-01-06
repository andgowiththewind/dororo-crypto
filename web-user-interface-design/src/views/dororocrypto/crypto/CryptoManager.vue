<template>
  <div>
    <el-container class="top-container-clazz">
      <el-aside v-drag style="width: 250px;">
        <!--        <AsideTree/>-->
        <span>1</span>
      </el-aside>
      <el-container>
        <el-header>Header</el-header>
        <el-main style="padding: 5px !important;">
          <!--          <CenterContent/>-->
          <span>2</span>
        </el-main>
        <el-footer>Footer</el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';

export default {
  // 请注意这里的.vue后缀需要去掉
  name: "CryptoManager",
  components: {},
  data() {
    return {}
  },
  methods: {
    devCheck() {
      if (process.env.NODE_ENV === 'development') {
        Notification({title: '环境提示', message: '当前处于开发模式', type: 'warning', duration: 2000, position: 'bottom-right'});
      }
    },
  },
  watch: {},
  mounted() {
    this.devCheck();
  },
}
</script>

<style scoped>
/* ===============================================[测试阶段颜色区分][start]======================================================== */
.el-header, .el-footer {
  background-color: #B3C0D1;
  color: #333;
}

.el-aside {
  background-color: #D3DCE6;
  color: #333;
}

.el-main {
  background-color: #E9EEF3;
  color: #333;
}

/* ===============================================[测试阶段颜色区分][end  ]======================================================== */


.top-container-clazz {
  overflow: hidden;
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  margin: 5px;
}
</style>