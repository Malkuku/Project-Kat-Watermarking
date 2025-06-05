<script setup>
import { ref, computed, watch } from 'vue'
import {generatePngTextApi} from "@/api/watermark.js";
import {ElNotification} from 'element-plus';
import { UploadFilled, Download, Right } from '@element-plus/icons-vue'

// 生成水印数据
const generateForm = ref({
  imageFile: null,
  text: '',
})
const generateImagePreview = ref('');
const generatedWatermarkImage = ref('');
const isLoading = ref(false);
const imageDimensions = ref({ width: 0, height: 0 });
const currentBytes = ref(0);

// 计算最大可容纳字节数
const maxBytes = computed(() => {
  if (imageDimensions.value.width && imageDimensions.value.height) {
    return Math.floor((imageDimensions.value.width * imageDimensions.value.height * 3) / 8);
  }
  return 0;
});

// 监听文本变化，实时计算字节数
watch(() => generateForm.value.text, (newText) => {
  currentBytes.value = new TextEncoder().encode(newText).length;
});

// 处理上传
const handleGenerateImageUpload = (imageFile) => {
  generateForm.value.imageFile = imageFile.raw;
  generateImagePreview.value = URL.createObjectURL(imageFile.raw);
  currentBytes.value = new TextEncoder().encode(generateForm.value.text).length;

  // 获取图片尺寸
  const img = new Image();
  img.onload = () => {
    imageDimensions.value = {
      width: img.width,
      height: img.height
    };
  };
  img.src = URL.createObjectURL(imageFile.raw);

  return false;
}

// 生成水印
const generateWatermark = async () => {
  if (!generateForm.value.imageFile) {
    ElNotification.warning('请上传图片');
    return;
  }

  if (!generateForm.value.text) {
    ElNotification.warning('请输入水印文本');
    return;
  }

  // 检查文本是否超过最大容量
  if (currentBytes.value > maxBytes.value) {
    ElNotification.warning(`水印文本过大 (${currentBytes.value} bytes)。当前图片最多可容纳 ${maxBytes.value} bytes`);
    return;
  }

  isLoading.value = true;

  const formData = new FormData();
  formData.append('imageFile', generateForm.value.imageFile);
  formData.append('text', generateForm.value.text);

  try {
    const { data } = await generatePngTextApi(formData);

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
  generateForm.value = { imageFile: null, text: '' };
  generateImagePreview.value = '';
  generatedWatermarkImage.value = '';
  imageDimensions.value = { width: 0, height: 0 };
  currentBytes.value = 0;
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
            :on-change="handleGenerateImageUpload"
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

      <div class="control-section">
        <el-input
            v-model="generateForm.text"
            placeholder="输入水印文本内容"
            class="watermark-input"
            clearable
            show-word-limit
        />
        <div class="byte-info">
          <span v-if="maxBytes > 0">
            已使用: {{ currentBytes }} / {{ maxBytes }} bytes
            <span v-if="currentBytes > maxBytes" class="warning-text">(超出容量限制!)</span>
          </span>
          <span v-else>请先上传图片</span>
        </div>

        <div class="action-buttons">
          <el-button
              type="primary"
              @click="generateWatermark"
              :loading="isLoading"
              class="generate-button"
              :disabled="currentBytes > maxBytes || !generateForm.text"
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

.warning-text {
  color: var(--el-color-danger);
  font-weight: bold;
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

.watermark-input {
  width: 100%;
}

.byte-info {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 5px;
  text-align: right;
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