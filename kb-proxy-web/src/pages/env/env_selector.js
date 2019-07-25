import LazyInput from "../../components/elements/ComponentLazyInput";
import {ajax_env_info_search, ajax_start_env_info} from "../../api/env/env_info_api";
import {notify_ok} from "../../plugins/PpNotify";

const EnvStatusEnum = {
  1: {
    label: '启用',
    color: 'positive'
  },
  '-1': {
    label: '禁用',
    color: 'warning'
  }
}

export default {
  name: 'env_selector',
  data: () => ({
    //env_list: [],
    filter_env_list: [],
    kw: null,
    show: true,
  }),
  computed: {
    active_env() {
      return this.$store.state.env.active_env
    },
    env_list() {
      return this.$store.state.env.env_list
    }
  },
  methods: {
    render_title(h) {
      return h('div', {
        staticClass: 'col-grow text-center',
        style: {
          minWidth: '160px'
        },
      }, [this.active_env.name ? this.active_env.name : '请选择环境'])
    },
    render_filter_input(h) {
      return h(LazyInput, {
        staticClass: 'pp-search-input',
        props: {value: this.kw, placeholder: '按名称查找'},
        on: {
          input: v => {
            this.kw = v;
            this.$nextTick(this.filter_env_list_by_kw)
          }
        }
      })
    },
    filter_env_list_by_kw() {
      this.env_list && (this.filter_env_list = this.env_list.filter(d => d.name.indexOf(this.kw) !== -1))
    },
    refresh_env_list() {
      ajax_env_info_search().then(d => {
        if (d.status === 1) {
          this.$store.state.env.env_list = d.data || []
          this.env_list.forEach(env => {
            if (env.status === 1) {
              this.$store.state.env.active_env = env
            }
          })
          this.filter_env_list = this.env_list.sort((a, b) => {
            return b.status - a.status
          })

        } else {
          this.$q.err('获取环境列表失败！')
        }
      }).catch(e => {
        this.$q.err('获取环境列表失败！')
      })
    },
    render_env_list(h) {
      return h('div', {
        staticClass: 'col-grow scroll',
        style: {
          height: '770px'
        }
      }, [this.render_env_catalog(h)])
    },
    render_env_catalog(h) {
      return h('div', {
        staticClass: 'font-13 text-dark scroll pp-border-3 bg-grey-1',
        style: {
          height: '100%'
        }
      }, [
        this.filter_env_list.map(env => [
          this.render_env_item(h, env),
          h('q-item-separator', {staticClass: 'q-ma-none'})])
      ])
    },
    render_env_item(h, env) {
      return h('div', {
        staticClass: 'q-pl-sm q-pr-sm flex no-wrap justify-between cursor-pointer text-left text-primary',
        'class': {
          'bg-blue-5 text-white': this.active_env.id === env.id
        },
        style: {lineHeight: '36px'},
        on: {
          click: () => {
            this.$store.state.env.active_env = env
            this.show = false
            this.switch_env_on_start()
          }
        },
      }, [
        h('div', {
          staticClass: 'ellipsis col-grow',
        }, [h('div', {
          staticClass: 'row no-wrap text-weight-bold'
        }, [
          h('span', {
            staticClass: 'q-mr-md ellipsis',
            style: {
              width: '160px'
            }
          }, [env.name]),
          h('span', {
            staticClass: 'q-mr-md col-grow text-right text-' + EnvStatusEnum[env.status].color,
            'class': {
              'text-white': this.active_env.id === env.id ? true : false
            }

          }, [EnvStatusEnum[env.status].label]),
        ])])
      ])
    },
    switch_env_on_start() {
      let vm = this;
      if (this.active_env.status !== 1) {
        ajax_start_env_info(this.active_env.id).then(d => {
          if (d.status === 1) {
            notify_ok("切换环境成功！")
            this.$store.state.env.active_env.status = -1
            vm.refresh_env_list()
          }
        }).catch(e => {
        })
      }
    },
  },
  activated() {
    this.refresh_env_list()
  },
  render(h) {
    return h('div', {
        staticClass: 'flex no-wrap items-center justify-between full-height q-pl-md q-pr-md font-14 cursor-pointer',
        style: {minWidth: '200px', backgroundColor: 'rgba(255, 255, 255,0.1)'}
      }, [
        h('div', {
          staticClass: 'row no-wrap',
          on: {
            click: () => this.show = true
          }
        }, [this.render_title(h),
          h('i', {staticClass: 'material-icons font-20', style: {width: '18px'}}, 'keyboard_arrow_down'),]),

        this.show ? h('q-popover', {
          staticClass: 'column no-wrap non-selectable',
          ref: 'popup',
          directives: [{name: 'close-overlay'}]
        }, [
          h('div',{
            staticClass:'q-pa-sm'
          }, [this.render_filter_input(h)]),
          h('div', [this.render_env_list(h)])
        ]) : null
      ]
    )
  }
}
