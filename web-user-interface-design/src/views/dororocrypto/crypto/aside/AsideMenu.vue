<template>
  <div>
    <!--part01：点击按钮展示弹出层-->
    <el-button type="warning" size="mini" style="width: 100%;height: 60px;" @click="dialogVo.show=true">
      <i class="fa fa-hand-pointer-o">&nbsp;输入路径获取目录树</i>
    </el-button>

    <!--part02：zTree组件-->
    <ul id="myTree" class="ztree"></ul>

    <!--part03：弹出层输入目录地址-->
    <el-dialog
        width="99%"
        top="60vh"
        :center="true"
        :show-close="false"
        title="直接输入或选择一个目录"
        :close-on-click-modal="true"
        :visible.sync="dialogVo.show"
        custom-class="folderPathDialogClazz">
      <div desc="dialog-content">
        <el-select v-model="topDirectoryPath" placeholder="请选择或输入目录地址" :style="{width:'100%'}" clearable filterable allow-create>
          <el-option
              v-for="item in topDirectoryPathOptions"
              :key="item.value"
              :label="item.label"
              :value="item.label">
          </el-option>
        </el-select>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="warning" :style="{marginTop:'5px'}" @click="folderAbsPathSubmit">确认</el-button>
        <el-button type="primary" @click="dialogVo.show = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {Notification, MessageBox, Message, Loading} from 'element-ui';
import $ from '@/utils/jquery-vendor';
import '@/assets/ztree/js/jquery.ztree.core.js';
import '@/assets/ztree/css/zTreeStyle/zTreeStyle.css';
import {devConsoleLog, getSysParam} from '@/utils/dororoUtils';
import {getTreeData} from '@/api/asideMenuApi';
import * as methodConsts from '@/config/methodConsts';

export default {
  // 请注意这里的.vue后缀需要去掉
  name: "AsideMenu",
  components: {},
  data() {
    return {
      dialogVo: {
        show: false,
      },
      topDirectoryPath: '',
      topDirectoryPathOptions: [],
      treeVo: {
        treeData: [],
        treeSetting: {
          // 指定数据相关配置
          data: {
            simpleData: {
              enable: true,
              idKey: "id",
              pIdKey: "parentId",
              rootPId: 0
            },
          },
          // 指定回调函数,例如单机事件
          callback: {
            onClick: this.zTreeOnClick
          },
        },
      },
    }
  },
  methods: {
    folderAbsPathSubmit() {
      getTreeData({path: this.topDirectoryPath}).then(res => {
        let fixList = res.data.map(item => {
          item.isParent = true;
          return item;
        });
        this.treeVo.treeData = fixList;
        this.$nextTick(() => {
          $.fn.zTree.init($("#myTree"), this.treeVo.treeSetting, this.treeVo.treeData);
        });
        this.dialogVo.show = false;
      }).catch();
    },
    // 树节点单击事件:触发文件递归查找,event:事件对象,treeId:树id,treeNode:树节点对象
    zTreeOnClick(event, treeId, treeNode) {
      this.$bus.$emit(methodConsts.TREE_NODE_CLICK_EVENT, treeNode.absPath);
    },
  },
  watch: {
    'dialogVo.show': function (newVal, oldVal) {
      if (newVal) {
        let promise = new Promise((resolve, reject) => {
          getSysParam('topDirectoryPathOptions').then(res => {
            resolve(res);
          }).catch(error => {
            reject(error);
          });
        });
        promise.then(res => {
          if (res.data && res.data.length > 0) {
            let arr = [];
            for (let i = 0; i < res.data.length; i++) {
              arr.push({label: res.data[i], value: i + 1});
            }
            this.topDirectoryPathOptions = arr;
            this.topDirectoryPath = this.topDirectoryPathOptions[0].label;
          }
        }).catch(error => {
          console.log(error);
        });
        this.dialogVo.show = true;
      }
    },
  },
  mounted() {
  },
}
</script>

<style>
.folderPathDialogClazz {
  border-radius: 9px !important;
  min-height: 34vh !important;
}
</style>