import Vue from 'vue'
import App from './App.vue'
import router from './router'
import naver from 'vue-naver-maps'
import ViewUI from 'view-design';
import 'view-design/dist/styles/iview.css';
import axios from 'axios'

Vue.use(ViewUI)
Vue.use(naver, {
  clientID: 'ria2swzxl4',
  useGovAPI: false,
  subModules: ''
})

Vue.config.productionTip = false
Vue.prototype.$http = axios

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
