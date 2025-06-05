<script setup>
import {ref} from 'vue'
import {extractPngTextApi} from "@/api/watermark.js"
import {ElNotification} from 'element-plus'
import { UploadFilled, Right } from '@element-plus/icons-vue'

// 解析水印相关
const extractForm = ref({
  imageFile: null
});
const extractImagePreview = ref('');
const extractedText = ref('');
const isLoading = ref(false);

// 处理解析水印的图片上传
const handleExtractImageUpload = (file) => {
  extractForm.value.imageFile = file.raw;
  extractImagePreview.value = URL.createObjectURL(file.raw);
  extractedText.value = ''; // 清空之前解析的文本
  return false; // 阻止自动上传
};

// 解析水印
const extractWatermark = async () => {
  if (!extractForm.value.imageFile) {
    ElNotification.warning('请上传含有水印的图片');
    return;
  }

  isLoading.value = true;

  const formData = new FormData();
  formData.append('imageFile', extractForm.value.imageFile);

  try {
    const { data } = await extractPngTextApi(formData);
    if (data.code) {
      extractedText.value = data.data;
      ElNotification.success('水印解析成功');
    } else {
      ElNotification.error(data.msg || '水印解析失败');
    }
  } catch (error) {
    ElNotification.error('水印解析失败: ' + (error.response?.data?.msg || error.message));
  } finally {
    isLoading.value = false;
  }
};

// 清空解析表单
const clearExtractForm = () => {
  extractForm.value = { imageFile: null };
  extractImagePreview.value = '';
  extractedText.value = '';
};
</script>

<template>
  <div class="watermark-container">
    <!-- 左侧上传区域 -->
    <div class="left-section">
      <h3>上传含有水印的图片</h3>
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
            <img :src="extractImagePreview" class="preview-image"  alt=""/>
          </div>
        </el-upload>
      </div>

      <div class="control-section">
        <div class="action-buttons">
          <el-button
              type="primary"
              @click="extractWatermark"
              :loading="isLoading"
              class="extract-button"
          >
            {{ isLoading ? '解析中...' : '解析水印' }}
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
      <h3>水印解析结果</h3>
      <div class="result-box">
        <div v-if="extractedText" class="text-result">
          <el-input
              type="textarea"
              :rows="8"
              v-model="extractedText"
              readonly
              resize="none"
              class="result-textarea"
          />
        </div>
        <div v-else class="result-placeholder">
          <p>水印解析结果将显示在这里</p>
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
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
}

.text-result {
  padding: 15px;
  flex: 1;
  display: flex;
}

.result-textarea {
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