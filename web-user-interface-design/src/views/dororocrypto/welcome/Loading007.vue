<template>
  <div class="full-screen-black">
    <div class="my-dororo-spinner">

      <div class="spinner">
        <div class="spinnerin"></div>
      </div>

    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
// import request from '@/utils/request'// 示例
// import {SECRET_KEY} from '@/config/headerKeyNameConstants'// 示例

export default {
  // 请注意这里的.vue后缀需要去掉
  name: "Loading007",
  components: {},
  data() {
    return {}
  },
  methods: {},
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
.full-screen-black {
  position: fixed; /* 固定定位，覆盖整个屏幕 */
  top: 0;
  left: 0;
  width: 100vw; /* 视口宽度 */
  height: 100vh; /* 视口高度 */
  background-color: #22292C; /* 黑色背景 */
  z-index: -1; /* 确保它在内容下方 */
}

.my-dororo-spinner {
  width: 100px;
  height: 100px;
  position: fixed;
  top: 56%;
  left: 48%;
  transform: translate(-50%, -50%);
  text-align: center;
  border-radius: 50px;
}

.spinner {
  width: 3em;
  height: 3em;
  cursor: not-allowed;
  border-radius: 50%;
  border: 2px solid #444;
  box-shadow: -10px -10px 10px #6359f8, 0px -10px 10px 0px #9c32e2, 10px -10px 10px #f36896, 10px 0 10px #ff0b0b, 10px 10px 10px 0px#ff5500, 0 10px 10px 0px #ff9500, -10px 10px 10px 0px #ffb700;
  animation: rot55 0.7s linear infinite;
}

.spinnerin {
  border: 2px solid #444;
  width: 1.5em;
  height: 1.5em;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@keyframes rot55 {
  to {
    transform: rotate(360deg);
  }
}

</style>