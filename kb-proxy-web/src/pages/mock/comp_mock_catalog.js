import {
  ajax_mock_proxy_search
} from "../../api/mock/mock_proxy_api";
import {MethodEnums} from "../../utils/request_dictionary";
import DomainFolder from './comp_domain_folder'
import LazyInput from "../../components/elements/ComponentLazyInput";
import {IsUsedEnums} from "../../utils/mock_dictionary";

export default {
  name: 'comp_collection_catalog',
  data: () => ({
    collection_list: [],
    minId: 0,
    kw: null,
    domain: null,
    collection: null,
    show_add_folder_input: false,
    folder_name: null,
  }),
  methods: {
    render_folder_list(h, collection) {
      return h(DomainFolder, {
        ref: 'DomainFolder',
        props: {
          folder: {name: collection.folder_name},
          children: collection.children || [],
          active: this.folder_name === collection.folder_name
        },
        nativeOn: {
          click: () => this.folder_name = collection.folder_name
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
        staticClass: ' font-12 row flex cursor-pointer no-wrap q-pl-lg items-center pp-selected-bg-grey-2-hover',
        'class': {
          'pp-selected-bg-grey-3': this.collection && this.collection.id === detail.id,
        },
      }, [
        h('div', {
          staticClass: 'row no-wrap items-center col-grow q-pl-lg',
          style: {
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'noWrap',
            height: '36px',
            width:'100%'
          },
          on: {click: () => this.collection_select(detail)},
        }, [h('div', {
          staticClass: 'q-mr-sm font-10 text-left text-weight-bold text-' + MethodEnums[detail.method].color,
          style:{
            width:'40px'
          }
        }, detail.method),
          h('div', {
            staticClass: 'q-mr-sm text-primary text-weight-bold text-left',
            style: {
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              wordWrap: 'break-word',
              width:'40%'
            }
          }, detail.url),
          h('div', {
            staticClass: 'q-mr-sm text-faded text-weight-bold text-left',
            style: {
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              wordWrap: 'break-word',
              width:'20%'
            }
          }, detail.name),
          h('div', {
            staticClass: 'q-mr-sm text-faded text-weight-bold text-left col-grow',
            style: {
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              wordWrap: 'break-word',
              width:'25%'
            }
          }, detail.description || '暂无描述'),
          h('div', {
            staticClass: 'q-mr-sm text-weight-bold text-left text-' + IsUsedEnums[detail.is_used].color,
            style: {
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              wordWrap: 'break-word',
              width:'30px'
            }
          }, [IsUsedEnums[detail.is_used].label]),
          h('div', {
            staticClass: 'q-mr-sm text-faded text-weight-bold text-left',
            style: {
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              wordWrap: 'break-word',
              width:'120px'
            }
          }, detail.create_time),
        ]),
      ])
    },
    render_no_item(h) {
      return h('div', {
        staticClass: 'row text-faded flex-center',
        style: {
          height: '36px'
        }
      }, '')
    },
    refresh_catalog(v) {
      let vm = this;
      typeof v !== 'undefined' && (this.kw = v)
      ajax_mock_proxy_search(this.kw).then(d => {
        vm.collection_list = [];
        vm.collection_list = vm.collection_list.concat(this.deal_data_to_folder_list(d.data.data) || [])
      })
    },
    collection_select(collection) {
      this.collection = collection
      this.$router.push({path: '/mock/detail', query: {id: collection.id}})
    },
    deal_data_to_folder_list(v) {
      let folder_list = [];
      v.map(d => {
        let folder_map = {}
        let contain_flag = false
        let index = 0;
        for (let i = 0; i < folder_list.length; i++) {
          if (folder_list[i].folder_name === d.domain) {
            contain_flag = true
            index = i
            break
          }
        }
        if (contain_flag) {
          let children = (folder_list[index]).children
          d.id && children.push(d)
          folder_map = {
            id: d.id,
            children: children,
            folder_name: d.domain
          }
          folder_list.splice(index, 1, folder_map)

        } else {
          let children = []
          d.id && children.push(d)
          folder_map = {
            id: d.id,
            children: children,
            folder_name: d.domain
          }
          folder_list.push(folder_map)
        }
      })
      return folder_list
    },
    render_tools(h) {
      return h('div', {
        staticClass: 'row items-center font-13 text-left',
        style: {
          height: '60px',
          width: '100%',
          marginLeft: '50px',
          zIndex: '1',
          position: 'fixed',
          marginBottom: '40px',
          top: '50px',
          right: 0,
          left: 0,
          overflow: 'hidden',
          backgroundColor: 'white',
          boxShadow: '-3px 0px 6px 0px rgba(128, 128, 128, 0.56)'
        },
      }, [
        h(LazyInput, {
            props: {
              placeholder: '请输入名称/URL链接'
            },
            staticClass: 'pp-search-input q-mr-md q-ml-md q-mt-sm',
            style: {
              width: '300px'
            },
            on: {
              input: (v) => {
                this.kw = v
                this.refresh_catalog()
              }
            }
          }
        ),
        h('q-btn', {
          staticClass: 'pp-search-button no-shadow q-mt-sm',
          props: {
            label: '新增',
            color: 'primary'
          },
          on: {
            click: () => {
              this.$router.push({path: '/mock/detail'})
            }
          }
        })
      ])
    },
  },
  render(h) {
    return h('div', {}, [
        this.render_tools(h),
        h('div', {
          style: {
            marginTop: '60px'
          }
        }, [this.collection_list.length <= 0 ? this.render_no_item(h) : this.collection_list.map(collection => [
          this.render_folder_list(h, collection)]
        )])
      ]
    )
  },
  mounted() {
    this.refresh_catalog()
  }
}
