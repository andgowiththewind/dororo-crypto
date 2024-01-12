<template>
  <div>
    <div class="loading-group" ref="loadingGroup">
      <div v-if="bingoNum===1">
        <Loading001/>
      </div>
      <div v-if="bingoNum===2">
        <Loading002/>
      </div>
      <div v-if="bingoNum===3">
        <Loading003/>
      </div>
      <div v-if="bingoNum===4">
        <Loading004/>
      </div>
      <div v-if="bingoNum===5">
        <Loading005/>
      </div>
      <div v-if="bingoNum===6">
        <Loading006/>
      </div>
      <div v-if="bingoNum===7">
        <Loading007/>
      </div>
      <div v-if="bingoNum===8">
        <Loading008/>
      </div>
      <div v-if="bingoNum===9">
        <Loading009/>
      </div>
      <div v-if="bingoNum===10">
        <Loading010/>
      </div>
      <div v-if="bingoNum===11">
        <Loading011/>
      </div>
      <div v-if="bingoNum===12">
        <Loading012/>
      </div>


    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import * as methodConsts from '@/config/methodConsts';


import Loading001 from '@/views/dororocrypto/welcome/Loading001.vue';
import Loading002 from "@/views/dororocrypto/welcome/Loading002.vue";
import Loading003 from "@/views/dororocrypto/welcome/Loading003.vue";
import Loading004 from "@/views/dororocrypto/welcome/Loading004.vue";
import Loading005 from "@/views/dororocrypto/welcome/Loading005.vue";
import Loading006 from "@/views/dororocrypto/welcome/Loading006.vue";
import Loading007 from "@/views/dororocrypto/welcome/Loading007.vue";
import Loading008 from "@/views/dororocrypto/welcome/Loading008.vue";
import Loading009 from "@/views/dororocrypto/welcome/Loading009.vue";
import Loading010 from "@/views/dororocrypto/welcome/Loading010.vue";
import Loading011 from "@/views/dororocrypto/welcome/Loading011.vue";
import Loading012 from "@/views/dororocrypto/welcome/Loading012.vue";


export default {
  name: "MillionLoading",
  components: {
    Loading001,
    Loading002,
    Loading003,
    Loading004,
    Loading005,
    Loading006,
    Loading007,
    Loading008,
    Loading009,
    Loading010,
    Loading011,
    Loading012,
  },
  data() {
    return {
      bingoNum: null,
    }
  },
  methods: {
    handleBingoNum() {
      if (!localStorage.getItem('bingoNum')) {
        this.bingoNum = 1;
        localStorage.setItem('bingoNum', 1);
      } else {
        let num = parseInt(localStorage.getItem('bingoNum')) + 1;
        if (num > 12) {
          num = 1;
        }
        localStorage.setItem('bingoNum', num);
        this.bingoNum = num;
      }
    },
    handleClickOutside(event) {
      // 检查点击是否在指定 div 或其子元素内
      if (this.$refs.loadingGroup && this.$refs.loadingGroup.contains(event.target)) {
        // 执行操作
        console.log('Clicked inside the div');

        this.$bus.$emit(methodConsts.LOADING_JUMP_CRYPTO, null);

      } else {
        // 显示提示信息
        // console.log('Clicked outside the div');
      }
    },
  },
  watch: {},
  mounted() {
    this.handleBingoNum();
    // 全局监听点击事件
    document.addEventListener('click', this.handleClickOutside);

    this.$bus.$on(methodConsts.LOADING_BINGO_NUM_CHANGE, (data) => {
      this.handleBingoNum();
    });
  },
  beforeDestroy() {
    // 全局监听点击事件
    document.removeEventListener('click', this.handleClickOutside);
  },
}
</script>

<style scoped>

</style>