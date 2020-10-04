module.exports = {
  assetsDir: "static",
  outputDir: "../mask-map-web/src/main/resources",
  indexPath: "templates/index.html",
  devServer: {
    proxy: 'http://127.0.0.1:80',
    // https: true
  }
};