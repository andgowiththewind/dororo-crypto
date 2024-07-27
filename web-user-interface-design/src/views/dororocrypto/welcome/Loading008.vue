<template>
  <div class="full-screen-black">
    <div class="my-dororo-spinner">



      <div class="gearbox">
        <div class="overlay"></div>
        <div class="gear one">
          <div class="gear-inner">
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
          </div>
        </div>
        <div class="gear two">
          <div class="gear-inner">
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
          </div>
        </div>
        <div class="gear three">
          <div class="gear-inner">
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
          </div>
        </div>
        <div class="gear four large">
          <div class="gear-inner">
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
            <div class="bar"></div>
          </div>
        </div>
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
  name: "Loading008",
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
  top: 50%;
  left: 46%;
  transform: translate(-50%, -50%);
  text-align: center;
  border-radius: 50px;
}

@keyframes clockwise {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

@keyframes counter-clockwise {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(-360deg);
  }
}

.gearbox {
  background: #22292C;
  height: 150px;
  width: 200px;
  position: relative;
  border: none;
  overflow: hidden;
  border-radius: 6px;
  box-shadow: 0px 0px 0px 1px rgba(255, 255, 255, 0.1);
}

.gearbox .overlay {
  border-radius: 6px;
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 10;
  box-shadow: inset 0px 0px 20px black;
  transition: background 0.2s;
}

.gearbox .overlay {
  background: transparent;
}

.gear {
  position: absolute;
  height: 60px;
  width: 60px;
  box-shadow: 0px -1px 0px 0px #888888, 0px 1px 0px 0px black;
  border-radius: 30px;
}

.gear.large {
  height: 120px;
  width: 120px;
  border-radius: 60px;
}

.gear.large:after {
  height: 96px;
  width: 96px;
  border-radius: 48px;
  margin-left: -48px;
  margin-top: -48px;
}

.gear.one {
  top: 12px;
  left: 10px;
}

.gear.two {
  top: 61px;
  left: 60px;
}

.gear.three {
  top: 110px;
  left: 10px;
}

.gear.four {
  top: 13px;
  left: 128px;
}

.gear:after {
  content: "";
  position: absolute;
  height: 36px;
  width: 36px;
  border-radius: 36px;
  background: #797634;
  top: 50%;
  left: 50%;
  margin-left: -18px;
  margin-top: -18px;
  z-index: 3;
  box-shadow: 0px 0px 10px rgba(255, 255, 255, 0.1), inset 0px 0px 10px rgba(0, 0, 0, 0.1), inset 0px 2px 0px 0px #090909, inset 0px -1px 0px 0px #888888;
}

.gear-inner {
  position: relative;
  height: 100%;
  width: 100%;
  background: #555;
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.large .gear-inner {
  border-radius: 60px;
}

.gear.one .gear-inner {
  animation: counter-clockwise 3s infinite linear;
}

.gear.two .gear-inner {
  animation: clockwise 3s infinite linear;
}

.gear.three .gear-inner {
  animation: counter-clockwise 3s infinite linear;
}

.gear.four .gear-inner {
  animation: counter-clockwise 6s infinite linear;
}

.gear-inner .bar {
  background: #555;
  height: 16px;
  width: 76px;
  position: absolute;
  left: 50%;
  margin-left: -38px;
  top: 50%;
  margin-top: -8px;
  border-radius: 2px;
  border-left: 1px solid rgba(255, 255, 255, 0.1);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
}

.large .gear-inner .bar {
  margin-left: -68px;
  width: 136px;
}

.gear-inner .bar:nth-child(2) {
  transform: rotate(60deg);
}

.gear-inner .bar:nth-child(3) {
  transform: rotate(120deg);
}

.gear-inner .bar:nth-child(4) {
  transform: rotate(90deg);
}

.gear-inner .bar:nth-child(5) {
  transform: rotate(30deg);
}

.gear-inner .bar:nth-child(6) {
  transform: rotate(150deg);
}

</style>