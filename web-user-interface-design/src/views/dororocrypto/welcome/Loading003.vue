<template>
  <div class="full-screen-black">
    <div class="okk">
      <div class="first-wrapper" v-if="number===0">
        <div class="square-loader">
          <div class="square first_square"></div>
          <div class="square second_square"></div>
          <div class="square third_square"></div>
        </div>
      </div>
      <div class="second-wrapper" v-if="number===1">
        <div class="circle-loader">
          <div class="circle circle_four"></div>
          <div class="circle circle_three"></div>
          <div class="circle circle_two"></div>
          <div class="circle circle_one"></div>
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
  name: "Loading003",
  components: {},
  data() {
    return {
      number: -1,
    }
  },
  methods: {},
  watch: {},
  mounted() {
    // 0~1
    this.number = Math.floor(Math.random() * 2);
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

.okk {
  width: 100px;
  height: 100px;
  position: fixed;
  top: 50%;
  left: 46%;
  transform: translate(-50%, -50%);
  text-align: center;
  border-radius: 50px;
}


div[class*=wrapper] {
  height: 10%;
  width: 10%;
  display: flex;
  justify-content: center;
  align-items: center;
}

div[class*=loader] {
  position: relative;
  width: 200px;
  height: 200px;
}

/*
=======================
    Square Preloader
=======================
*/
.first-wrapper {
  background-color: #22292C;
}

.square {
  width: 50px;
  height: 50px;
  background-color: rgba(255, 255, 255, 0);
  margin-right: auto;
  margin-left: auto;
  border: 2px solid #fff;
  left: 73px;
  top: 73px;
  position: absolute;
}

.square-loader {
  transform: rotate(45deg);
}

.first_square {
  animation: first_square_animate 1s infinite ease-in-out;
}

.second_square {
  animation: second_square 1s forwards,
  second_square_animate 1s infinite ease-in-out;
}

.third_square {
  animation: third_square 1s forwards,
  third_square_animate 1s infinite ease-in-out;
}

@keyframes second_square {
  100% {
    width: 100px;
    height: 100px;
    left: 48px;
    top: 48px;
  }
}

@keyframes third_square {
  100% {
    width: 150px;
    height: 150px;
    left: 23px;
    top: 23px;
  }
}

@keyframes first_square_animate {
  0% {
    transform: perspective(100px) rotateX(0deg) rotateY(0deg);
  }
  50% {
    transform: perspective(100px) rotateX(-180deg) rotateY(0deg);
  }
  100% {
    transform: perspective(100px) rotateX(-180deg) rotateY(-180deg);
  }
}

@keyframes second_square_animate {
  0% {
    transform: perspective(200px) rotateX(0deg) rotateY(0deg);
  }
  50% {
    transform: perspective(200px) rotateX(180deg) rotateY(0deg);
  }
  100% {
    transform: perspective(200px) rotateX(180deg) rotateY(180deg);
  }
}

@keyframes third_square_animate {
  0% {
    transform: perspective(300px) rotateX(0deg) rotateY(0deg);
  }
  50% {
    transform: perspective(300px) rotateX(-180deg) rotateY(0deg);
  }
  100% {
    transform: perspective(300px) rotateX(-180deg) rotateY(-180deg);
  }
}


/*
=======================
   Circle Preloader
=======================
*/
.second-wrapper {
  background-color: #22292C;
}

.circle-loader {
  position: relative;
}

.circle {
  border-radius: 50% 50% 50% 50%;
  position: absolute;
  border-top: 2px solid #fff;
  border-bottom: 2px solid transparent;
  border-left: 2px solid #fff;
  border-right: 2px solid transparent;
  animation: animate 2s infinite;
}

.circle_one {
  left: 75px;
  top: 75px;
  width: 50px;
  height: 50px;
}

.circle_two {
  left: 65px;
  top: 65px;
  width: 70px;
  height: 70px;
  animation-delay: 0.2s;
}

.circle_three {
  left: 55px;
  top: 55px;
  width: 90px;
  height: 90px;
  animation-delay: 0.4s;
}

.circle_four {
  left: 45px;
  top: 45px;
  width: 110px;
  height: 110px;
  animation-delay: 0.6s;
}

@keyframes animate {
  50% {
    transform: rotate(360deg) scale(0.8);
  }
}


</style>