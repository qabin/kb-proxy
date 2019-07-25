export default {
  name: 'comp_response_headers_table',
  data: () => ({
    headers: [],
  }),
  props: {
    default_headers: {
      type: Array,
      require: false,
      default: []
    },
    disable: {
      type: Boolean,
      require: false,
      default: false
    }
  },
  methods: {
    render_header_table_header(h) {
      return h('thead', [
        h('tr', [
          h('th', {
            staticClass: 'text-left',
          }, 'KEY'),
          h('th', {
            staticClass: 'text-left',
          }, 'VALUE'),
          h('th', {
            staticClass: 'items-center',
            style: {
              width: '10px'
            }
          }, '#'),
        ])
      ])
    },
    render_header_catalog_body(h) {
      let count = 0;
      if (!this.headers || this.headers.length <= 0) {
        this.headers = []
        this.headers.push({
          ...{
            key: null,
            value: null,
            description: null
          }
        });
      }
      let lastHeader = this.headers[this.headers.length - 1]
      if (lastHeader.key !== null || lastHeader.value !== null || lastHeader.description !== null) {
        this.headers.push({
          ...{
            key: null,
            value: null,
            description: null
          }
        });
      }
      return h('tbody', {}, [this.headers.map(header => [
          this.render_header_item(h, header, ++count)
        ]
      )])
    },
    render_header_item(h, header, count) {
      return h('tr', {}, [
        h('td', {staticClass: 'text-left'}, [
          h('q-input', {
            staticClass: 'pp-search-input',
            props: {
              value: header.key,
              placeholder: 'Key',
              hideUnderline: true,
              disable: this.disable
            },
            on: {
              input: (v) => this.input_event(count, 'key', v)
            }
          })
        ]),
        h('td', {staticClass: 'text-left'}, [
          h('q-input', {
            staticClass: 'pp-search-input',
            props: {
              value: header.value,
              placeholder: 'Value',
              hideUnderline: true,
              disable: this.disable
            },
            on: {
              input: (v) => this.input_event(count, 'value', v)
            }
          })
        ]),
        h('td', {
          staticClass: 'items-center text-faded',
          style: {
            width: '10px'
          }
        }, [this.headers.length > 1 && !this.disable ? h('q-icon', {
          staticClass: 'cursor-pointer icon-red-hover',
          props: {
            name: 'clear',
            color: 'amber',
            size: '16px',
          },
          nativeOn: {
            click: () => {
              this.headers.splice(count - 1, 1)
              this.$emit('input', this.headers)
            }
          }
        }) : null]),
      ])
    },
    render_headers_catalog(h) {
      return h('div', {
        staticClass: 'q-table-dense pp-border-4 scroll',
        style: {
          height: '100%'
        },
      }, [
        h('table', {staticClass: 'q-table q-table-horizontal-separator no-shadow q-table-dense'}, [
          this.render_header_table_header(h),
          this.render_header_catalog_body(h)
        ])])
    },
    input_event(count, type, value) {
      count === this.headers.length && this.headers.push({
        ...{
          key: null,
          value: null,
          description: null
        }
      })
      this.update_headers_ob(count, type, value)
      this.$emit('input', this.headers)
    },
    update_headers_ob(count, type, value) {
      (this.headers[count - 1])[type] = value;
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
  mounted() {
    if (typeof this.default_headers !== 'undefined') {
      this.headers = this.default_headers
    }
  }
}
