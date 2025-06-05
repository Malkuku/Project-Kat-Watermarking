import request from '@/utils/request.js'

export const generatePngTextApi = (formData) => request.post('watermark/generate/png/text',formData,{
    headers:{
        'Content-Type': 'multipart/form-data'
    },
});

export const extractPngTextApi = (formData) => request.post('watermark/extract/png/text',formData,{
    headers:{
        'Content-Type': 'multipart/form-data'
    }
});

export const generatePngFileApi = (formData) => request.post('watermark/generate/png/file',formData,{
    headers:{
        'Content-Type': 'multipart/form-data'
    }
});

export const extractPngFileApi = (formData) => request.post('watermark/extract/png/file',formData,{
    headers:{
        'Content-Type': 'multipart/form-data'
    }
});
