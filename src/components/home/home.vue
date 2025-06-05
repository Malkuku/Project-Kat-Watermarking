<script setup>
import {CoffeeCup, Document, Setting, Picture, Reading, View, Connection} from "@element-plus/icons-vue";
import {ElNotification} from "element-plus";
</script>

<template>
  <div class="common-layout">
    <el-container style="height: 100vh">
      <!-- 顶栏固定 -->
      <el-header class="el-header">
        <div class="header-content">
          <span class="logo-text">Kat数字水印处理工具</span>
          <el-icon class="logo-icon"><CoffeeCup /></el-icon>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边栏 -->
        <el-aside class="el-aside" width="220px">
          <el-menu
              router
              default-active="2"
              background-color="#2c3e50"
              text-color="#ecf0f1"
              active-text-color="#3498db"
              style="height: calc(100vh - 120px)"
              :collapse-transition="false"
              class="custom-menu"
          >
            <!-- 图片处理-->
            <el-sub-menu index="1">
              <template #title>
                <el-icon class="menu-icon "><Picture /></el-icon>
                <span class="menu-title">图片处理</span>
              </template>
              <el-menu-item index="/watermark/generate/png/text" class="sub-menu-item">
                <el-icon><Reading /></el-icon>
                <span>文本水印</span>
              </el-menu-item>
              <el-menu-item index="/watermark/generate/png/file" class="sub-menu-item">
                <el-icon><Document /></el-icon>
                <span>文件水印</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 图片解析-->
            <el-sub-menu index="2">
              <template #title>
                <el-icon class="menu-icon"><Connection /></el-icon>
                <span class="menu-title">图片解析</span>
              </template>
              <el-menu-item index="/watermark/extract/png/text" class="sub-menu-item">
                <el-icon><Reading /></el-icon>
                <span>文本水印</span>
              </el-menu-item>
              <el-menu-item index="/watermark/extract/png/file" class="sub-menu-item">
                <el-icon><Document /></el-icon>
                <span>文件水印</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 设置 -->
            <el-menu-item
                class="bottom-menu-item"
                @click="() => {
                  ElNotification.info('你点击了系统设置，但是并没有可以设置的东西😺')
                }"
            >
              <el-icon class="menu-icon"><Setting /></el-icon>
              <span class="menu-title">系统设置</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主内容区可滚动 -->
        <el-main class="main-content">
          <router-view></router-view>
        </el-main>
      </el-container>

      <!-- 底栏固定 -->
      <el-footer class="el-footer">
        <div class="footer-content">
          © 2026 Kat数字水印处理工具 - 作者: Malkuku
          <a href="https://github.com/Malkuku" target="_blank">GitHub</a>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<style scoped>
.common-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶栏样式 */
.el-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: linear-gradient(135deg, #3498db, #2c3e50);
  color: white;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  height: 60px;
}

.header-content {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0 20px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  margin-right: 10px;
}

.logo-icon {
  font-size: 24px;
  color: #f1c40f;
}

/* 侧边栏样式 */
.el-aside {
  background-color: #2c3e50;
  height: calc(100vh - 120px);
  position: sticky;
  top: 60px;
  transition: width 0.3s;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.custom-menu {
  border-right: none;
}

.menu-icon {
  margin-right: 12px;
  font-size: 18px;
  color: #ecf0f1;
}

.menu-title {
  font-size: 15px;
  font-weight: 500;
}

.sub-menu-item {
  padding-left: 50px !important;
  margin: 4px 0;
  transition: all 0.3s;
}

.sub-menu-item:hover {
  background-color: rgba(52, 152, 219, 0.2) !important;
}

.bottom-menu-item {
  margin-top: 20px;
  border-top: 1px solid rgba(236, 240, 241, 0.1);
  padding-top: 10px;
}

/* 主内容区样式 */
.main-content {
  overflow: auto;
  height: calc(100vh - 120px);
  padding: 20px;
  background-color: #f5f7fa;
}

/* 底栏样式 */
.el-footer {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  border-top: 1px solid var(--el-border-color);
}

.footer-content {
  font-size: 14px;
  color: #666;
}
</style>