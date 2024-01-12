<template>
  <div class="full-screen-black">
    <div class="my-dororo-spinner">
      <div class="loader">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';

export default {
  // 请注意这里的.vue后缀需要去掉
  name: "Loading004",
  components: {},
  data() {
    return {}
  },
  methods: {},
  watch: {},
  mounted() {
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
  top: 50%;
  left: 46%;
  transform: translate(-50%, -50%);
  text-align: center;
  border-radius: 50px;
}

.loader {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(#14ffe9, #ffeb3b, #ff00e0);
  animation: animate 0.8s linear infinite;
}

@keyframes animate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loader span {
  //position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(#14ffe9, #ffeb3b, #ff00e0);
  animation: animate 0.8s linear infinite;
}

.loader span:nth-child(1) {
  filter: blur(5px);
}

.loader span:nth-child(2) {
  filter: blur(10px);
}

.loader span:nth-child(3) {
  filter: blur(25px);
}

.loader span:nth-child(4) {
  filter: blur(50px);
}

.loader:after {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  background: #240229;
  border-radius: 50%;
}

</style>