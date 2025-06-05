<script setup>
import {CoffeeCup, Document, Setting} from "@element-plus/icons-vue";
import router from "@/router/index.js";

</script>

<template>
  <div class="common-layout" style="height: 500px">
    <el-container>
      <el-header class ="el-header">
        Kat数字水印处理工具<el-icon><CoffeeCup /></el-icon>
      </el-header>
      <el-container>
        <el-aside class="el-aside" width="200px">
          <el-menu
              router
              default-active="2"
              background-color="#545c64"
              text-color="#fff"
              active-text-color="#ffd04b"
          >
            <el-menu-item index="/watermark/generate/png">
              <el-icon><document /></el-icon>
              <template #title>图片处理</template>
            </el-menu-item>
            <el-menu-item index="/watermark/extract/png">
              <el-icon><document /></el-icon>
              <template #title>图片解析</template>
            </el-menu-item>
            <el-menu-item index="3">
              <el-icon><setting /></el-icon>
              <template #title>设置</template>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.common-layout .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}
</style>