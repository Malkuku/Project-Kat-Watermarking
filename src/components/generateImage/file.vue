<script setup>
import { ref, computed } from 'vue'
import { generatePngFileApi } from "@/api/watermark.js";
import { ElNotification } from 'element-plus';
import { UploadFilled, Download, Right } from '@element-plus/icons-vue'

// 生成水印数据
const generateForm = ref({
  imageFile: null,
  embedFile: null,
})
const generateImagePreview = ref('');
const embedFilePreview = ref('');
const generatedWatermarkImage = ref('');
const isLoading = ref(false);
const imageDimensions = ref({ width: 0, height: 0 });
const embedFileSize = ref(0);

// 计算最大可容纳字节数
const maxBytes = computed(() => {
  if (imageDimensions.value.width && imageDimensions.value.height) {
    return Math.floor((imageDimensions.value.width * imageDimensions.value.height * 3) / 8);
  }
  return 0;
});

// 处理图片上传
const handleImageUpload = (file) => {
  generateForm.value.imageFile = file.raw;
  generateImagePreview.value = URL.createObjectURL(file.raw);

  // 获取图片尺寸
  const img = new Image();
  img.onload = () => {
    imageDimensions.value = {
      width: img.width,
      height: img.height
    };
  };
  img.src = URL.createObjectURL(file.raw);

  return false;
}

// 处理嵌入文件上传
const handleEmbedFileUpload = (file) => {
  generateForm.value.embedFile = file.raw;
  embedFilePreview.value = file.name;
  embedFileSize.value = file.size; // 记录文件大小
  return false;
}

// 生成水印
const generateWatermark = async () => {
  if (!generateForm.value.imageFile) {
    ElNotification.warning('请上传PNG图片');
    return;
  }

  if (!generateForm.value.embedFile) {
    ElNotification.warning('请上传要嵌入的文件');
    return;
  }

  // 检查文件大小是否超过最大容量
  if (embedFileSize.value > maxBytes.value) {
    ElNotification.warning(`文件过大 (${formatFileSize(embedFileSize.value)})。当前图片最多可嵌入 ${formatFileSize(maxBytes.value)}`);
    return;
  }

  isLoading.value = true;

  const formData = new FormData();
  formData.append('imageFile', generateForm.value.imageFile);
  formData.append('file', generateForm.value.embedFile);

  try {
    const { data } = await generatePngFileApi(formData);

    if (data.code === 1) {
      generatedWatermarkImage.value = `data:image/png;base64,${data.data}`;
      ElNotification.success('水印生成成功');
    } else {
      ElNotification.error(data.msg || '水印生成失败');
    }
  } catch (error) {
    ElNotification.error('请求失败: ' + (error.response?.data?.msg || error.message));
  } finally {
    isLoading.value = false;
  }
};

// 格式化文件大小显示
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]);
};

// 下载生成的水印图片
const downloadWatermarkedImage = () => {
  if (!generatedWatermarkImage.value) {
    ElNotification.warning('没有可下载的水印图片');
    return;
  }

  const a = document.createElement('a');
  a.href = generatedWatermarkImage.value;
  a.download = 'watermarked.png';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
};

// 清空生成表单
const clearGenerateForm = () => {
  generateForm.value = { imageFile: null, embedFile: null };
  generateImagePreview.value = '';
  embedFilePreview.value = '';
  generatedWatermarkImage.value = '';
  imageDimensions.value = { width: 0, height: 0 };
  embedFileSize.value = 0;
};
</script>

