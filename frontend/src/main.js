import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { 
  faTemperatureLow, faWind, faHotTub, faSink,
  faBurn, faWater, faBox,
  faSnowflake, faFan, faFire, 
  faSoap, faGasPump, faDroplet, 
  faBoxOpen, faTintSlash, faRotate ,
  faSun, faMoon ,faPlay, faPause,
  faMusic
} from '@fortawesome/free-solid-svg-icons'

// 添加 CSS 导入
import './assets/main.css';

library.add(faSnowflake, faFan, faFire, faSoap, faGasPump, 
  faDroplet, faBoxOpen, faTintSlash, faRotate,
  faTemperatureLow, faWind, faHotTub, faSink,
  faBurn, faWater, faBox,faSun, faMoon, faPlay, faPause, faMusic // 添加到库
   )


createApp(App)
  .use(router)
  .component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')