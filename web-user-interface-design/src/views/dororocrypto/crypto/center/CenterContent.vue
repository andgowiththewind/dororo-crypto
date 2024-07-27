<template>
  <div>
    <ConditionalPagination/>
    <InsightTable/>
    <ProcessTable/>
  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import * as methodConsts from '@/config/methodConsts';
import {devConsoleLog} from '@/utils/dororoUtils';
import ConditionalPagination from '@/views/dororocrypto/crypto/center/ConditionalPagination.vue';
import InsightTable from '@/views/dororocrypto/crypto/center/InsightTable.vue';
import ProcessTable from '@/views/dororocrypto/crypto/center/ProcessTable.vue';


export default {
  // 请注意这里的.vue后缀需要去掉
  name: "CenterContent",
  components: {ConditionalPagination, InsightTable, ProcessTable},
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
/* .foot-text {color: rgba(0, 0, 0, 0.15);position: fixed;bottom: 30px;left: 35vw;} */
</style>