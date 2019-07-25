import Quasar, * as All from 'quasar-framework/dist/quasar.mat.esm'
import 'quasar-framework/dist/umd/quasar.mat.min.css'
import 'quasar-extras/material-icons'

export default ({Vue}) => {

  Vue.use(Quasar, {
    components: {...All},
    directives: All,
    plugins: All,
  });


  Vue.prototype.$q.err = (message, err) => {
    Vue.prototype.$q.notify({
      message: message,
      timeout: 1000,
      type: 'negative',
      position: 'top'
    })
  };
  Vue.prototype.$q.ok = (message) => {
    Vue.prototype.$q.notify({
      message: message,
      timeout: 1000,
      type: 'positive',
      color: 'secondary',
      position: 'top'
    })
  };
}
