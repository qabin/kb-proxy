import ResponseJson from './comp_response_json'
import ResponseCookiesTable from './comp_response_cookie_table'
import ResponseHeadersTable from './comp_response_headers_table'

export default {
  name: 'comp_response_mime_type_tabs',
  data: () => ({
    select_tab: 'response-tab',
    headers: [],
    response: null,
    cookies: []
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
          borderTop: 'solid 1px #e0e0e0',
        },
        props: {
          value: this.select_tab,
          color: 'grey-1',
          textColor: 'faded'
        },
        on: {
          select: (v) => this.select_tab = v
        }
      }, [
        h('q-tab', {
          staticClass: 'font-14 no-wrap',
          slot: 'title',
          props: {
            name: 'response-tab',
            label: '响应结果',
            alert: this.response ? true : false
          }
        }),
        h('q-tab', {
          staticClass: 'font-14',
          slot: 'title',
          props: {
            name: 'headers-tab',
            label: '响应头',
            alert: this.headers && this.headers.length > 0 ? true : false
          }
        }),
        h('q-tab', {
          staticClass: 'font-14',
          slot: 'title',
          props: {
            name: 'cookie-tab',
            label: 'Cookie',
            alert: this.cookies && this.cookies.length > 0 ? true : false
          }
        }),
        h('div', {
          attrs: {
            hidden: this.select_tab !== 'response-tab'
          }
        }, [
          h(ResponseJson, {
            ref: 'ResponseJson',
          })
        ]),
        h('div', {
          attrs: {
            hidden: this.select_tab !== 'headers-tab'
          }
        }, [
          h(ResponseHeadersTable, {
            ref: 'ResponseHeadersTable',
          })
        ]),
        h('div', {
          attrs: {
            hidden: this.select_tab !== 'cookie-tab'
          }
        }, [
          h(ResponseCookiesTable, {
            ref: 'ResponseCookiesTable',
          })
        ]),
      ])
    },
    refresh_response(v) {
      this.response = v
      this.$refs.ResponseJson.refresh_response(v)
    },
    refresh_response_headers(v) {
      this.headers = v
      this.$refs.ResponseHeadersTable.refresh_table(v)
      this.get_cookies_from_header(v)
      this.$refs.ResponseCookiesTable.refresh_table(this.cookies)
    },
    get_cookies_from_header(v) {
      let cookieArr = [];
      v && v.map(h => {
        if (h.name === 'Set-Cookie') {
          let nvArr = h.value.split(';')
          let kvArr = nvArr[0].split('=')
          let cookieMap = {
            name: kvArr[0],
            value: kvArr[1]
          }
          nvArr.splice(0, 1)
          nvArr.map(nv => {
            let ovArr = nv.split('=')
            cookieMap[ovArr[0].trim()] = ovArr[1]
          })
          cookieArr.push(cookieMap)
        }
      })
      this.cookies = cookieArr
    }
  },
  render(h) {
    return h('div', {
      staticClass: 'q-mt-sm',
    }, [this.render_mime_type_tabs(h)])
  }
}
