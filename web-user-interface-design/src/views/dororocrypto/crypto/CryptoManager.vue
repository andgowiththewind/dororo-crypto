<template>
  <div>
    <el-container v-cloak class="top-container-clazz">
      <el-aside v-drag style="width: 250px;">
        <AsideMenu/>
      </el-aside>
      <el-container>
        <el-header style="height: 50px;">
          <!--Header-->
        </el-header>
        <el-main style="padding: 5px !important;">
          <CenterContent/>
        </el-main>
        <el-footer style="height: 50px;">
          <!--Footer-->
        </el-footer>
      </el-container>
    </el-container>

    <div class="author-info-zone">
      <p>
        <span style="color:#4aa3fb;">© 2024 </span>
        <span style="color:#fc0000;">dororo. </span>
        <span style="color:#EB547D;">All </span>
        <span style="color:#9F6AA7;">rights </span>
        <span style="color:#5476B3;">reserved. </span>
      </p>
    </div>

  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import AsideMenu from '@/views/dororocrypto/crypto/aside/AsideMenu.vue';
import CenterContent from '@/views/dororocrypto/crypto/center/CenterContent.vue';

export default {
  // 请注意这里的.vue后缀需要去掉
  name: "CryptoManager",
  components: {AsideMenu, CenterContent},
  data() {
    return {}
  },
  methods: {
    devCheck() {
      if (process.env.NODE_ENV === 'development') {
        Notification({title: '环境提示', message: '当前处于开发模式', type: 'warning', duration: 2000, position: 'bottom-right'});
      }
    },
  },
  watch: {},
  mounted() {
    this.devCheck();
  },
}
</script>

<style scoped>
[v-cloak] {
  display: none;
}

/* ===============================================[测试阶段颜色区分][start]======================================================== */
.el-header, .el-footer {
  background-color: #B3C0D1;
  color: #333;
}

.el-aside {
  background-color: #D3DCE6;
  color: #333;
}

.el-main {
  background-color: #E9EEF3;
  color: #333;
}

/* ===============================================[测试阶段颜色区分][end  ]======================================================== */


.top-container-clazz {
  overflow: hidden;
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  margin: 0px;
}


.author-info-zone {
  font-family: 'Rubik Mono One', sans-serif;
  position: fixed;
  right: 10px;
  bottom: -3px;
}
</style>