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
  faBoxOpen, faTintSlash, faRotate  
} from '@fortawesome/free-solid-svg-icons'

library.add(faSnowflake, faFan, faFire, faSoap, faGasPump, 
  faDroplet, faBoxOpen, faTintSlash, faRotate,
  faTemperatureLow, faWind, faHotTub, faSink,
  faBurn, faWater, faBox )


createApp(App)
  .use(router)
  .mount('#app')