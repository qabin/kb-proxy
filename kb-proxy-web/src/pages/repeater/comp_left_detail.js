import LazyInput from '../../components/elements/ComponentLazyInput';
import localStorage from '../../utils/local_storage_utils'
import HistoryCatalog from './comp_history_catalog'
import store from '../../store'
import CollectionCatalog from './comp_collection_catalog'

export default {
  name: 'comp_left_detail',
  data: () => ({
    kw: null,
    page: 1,
    size: 100,
    toolsInfoList: [],
    requestInfo: null,
    show_list: true,
    show_list_width: '16%',
    hidden_list_width: '0.5%',
    hidden_style: {
      borderTopRightRadius: '3px', borderBottomRightRadius: '3px'
    },
    no_hidden_style: {
      borderTopLeftRadius: '3px', borderBottomLeftRadius: '3px'
    },
    no_right_border: {
      borderRight: 'none'
    },
    select_tab: 'history-tab'
  }),
  methods: {
    render_search(h) {
      return h('div', {
        staticClass: 'q-mt-md'
      }, [
        h(LazyInput, {
          staticClass: 'pp-search-input q-ma-sm',
          props: {value: this.kw, placeholder: '按名称查找', width: 245},
          on: {
            input: v => {
              this.kw = v;
              this.$nextTick(this.refresh_catalog)
            }
          }
        })
      ])
    },
    render_tabs(h) {
      return h('div', {
        style: {
          height: '100%'
        }
      }, [
        h('div', {
          style: {
            maxHeight: '15%'
          }
        }, [h('q-tabs', {
          props: {
            value: this.select_tab,
            color: 'grey-1',
            textColor: 'faded',
          },
          style: {
            borderTop: '1px solid var(--q-color-grey-4)',
            overflow: 'auto !important'
          },
          on: {
            select: (v) => this.select_tab = v
          }
        }, [h('q-tab', {
          staticClass: 'font-14 no-wrap',
          slot: 'title',
          style: {
            width: '50%'
          },
          props: {
            name: 'history-tab',
            label: '历史',
          }
        }),
          h('q-tab', {
            staticClass: 'font-14',
            slot: 'title',
            style: {
              width: '50%'
            },
            props: {
              name: 'collection-tab',
              label: '集合',
            }
          }),

        ])]),
        h('div', {
          style: {
            height: '85%'
          }
        }, [h('div', {
          style: {
            height: '100%',
            overflow: 'auto'
          },
          attrs: {
            hidden: this.select_tab !== 'history-tab'
          }
        }, [
          h(HistoryCatalog, {
            ref: 'HistoryCatalog',
            on: {
              history_open_in_new_tab: (v) => this.$emit('history_open_in_new_tab', v),
              history_open_in_first_tab: (v) => this.$emit('history_open_in_first_tab', v),
              refresh_collection: () => this.refresh_collection_catalog()
            }
          })]),
          h('div', {
            attrs: {
              hidden: this.select_tab !== 'collection-tab'
            }
          }, [
            h(CollectionCatalog, {
              ref: 'CollectionCatalog',
              on: {
                collection_open_in_new_tab: (v) => this.$emit('collection_open_in_new_tab', v),
                collection_open_in_first_tab: (v) => this.$emit('collection_open_in_first_tab', v)
              }
            })]),])
      ])
    },
    refresh_catalog() {
      if (this.select_tab === 'history-tab') {
        this.refresh_history_catalog(this.kw)
      } else if (this.select_tab === 'collection-tab') {
        this.refresh_collection_catalog(this.kw)
      }
    },
    refresh_history_catalog() {
      this.$refs.HistoryCatalog.refresh_catalog(this.kw)
    },
    refresh_collection_catalog() {
      this.$refs.CollectionCatalog.refresh_catalog(this.kw)
    }
  },
  render(h) {
    return h('div', {
      staticClass: 'flex relative-position no-shadow pp-border-4',
      style: {
        minWidth: this.show_list ? this.show_list_width : this.hidden_list_width,
        borderLeft: 'none',
        ...this.show_list ? null : this.no_right_border
      }
    }, [
      h('div', {
        staticClass: 'absolute-left items-center',
        style: {
          width: '100%',
        },
        attrs: {
          hidden: !this.show_list
        }
      }, [this.render_search(h),
        this.render_tabs(h)]),
      h('div', {
        staticClass: 'absolute-right flex items-center no-wrap',
        style: {
          marginBottom: '100px'
        }
      }, [
        h('q-icon', {
          staticClass: 'font-14 text-white bg-primary pp-selectable-bg-blue-5 cursor-pointer',
          style: {
            ...this.show_list ? this.no_hidden_style : this.hidden_style,
            width: '10px', height: '80px', lineHeight: '80px'
          },
          props: {name: this.show_list ? 'chevron_left' : 'keyboard_arrow_right'},
          nativeOn: {
            click: () => {
              this.show_list = !this.show_list
              store.state.repeater.show_list = this.show_list
              localStorage.set('repeater_show_list', this.show_list)
            }
          }
        })
      ])
    ])
  },
  mounted() {
    localStorage.get('repeater_show_list') != null && (this.show_list = localStorage.get('repeater_show_list')) && (store.state.repeater.show_list = localStorage.get('repeater_show_list'))

  }
}
