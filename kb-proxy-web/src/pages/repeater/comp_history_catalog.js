import {
  ajax_get_history_list,
  ajax_delete_history_by_id,
  ajax_delete_all_history
} from "../../api/repeater/history/history_api";
import {MethodEnums} from "../../utils/request_dictionary";
import RequestDetailEditModal from './modal_request_detail_edit'

export default {
  name: 'comp_history_catalog',
  data: () => ({
    history_list: [],
    kw: null,
    history: null
  }),
  methods: {
    render_clear_btn(h) {
      return h('div', {
        staticClass: 'text-right bg-grey-1',
        style: {
          height: '40px',
          borderBottom: '1px solid var(--q-color-grey-4)',
          borderTop: '1px solid var(--q-color-grey-4)'
        }
      }, [h('q-icon', {
        staticClass: 'icon-red-hover cursor-pointer q-mt-sm',
        props: {
          name: 'delete_forever',
          color: 'faded',
          size: '26px'
        },
        nativeOn: {
          click: () => this.delete_all_history()
        }
      }, [h('q-tooltip', {
        props: {
          offset: [5, 5]
        }
      }, ['删除历史'])])
      ])
    },
    render_item(h, history) {
      return h('div', {
        staticClass: 'q-pl-sm q-pr-sm flex no-wrap justify-between cursor-pointer text-left pp-selected-bg-grey-2-hover',
        'class': {
          'pp-selected-bg-grey-3': this.history && this.history.id === history.id,
        },
        style: {lineHeight: '36px'},
        key: history.id,
      }, [
        h('div', {
          staticClass: 'ellipsis text-primary text-weight-bold',
          style: {
            width: '100%'
          }
        }, [h('div', {
          staticClass: 'row flex cursor-pointer no-wrap',
          style: {
            width: '100%'
          },
        }, [
          h('div', {
            staticClass: 'row no-wrap',
            style: {
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'noWrap',
              width: '90%'
            },
            on: {click: () => this.history_select(history)},
            attrs: {
              title: history.url
            }
          }, [h('div', {
            style: {
              maxWidth: '30%',
              minWidth:'15%'
            },
            staticClass: 'q-mr-sm font-10 text-weight-bold text-' + MethodEnums[history.method].color
          }, history.method),
            h('div', {
              staticClass: 'q-mr-sm text-faded',
              style: {
                width: '85%',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                wordWrap: 'break-word',
              }
            }, history.url),]),
          h('div', {
            staticClass: 'text-faded',
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
                  click: () => this.show_request_detail_add_modal(history)
                }
              }, ['保存']),
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
                    this.open_in_new_tab(history)
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
                  click: () => this.delete_by_id(history.id)
                }
              }, ['删除']),
            ])
          ])])]),
        ]),
          h('q-item-separator', {staticClass: 'q-ma-none'})])
      ])
    },
    render_no_item(h) {
      return h('div', {staticClass: 'text-faded'}, '无数据')
    },
    refresh_catalog(v) {
      let vm = this;
      typeof v !== 'undefined' && (this.kw = v)
      ajax_get_history_list(this.kw).then(data => {
        vm.history_list = [];
        vm.history_list = vm.history_list.concat(data.data || [])
      })
    },
    history_select(history) {
      this.history = history
      this.open_in_first_tab(history)
    },
    delete_by_id(id) {
      ajax_delete_history_by_id(id).then(d => {
        d.status === 1 && this.refresh_catalog()
      })
    },
    delete_all_history() {
      ajax_delete_all_history().then(d => {
        d.status === 1 && this.refresh_catalog()
      })
    },
    open_in_new_tab(history) {
      this.$emit('history_open_in_new_tab', history)
    },
    open_in_first_tab(history) {
      this.$emit('history_open_in_first_tab', history)
    },
    show_request_detail_add_modal(v) {
      v.id = null
      this.$refs.RequestDetailEditModal.show(v);
    },
  },
  render(h) {
    return h('div', {
      style: {
        height: '100%'
      }
    }, [
      h(RequestDetailEditModal, {
        ref: 'RequestDetailEditModal',
        on: {
          submit: () => {
            this.$emit('refresh_collection')
          }
        }
      }),
      this.history_list && this.history_list.length > 0 ? this.render_clear_btn(h) : null,
      h('div', {
        style: {
          overflow: 'auto',
          maxHeight: '100%'
        }
      }, [this.history_list.length <= 0 ? this.render_no_item(h) : this.history_list.map(history => [
        this.render_item(h, history)]
      )])])
  },
  mounted() {
    this.refresh_catalog()
  }
}
