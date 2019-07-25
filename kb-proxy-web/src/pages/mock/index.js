//import MockCatalog from './mock_catalog'

import CompMockCatalog from './comp_mock_catalog'



export default {
  name: 'mock_index',
  data: () => ({}),
  methods: {},
  render(h) {
    return h('div', {}, [h(CompMockCatalog)])
  }
}
