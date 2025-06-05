import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '@/components/home/home.vue'
import ExtractTextView from '@/components/extractImage/text.vue'
import GenerateTextView from '@/components/generateImage/text.vue'
import ExtractFileView from '@/components/extractImage/file.vue'
import GenerateFileView from '@/components/generateImage/file.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes:[
        {
            path: '/',
            name: "home",
            component: HomeView,
            children:[
                {path: '/watermark/generate/png/text',name:"generateImage",component:GenerateTextView},
                {path: '/watermark/extract/png/text',name:"extractImage",component:ExtractTextView},
                {path: '/watermark/generate/png/file',name:"generateFile",component:GenerateFileView},
                {path: '/watermark/extract/png/file',name:"extractFile",component:ExtractFileView},
            ]
        }
    ]
})

export default router