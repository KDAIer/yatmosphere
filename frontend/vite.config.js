import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  server: {
    proxy: {
      '/auth': {
        target: 'http://localhost:81', // 后端地址（默认端口 80 可省略）
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/auth/, '/auth') // 如有需要可去掉 /auth 前缀
      },
      '/api': {  // 新增对 /api 的代理配置
        target: 'http://localhost:81',
        changeOrigin: true,
        // 根据你后端接口实际路径，如果不需要改路径就不用 rewrite
        rewrite: (path) => path.replace(/^\/api/, '/api')
      },
      '/aircon': {
        target: 'http://localhost:81',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/aircon/, '/aircon')
      },

        '/device': {
        target: 'http://localhost:81',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/device/, '/device')
      }
    }
  },
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
