<template>
  <div class="full-screen-black">
    <div class="my-dororo-spinner">

      <div class="loader">
        <div class="red bar"></div>
        <div class="orange bar"></div>
        <div class="yellow bar"></div>
        <div class="green bar"></div>
        <div class="blue bar"></div>
        <div class="violet bar"></div>
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
  name: "Loading012",
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
  top: 54%;
  left: 48%;
  transform: translate(-50%, -50%);
  text-align: center;
  border-radius: 50px;
}


.loader {
  height: 50px;
  display: flex;
  align-items: flex-end;
  gap: 3px;
}

.bar {
  height: 5px;
  width: 12px;
  animation-duration: 0.45s;
  animation-name: changeHeight;
  animation-iteration-count: infinite;
  animation-direction: alternate;
}

.red {
  background-color: #E50000;
  box-shadow: 1px 1px 10px #E50000;
  animation-delay: 0.10s;
}

.orange {
  background-color: #FF8D00;
  box-shadow: 1px 1px 10px #FF8D00;
  animation-delay: 0.20s;
}

.yellow {
  background-color: #FFEE00;
  box-shadow: 1px 1px 10px #FFEE00;
  animation-delay: 0.25s;
}

.green {
  background-color: #008121;
  box-shadow: 1px 1px 10px #008121;
  animation-delay: 0.30s;
}

.blue {
  background-color: #004CFF;
  box-shadow: 1px 1px 10px #004CFF;
  animation-delay: 0.35s;
}

.violet {
  background-color: #760188;
  box-shadow: 1px 1px 10px #760188;
  animation-delay: 0.40s;
}

@keyframes changeHeight {
  from {
    height: 5px;
  }

  to {
    height: 40px;
  }
}

</style>