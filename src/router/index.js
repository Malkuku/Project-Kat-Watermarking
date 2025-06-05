import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '@/components/home/home.vue'
import ExtractImageView from '@/components/extractImage/extractImage.vue'
import GenerateImageView from '@/components/generateImage/generateImage.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes:[
        {
            path: '/',
            name: "home",
            component: HomeView,
            children:[
                {path: '/watermark/generate/png',name:"generateImage",component:GenerateImageView},
                {path: '/watermark/extract/png',name:"extractImage",component:ExtractImageView}
            ]
        }
    ]
})

export default router