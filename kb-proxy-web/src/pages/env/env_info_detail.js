import {
  ajax_add_env_info,
  ajax_delete_env_info_by_id,
  ajax_env_info_search,
  ajax_start_env_info,
  ajax_stop_env_info,
  ajax_update_env_info_by_id
} from '../../api/env/env_info_api'
import PpSection from "../../components/elements/pp_section";
import Editor from "../../plugins/editor/host-editor/host-editor";

import LazyInput from '../../components/elements/ComponentLazyInput';
import {notify_ok} from "../../plugins/PpNotify";

const new_env = {
  name: null,
  hosts: null,
  id: null
}

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
  name: 'env_info_detail',
  data: () => ({
    new: false,
    edit: true,
    filter_env_list: [],
    kw: null,
  }),
  computed: {
    env_list() {
      return this.$store.state.env.env_list
    },
    selected_env() {
      return this.$store.state.env.selected_env
    }
  },
  validations: {},
  methods: {
    render_left_detail(h) {
      return h('div', {}, [
        h('div', {
          staticClass: 'row'
        }, [
          this.render_filter_input(h),
          this.render_new_env_btn(h)
        ]),
        this.render_env_list(h)])
    },
    render_new_env_btn(h) {
      return h('q-btn', {
        staticClass: 'pp-search-button no-shadow q-ml-sm',
        style: {
          height: '35px'
        },
        props: {
          label: '新增',
          color: 'positive',
        },
        nativeOn: {
          click: () => {
            this.new = true
            this.edit = true
            this.$store.state.env.selected_env = new_env
          }
        }
      })
    },
    render_env_list(h) {
      return h('div', {
        staticClass: 'col-grow',
        style: {
          height: '770px'
        }
      }, [this.render_env_catalog(h)])
    },
    render_env_catalog(h) {
      !this.new && this.selected_env.name == null && (this.$store.state.env.selected_env = this.filter_env_list.length <= 0 ? new_env : this.filter_env_list[0])
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
          'bg-blue-5 text-white': this.selected_env && this.selected_env.id === env.id ? true : false,
        },
        style: {lineHeight: '36px'},
        on: {
          click: () => {
            this.new = false
            this.edit = false
            this.$store.state.env.selected_env = env
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
              'text-white': this.selected_env.id === env.id ? true : false
            }
          }, [EnvStatusEnum[env.status].label]),
        ])])
      ])
    },
    render_filter_input(h) {
      return h(LazyInput, {
        staticClass: 'pp-search-input q-mb-sm',
        style: {
          width: '200px'
        },
        props: {value: this.kw, placeholder: '按名称查找'},
        on: {
          input: v => {
            this.kw = v;
            this.$nextTick(this.filter_env_list_by_kw)
          }
        }
      })
    },
    render_right_detail(h) {
      return h('div', {}, [this.render_header(h), h(PpSection, {
        staticClass: 'font-13 text-dark q-mb-md',
        props: {label: 'HOST配置', collapse: false}
      }, [
        this.render_config_info(h)])])
    },
    render_config_info(h) {
      return h(Editor, {
        props: {
          disable: !this.edit,
          value: this.selected_env.hosts || null,
          width: '100%',
          toolbar: true,
          height: '644px'
        },
        on: {
          input: (v) => this.selected_env.hosts = v
        }
      })
    },
    render_header(h) {
      return h('div', {
        staticClass: 'q-mb-md bg-grey-3 q-pa-sm row no-wrap items-center text-left pp-border-4'
      }, [
        h('div', {
          staticClass: 'q-mr-sm'
        }, [
          h('q-icon', {
            props: {
              name: 'settings',
              color: 'primary',
              size: '50px'
            }
          })
        ]),
        h('div', {
          staticClass: 'col-grow'
        }, [h('div', {
          staticClass: 'font-16 text-weight-bold ellipsis',
          style: {
            maxWidth: '600px'
          }
        }, [
          this.edit ? h('q-input', {
            staticClass: 'pp-input-focus font-16',
            'class': {
              'pp-border-red': this.selected_env.name ? false : true
            },
            style: {
              width: '300px'
            },
            props: {
              hideUnderline: true,
              placeholder: '请输入环境名称',
              value: this.selected_env.name,
            },
            on: {
              input: (v) => {
                this.selected_env.name = v
              }
            }
          }) : h('span', {}, [this.selected_env.name || '请输入环境名称'])
        ]),
          h('div', {
            staticClass: 'font-13',
            style: {
              marginTop: '3px',
              width: '900px',
              overflow: 'hidden',
              display: '-webkit-box',
              webkitLineClamp: 2,
              webkitBoxOrient: 'vertical'
            }
          }, [
            this.edit ? h('q-input', {
              staticClass: 'pp-input-focus font-13',
              style: {
                paddingLeft: '4px'
              },
              props: {
                type: 'textarea',
                hideUnderline: true,
                maxHeight: 20,
                rows: 2,
                placeholder: '请输入描述',
                value: this.selected_env.description,
              },
              on: {
                input: (v) => this.selected_env.description = v
              }
            }) : h('span', [this.selected_env.description || '请输入描述'])
          ])]),
        h('div', {}, [
          this.selected_env.id != null ? (!this.new_create ? h('q-btn', {
            staticClass: 'pp-search-button no-shadow q-mr-sm',
            props: {
              label: '编辑',
              color: this.edit ? 'teal-2' : 'secondary'
            },
            on: {
              click: () => this.edit = !this.edit
            }
          }) : null) : null,
          h('q-btn', {
            staticClass: 'pp-search-button no-shadow q-mr-sm',
            props: {
              label: '保存',
              color: 'primary'
            },
            on: {
              click: () => {
                this.save_env_config()
              }
            }
          }),
          this.selected_env.id != null ? (!this.new_create ? h('q-btn', {
            staticClass: 'pp-search-button no-shadow q-mr-sm',
            props: {
              label: this.selected_env.status === -1 ? '启用' : '禁用',
              color: this.selected_env.status === -1 ? 'positive' : 'warning',
            },
            on: {
              click: () => {
                this.update_env_status()
              }
            }
          }) : null) : null,
          this.selected_env.id != null ? (!this.new_create ? h('q-btn', {
            staticClass: 'pp-search-button no-shadow q-mr-sm',
            props: {
              label: '删除',
              color: 'negative'
            },
            on: {
              click: () => this.delete_env()
            }
          }) : null) : null,
        ])
      ])
    },
    refresh_env_list() {
      ajax_env_info_search().then(d => {
        if (d.status === 1) {
          this.$store.state.env.env_list = d.data || []
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
    filter_env_list_by_kw() {
      this.env_list && (this.filter_env_list = this.env_list.filter(d => d.name.indexOf(this.kw) !== -1))
    },
    save_env_config() {
      let vm = this
      if (this.selected_env.id) {
        ajax_update_env_info_by_id(this.selected_env.id, this.selected_env).then(d => {
          if (d.status === 1) {
            vm.$q.ok('修改环境成功！')
            vm.new = false
            vm.$store.state.env.selected_env = d.data
            vm.refresh_env_list()
          }
        })
      } else {
        ajax_add_env_info(this.selected_env).then(d => {
          if (d.status === 1) {
            vm.$q.ok('新增环境成功！')
            vm.new = false
            vm.$store.state.env.selected_env = d.data
            vm.refresh_env_list()
          }
        })
      }
    },
    update_env_status() {
      let vm = this
      if (this.selected_env.status === 1) {
        ajax_stop_env_info(this.selected_env.id).then(d => {
          if (d.status === 1) {
            notify_ok("已停用！")
            vm.$store.state.env.selected_env.status = -1
            vm.new = false
            vm.refresh_env_list()
          }
        }).catch(e => {
        })
      } else {
        ajax_start_env_info(this.selected_env.id).then(d => {
          if (d.status === 1) {
            notify_ok("已启用！")
            vm.$store.state.env.selected_env.status = 1
            vm.$store.state.env.active_env=this.selected_env
            vm.new = false
            vm.refresh_env_list()
          }
        }).catch(e => {
        })
      }
    },
    delete_env() {
      let vm = this;
      ajax_delete_env_info_by_id(this.selected_env.id).then(d => {
        if (d.status === 1) {
          notify_ok("已删除！")
          vm.$store.state.env.selected_env = new_env
          vm.new = false
          vm.refresh_env_list()
        }
      }).catch(e => {
      })
    }
  },
  mounted() {
    this.refresh_env_list()
  },
  render(h) {
    return h('div', {
      staticClass: 'q-pl-md q-pr-md',
      style: {
        marginTop: '18px'
      }
    }, [
      h('div', {
        staticClass: 'row',
        style: {
          width: '100%'
        }
      }, [
        h('div', {
          staticClass: 'q-mr-md',
          style: {
            width: '268px'
          }
        }, [
          this.render_left_detail(h)]),
        h('div', {
          staticClass: 'col-grow'
        }, [
          this.render_right_detail(h)])
      ])
    ])
  }
}
