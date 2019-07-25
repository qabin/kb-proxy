import {
  ajax_get_detail_list, ajax_delete_detail_by_id
} from "../../api/repeater/collection/request_detail_api";
import {
  ajax_add_folder
} from "../../api/repeater/collection/folder_api";
import {MethodEnums} from "../../utils/request_dictionary";
import CollectionFolder from './comp_collection_folder'
import RequestDetailEditModal from './modal_request_detail_edit'

export default {
  name: 'comp_collection_catalog',
  data: () => ({
    collection_list: [],
    minId: 0,
    kw: null,
    folder_id: null,
    collection: null,
    show_add_folder_input: false,
    folder_name: null,
  }),
  methods: {
    render_add_folder_btn(h) {
      return h('div', {
        staticClass: 'text-right bg-grey-1',
        style: {
          minHeight: '40px',
          borderBottom: '1px solid var(--q-color-grey-4)',
          borderTop: '1px solid var(--q-color-grey-4)'
        }
      }, [!this.show_add_folder_input ? h('q-icon', {
        staticClass: 'icon-red-hover cursor-pointer q-mt-sm',
        props: {
          name: 'create_new_folder',
          color: 'faded',
          size: '26px'
        },
        nativeOn: {
          click: () => this.show_add_folder_input = !this.show_add_folder_input
        }
      }, [h('q-tooltip', {
        props: {
          offset: [5, 5]
        }
      }, ['添加文件夹'])]) : h('div', {
        staticClass: 'row no-wrap items-center q-pt-sm q-pb-sm'
      }, [
        h('q-input', {
          staticClass: 'pp-search-input-sm q-ml-sm',
          props: {
            placeholder: '输入文件夹名',
            hideUnderline: true,
            value: this.folder_name
          },
          style: {
            width: '70%'
          },
          on: {
            input: (v) => this.folder_name = v
          }
        }),
        h('q-btn', {
          staticClass: 'pp-search-btn-sm',
          props: {
            label: '保存',
            flat: true,
            color: 'primary',
            disable: this.folder_name == null || this.folder_name.length <= 0 ? true : false
          },
          on: {
            click: () => this.add_folder()
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
            click: () => this.show_add_folder_input = false
          }
        }),
      ])
      ])
    },
    render_folder_list(h, collection) {
      return h(CollectionFolder, {
        ref: 'CollectionFolder',
        props: {
          folder: {name: collection.folder_name, id: collection.folder_id},
          children: collection.children || [],
          active: this.folder_id === collection.folder_id
        },
        nativeOn: {
          click: () => this.folder_id = collection.folder_id
        },
        on: {
          refresh: () => this.refresh_catalog()
        }
      }, [
        h('div', {
          slot: 'content',
        }, [this.render_detail_list(h, collection.children)])
      ])
    },
    render_detail_list(h, children) {
      return h('div', {}, [
        children && children.length > 0 ? children.map(d => [this.render_detail_item(h, d)]) :
          this.render_no_item(h)
      ])
    },
    render_detail_item(h, detail) {
      return h('div', {
        staticClass: 'row flex cursor-pointer no-wrap q-pl-md items-center pp-selected-bg-grey-2-hover',
        'class': {
          'pp-selected-bg-grey-3': this.collection && this.collection.id === detail.id,
        },
        style: {
          width: '100%'
        },
      }, [
        h('div', {
          staticClass: 'row no-wrap items-center',
          style: {
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'noWrap',
            width: '90%',
            height: '36px'
          },
          on: {click: () => this.collection_select(detail)},
        }, [h('div', {
          style: {
            maxWidth: '30%'
          },
          staticClass: 'q-mr-sm font-10 text-left text-weight-bold text-' + MethodEnums[detail.method].color
        }, detail.method),
          h('div', {
            staticClass: 'q-mr-sm text-faded text-weight-bold text-left',
            style: {
              width: '85%',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              wordWrap: 'break-word',
            }
          }, detail.name),]),
        h('div', {
          staticClass: 'q-mr-sm text-faded',
          style: {
            width: '10%',
          }
        }, [h('q-icon', {
          staticClass: 'cursor-pointer icon-red-hover',
          props: {
            name: 'more_horiz',
            color: 'faded',
            size: '20px',
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
                click: () => this.show_request_detail_add_modal(detail)
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
                click: () => {
                  this.open_in_new_tab(detail)
                }
              }
            }, [
              '新窗口打开'
            ]),
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
                click: () => this.delete_by_id(detail.id)
              }
            }, ['删除']),
          ])
        ])])]),
      ])
    },
    render_no_item(h) {
      return h('div', {
        staticClass: 'row text-faded flex-center',
        style: {
          height: '36px'
        }
      }, '无数据')
    },
    refresh_catalog(v) {
      let vm = this;
      typeof v !== 'undefined' && (this.kw = v)
      ajax_get_detail_list(this.kw).then(data => {
        vm.collection_list = [];
        vm.collection_list = vm.collection_list.concat(this.deal_data_to_folder_list(data.data) || [])
      })
    },
    collection_select(collection) {
      this.collection = collection
      this.open_in_first_tab(collection)
    },
    delete_by_id(id) {
      ajax_delete_detail_by_id(id).then(d => {
        d.status === 1 && this.refresh_catalog()
      })
    },
    open_in_new_tab(collection) {
      this.$emit('collection_open_in_new_tab', collection)
    },
    open_in_first_tab(collection) {
      this.$emit('collection_open_in_first_tab', collection)
    },
    add_folder() {
      this.folder_name && ajax_add_folder(this.folder_name).then(d => {
        d.status === 1 && this.refresh_catalog()
      })
    },
    deal_data_to_folder_list(v) {
      let folder_list = [];
      v.map(d => {
        let folder_map = {}
        let contain_flag = false
        let index = 0;
        for (let i = 0; i < folder_list.length; i++) {
          if (folder_list[i].folder_id === d.folder_id) {
            contain_flag = true
            index = i
            break
          }
        }
        if (contain_flag) {
          let children = (folder_list[index]).children
          d.id && children.push(d)
          folder_map = {
            folder_id: d.folder_id,
            id: d.id,
            children: children,
            folder_name: d.folder_name
          }
          folder_list.splice(index, 1, folder_map)

        } else {
          let children = []
          d.id && children.push(d)
          folder_map = {
            folder_id: d.folder_id,
            id: d.id,
            children: children,
            folder_name: d.folder_name
          }
          folder_list.push(folder_map)
        }
      })
      return folder_list
    },
    show_request_detail_add_modal(v) {
      this.$refs.RequestDetailEditModal.show(v);
    },
  },
  render(h) {
    return h('div', {}, [
      h(RequestDetailEditModal, {
        ref: 'RequestDetailEditModal',
        on: {
          submit: () => this.refresh_catalog()
        }
      }),
      this.render_add_folder_btn(h),
      this.collection_list.length <= 0 ? this.render_no_item(h) : this.collection_list.map(collection => [
        this.render_folder_list(h, collection)]
      )],
    )
  },
  mounted() {
    this.refresh_catalog()
  }
}