<template>
  <div class="watermark-container">
    <!-- 左侧上传区域 -->
    <div class="left-section">
      <h3>上传原始图片</h3>
      <div class="upload-box">
        <el-upload
            action=""
            drag
            :auto-upload="false"
            :on-change="handleImageUpload"
            :show-file-list="false"
            accept="image/png"
        >
          <div v-if="!generateImagePreview" class="upload-empty">
            <el-icon class="upload-icon"><upload-filled /></el-icon>
            <div class="upload-text">
              <p>点击或拖拽PNG图片到此处</p>
              <p class="upload-tip">仅支持PNG格式图片</p>
            </div>
          </div>
          <div v-else class="image-preview">
            <img :src="generateImagePreview" class="preview-image"  alt=""/>
          </div>
        </el-upload>
      </div>
      <h3 v-if="generateImagePreview"> 最大容量：{{ maxBytes }} Bytes</h3>
      <h3 v-if="!generateImagePreview" class="embed-title">上传要嵌入的文件</h3>

      <div class="file-upload">
        <el-upload
            action=""
            :auto-upload="false"
            :on-change="handleEmbedFileUpload"
            :show-file-list="false"
            accept=".mp3,.mp4,.png,.txt,.wav"
        >
          <el-button type="primary">
            <el-icon class="el-icon--left"><upload-filled /></el-icon>
            选择文件(MP3/MP4/PNG/TXT/WAV)
          </el-button>
        </el-upload>
        <div v-if="embedFilePreview" class="file-preview">
          <div class="file-info">
            <el-text class="file-name" truncated>{{ embedFilePreview }}</el-text>
            <span class="file-size" :class="{ 'over-limit': embedFileSize > maxBytes && maxBytes > 0 }">
              <span v-if="maxBytes > 0">
                ({{ Math.round((embedFileSize / maxBytes) * 100) }}%)
                <span v-if="embedFileSize > maxBytes" class="warning-text">超出限制!</span>
              </span>
            </span>
          </div>
          <div v-if="maxBytes > 0" class="capacity-bar">
            <div
                class="capacity-fill"
                :style="{ width: `${Math.min(100, (embedFileSize / maxBytes) * 100)}%` }"
                :class="{ 'over-capacity': embedFileSize > maxBytes }"
            ></div>
          </div>
        </div>
      </div>

      <div class="control-section">
        <div class="action-buttons">
          <el-button
              type="primary"
              @click="generateWatermark"
              :loading="isLoading"
              class="generate-button"
              :disabled="embedFileSize > maxBytes || !generateForm.embedFile || !generateForm.imageFile"
          >
            {{ isLoading ? '生成中...' : '生成水印' }}
          </el-button>
          <el-button @click="clearGenerateForm">清空</el-button>
        </div>
      </div>
    </div>

    <!-- 中间箭头 -->
    <div class="arrow-section">
      <el-icon class="arrow-icon"><Right /></el-icon>
    </div>

    <!-- 右侧结果区域 -->
    <div class="right-section">
      <h3>水印生成结果</h3>
      <div class="result-box">
        <div v-if="generatedWatermarkImage" class="image-result">
          <img :src="generatedWatermarkImage" class="result-image"  alt=""/>
        </div>
        <div v-else class="result-placeholder">
          <p>水印生成结果将显示在这里</p>
        </div>
      </div>
      <el-button
          v-if="generatedWatermarkImage"
          type="success"
          @click="downloadWatermarkedImage"
          class="download-button"
      >
        <el-icon><download /></el-icon>
        下载图片
      </el-button>
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

.embed-title {
  margin-top: 20px;
}

.upload-box, .result-box {
  height: 300px;
  border: 1px dashed var(--el-border-color);
  border-radius: 4px;
  background-color: #fafafa;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.file-upload {
  margin-bottom: 20px;
  text-align: center;
}

.file-preview {
  margin-top: 10px;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.file-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.file-name {
  word-break: break-all;
  text-align: left;
}

.file-size {
  margin-left: 10px;
  white-space: nowrap;
}

.file-size.over-limit {
  color: var(--el-color-danger);
  font-weight: bold;
}

.warning-text {
  color: var(--el-color-danger);
}

.capacity-bar {
  width: 100%;
  height: 6px;
  background-color: #ddd;
  border-radius: 3px;
  overflow: hidden;
}

.capacity-fill {
  height: 100%;
  background-color: var(--el-color-primary);
  transition: width 0.3s;
}

.capacity-fill.over-capacity {
  background-color: var(--el-color-danger);
}

.image-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 8px;
  font-size: 12px;
  text-align: center;
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

.image-preview, .image-result {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.preview-image, .result-image {
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

.generate-button {
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
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
}

.download-button {
  width: 100%;
  margin-top: 10px;
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