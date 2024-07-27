<template>
  <div class="full-screen-black">
    <div class="my-dororo-spinner">
      <div class="loader"></div>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';

export default {
  name: "Loading005",
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
  top: 56%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  border-radius: 50px;
}


.loader {
  animation: rotate 1s infinite;
  height: 50px;
  width: 50px;
}

.loader:before,
.loader:after {
  border-radius: 50%;
  content: '';
  display: block;
  height: 20px;
  width: 20px;
}

.loader:before {
  animation: ball1 1s infinite;
  background-color: #cb2025;
  box-shadow: 30px 0 0 #f8b334;
  margin-bottom: 10px;
}

.loader:after {
  animation: ball2 1s infinite;
  background-color: #00a096;
  box-shadow: 30px 0 0 #97bf0d;
}

@keyframes rotate {
  0% {
    -webkit-transform: rotate(0deg) scale(0.8);
    -moz-transform: rotate(0deg) scale(0.8);
  }
  50% {
    -webkit-transform: rotate(360deg) scale(1.2);
    -moz-transform: rotate(360deg) scale(1.2);
  }
  100% {
    -webkit-transform: rotate(720deg) scale(0.8);
    -moz-transform: rotate(720deg) scale(0.8);
  }
}

@keyframes ball1 {
  0% {
    box-shadow: 30px 0 0 #f8b334;
  }
  50% {
    box-shadow: 0 0 0 #f8b334;
    margin-bottom: 0;
    -webkit-transform: translate(15px, 15px);
    -moz-transform: translate(15px, 15px);
  }
  100% {
    box-shadow: 30px 0 0 #f8b334;
    margin-bottom: 10px;
  }
}

@keyframes ball2 {
  0% {
    box-shadow: 30px 0 0 #97bf0d;
  }
  50% {
    box-shadow: 0 0 0 #97bf0d;
    margin-top: -20px;
    -webkit-transform: translate(15px, 15px);
    -moz-transform: translate(15px, 15px);
  }
  100% {
    box-shadow: 30px 0 0 #97bf0d;
    margin-top: 0;
  }
}
</style>