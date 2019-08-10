import LazyInput from '../../components/elements/ComponentLazyInput'
import QRCodeRender from './qrcode_render'
import store from '../../store'
import {ajax_start_proxy_server, ajax_stop_proxy_server} from "../../api/proxy/server/proxy_server_api";

export default {
  name: 'compToolsHeader',
  data: () => ({
    method: [],
    code: [],
    //proxyStatus: true,
    kw: null,
  }),
  computed: {
    proxy_port() {
      return store.state.user.id;
    },
    proxy_ip() {
      return store.state.user.ip;
    },
    proxyStatus() {
      return store.state.user.status === 1 ? true : false
    }
  },
  watch: {
    proxyStatus() {
    }
  },
  methods: {
    render_search(h) {
      return h('div', {}, [
        h(LazyInput, {
          staticClass: 'pp-search-input q-ml-md q-mt-sm',
          props: {
            placeholder: '按Method/Code/Host/Path搜索',
            value: this.kw,
            hideUnderline: true,
          },
          on: {
            input: (v) => {
              this.$emit("search", v)
            }
          }
        })
      ])
    },
    render_tools_bar(h) {
      return h('div', {
        staticClass: 'row font-13 text-faded absolute-right q-mr-md'
      }, [
        h('div', {
          staticClass: 'q-ml-md div-blue-hover'
        }, [
          h('div', {
            staticClass: 'items-center row cursor-pointer',
            style: {
              height: '36px',
            }
          }, [h('q-icon', {
            staticClass: '',
            props: {
              name: 'info',
              size: '28px',
            },
          }, []),]),
          h('div', {}, [h('span', {}, ['代理'])]),
          h('q-popover', {
            staticClass: 'font-13',
          }, [
            h('div', {
              staticClass: 'q-pa-sm text-weight-bold'
            }, '代理服务详情'),
            h('hr', {
              style: {
                borderWidth: '0.5px'
              }
            }),
            h('div', {
              staticClass: 'q-pa-sm no-wrap row items-center'
            }, [
              h('span', ['代理地址: ']),
              h('span', {staticClass: 'text-weight-bold'}, [this.proxy_ip])]),
            h('div', {
              staticClass: 'q-pa-sm no-wrap'
            }, [
              h('span', ['代理端口: ']),
              h('span', {staticClass: 'text-weight-bold'}, [this.proxy_port])])
          ])
        ]),
        h('div', {
          staticClass: 'q-ml-md cursor-pointer div-blue-hover',
          on: {
            click: () => {
              this.proxy_status_switch()
            }
          }
        }, [
          h('div', {}, [h('q-icon', {
            staticClass: '',
            props: {
              name: this.proxyStatus ? 'stop' : 'play_arrow',
              size: '36px',
            },
          }),]),
          h('span', {}, [this.proxyStatus ? '停止' : '开启'])
        ]),
        h('div', {
          staticClass: 'q-ml-md cursor-pointer div-blue-hover',
          on: {
            click: () => {
              this.request_record_clear()
            }
          }
        }, [
          h('div', {
            staticClass: 'items-center row',
            style: {
              height: '36px'
            }
          }, [h('q-icon', {
            staticClass: '',
            props: {
              name: 'delete',
              size: '26px',
            },
          })]),
          h('span', {}, '清除')
        ]),
        h('div', {
          staticClass: 'q-ml-md q-mr-xl cursor-pointer div-blue-hover',
          on:{
            click:()=>{
              this.$refs.QRCodeRender.bindQRCode()
            }
          }
        }, [
          h('div', {
            staticClass: 'items-center row',
            style: {
              height: '36px'
            },
          }, [h('q-icon', {
            staticClass: '',
            props: {
              name: 'get_app',
              size: '36px',
            },
          })]),
          h('span', {}, '证书'),
          h('q-popover', {
          }, [
            h(QRCodeRender,{
              ref:'QRCodeRender'
            })
          ])
        ])
      ])

    },
    proxy_status_switch() {
      if (!this.proxyStatus) {
        ajax_start_proxy_server().then(d => {
            if (d.status === 1) {
              this.$store.dispatch('user/refreshUserInfo').then().catch()
            }
          }
        ).catch()
        this.$emit('start')

      } else {
        ajax_stop_proxy_server().then(d => {
          if (d.status === 1) {
            this.$store.dispatch('user/refreshUserInfo').then().catch()
          }
        }).catch()
        this.$emit('stop')
      }
    },
    request_record_clear() {
      this.$emit('clear')
    }
  },
  render(h) {
    return h('div', {
      staticClass: 'row items-center font-13 text-lef',
      style: {
        height: '60px',
        width: '100%',
        marginLeft: '50px',
        zIndex: '1',
        position: 'fixed',
        marginBottom: '40px',
        top: '50px',
        right: 0,
        left: 0,
        overflow: 'hidden',
        backgroundColor: 'white',
        boxShadow: '-3px 0px 6px 0px rgba(128, 128, 128, 0.56)'
      },
    }, [
      h('div', {}, [this.render_search(h)]),
      h('div', {}, [this.render_tools_bar(h)]),
    ])
  },
}
