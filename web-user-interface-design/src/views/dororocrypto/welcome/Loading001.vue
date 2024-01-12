<template>
  <div class="full-screen-black">
    <div class="spinner0">
      <div class="spinner1">
        <div class="spinner2"></div>
      </div>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';

export default {
  name: "Loading001",
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

.spinner0 {
  width: 100px;
  height: 100px;
  position: fixed;
  top: 50%;
  left: 46%;
  transform: translate(-50%, -50%);
  text-align: center;
  border-radius: 50px;
}

.spinner1 {
  width: 100px;
  height: 100px;
  text-align: center;
  border-radius: 50px;

  animation: spinning82341 1.7s linear infinite;
  filter: blur(1px);
  background-image: linear-gradient(rgb(186, 66, 255) 35%, rgb(0, 225, 255));
  box-shadow: 0px -5px 20px 0px rgb(186, 66, 255), 0px 5px 20px 0px rgb(0, 225, 255);
}

.spinner2 {
  background-color: rgb(36, 36, 36);
  width: 100px;
  height: 100px;
  border-radius: 50px;
  filter: blur(10px);
}

@keyframes spinning82341 {
  to {
    transform: rotate(360deg);
  }
}
</style>