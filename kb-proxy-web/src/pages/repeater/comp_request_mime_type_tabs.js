import RequestParamsTable from './comp_request_params_table'
import RequestBody from './comp_request_body'
import RequestHeadersTable from './comp_request_headers_table'

export default {
  name: 'comp_request_mime_type_tabs',
  data: () => ({
    select_tab: 'params-tab',
    params: [],
    headers: [],
    url: null,
    request_json: null,
    request_form: null,
    body_type: null
  }),
  props: {
    default_url: {
      type: String,
      require: true,
      default: null
    },
    default_headers: {
      type: Array,
      require: true,
    }
  },
  methods: {
    render_mime_type_tabs(h) {
      return h('q-tabs', {
        style: {
          borderTop: 'solid 1px var(--q-color-grey-4)',
        },
        props: {
          value: this.select_tab,
          color: 'grey-1',
          textColor: 'faded',
        },
        on: {
          select: (v) => this.select_tab = v
        }
      }, [
        h('q-tab', {
          staticClass: 'font-14 no-wrap',
          slot: 'title',
          props: {
            name: 'params-tab',
            label: '请求参数',
            alert: this.params && this.params.length > 1 ? true : false
          }
        }),
        h('q-tab', {
          staticClass: 'font-14',
          slot: 'title',
          props: {
            name: 'headers-tab',
            label: '请求头',
            alert: this.headers && this.headers.length > 1 ? true : false
          }
        }),
        h('q-tab', {
          staticClass: 'font-14',
          slot: 'title',
          props: {
            name: 'body-tab',
            label: '请求体',
            alert: this.body_type && this.body_type !== null ? true : false
          }
        }),
        h('div', {
          attrs: {
            hidden: this.select_tab !== 'params-tab'
          }
        }, [
          h(RequestParamsTable, {
            ref: 'RequestParamsTable',
            props: {
              default_params: this.params
            },

            on: {
              input: (v) => this.input_event(v)
            }
          })
        ]),
        h('div', {
          attrs: {
            hidden: this.select_tab !== 'headers-tab'
          }
        }, [
          h(RequestHeadersTable, {
            ref: 'RequestHeadersTable',
            props: {
              default_headers: this.headers
            },
            on: {
              input: (v) => {
                this.headers = v
                this.$emit('header_input', this.headers)
              }
            }
          })
        ]),
        h('div', {
          attrs: {
            hidden: this.select_tab !== 'body-tab'
          }
        }, [
          h(RequestBody, {
            ref: 'RequestBody',
            on: {
              request_form_input: (v) => {
                this.request_form = v
                this.$emit('request_form_input', v)
              },
              request_json_input: (v) => {
                this.request_json = v
                this.$emit('request_json_input', v)
              },
              request_body_type: (v) => {
                this.body_type = v
                this.$emit('request_body_type', v)
              }
            }
          })
        ]),
      ])
    },
    input_event(v) {
      this.params = v
      let url = this.url ? this.url : null;
      if (url.indexOf('?') !== -1) {
        url = url.substring(0, url.indexOf('?'))
      }
      url = url + '?' + this.get_param_url()
      this.url = url
      this.$emit('url_input', this.url)
    },
    get_param_url() {
      let url = '';
      this.params.map(param => {
        if (param.key) {
          url += (param.key + '=' + (param.value == null ? '' : param.value) + '&')
        }
      })
      if (url.lastIndexOf('&') !== -1) {
        url = url.substring(0, url.lastIndexOf('&'))
      }
      return url;
    },
    refresh_mime_type_pane(url, headers, json, form, body_type) {
      this.url = typeof url !== 'undefined' ? url : null
      if (this.url && this.url.indexOf('?') !== -1) {
        let paramUrl = this.url.substring(this.url.indexOf('?') + 1);
        let paramsTemp = [];
        paramUrl.split('&').map(s => {
          let param = {
            key: null,
            value: null
          }
          if (s.indexOf('=') !== -1) {
            param.key = s.substring(0, s.indexOf('='))
            param.value = s.substring(s.indexOf('=') + 1);
          } else {
            param.key = s
          }
          paramsTemp.push(param)
        })
        this.params = paramsTemp
      } else {
        this.params = []
      }
      this.body_type = body_type
      this.$refs.RequestParamsTable.refresh_table(this.params)

      this.headers = typeof headers !== 'undefined' ? headers : []
      this.$refs.RequestHeadersTable.refresh_table(this.headers)
      this.$refs.RequestBody.refresh_request_body(json, form, body_type)

    }
  },
  render(h) {
    return h('div', {
      staticClass: 'q-mt-sm',
    }, [this.render_mime_type_tabs(h)])
  },
  mounted() {
    this.headers = this.default_headers
    this.url = this.default_url
    this.refresh_mime_type_pane(this.url, this.headers, this.request_json, this.request_form, this.body_type)
  }
}
