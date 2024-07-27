<template>
  <div>
    <div v-if="cryptoManagerShow" key="crypto-manager">
      <CryptoManager/>
    </div>
    <div v-if="welcomePageShow" key="welcome-page">
      <WelcomePage/>
    </div>
    <div v-if="millionLoadingShow" key="million-loading">
      <MillionLoading/>
    </div>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import axios from "axios";
import CryptoManager from '@/views/dororocrypto/crypto/CryptoManager.vue';
import WelcomePage from '@/views/dororocrypto/welcome/WelcomePage.vue';
import MillionLoading from '@/views/dororocrypto/welcome/MillionLoading.vue';
import * as methodConsts from '@/config/methodConsts';
import {devConsoleLog} from "@/utils/dororoUtils";

export default {
  name: "TopContainer",
  components: {
    CryptoManager, WelcomePage, MillionLoading
  },
  data() {
    return {
      welcomePageShow: true,
      millionLoadingShow: false,
      cryptoManagerShow: false,
      //
      heartBeatAlive: false,
      intervalId: null,
    }
  },
  methods: {
    checkHeartBeatInterval() {
      this.intervalId = setInterval(() => {
        let p = axios.get('/sys/checkHeartBeat').then(function (response) {
          return response;
        }).catch(function (error) {
          return error;
        });

        p.then((response) => {
          if (response.status === 200) {
            this.heartBeatAlive = true;
            this.$bus.$emit(methodConsts.WATCH_SERVE_HEART_BEAT, true);
          } else {
            this.heartBeatAlive = false;
            this.$bus.$emit(methodConsts.WATCH_SERVE_HEART_BEAT, false);
          }
        }).catch((error) => {
          this.heartBeatAlive = false;
        });

      }, 1000);
    },
  },
  watch: {},
  mounted() {
    this.checkHeartBeatInterval();

    this.$bus.$on(methodConsts.WELCOME_JUMP_LOADING, (data) => {
      this.welcomePageShow = false;
      this.millionLoadingShow = true;
    });


    this.$bus.$on(methodConsts.LOADING_JUMP_CRYPTO, (data) => {
      if (this.heartBeatAlive) {
        this.millionLoadingShow = false;
        clearInterval(this.intervalId);
        console.clear();
        this.cryptoManagerShow = true;
      } else {
        // Notification({title: '提示', message: '服务端未启动，请先启动服务端', type: 'warning', duration: 2000, position: 'bottom-right',});
        this.$bus.$emit(methodConsts.LOADING_BINGO_NUM_CHANGE, null);
      }

    });

  },
  beforeDestroy() {
    clearInterval(this.intervalId);
  },
}
</script>

<style scoped>
</style>

