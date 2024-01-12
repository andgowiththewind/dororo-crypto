<template>
  <div>
    <div class="welcome-body" @click="cryptoManagerShowAction">
      <p ref="offscreenText" class="offscreen-text"></p>
      <p ref="text" class="text"></p>
      <svg ref="svg"></svg>
      <input v-show="false" type="text" class="input" ref="userInput" @input="onInputChange" v-model="inputValue"/>
    </div>
    <div class="author-info-zone">
      <p>
        <span style="color:#FBDB4A;">© 2024 </span>
        <span style="color:#F3934A;">dororo. </span>
        <span style="color:#EB547D;">All </span>
        <span style="color:#9F6AA7;">rights </span>
        <span style="color:#5476B3;">reserved. </span>
      </p>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
// npm install gsap
import {gsap} from "gsap";
import {devConsoleLog} from "@/utils/dororoUtils";
import * as methodConsts from '@/config/methodConsts';


export default {
  // 请注意这里的.vue后缀需要去掉
  name: "WelcomePage",
  components: {},
  data() {
    return {
      inputValue: '',
      colors: [
        {main: '#FBDB4A', shades: ['#FAE073', '#FCE790', '#FADD65', '#E4C650']},
        {main: '#F3934A', shades: ['#F7B989', '#F9CDAA', '#DD8644', '#F39C59']},
        {main: '#EB547D', shades: ['#EE7293', '#F191AB', '#D64D72', '#C04567']},
        {main: '#9F6AA7', shades: ['#B084B6', '#C19FC7', '#916198', '#82588A']},
        {main: '#5476B3', shades: ['#6382B9', '#829BC7', '#4D6CA3', '#3E5782']},
        {main: '#2BB19B', shades: ['#4DBFAD', '#73CDBF', '#27A18D', '#1F8171']},
        {main: '#70B984', shades: ['#7FBE90', '#98CBA6', '#68A87A', '#5E976E']}
      ],
      letters: [],
      runPrompt: true,
      // 请阅读免责声明和注意事项哦
      // prompt: ['请', '阅', '读', '免', '责', '声', '明', '和', '注', '意', '事', '项', '哦'],
      prompt: ['欢', '迎', '使', '用', '!', '~'],
      // prompt: ['s', 't', 'a', 'r', 't', ' ', 't', 'y', 'p', 'i', 'n', 'g'],
      textCenter: 0,
      textSize: 0,
      width: window.innerWidth,
      height: window.innerHeight,
      // 控制不能跳太快
      allowJump: false,
    }
  },
  methods: {
    resizePage() {
      this.width = window.innerWidth;
      this.height = window.innerHeight;
      this.$refs.svg.setAttribute('height', this.height);
      this.$refs.svg.setAttribute('width', this.width);
      this.$refs.svg.setAttribute('viewBox', `0 0 ${this.width} ${this.height}`);
      this.resizeLetters();
    },
    resizeLetters() {
      this.textSize = this.width / (this.letters.length + 2);
      if (this.textSize > 100) this.textSize = 100;
      this.$refs.text.style.fontSize = `${this.textSize}px`;
      this.$refs.text.style.height = `${this.textSize}px`;
      this.$refs.text.style.lineHeight = `${this.textSize}px`;
      this.$refs.offscreenText.style.fontSize = `${this.textSize}px`;
      const textRect = this.$refs.text.getBoundingClientRect();
      this.textCenter = textRect.top + textRect.height / 2;
      this.positionLetters();
    },
    positionLetters() {
      this.letters.forEach(letter => {
        const timing = letter.shift ? 0.1 : 0;
        // gsap.to(letter.onScreen, timing, {x: letter.offScreen.offsetLeft + 'px', ease: 'Power3.easeInOut'});
        gsap.to(letter.onScreen, {duration: timing, ease: "Power3.easeInOut", x: letter.offScreen.offsetLeft + 'px'});
        letter.shift = true;
      });
    },
    addPrompt(i) {
      setTimeout(() => {
        if (this.runPrompt && this.prompt[i]) {
          this.$refs.userInput.value = this.$refs.userInput.value + this.prompt[i];
          this.onInputChange();
          this.addPrompt(i + 1);
        }
      }, 100);
    },
    onInputChange() {
      const value = this.$refs.userInput.value === '' ? [] : this.$refs.userInput.value.toLowerCase().split('');
      this.addLetters(value);
      this.removeLetters(value);
      this.resizeLetters();
    },
    addLetters(value) {
      value.forEach((char, i) => {
        if (this.letters[i] && this.letters[i].char !== char) {
          this.letters[i].onScreen.innerHTML = char;
          this.letters[i].offScreen.innerHTML = char;
          this.letters[i].char = char;
        }
        if (this.letters[i] === undefined) {
          this.addLetter(char, i);
        }
      });
    },
    addLetter(char, i) {
      const letter = document.createElement('span');
      const oLetter = document.createElement('span');
      letter.innerHTML = char;
      oLetter.innerHTML = char;
      this.$refs.text.appendChild(letter);
      const color = this.colors[i % this.colors.length];
      letter.style.color = color.main;
      this.$refs.offscreenText.appendChild(oLetter);
      this.letters[i] = {offScreen: oLetter, onScreen: letter, char: char};
      this.animateLetterIn(letter);
      this.addDecor(oLetter, color);
    },
    animateLetterIn(letter) {
      const yOffset = (0.5 + Math.random() * 0.5) * this.textSize;
      gsap.fromTo(letter, {scale: 0}, {scale: 1, ease: 'Back.easeOut', duration: 0.4});
      gsap.fromTo(letter, {opacity: 0}, {opacity: 1, ease: 'Power3.easeOut', duration: 0.4});
      gsap.to(letter, {y: -yOffset, ease: 'Power3.easeInOut', duration: 0.2});
      gsap.to(letter, {y: 0, ease: 'Power3.easeInOut', delay: 0.2, duration: 0.2});
      const rotation = -50 + Math.random() * 100;
      gsap.to(letter, {rotation: rotation, ease: 'Power3.easeInOut', duration: 0.2});
      gsap.to(letter, {rotation: 0, ease: 'Power3.easeInOut', delay: 0.2, duration: 0.2});
    },
    addDecor(letter, color) {
      setTimeout(() => {
        var rect = letter.getBoundingClientRect();
        const x0 = letter.offsetLeft + letter.offsetWidth / 2;
        const y0 = this.textCenter - this.textSize * 0.5;
        const shade = color.shades[Math.floor(Math.random() * 4)];
        for (var i = 0; i < 8; i++) this.addTri(x0, y0, shade);
        for (var i = 0; i < 8; i++) this.addCirc(x0, y0);
      }, 150);
    },
    addTri(x0, y0, shade) {
      const tri = this.createSVG('polygon');
      const a = Math.random();
      const a2 = a + (-0.2 + Math.random() * 0.4);
      const r = this.textSize * 0.52;
      const r2 = r + this.textSize * Math.random() * 0.2;
      const x = x0 + r * Math.cos(2 * Math.PI * a);
      const y = y0 + r * Math.sin(2 * Math.PI * a);
      const x2 = x0 + r2 * Math.cos(2 * Math.PI * a2);
      const y2 = y0 + r2 * Math.sin(2 * Math.PI * a2);
      const triSize = this.textSize * 0.1;
      const scale = 0.3 + Math.random() * 0.7;
      const offset = triSize * scale;
      tri.set('points', `0,0 ${triSize * 2},0 ${triSize},${triSize * 2}`);
      tri.setStyle('fill', shade);
      this.$refs.svg.appendChild(tri);
      gsap.fromTo(tri,
          {rotation: Math.random() * 360, scale: scale, x: x - offset, y: y - offset, opacity: 1},
          {
            x: x2 - offset, y: y2 - offset, opacity: 0, ease: 'Power1.easeInOut', onComplete: () => {
              this.$refs.svg.removeChild(tri);
            }, duration: 0.6
          });
    },
    addCirc(x0, y0) {
      const circ = this.createSVG('circle');
      const a = Math.random();
      const r = this.textSize * 0.52;
      const r2 = r + this.textSize;
      const x = x0 + r * Math.cos(2 * Math.PI * a);
      const y = y0 + r * Math.sin(2 * Math.PI * a);
      const x2 = x0 + r2 * Math.cos(2 * Math.PI * a);
      const y2 = y0 + r2 * Math.sin(2 * Math.PI * a);
      const circSize = this.textSize * 0.05 * Math.random();
      circ.set('r', circSize);
      circ.setStyle('fill', '#eee');
      this.$refs.svg.appendChild(circ);
      gsap.fromTo(circ, {x: x - circSize, y: y - circSize, opacity: 1}, {
        x: x2 - circSize, y: y2 - circSize, opacity: 0, ease: 'Power1.easeInOut', onComplete: () => {
          this.$refs.svg.removeChild(circ);
        }, duration: 0.6
      });
    },
    removeLetters(value) {
      for (let i = this.letters.length - 1; i >= 0; i--) {
        const letter = this.letters[i];
        if (value[i] === undefined) {
          this.animateLetterOut(letter, i)
        }
      }
    },
    createSVG(type) {
      const svg = document.createElementNS('http://www.w3.org/2000/svg', type);
      svg.set = (attr, val) => svg.setAttribute(attr, val);
      // 为了避免覆盖原生的 style 对象，使用一个不同的方法名，例如 setStyle
      svg.setStyle = (prop, val) => svg.style[prop] = val;
      return svg;
    },
    animateLetterOut(letter, i) {
      gsap.to(letter.onScreen, {
        scale: 0, opacity: 0, ease: 'Power2.easeIn', onComplete: () => {
          console.log('removing');
          console.log(letter);
          this.$refs.offscreenText.removeChild(letter.offScreen);
          this.$refs.text.removeChild(letter.onScreen);
          this.positionLetters();
        }, duration: 0.1
      });
      this.letters.splice(i, 1);
    },
    keyup(e) {
      if (this.runPrompt) {
        this.$refs.userInput.value = '';
        this.runPrompt = false;
      }
      ;
      this.onInputChange();
    },
    cryptoManagerShowAction() {
      if (!this.allowJump) {
        devConsoleLog('不能跳太快哦');
        return false;
      }
      this.$bus.$emit(methodConsts.WELCOME_JUMP_LOADING, {});
    },
    allowJumpChange() {
      setTimeout(() => {
        this.allowJump = true;
      }, 1500);
    },
  },
  watch: {},
  mounted() {
    this.resizePage();
    window.addEventListener('resize', this.resizePage);
    this.$refs.userInput.addEventListener('keyup', this.keyup);
    this.$refs.userInput.focus();
    // 添加欢迎语
    this.addPrompt(0);

    this.allowJumpChange();
  },
  beforeDestroy() {
    try {
      window.removeEventListener('resize', this.resizePage);
      this.$refs.userInput.removeEventListener('keyup', this.keyup);
    } catch (e) {
      // devConsoleLog(e);
    }
  },
}
</script>

<style scoped>
.welcome-body {
  overflow: hidden;
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  margin: 0px;
  font-family: 'Rubik Mono One', sans-serif;
  background: #22292C;
}

svg {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0px;
  left: 0px;
  z-index: 0;
}

.input {
  position: absolute;
  z-index: 1;
  bottom: 0px;
  font-size: 20px;
  text-align: center;
  left: 50%;
  transform: translateX(-50%);
  font-family: helvetica, sans-serif;
  bottom: 20px;
  background: none;
  border: 1px solid #ddd;
  color: #eee;
}

.text, .offscreen-text {
  width: 100%;
  top: 50%;
  left: 85%;
  transform: translate(-50%, -50%);
  display: block;
  position: absolute;
  margin: 0;
}

.offscreen-text {
  text-align: center;
  top: -9999px;
}

.text span {
  position: absolute;
}

.author-info-zone {
  font-family: 'Rubik Mono One', sans-serif;
  position: fixed;
  right: 10px;
  bottom: -5px;
}
</style>