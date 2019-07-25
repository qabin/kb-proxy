export default {
  name: 'comp_response_headers_table',
  data: () => ({
    headers: [],
  }),
  methods: {
    render_header_table_header(h) {
      return h('thead', [
        h('tr', [
          h('th', {
            staticClass: 'text-left',
          }, 'Key'),
          h('th', {
            staticClass: 'text-left',
          }, 'Value'),
        ])
      ])
    },
    render_header_catalog_body(h) {
      return h('tbody', [this.headers.map(header => [
          this.render_header_item(h, header)
        ]
      )])
    },
    render_header_item(h, header) {
      return h('tr', {}, [
        h('td', {staticClass: 'text-left'}, [
          h('span', {}, [header.name])
        ]),
        h('td', {staticClass: 'text-left'}, [
          h('span', {}, [header.value])
        ])
      ])
    },
    render_headers_catalog(h) {
      return h('div', {
        staticClass: 'q-table-dense pp-border-4 scroll',
        style:{
          height:'100%'
        },      }, [
        this.headers && this.headers.length > 0 ? h('table', {staticClass: 'q-table q-table-horizontal-separator no-shadow q-table-dense pp-border-4'}, [
          this.render_header_table_header(h),
          this.render_header_catalog_body(h)
        ]) : h('div', {
          staticClass: 'text-center text-faded',
        }, ['无数据'])])
    },
    refresh_table(v) {
      if (typeof v !== 'undefined')
        this.headers = v
    },
  },
  render(h) {
    return h('div', {}, [
      this.render_headers_catalog(h)
    ])
  },
}
