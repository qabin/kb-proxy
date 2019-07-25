import {ajax_add_folder} from "../../api/repeater/collection/folder_api";

export default {
  data: () => ({
    kw: '',
    filter_placeholder: '关键字过滤',
    filter_distinct_key: 'title',
    filter_icon: 'filter_list',
    raw_options_filtered: [],
    show_add_folder_input: false,
    folder_name:null
  }),
  watch: {
    raw_options(v) {
      v && (this.raw_options_filtered = v)
    }
  },
  methods: {
    render_list_top(h) {
      return h('div',{},[
        h('q-input', {
          staticClass: 'font-12 q-pl-xs q-pr-xs q-ml-xs q-mr-xs q-mt-xs pp pp-border-4 pp-radius-3 bg-grey-1',
          props: {
            value: this.kw,
            placeholder: this.filter_placeholder,
            hideUnderline: true,
            before: [{icon: this.filter_icon}],
          },
          on: {
            input: v => {
              this.kw = v;
              if (v)
                this.raw_options_filtered = this.raw_options.filter(option => {
                  return this.__filter(option, v)
                });
              else
                this.raw_options_filtered = this.raw_options;
            }
          },
          ref: 'input'
        }),
        h('div', {
          staticClass: 'text-left',
        }, [!this.show_add_folder_input ? h('q-btn', {
          staticClass: 'pp-search-btn-sm',
          style:{
            marginTop:'1px'
          },
          props: {
            label:'添加文件夹',
            color: 'primary',
            size: '12px',
            icon:'add',
            flat:true,
          },
          nativeOn: {
            click: () => this.show_add_folder_input = !this.show_add_folder_input
          }
        }) : h('div', {
          staticClass: 'row no-wrap items-center q-pt-sm q-pb-sm',
          style:{
            marginLeft:'3px'
          }
        }, [
          h('q-input', {
            staticClass: 'pp-search-input-sm',
            props: {
              placeholder: '输入文件夹名',
              hideUnderline: true,
              value: this.folder_name
            },
            style: {
              width: '75%'
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
      ])
    },
    options() {
      return this.raw_options_filtered;
    },
    __show() {
      this.kw = '';
      this.raw_options_filtered = this.raw_options = [];
      this.$refs.input.focus();
      this.show()
    },
    __filter(option, kw) {
      if (!option)
        return false;

      if (typeof option === 'string')
        return option.includes(kw);
      else if (typeof option === 'number')
        return option.toString().includes(kw);
      else
        return this.__filter(option[this.filter_distinct_key], kw)
    },
    add_folder() {
      this.folder_name && ajax_add_folder(this.folder_name).then(d => {
        d.status === 1 && this.__show()
      })
    },
  }
}
