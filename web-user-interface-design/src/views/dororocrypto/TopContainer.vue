<template>
  <div>
    <CryptoManager v-if="true"></CryptoManager>
    <WelcomePage v-if="false"></WelcomePage>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import CryptoManager from '@/views/dororocrypto/crypto/CryptoManager.vue';
import WelcomePage from '@/views/dororocrypto/welcome/WelcomePage.vue';

export default {
  name: "TopContainer",
  components: {
    CryptoManager, WelcomePage
  },
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
</style>