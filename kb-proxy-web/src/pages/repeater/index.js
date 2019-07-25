import LeftDetail from './comp_left_detail'
import RightDetail from './comp_right_detail'
import store from '../../store'
import ActiveApiTabs from './comp_active_api_tabs'
import localStorage from '../../utils/local_storage_utils'

export default {
  name: 'repeater_index',
  data: () => ({
    show_list_width: '84%',
    hidden_list_width: '99.5%',
  }),
  computed: {
    from_proxy_to_repeater() {
      if (this.$route.path == "/repeater")
        return (new Date()).getTime() >= this.$route.query.timestamp && (new Date()).getTime() - this.$route.query.timestamp <= 1 * 60 * 1000
      return false
    },
    show_list_status() {
      return store.state.repeater.show_list
    }
  },
  watch: {
    from_proxy_to_repeater: {
      immediate: true,
      handler: function (nv, ov) {
        if (nv) {
          setTimeout(this.from_proxy_add, 300)
        }
      }
    }
  },
  methods: {
    render_body(h) {
      return h('div', {
        staticClass: 'row no-wrap',
        style: {
          width: '100%',
          maxHeight: '100%',
          minHeight:'833px'
        },
      }, [
        h(LeftDetail, {
          ref: 'LeftDetail',
          on: {
            history_open_in_new_tab: (v) => this.$refs.ActiveApiTabs.add_active_tab(v, false),
            history_open_in_first_tab: (v) => this.$refs.ActiveApiTabs.add_active_tab(v, true),
            collection_open_in_new_tab: (v) => this.$refs.ActiveApiTabs.add_active_tab(v, false),
            collection_open_in_first_tab: (v) => this.$refs.ActiveApiTabs.add_active_tab(v, true),
          }
        }),
        h('div', {
          style: {
            width: this.show_list_status ? this.show_list_width : this.hidden_list_width
          }
        }, [
          h(ActiveApiTabs, {
            ref: 'ActiveApiTabs',
            on: {
              select: (v) => {
                this.active_tab_select(v)
              }
            }
          }),
          h(RightDetail, {
            ref: 'rightDetail',
            on: {
              url_input: (v) => {
                this.$refs.ActiveApiTabs.refresh_url(v)
              },
              header_input: (v) => {
                this.$refs.ActiveApiTabs.refresh_headers(v)
              },
              request_json_input: (v) => {
                this.$refs.ActiveApiTabs.refresh_request_json(v)
              },
              request_form_input: (v) => {
                this.$refs.ActiveApiTabs.refresh_request_form(v)
              },
              request_body_type: (v) => {
                this.$refs.ActiveApiTabs.refresh_request_body_type(v)
              },
              method_select: (v) => {
                this.$refs.ActiveApiTabs.refresh_request_method(v)
              },
              response_body: (v) => {
                this.$refs.ActiveApiTabs.refresh_response_body(v)
              },
              response_headers: (v) => {
                this.$refs.ActiveApiTabs.refresh_response_headers(v)
              },
              refresh_history: () => {
                this.$refs.LeftDetail.refresh_history_catalog()
              },
              refresh_collection: () => {
                this.$refs.LeftDetail.refresh_collection_catalog()
              },
              name_input: (v) => {
                this.$refs.ActiveApiTabs.refresh_name(v)
              }
            }
          })
        ]),
      ])
    },
    active_tab_select(v) {
      this.$refs.rightDetail.active_tab_select(v)
    },
    select_request(v) {

    },
    from_proxy_add() {
      let model = {
        url: store.state.repeater.url,
        method: store.state.repeater.method,
        headers: store.state.repeater.headers,
        request_json: store.state.repeater.request_json,
        request_form: store.state.repeater.request_form,
        body_type: store.state.repeater.body_type
      }
      this.$refs.ActiveApiTabs.add_active_tab(model, false)
    }
  },
  render(h) {
    return h('div', {
        staticClass: 'col-grow shadow-0 column',
        style: {
          borderRadius: '3px',
          fontSize: '13px',
        }
      },
      [this.render_body(h)]
    )
  },
  mounted() {
    localStorage.get('repeater_show_list') != null && (store.state.repeater.show_list = localStorage.get('repeater_show_list'))
  }
}
