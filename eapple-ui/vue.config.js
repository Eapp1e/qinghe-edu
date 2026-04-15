'use strict'
const path = require('path')

function resolve(dir) {
  return path.join(__dirname, dir)
}

const CompressionPlugin = require('compression-webpack-plugin')

const name = process.env.VUE_APP_TITLE || '鑻ヤ緷绠＄悊绯荤粺' // 缃戦〉鏍囬

const baseUrl = 'http://localhost:8080' // 鍚庣鎺ュ彛

const port = process.env.port || process.env.npm_config_port || 80 // 绔彛

// vue.config.js 閰嶇疆璇存槑
//瀹樻柟vue.config.js 鍙傝€冩枃妗?https://cli.vuejs.org/zh/config/#css-loaderoptions
// 杩欓噷鍙垪涓€閮ㄥ垎锛屽叿浣撻厤缃弬鑰冩枃妗?
module.exports = {
  // 閮ㄧ讲鐢熶骇鐜鍜屽紑鍙戠幆澧冧笅鐨刄RL銆?
  // 榛樿鎯呭喌涓嬶紝Vue CLI 浼氬亣璁句綘鐨勫簲鐢ㄦ槸琚儴缃插湪涓€涓煙鍚嶇殑鏍硅矾寰勪笂
  // 渚嬪 https://www.gitee.com/eapp1e/edu-after-school/銆傚鏋滃簲鐢ㄨ閮ㄧ讲鍦ㄤ竴涓瓙璺緞涓婏紝浣犲氨闇€瑕佺敤杩欎釜閫夐」鎸囧畾杩欎釜瀛愯矾寰勩€備緥濡傦紝濡傛灉浣犵殑搴旂敤琚儴缃插湪 https://www.gitee.com/eapp1e/edu-after-school/admin/锛屽垯璁剧疆 baseUrl 涓?/admin/銆?
  publicPath: process.env.NODE_ENV === "production" ? "/" : "/",
  // 鍦╪pm run build 鎴?yarn build 鏃?锛岀敓鎴愭枃浠剁殑鐩綍鍚嶇О锛堣鍜宐aseUrl鐨勭敓浜х幆澧冭矾寰勪竴鑷达級锛堥粯璁ist锛?
  outputDir: 'dist',
  // 鐢ㄤ簬鏀剧疆鐢熸垚鐨勯潤鎬佽祫婧?(js銆乧ss銆乮mg銆乫onts) 鐨勶紱锛堥」鐩墦鍖呬箣鍚庯紝闈欐€佽祫婧愪細鏀惧湪杩欎釜鏂囦欢澶逛笅锛?
  assetsDir: 'static',
  // 濡傛灉浣犱笉闇€瑕佺敓浜х幆澧冪殑 source map锛屽彲浠ュ皢鍏惰缃负 false 浠ュ姞閫熺敓浜х幆澧冩瀯寤恒€?
  productionSourceMap: false,
  transpileDependencies: ['quill'],
  // webpack-dev-server 鐩稿叧閰嶇疆
  devServer: {
    host: '0.0.0.0',
    port: port,
    open: true,
    proxy: {
      // detail: https://cli.vuejs.org/config/#devserver-proxy
      [process.env.VUE_APP_BASE_API]: {
        target: baseUrl,
        changeOrigin: true,
        pathRewrite: {
          ['^' + process.env.VUE_APP_BASE_API]: ''
        }
      },
      // springdoc proxy
      '^/v3/api-docs/(.*)': {
        target: baseUrl,
        changeOrigin: true
      }
    },
    disableHostCheck: true
  },
  css: {
    loaderOptions: {
      sass: {
        sassOptions: { outputStyle: "expanded" }
      }
    }
  },
  configureWebpack: {
    name: name,
    resolve: {
      alias: {
        '@': resolve('src')
      }
    },
    plugins: [
      // 静态资源 gzip 压缩配置说明
      new CompressionPlugin({
        cache: false,                                  // 涓嶅惎鐢ㄦ枃浠剁紦瀛?
        test: /\.(js|css|html|jpe?g|png|gif|svg)?$/i,  // 鍘嬬缉鏂囦欢鏍煎紡
        filename: '[path][base].gz[query]',            // 鍘嬬缉鍚庣殑鏂囦欢鍚?
        algorithm: 'gzip',                             // 浣跨敤gzip鍘嬬缉
        minRatio: 0.8,                                 // 鍘嬬缉姣斾緥锛屽皬浜?80% 鐨勬枃浠朵笉浼氳鍘嬬缉
        deleteOriginalAssets: false                    // 鍘嬬缉鍚庡垹闄ゅ師鏂囦欢
      })
    ],
  },
  chainWebpack(config) {
    config.plugins.delete('preload') // TODO: need test
    config.plugins.delete('prefetch') // TODO: need test

    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/assets/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/assets/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()

    config.when(process.env.NODE_ENV !== 'development', config => {
          config
            .plugin('ScriptExtHtmlWebpackPlugin')
            .after('html')
            .use('script-ext-html-webpack-plugin', [{
            // `runtime` must same as runtimeChunk name. default is `runtime`
              inline: /runtime\..*\.js$/
            }])
            .end()

          config.optimization.splitChunks({
            chunks: 'all',
            cacheGroups: {
              libs: {
                name: 'chunk-libs',
                test: /[\\/]node_modules[\\/]/,
                priority: 10,
                chunks: 'initial' // only package third parties that are initially dependent
              },
              elementUI: {
                name: 'chunk-elementUI', // split elementUI into a single package
                test: /[\\/]node_modules[\\/]_?element-ui(.*)/, // in order to adapt to cnpm
                priority: 20 // the weight needs to be larger than libs and app or it will be packaged into libs or app
              },
              commons: {
                name: 'chunk-commons',
                test: resolve('src/components'), // can customize your rules
                minChunks: 3, //  minimum common number
                priority: 5,
                reuseExistingChunk: true
              }
            }
          })
          config.optimization.runtimeChunk('single')
    })
  }
}

