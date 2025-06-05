<script setup>
import { ref } from 'vue'
import { extractPngFileApi } from "@/api/watermark.js"
import { ElNotification } from 'element-plus'
import { UploadFilled, Right } from '@element-plus/icons-vue'
import { fileTypeFromBlob } from 'file-type' // 引入文件类型检测库

// 解析文件水印相关
const extractForm = ref({
  imageFile: null,
})
const extractImagePreview = ref('')
const extractedFile = ref(null)
const isLoading = ref(false)

// 处理解析文件水印的图片上传
const handleExtractImageUpload = (file) => {
  if (file.raw.type !== 'image/png') {
    ElNotification.error('请上传PNG格式的图片')
    return false
  }

  extractForm.value.imageFile = file.raw
  extractImagePreview.value = URL.createObjectURL(file.raw)
  extractedFile.value = null // 清空之前解析的文件
  return false // 阻止自动上传
}

// 解析文件水印
const extractFileWatermark = async () => {
  if (!extractForm.value.imageFile) {
    ElNotification.warning('请上传含有水印的PNG图片')
    return
  }

  isLoading.value = true

  const formData = new FormData()
  formData.append('imageFile', extractForm.value.imageFile)

  try {
    const { data } = await extractPngFileApi(formData)
    if (data.code) {
      const fileData = data.data
      const byteCharacters = atob(fileData)
      const byteArray = new Uint8Array(byteCharacters.length)
      for (let i = 0; i < byteCharacters.length; i++) {
        byteArray[i] = byteCharacters.charCodeAt(i)
      }

      // 创建 Blob 并检测文件类型
      const blob = new Blob([byteArray])
      const detectedType = await fileTypeFromBlob(blob)

      // 使用检测到的类型（如果检测不到，默认用 'bin'）
      const fileExt = detectedType?.ext || 'bin'
      const mimeType = detectedType?.mime || 'application/octet-stream'

      // 创建文件对象
      extractedFile.value = new File([blob], `extracted.${fileExt}`, {
        type: mimeType,
      })

      ElNotification.success('文件水印解析成功')
    } else {
      ElNotification.error(data.msg || '文件水印解析失败')
    }
  } catch (error) {
    ElNotification.error('文件水印解析失败: ' + (error.response?.data?.msg || error.message))
  } finally {
    isLoading.value = false
  }
}

// 下载提取的文件
const downloadExtractedFile = () => {
  if (!extractedFile.value) return

  const url = URL.createObjectURL(extractedFile.value)
  const a = document.createElement('a')
  a.href = url
  a.download = extractedFile.value.name
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

// 清空解析表单
const clearExtractForm = () => {
  extractForm.value = { imageFile: null }
  extractImagePreview.value = ''
  extractedFile.value = null
}
</script>

<template>
  <div class="watermark-container">
    <!-- 左侧上传区域 -->
    <div class="left-section">
      <h3>上传含有文件水印的PNG图片</h3>
      <div class="upload-box">
        <el-upload
            action=""
            drag
            :auto-upload="false"
            :on-change="handleExtractImageUpload"
            :show-file-list="false"
            accept="image/png"
        >
          <div v-if="!extractImagePreview" class="upload-empty">
            <el-icon class="upload-icon"><upload-filled /></el-icon>
            <div class="upload-text">
              <p>点击或拖拽PNG图片到此处</p>
              <p class="upload-tip">仅支持PNG格式图片</p>
            </div>
          </div>
          <div v-else class="image-preview">
            <img :src="extractImagePreview" class="preview-image" alt="" />
          </div>
        </el-upload>
      </div>

      <div class="control-section">
        <div class="action-buttons">
          <el-button
              type="primary"
              @click="extractFileWatermark"
              :loading="isLoading"
              class="extract-button"
          >
            {{ isLoading ? '解析中...' : '解析文件水印' }}
          </el-button>
          <el-button @click="clearExtractForm">清空</el-button>
        </div>
      </div>
    </div>

    <!-- 中间箭头 -->
    <div class="arrow-section">
      <el-icon class="arrow-icon"><Right /></el-icon>
    </div>

    <!-- 右侧结果区域 -->
    <div class="right-section">
      <h3>文件水印解析结果</h3>
      <div class="result-box">
        <div v-if="extractedFile" class="file-result">
          <div class="file-info">
            <p class="file-name">{{ extractedFile.name }}</p>
            <p class="file-type">类型: {{ extractedFile.type }}</p>
            <p class="file-size">大小: {{ (extractedFile.size / 1024).toFixed(2) }} KB</p>
          </div>
          <el-button
              type="primary"
              @click="downloadExtractedFile"
              class="download-button"
          >
            下载文件
          </el-button>
        </div>
        <div v-else class="result-placeholder">
          <p>文件水印解析结果将显示在这里</p>
          <p class="placeholder-tip">请先上传PNG图片</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.watermark-container {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.left-section, .right-section {
  flex: 1;
  min-width: 0;
}

h3 {
  margin-bottom: 15px;
  color: var(--el-text-color-primary);
  text-align: center;
}

.upload-box, .result-box {
  height: 300px;
  border: 1px dashed var(--el-border-color);
  border-radius: 4px;
  background-color: #fafafa;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.upload-empty {
  padding: 40px 20px;
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.image-preview {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.upload-icon {
  font-size: 48px;
  color: var(--el-color-primary);
  margin-bottom: 10px;
}

.upload-text {
  font-size: 16px;
  color: var(--el-text-color-regular);
}

.upload-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 5px;
}

.control-section {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.extract-button {
  flex: 1;
}

.arrow-section {
  display: flex;
  align-items: center;
  height: 300px;
  padding: 0 10px;
}

.arrow-icon {
  font-size: 40px;
  color: var(--el-color-primary);
}

.result-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
}

.placeholder-tip {
  font-size: 12px;
  margin-top: 5px;
  color: var(--el-text-color-placeholder);
}

.file-result {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.file-info {
  text-align: center;
}

.file-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

.file-type, .file-size {
  font-size: 14px;
  color: var(--el-text-color-regular);
  margin-bottom: 5px;
}

.download-button {
  width: 100%;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .watermark-container {
    flex-direction: column;
  }

  .arrow-section {
    height: auto;
    padding: 10px 0;
    justify-content: center;
    transform: rotate(90deg);
  }

  .upload-box, .result-box {
    height: 250px;
  }
}
</style>