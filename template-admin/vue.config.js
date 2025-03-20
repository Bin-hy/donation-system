const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8082, // 前端开发服务器端口
    proxy: {
      // 代理 /admin 路径
      '/admin': {
        target: 'http://localhost:8080', // 后端服务器的地址
        changeOrigin: true, // 允许跨域
        pathRewrite: {
          '^/admin': '/admin' // 重写路径，去掉前缀
        }
      },
      // 代理 /files 路径
      '/files': {
        target: 'http://localhost:8080', // 后端服务器的地址
        changeOrigin: true, // 允许跨域
        pathRewrite: {
          '^/files': '/files' // 重写路径，去掉前缀
        }
      }
    }
  },
});
