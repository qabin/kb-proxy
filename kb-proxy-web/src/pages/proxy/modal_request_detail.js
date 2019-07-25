import PPSection from '../../components/elements/pp_section'
import {ajax_get_request_by_id} from "../../api/proxy/request/request_detail_api";
import {ajax_get_response_by_id} from "../../api/proxy/response/response_detail_api";
import store from '../../store'
import {BodyTypeOptions} from "../../utils/repeater_dictionary";
import {header_map_to_arr} from '../../utils/data_format_utils'

export default {
  name: 'modal_request_detail',
  data: () => ({
    request_detail: {},
    response_detail: {},
    detail: {},
    show: false,
    tab_name: 'request'
  }),
  methods: {
    render_header_tabs(h) {
      return h('q-tabs', {
        props: {
          inverted: true,
          value: this.tab_name
        },
        on: {
          input: (v) => {
            this.tab_name = v
          }
        }
      }, [
        h('q-tab', {
          props: {
            label: '请求',
            name: 'request'
          },
          slot: "title",
          on: {
            click: () => {
              this.detail = this.request_detail
            }
          }
        }),
        h('q-tab', {
          props: {
            label: '响应',
            name: 'response'
          },
          slot: "title",
          on: {
            click: () => {
              this.detail = this.response_detail
            }
          }
        })
      ])
    },
    render_header_repeater_btn(h) {
      return h('div', {staticClass: 'q-pa-md cursor-pointer'}, [
        h('q-icon', {
          staticClass: 'icon-red-hover',
          props: {
            name: 'redo',
            size: '20px',
            color: 'primary'
          },
          nativeOn: {
            click: () => {
              store.state.repeater.url = this.request_detail.url
              store.state.repeater.headers = header_map_to_arr(this.request_detail.header)
              store.state.repeater.method = this.request_detail.method
              let form_body = false
              let json_body = false

              try {
                let header_json = JSON.parse(this.request_detail.header)
                for (let k in header_json) {
                  if ((k === 'Content-Type') && header_json[k].indexOf('www-form-urlencoded') !== -1) {
                    form_body = true
                    break
                  }
                  if ((k === 'Content-Type') && header_json[k].indexOf('json') !== -1) {
                    json_body = true
                    break
                  }
                }
              } catch (e) {
              }

              if (form_body) {
                store.state.repeater.request_form = this.form_string_to_arr(this.request_detail.body)
                store.state.repeater.request_json = null
                store.state.repeater.body_type = BodyTypeOptions.FORM
              } else if (json_body) {
                store.state.repeater.request_json = JSON.parse(this.request_detail.body)
                store.state.repeater.request_form = []
                store.state.repeater.body_type = BodyTypeOptions.JSON
              } else {
                store.state.repeater.request_body = this.request_detail.body
                store.state.repeater.request_json = null
                store.state.repeater.request_form = []
              }

              this.$router.push({
                path: '/repeater',
                query: {
                  timestamp: (new Date()).getTime()
                }
              })
            }
          }
        }, [h('q-tooltip', {
          props: {
            offset: [5, 5]
          }
        }, ['重发'])])
      ])
    },
    render_header_mock_btn(h) {
      return h('div', {staticClass: 'q-pa-md cursor-pointer'}, [
        h('q-icon', {
          staticClass: 'icon-red-hover',
          props: {
            name: 'vpn_lock',
            size: '20px',
            color: 'primary'
          },
          nativeOn: {
            click: () => {
              store.state.mock.url = this.request_detail.url
              store.state.mock.headers = header_map_to_arr(this.response_detail.header)
              store.state.mock.method = this.request_detail.method
              store.state.mock.code = this.request_detail.code
              store.state.mock.response = this.response_detail.body

              this.$router.push({
                path: '/mock/detail',
                query: {
                  timestamp: (new Date()).getTime()
                }
              })
            }
          }
        }, [h('q-tooltip', {
          props: {
            offset: [5, 5]
          }
        }, ['Mock'])])
      ])
    },
    render_header_close_btn(h) {
      return h('div', {staticClass: 'q-pa-md cursor-pointer'}, [
        h('q-icon', {
          staticClass: 'icon-red-hover',
          props: {
            name: 'clear',
            size: '20px',
            color: 'primary'
          },
          nativeOn: {
            click: () => {
              this.show = false;
            }
          }
        }, [h('q-tooltip', {
          props: {
            offset: [5, 5]
          }
        }, ['关闭'])])
      ])
    },
    render_general(h) {
      return h(PPSection, {
        staticClass: 'q-mt-sm',
        props: {
          label: 'General'
        }
      }, [
        h('div', {
          slot: 'before',
          staticClass: 'bg-primary q-mr-sm',
          style: {
            width: '3px',
            height: '13px'
          }
        }),
        h('div', {staticClass: 'q-ml-md'}, [
          this.detail.method ? h('div', {}, [
            h('span', {staticClass: 'text-weight-bold'}, 'Method:'),
            h('span', {
              staticClass: 'q-ml-md'
            }, this.detail.method)
          ]) : null,
          this.detail.url ? h('div', {}, [
            h('span', {staticClass: 'text-weight-bold'}, 'URL:'),
            h('span', {
              staticClass: 'q-ml-md'
            }, this.detail.url)
          ]) : null,
          this.detail.ip ? h('div', {}, [
            h('span', {staticClass: 'text-weight-bold'}, 'IP:'),
            h('span', {
              staticClass: 'q-ml-md'
            }, this.detail.ip)
          ]) : null,
          this.detail.code ? h('div', {}, [
            h('span', {staticClass: 'text-weight-bold'}, 'Code:'),
            h('span', {
              staticClass: 'q-ml-md'
            }, this.detail.code)
          ]) : null,
          this.detail.protocol ? h('div', {}, [
            h('span', {staticClass: 'text-weight-bold'}, 'Protocol:'),
            h('span', {
              staticClass: 'q-ml-md'
            }, this.detail.protocol)
          ]) : null,
        ])
      ])
    },
    render_request_header(h) {
      return h(PPSection, {
        staticClass: 'q-mt-sm',
        props: {
          label: 'Header'
        }
      }, [
        h('div', {
          slot: 'before',
          staticClass: 'bg-primary q-mr-sm',
          style: {
            width: '3px',
            height: '13px'
          }
        }),
        h('div', {staticClass: 'q-ml-md'}, [this.__render_request_header(h)])
      ])
    },
    render_cookies(h) {
      return h(PPSection, {
        staticClass: 'q-mt-sm',
        props: {
          label: 'Cookies'
        }
      }, [
        h('div', {
          slot: 'before',
          staticClass: 'bg-primary q-mr-sm',
          style: {
            width: '3px',
            height: '13px'
          }
        }),
        h('div', {staticClass: 'q-ml-md'}, [this.__render_cookies(h)])
      ])
    },
    render_request_body(h) {
      return h(PPSection, {
        staticClass: 'q-mt-sm',
        props: {
          label: 'Body'
        }
      }, [
        h('div', {
          slot: 'before',
          staticClass: 'bg-primary q-mr-sm',
          style: {
            width: '3px',
            height: '13px'
          }
        }),
        h('div', {
          staticClass: 'q-ml-md',
          style: {
            width:'100%'
          }
        }, [
          this.tab_name === 'response' && this.request_detail.mime_type && this.request_detail.mime_type.startsWith('image') ? h('img', {
            style:{
              maxWidth:'100%'
            },
            attrs: {
              src: this.request_detail.url
            }
          }) : h('div', {}, this.detail.body)
        ])
      ])
    },
    show_modal(id) {
      this.show = true

      ajax_get_response_by_id(id).then(d => {
        d && (this.response_detail = d.data) && (this.tab_name === 'response' && (this.detail = d.data))
      })

      ajax_get_request_by_id(id).then(d => {
        d && (this.request_detail = d.data) && (this.tab_name === 'request' && (this.detail = d.data))

      })

    },
    __render_request_header(h) {
      try {
        let headerMap = JSON.parse(this.detail.header);
        return Object.keys(headerMap).map(head => [
          h('div', {}, [
            h('span', {staticClass: 'text-weight-bold'}, head),
            h('span', {staticClass: 'q-ml-md'}, headerMap[head])
          ])
        ])
      } catch (e) {
        return null
      }
    },
    __render_cookies(h) {
      try {
        let headerMap = JSON.parse(this.detail.header)
        let cookieString = headerMap.Cookie
        let cookieArr = cookieString.split(';')
        let cookieMapArr = []
        cookieArr.map(cookie => {
          let coArr = cookie.split('=')
          let cookieMap = {}
          cookieMap['name'] = coArr[0]
          cookieMap['value'] = coArr[1]
          cookieMapArr.push(cookieMap)
        })
        let table_columns = [
          {
            name: 'name',
            align: 'left',
            field: 'name',
            label: 'Name',
          },
          {
            name: 'value',
            align: 'left',
            field: 'value',
            label: 'Value',
          }
        ]
        return h('q-table', {
          staticClass: 'no-shadow pp-border-4',
          props: {
            dense: true,
            columns: table_columns,
            data: cookieMapArr,
            hideBottom: true
          }
        })
      } catch (e) {
        return null
      }
    },
    form_string_to_arr(v) {
      let formArr = []
      if (v) {
        let kAv = v.split('&')
        kAv.map(v => {
          let kv = v.split('=')
          let formMap = {
            key: kv[0],
            value: kv[1]
          }
          formArr.push(formMap)
        })
        return formArr
      }
      return []
    }
  },
  render(h) {
    return h('div', {
      staticClass: 'items-center font-13 text-left pp-text-wrap',
      style: {
        width: '50vw',
        zIndex: 2,
        display: 'none',
        position: 'fixed',
        top: '50px',
        right: 0,
        bottom: 0,
        overflow: 'auto',
        backgroundColor: 'white',
        boxShadow: '-3px 0px 6px 0px rgba(128, 128, 128, 0.56)'
      },
      directives: [{
        name: 'show',
        value: this.show
      }]
    }, [
      h('div', {}, [
        this.render_header_tabs(h),
        h('div', {
          staticClass: 'absolute-right row flex'
        }, [
          this.render_header_repeater_btn(h),
          this.render_header_mock_btn(h),
          this.render_header_close_btn(h)])
      ]),
      h('div', {
        staticClass: 'q-ma-md'
      }, [
        this.render_general(h),
        this.render_request_header(h),
        this.render_cookies(h),
        this.render_request_body(h)
      ])
    ])
  }
}
