export default {
  name: 'comp_request_body_form_table',
  data: () => ({
    params: [],
    default_param: {
      key: null,
      value: null,
      description: null
    }
  }),
  props: {
    default_params: {
      type: Array,
      require: false,
    },
  },
  methods: {
    render_param_table_header(h) {
      return h('thead', [
        h('tr', [
          h('th', {
            staticClass: 'text-left',
          }, 'KEY'),
          h('th', {
            staticClass: 'text-left',
          }, 'VALUE'),
          h('th', {
            staticClass: 'text-left',
          }, 'DESCRIPTION'),
          h('th', {
            staticClass: 'items-center',
            style: {
              width: '10px'
            }
          }, '#'),
        ])
      ])
    },
    render_param_catalog_body(h) {
      let count = 0;
      if (this.params.length <= 0)
        this.params.push({
          ...this.default_param
        });
      let lastParam = this.params[this.params.length - 1]
      if (lastParam.key !== null || lastParam.value !== null || lastParam.description !== null) {
        this.params.push({
          ...this.default_param
        });
      }
      return h('tbody', [this.params.map(param => [
          this.render_param_item(h, param, ++count)
        ]
      )])
    },
    render_param_item(h, param, count) {
      return h('tr', {}, [
        h('td', {staticClass: 'text-left'}, [
          h('q-input', {
            staticClass: 'pp-search-input',
            props: {
              value: param.key,
              placeholder: 'Key',
              hideUnderline: true
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
              value: param.value,
              placeholder: 'Value',
              hideUnderline: true
            },
            on: {
              input: (v) => this.input_event(count, 'value', v)
            }
          })
        ]),
        h('td', {staticClass: 'text-left'}, [h('q-input', {
          staticClass: 'pp-search-input',
          props: {
            value: param.description,
            placeholder: 'Description',
            hideUnderline: true,
          },
          on: {
            input: (v) => this.input_event(count, 'description', v)
          }
        })]),
        h('td', {
          staticClass: 'items-center text-faded',
          style: {
            width: '10px'
          }
        }, [this.params.length > 1 ? h('q-icon', {
          staticClass: 'cursor-pointer icon-red-hover',
          props: {
            name: 'clear',
            color: 'amber',
            size: '16px',
          },
          nativeOn: {
            click: () => {
              this.params.splice(count - 1, 1)
              this.$emit('input', this.params)
            }
          }
        }) : null]),
      ])
    },
    render_params_catalog(h) {
      return h('div', {
        staticClass: 'q-table-dense pp-border-4 scroll',
        style:{
          height:'100%'
        },      }, [
        h('table', {staticClass: 'q-table q-table-horizontal-separator no-shadow q-table-dense'}, [
          this.render_param_table_header(h),
          this.render_param_catalog_body(h)
        ])])
    },
    input_event(count, type, value) {
      count === this.params.length && this.params.push({
        ...this.default_param
      })
      this.update_params_ob(count, type, value)
      this.$emit('input', this.params)
    },
    update_params_ob(count, type, value) {
      (this.params[count - 1])[type] = value;
    },
    refresh_table(v) {
      if (v && v != null) {
        this.params = v
      } else {
        this.params = []
      }
    }
  },
  render(h) {
    return h('div', {}, [
      this.render_params_catalog(h)
    ])
  },
  mounted() {
    this.params = this.default_params ? this.default_params : []
  }
}
