import CompRequestCatalog from './comp_request_catalog'
import CompToolsHeader from './comp_tools_header'
import ModalRequestDetail from './modal_request_detail'

export default {
  name: 'home',
  data: () => ({}),
  methods: {},
  render(h) {
    return h('div', {staticClass:'relative-position'}, [
      h(CompToolsHeader, {
        ref: 'CompToolsHeader',
        on: {
          stop: () => {
            this.$refs.CompRequestCatalog.stop_request_list_interval();
          },
          clear: () => {
            this.$refs.CompRequestCatalog.clear_request_list();
          },
          start: () => {
            this.$refs.CompRequestCatalog.start_request_list_interval();
          },
          search:(v)=>{
            this.$refs.CompRequestCatalog.search_request_list(v);
          }
        }
      }),
      h(CompRequestCatalog, {
        ref: 'CompRequestCatalog',
        on: {
          select: (v) => {
            this.$refs.ModalRequestDetail.show_modal(v)
          }
        }
      }),
      h(ModalRequestDetail, {
        ref: 'ModalRequestDetail'
      })
    ])
  },
}
