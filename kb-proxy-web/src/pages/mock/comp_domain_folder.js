export default {
  name: 'comp_domain_folder',
  data: () => ({
    show: true,
  }),
  props: {
    folder: {
      type: Object,
      require: true,
    },
    children: {
      type: Array,
      require: false
    },
    active: {
      type: Boolean,
      require: false,
      default: false
    }
  },
  methods: {
    render_collapse_icon(h) {
      return h('q-icon', {
        props: {
          name: this.show ? 'arrow_drop_down' : 'arrow_right',
          size: '20px',
          color: this.active ? 'white' : 'deep-orange'
        }
      })
    },
    render_folder_icon(h) {
      return h('q-icon', {
        props: {
          name: 'tv',
          size: '20px',
          color: this.active ? 'white' : 'deep-orange'
        }
      })
    },
    render_desc(h) {
      return h('div', {
        staticClass: 'q-ml-md text-left row',
      }, [
        h('div', {
          staticClass: 'font-14 text-weight-bold',
          'class': {
            'text-deep-orange': !this.active,
            'text-white': this.active
          },
          style: {
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'noWrap',
          },
          attrs: {
            title: this.folder.name
          }
        }, [this.folder.name]),
        h('div', {
          staticClass: 'font-10 q-ml-md text-weight-bold',
          'class': {
            'text-deep-orange': !this.active,
            'text-white': this.active
          },
          style: {
            marginTop: '2px'
          }
        }, ['(总计：' + (this.children ? this.children.length : 0) + ' ）'])
      ])
    },
  },
  render(h) {
    return h('div', {
      staticClass:'font-14',
      style: {
        borderBottom: '1px solid var(--q-color-grey-4)'
      },
      on: {
        mouseout: () => this.show_more_btn = false,
        mouseover: () => this.show_more_btn = true
      }
    }, [
      h('div', {
        staticClass: 'row items-center cursor-pointer',
        style: {
          width: '100%',
          minHeight: '50px'
        },
        'class': {
          'bg-blue-5': this.active,
          'bg-grey-1': !this.active
        },
      }, [
        h('div', {
          staticClass: 'row items-center cursor-pointer col-grow q-pl-md',
          on: {
            click: () => this.show = !this.show
          }
        }, [this.render_collapse_icon(h),
          this.render_folder_icon(h),
          this.render_desc(h),]),
        h('div', {
          attrs: {
            hidden: !this.show_more_btn
          }
        }, [])]),
      this.show ? h('div', {}, [
        this.$slots.content,
      ]) : null
    ])
  }
}
