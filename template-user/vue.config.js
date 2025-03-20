const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    proxy: {
      '/admin': {
        target: 'http://localhost:8080', // 后端服务器的地址
        changeOrigin: true,
        pathRewrite: {
          '^/user': '/user' // 重写路径，去掉前缀
        }
      }
    }
  },
});
