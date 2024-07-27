<template>
  <div class="full-screen-black">
    <div class="my-dororo-spinner">
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';

export default {
  name: "Loading000",
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
</style>