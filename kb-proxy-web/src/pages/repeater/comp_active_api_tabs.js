import {MethodEnums} from '../../utils/request_dictionary'
import {RepeaterObjOptions} from '../../utils/repeater_dictionary'
import {header_arr_to_map, header_map_to_arr, string_to_json} from "../../utils/data_format_utils";

export default {
  name: 'comp_active_api_tabs',
  data: () => ({
    selectTab: null,
    tabs: [],
  }),
  watch: {
    selectTab() {
      this.selectTab && this.$emit('select', this.selectTab)
    }
  },
  methods: {
    render_tabs_item(h, tab) {
      return h('div', {
        staticClass: 'q-pa-sm row flex pp-radius-top-3 cursor-pointer pp-border-4-no-bottom no-wrap pp-selected-bg-grey-2-hover',
        'class': {
          'pp-tab-selected-top-high-light': this.selectTab && this.selectTab.id === tab.id ? true : false,
        },
        style: {
          marginRight: '3px',
          width: '200px',
        }

      }, [
        h('div', {
          staticClass: 'row flex cursor-pointer no-wrap',
          style: {
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'noWrap',
          },
          on: {
            click: () => {
              this.select_tab(tab)
            }
          }
        }, [h('div', {
          staticClass: 'q-mr-sm text-weight-bold text-' + MethodEnums[tab.method].color
        }, tab.method),
          h('div', {
            staticClass: 'q-mr-sm',
            style: {
              width: '120px',
              overflow: 'hidden',
              textOverflow: 'ellipsis'
            }
          }, tab.name || tab.url || '请输入URL')]),
        h('div', {}, [
          this.tabs.length <= 1 && !this.tabs[0].url ? null : h('q-icon', {
            staticClass: 'cursor-pointer icon-red-hover',
            props: {
              name: 'clear',
              size: '16px',
              color: 'amber'
            },
            nativeOn: {
              click: () => {
                this.close_cur_tab(tab)
              }
            }
          })
        ])
      ])
    },
    render_tabs_add(h) {
      return h('div', {
        staticClass: 'pp-border-4-no-bottom flex-center row cursor-pointer pp-radius-top-3',
        style: {
          height: '35px',
          width: '35px',
          marginRight: '3px'
        },
        on: {
          click: () => {
            let model = {
              method: MethodEnums.GET.label
            }
            this.add_active_tab(model, false);
          }
        }
      }, [h('q-icon', {
        staticClass: 'icon-red-hover',
        props: {
          name: 'add',
          color: 'primary',
          size: '16px',
        },
      })])
    },
    render_tabs_more_options(h) {
      return h('div', {
        staticClass: 'pp-border-4-no-bottom flex-center row cursor-pointer pp-radius-top-3',
        style: {
          height: '35px',
          width: '35px',
          marginRight: '3px'
        }
      }, [h('q-icon', {
        staticClass: 'icon-red-hover',
        props: {
          name: 'more_horiz',
          color: 'primary',
          size: '16px',
        },
      }),
        h('q-popover', {
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
                click: () => {
                  this.close_other_tabs()
                }
              }
            }, [
              '关闭其他'
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
                click: () => this.close_all_tabs()
              }
            }, ['关闭所有']),
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
                click: () => this.close_cur_tab()

              }
            }, ['关闭当前'])
          ])
        ])
      ])
    },
    render_tabs_list(h) {
      let count = 0;
      this.tabs.length <= 0 && this.tabs.push(
        this.new_default_tab()
      )
      !this.selectTab && this.tabs.length > 0 && (this.selectTab = this.tabs[0])
      return this.tabs.map(tab => [this.render_tabs_item(h, tab, ++count)])

    },
    select_tab(v) {
      this.selectTab = v;
    },
    refresh_url(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.URL)
    },
    refresh_name(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.NAME)
    },
    refresh_headers(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.HEADERS)
    },
    refresh_request_json(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.REQUEST_JSON)
    },
    refresh_request_form(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.REQUEST_FORM)
    },
    refresh_request_body_type(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.BODY_TYPE)
    },
    refresh_request_method(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.METHOD)
    },
    refresh_response_body(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.RESPONSE_BODY)
    },
    refresh_response_headers(v) {
      this.refresh_tabs_by_type(v, RepeaterObjOptions.RESPONSE_HEADERS)
    },
    add_active_tab(v, first) {
      if (v.method) {
        let id = 'new_' + (new Date()).getTime()
        let newTab = {
          url: v.url,
          method: v.method,
          id: id,
          headers: typeof v.headers === 'string' ? header_map_to_arr(v.headers) : v.headers,
          request_json: typeof v.request_json === 'string' ? string_to_json(v.request_json) : v.request_json,
          request_form: typeof v.request_form === 'string' ? header_map_to_arr(v.request_form) : v.request_form,
          body_type: v.body_type,
          response_body: typeof v.response_body === 'string' ? string_to_json(v.response_body) : v.response_body,
          response_headers: typeof v.response_headers === 'string' ? string_to_json(v.response_headers) : v.response_headers,
          name: v.name,
          description: v.description,
          folder_id:v.folder_id,
          folder_name:v.folder_name,
        }
        this.selectTab = {
          ...newTab
        }
        if (!first) {
          this.tabs.push({
            ...newTab
          })
          //添加元素后自动滚动到最右侧
          setTimeout(this.auto_scroll_to_right, 100)
        } else {
          this.tabs.splice(0, 1, newTab)
          //添加元素后自动滚动到最左侧
          setTimeout(this.auto_scroll_to_left, 100)
        }

      }
    },
    refresh_tabs_by_type(v, type) {
      let index = 0;
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].id === this.selectTab.id) {
          index = i
          break
        }
      }
      let copyObj = {
        ...this.selectTab
      }
      switch (type) {
        case RepeaterObjOptions.HEADERS: {
          copyObj.headers = v
          this.selectTab.headers = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        case RepeaterObjOptions.URL: {
          copyObj.url = v
          this.selectTab.url = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        case RepeaterObjOptions.REQUEST_JSON: {
          copyObj.request_json = v
          this.selectTab.request_json = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        case RepeaterObjOptions.REQUEST_FORM: {
          copyObj.request_form = v
          this.selectTab.request_form = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        case RepeaterObjOptions.BODY_TYPE: {
          copyObj.body_type = v
          this.selectTab.body_type = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        case RepeaterObjOptions.METHOD: {
          copyObj.method = v
          this.selectTab.method = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        case RepeaterObjOptions.RESPONSE_BODY: {
          copyObj.response_body = v
          this.selectTab.response_body = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        case RepeaterObjOptions.RESPONSE_HEADERS: {
          copyObj.response_headers = v
          this.selectTab.response_headers = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        case RepeaterObjOptions.NAME: {
          copyObj.name = v
          this.selectTab.name = v
          this.tabs.splice(index, 1, {
            ...copyObj
          })
          break
        }
        default:
          break
      }
    },
    auto_scroll_to_right() {
      let tabs_div = document.getElementById('tabs_list_div')
      tabs_div.scrollLeft = tabs_div.scrollWidth
    },
    auto_scroll_to_left() {
      let tabs_div = document.getElementById('tabs_list_div')
      tabs_div.scrollLeft = 0
    },
    close_other_tabs() {
      this.tabs = []
      this.tabs.push(this.selectTab)
    },
    close_all_tabs() {
      this.tabs = []
      this.selectTab = null
    },
    close_cur_tab(v) {
      let flag = false;
      if (typeof v !== 'undefined') {
        flag = this.selectTab.id === v.id
      } else {
        flag = true
        v = this.selectTab
      }
      let index = 0;
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].id === v.id) {
          index = i
          break
        }
      }
      this.tabs.splice(index, 1)
      flag && (this.selectTab = null)
      this.tabs.length <= 0 && this.tabs.push(this.new_default_tab())
    },
    new_default_tab() {
      return {
        url: null,
        method: 'GET',
        id: 'new_' + (new Date()).getTime(),
        headers: [],
        request_json: null,
        request_form: [],
        body_type: null
      }
    }
  },
  render(h) {
    return h('div', {
      staticClass: 'row items-center q-mt-md font-12 pp-border-4-bottom no-wrap',
      style: {
        marginLeft: '5px',
      }
    }, [
      h('div', {
        staticClass: 'row no-wrap',
        style: {
          maxWidth: '95%',
          overflow: 'auto',
        },
        attrs: {
          id: 'tabs_list_div'
        }
      }, [
        this.render_tabs_list(h)
      ])
      ,
      h('div', {
        staticClass: 'row no-wrap',
        style: {
          width: '5%',
          marginLeft: '3px'
        }
      }, [this.render_tabs_add(h),
        this.render_tabs_more_options(h)])
    ])
  }
}
