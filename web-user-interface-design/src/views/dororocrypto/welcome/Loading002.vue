<template>
  <div class="full-screen-black">
    <div class="loading-wave">
      <div class="loading-bar"></div>
      <div class="loading-bar"></div>
      <div class="loading-bar"></div>
      <div class="loading-bar"></div>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';

export default {
  // 请注意这里的.vue后缀需要去掉
  name: "Loading002",
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


.loading-wave {
  width: 300px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: flex-end;

  position: fixed;
  top: 50%;
  left: 46%;
  transform: translate(-50%, -50%);
  text-align: center;
  border-radius: 50px;

}

.loading-bar {
  width: 20px;
  height: 10px;
  margin: 0 5px;
  background-color: #dbc534;
  border-radius: 5px;
  animation: loading-wave-animation 1s ease-in-out infinite;
}

.loading-bar:nth-child(2) {
  animation-delay: 0.1s;
}

.loading-bar:nth-child(3) {
  animation-delay: 0.2s;
}

.loading-bar:nth-child(4) {
  animation-delay: 0.3s;
}

@keyframes loading-wave-animation {
  0% {
    height: 10px;
  }

  50% {
    height: 50px;
  }

  100% {
    height: 10px;
  }
}

</style>