// import CompEnvCatalog from './comp_env_catalog'
// import EnvDetail from './comp_env_detail'

import EnvDetail from './env_info_detail'

export default {
  name: 'env_index',
  data: () => ({}),
  methods: {},
  render(h) {
    return h('div', {
    }, [
      h(EnvDetail)
    ])
  }
}
