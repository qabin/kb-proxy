import {ajax_delete_folder, ajax_update_folder} from "../../api/repeater/collection/folder_api";

export default {
  name: 'comp_collection_folder',
  data: () => ({
    show: true,
    edit_folder: false,
    show_more_btn: false
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
          color: 'faded'
        }
      })
    },
    render_folder_icon(h) {
      return h('q-icon', {
        props: {
          name: 'folder',
          size: '16px',
          color: 'faded'
        }
      })
    },
    render_desc(h) {
      return h('div', {
        staticClass: 'q-ml-md text-left',
      }, [
        !this.edit_folder ? h('div', {
          staticClass: 'font-14 text-weight-bold text-faded',
          style: {
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'noWrap',
          },
          attrs: {
            title: this.folder.name
          }
        }, [this.folder.name]) : h('div', {
          staticClass: 'row no-wrap items-center q-pt-sm q-pb-sm'
        }, [
          h('q-input', {
            staticClass: 'pp-search-input-sm',
            props: {
              placeholder: '输入文件夹名',
              hideUnderline: true,
              value: this.folder.name
            },
            style: {
              width: '60%',
            },
            on: {
              input: (v) => this.folder.name = v
            }
          }),
          h('q-btn', {
            staticClass: 'pp-search-btn-sm',
            props: {
              label: '保存',
              flat: true,
              color: 'primary',
              disable: this.folder.name == null || this.folder.name.length <= 0 ? true : false
            },
            on: {
              click: () => this.update_folder(this.folder.id, this.folder.name)
            }
          }),
          h('q-btn', {
            staticClass: 'pp-search-btn-sm',
            props: {
              label: '取消',
              flat: true,
              color: 'faded',
            },
            on: {
              click: () => this.edit_folder = false
            }
          }),
        ]),
        h('div', {
          staticClass: 'font-10 text-faded'
        }, [(this.children ? this.children.length : 0) + ' 个请求'])
      ])
    },
    render_more_btn(h) {
      return h('q-icon', {
        staticClass: 'icon-red-hover',
        props: {
          name: 'more_horiz',
          size: '26px',
          color: 'faded'
        },
        attrs: {
          hidden: true
        }
      }, [h('q-popover', {
        props: {}
      }, [
        h('q-list', {
          props: {
            link: true,
            dense: true,
            width: '50px',
          },
        }, [
          h('q-item', {
            staticClass: 'font-12',
            style: {
              paddingLeft: '5px',
              paddingRight: '5px'
            },
            directives: [{
              name: 'close-overlay',
            }],
            nativeOn: {
              click: () => this.edit_folder = true
            }
          }, ['编辑']),
          h('q-item', {
            staticClass: 'font-12',
            style: {
              paddingLeft: '5px',
              paddingRight: '5px'
            },
            directives: [{
              name: 'close-overlay',
            }],
            nativeOn: {
              click: () => this.delete_by_id(this.folder.id)
            }
          }, ['删除']),
        ])
      ])])
    },
    delete_by_id(id) {
      ajax_delete_folder(id).then(d => {
        d.status === 1 && this.$emit('refresh')
      })
    },
    update_folder(id, name) {
      ajax_update_folder(id, name).then(d => {
      })
    }
  },
  render(h) {
    return h('div', {
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
          'pp-selected-bg-grey-3': this.active,
          'bg-grey-1': !this.active
        },
      }, [
        h('div', {
          staticClass: 'row items-center cursor-pointer col-grow',
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
        }, [this.render_more_btn(h)])]),
      this.show ? h('div', {}, [
        this.$slots.content,
      ]) : null
    ])
  }
}
