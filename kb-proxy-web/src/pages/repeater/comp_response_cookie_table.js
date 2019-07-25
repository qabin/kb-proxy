export default {
  name: 'comp_response_cookie_table',
  data: () => ({
    cookies: [],
  }),
  methods: {
    render_cookie_table_cookie(h) {
      return h('thead', [
        h('tr', [
          h('th', {
            staticClass: 'text-left',
          }, 'Name'),
          h('th', {
            staticClass: 'text-left',
          }, 'Value'),
          h('th', {
            staticClass: 'text-left',
          }, 'Domain'),
          h('th', {
            staticClass: 'text-left',
          }, 'Path'),
          h('th', {
            staticClass: 'text-left',
          }, 'Expires'),
          h('th', {
            staticClass: 'text-left',
          }, 'HttpOnly'),
          h('th', {
            staticClass: 'text-left',
          }, 'Secure'),
        ])
      ])
    },
    render_cookie_catalog_body(h) {
      return h('tbody', [this.cookies.map(cookie => [
          this.render_cookie_item(h, cookie)
        ]
      )])
    },
    render_cookie_item(h, cookie) {
      return h('tr', {}, [
        h('td', {staticClass: 'text-left'}, [
          h('span', {}, [cookie.name])
        ]),
        h('td', {staticClass: 'text-left'}, [
          h('span', {}, [cookie.value])
        ]),
        h('td', {staticClass: 'text-left'}, [
          h('span', {}, [cookie.domain])
        ]),
        h('td', {staticClass: 'text-left'}, [
          h('span', {}, [cookie.path])
        ]),
        h('td', {staticClass: 'text-left'}, [
          h('span', {}, [cookie.expires])
        ]),
        h('td', {staticClass: 'text-left'}, [
          h('span', {}, [cookie.httpOnly])
        ]), h('td', {staticClass: 'text-left'}, [
          h('span', {}, [cookie.secure])
        ]),

      ])
    },
    render_cookies_catalog(h) {
      return h('div', {
        staticClass: 'overflow-hidden q-table-dense'
      }, [
        this.cookies && this.cookies.length > 0 ? h('table', {staticClass: 'q-table q-table-horizontal-separator no-shadow q-table-dense pp-border-4'}, [
          this.render_cookie_table_cookie(h),
          this.render_cookie_catalog_body(h)
        ]) : h('div', {
          staticClass: 'text-center text-faded',
        }, ['无数据'])])
    },
    refresh_table(v) {
      if (typeof v !== 'undefined')
        this.cookies = v
    },
  },
  render(h) {
    return h('div', {}, [
      this.render_cookies_catalog(h)
    ])
  },
}
