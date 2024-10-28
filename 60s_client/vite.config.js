// vite.config.js
import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  // 添加其他配置项
  build: {
    // 输出目录
    outDir: 'dist',
    // 是否生成源代码映射文件
    sourcemap: true,
    // 打包后的静态资源前缀
    assetsDir: 'static',
    // 是否开启压缩
    minify: true,
    // 打包后的文件名
    rollupOptions: {
      input: 'index.html',
    },
  },
  // 服务器配置
  server: {
    host: 'localhost',
    port: 5173,
    open: true,
    https: false,
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, ''),
      },
    },
  },
  // 其他配置
  optimizeDeps: {
    include: ['vue'],
  },
});
