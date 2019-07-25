import {ajax_get_request_list, ajax_delete_requests} from "../../api/proxy/request/request_detail_api";
import {CodeEnums} from "../../utils/request_dictionary";
import store from "../../store";

export default {
  name: 'compRequestCatalog',
  data: () => ({
    requestList: [],
    maxRequestId: 0,
    curId: 0,
    interval_id: null,
    request: {},
    kw: null,
  }),
  computed: {
    proxyStatus() {
      return store.state.user.status === 1 ? true : false
    }
  },
  methods: {
    render_request_table_header(h) {
      return h('thead', [
        h('tr', [
          h('th', {
            staticClass: 'text-left',
            style: {
              width: '6vw'
            }
          }, '#'),
          h('th', {
            staticClass: 'text-left',
            style: {
              width: '1vw'
            }
          }, 'Mock'),
          h('th', {
            staticClass: 'text-left',
            style: {
              width: '7vw'
            }
          }, 'Method'),
          h('th', {
            staticClass: 'text-left',
            style: {
              width: '7vw'
            }
          }, 'Code'),
          h('th', {
            staticClass: 'text-left',
            style: {
              width: '10vw'
            }
          }, 'Host'),
          h('th', {
            staticClass: 'text-left',
            style: {
              width: '10vw'
            }
          }, 'IP'),
          h('th', {
            staticClass: 'text-left ellipsis over',
            style: {
              width: '40vw'
            }
          }, 'Path'),
          h('th', {
            staticClass: 'text-left',
            style: {
              width: '7vw'
            }
          }, 'Mime'),
          h('th', {
            staticClass: 'text-left',
            style: {
              width: '7vw'
            }
          }, 'Start'),
        ])
      ])
    },
    render_request_catalog_body(h) {
      let id = this.curId - this.requestList.length;
      return h('tbody', [this.requestList.map(request => [
          this.render_request_item(h, request, ++id)
        ]
      )])
    },
    render_request_item(h, request, id) {
      return h('tr', {
        staticClass: 'cursor-pointer ' + (id % 2 === 0 && this.request.id !== request.id ? 'bg-grey-1' : ''),
        'class': {
          'pp-selected-bg-grey-3': this.request.id === request.id
        },
        on: {
          click: () => {
            this.request = request;
            this.$emit('select', request.id)
          }
        }
      }, [
        h('td', {staticClass: 'text-left'}, [h('span', {}, id)]),
        h('td', {staticClass: 'text-left'}, [request.mock === 1 ? h('q-icon', {
          props: {
            name: 'vpn_lock',
            color: 'primary'
          }
        }) : null]),
        h('td', {staticClass: 'text-left'}, [h('div', {
          staticClass: 'row items-center'
        }, [h('span', {}, request.method)])]),
        h('td', {staticClass: 'text-left'}, [h('span', {
          staticClass: 'text-weight-bold ' + this.get_color_for_code(request.code)
        }, request.code)]),
        h('td', {staticClass: 'text-left'}, [h('span', {}, request.host)]),
        h('td', {staticClass: 'text-left'}, [h('span', {}, request.ip)]),
        h('td', {staticClass: 'text-left'}, [h('span', {
          style: {
            width: '40vw',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'noWrap',
            display: 'inline-block'
          },
          attrs: {
            title: request.path
          }
        }, request.path)]),
        h('td', {staticClass: 'text-left'}, [h('span', {}, request.mime_type)]),
        h('td', {staticClass: 'text-left'}, [h('span', {}, request.create_time)]),
      ])
    },
    render_request_catalog(h) {
      return h('div', {
        staticClass: 'scroll q-table-dense'
      }, [
        h('table', {staticClass: 'q-table q-table-horizontal-separator no-shadow q-table-dense'}, [
          this.render_request_table_header(h),
          this.render_request_catalog_body(h)
        ])])
    },
    refresh_catalog() {
      ajax_get_request_list(0, this.kw).then(d => {
        if (d.status === 1) {
          if (d.data) {
            this.requestList = d.data
            this.requestList.map(r => {
              r && r.id > this.maxRequestId && (this.maxRequestId = r.id)
            })
            this.curId = 0;
            this.curId += d.data.length;
          }
        }
      })
    },
    refresh_catalog_interval() {
      ajax_get_request_list(this.maxRequestId, this.kw).then(d => {
        if (d.status === 1) {
          if (d.data) {
            this.requestList = this.requestList.concat(d.data)
            this.requestList.map(r => {
              r && r.id > this.maxRequestId && (this.maxRequestId = r.id)
            })
            this.curId += d.data.length;
          }
        }
      })
    },
    start_request_list_interval() {
      !this.interval_id && (this.interval_id = setInterval(this.refresh_catalog_interval, 1000))
    },
    stop_request_list_interval() {
      clearInterval(this.interval_id)
      this.interval_id = null
    },
    clear_request_list() {
      this.requestList = [];
      this.curId = 0;
      ajax_delete_requests().then(d => {

      }).catch(e => {

      })
    },
    search_request_list(kw) {
      this.kw = kw
      this.refresh_catalog()
    },
    get_color_for_code(code) {
      try {
        return 'text-' + CodeEnums[code].color
      } catch (e) {
        return 'text-faded'
      }
    }
  },
  render(h) {
    return h('div', {
      staticClass: 'q-pa-md',
      style: {
        paddingTop: '70px'
      }
    }, [
      this.render_request_catalog(h),
    ])
  },
  deactivated() {
    this.stop_request_list_interval();
  },
  activated() {
    this.start_request_list_interval();
  }
}
