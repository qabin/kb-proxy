import {MethodOptions} from '../../utils/request_dictionary'
import MimeTypeTabs from './comp_request_mime_type_tabs'
import {ajax_request_api_by_server} from "../../api/utils/api_request_utils";
import ResponseMimeTypeTabs from './comp_response_mime_type_tabs'
import {string_to_json, header_arr_to_map} from '../../utils/data_format_utils'
import RequestDetailEditModal from './modal_request_detail_edit'

export default {
  name: 'compRightDetail',
  data: () => ({
    headers: [],
    request_json: null,
    request_form: [],
    request_body: null,
    method: 'GET',
    url: '',
    body_type: null,
    name: null,
    description: null,
    folder_id: null,
    folder_name: null,
  }),
  watch: {
    url() {
      if (this.url && !this.url.startsWith("http")) {
        this.url = "http://" + this.url
      }
    }
  },
  methods: {
    render_request_desc(h) {
      return h('div', {
        staticClass: 'row bg-grey-1',
        style: {
          borderBottom: '1px solid var(--q-color-grey-4)',
          borderLeft: '1px solid var(--q-color-grey-4)',
        }
      }, [
        h('div', {}, [h('q-item', [h('q-item-side', [
          h('q-icon', {
            props: {color: 'primary', name: 'flash_on', size: '40px'}
          })
        ]),
          h('q-item-main', [
            h('div', {staticClass: 'font-18 text-weight-bold text-primary'}, this.name || '请求暂未命名'),
            h('div', {staticClass: 'font-13 text-faded'}, this.description || '暂无描述')
          ])])])
      ])
    },
    render_request_url(h) {
      return h('div', {
        staticClass: 'row items-center flex q-mt-sm'
      }, [
        h('q-select', {
          staticClass: 'pp-search-input',
          style: {
            width: '100px',
            marginRight: '3px'
          },
          props: {
            value: this.method,
            options: MethodOptions,
            hideUnderline: true
          },
          on: {
            input: (v) => {
              this.method = v
              this.$emit('method_select', v)
            }
          }
        }),
        h('q-input', {
          staticClass: 'pp-search-input col-grow q-mr-sm',
          props: {
            value: this.url,
            hideUnderline: true,
            placeholder: '请输入请求URL'
          },
          on: {
            input: (v) => this.url_input_event(v)
          }
        }),
        h('q-btn', {
          staticClass: 'pp-search-button q-mr-sm no-shadow',
          props: {
            label: '发送',
            color: 'primary',
            disable: !this.url || this.url.length <= 0
          },
          on: {
            click: () => {
              ajax_request_api_by_server(this.url,
                this.method,
                header_arr_to_map(this.headers),
                this.request_json ? JSON.stringify(this.request_json) : null,
                header_arr_to_map(this.request_form),
                this.body_type)
                .then(r => {
                  this.$emit('response_body', string_to_json(r.response))
                  this.$emit('response_headers', r.headers)
                  this.$emit('refresh_history')
                  this.refresh_response(string_to_json(r.response), r.headers)
                })
            }
          }
        }),
        h('q-btn', {
          staticClass: 'pp-search-button q-mr-sm no-shadow',
          props: {
            label: '保存',
            color: 'secondary'
          },
          on: {
            click: () => this.show_request_detail_add_modal()
          }
        })
      ])
    },
    url_input_event(v) {
      this.url = v
      this.$refs.MimeTypeTabs.refresh_mime_type_pane(this.url, this.headers, this.request_json, this.request_form, this.body_type)
      this.$emit('url_input', v)
    },
    active_tab_select(v) {
      this.url = v.url
      this.method = v.method
      this.headers = v.headers
      this.request_json = v.request_json
      this.request_form = v.request_form
      this.body_type = v.body_type
      this.name = v.name
      this.description = v.description
      this.folder_id = v.folder_id
      this.folder_name = v.folder_name
      this.refresh_response(v.response_body, v.response_headers)
      this.$refs.MimeTypeTabs.refresh_mime_type_pane(this.url, this.headers, this.request_json, this.request_form, this.body_type)
    },
    refresh_response(body, headers) {
      this.$refs.ResponseMimeTypeTabs.refresh_response(body)
      this.$refs.ResponseMimeTypeTabs.refresh_response_headers(headers)
    },
    show_request_detail_add_modal() {
      let model = {
        url: this.url,
        method: this.method,
        headers: JSON.stringify(header_arr_to_map(this.headers)),
        request_json: JSON.stringify(this.request_json),
        request_form: JSON.stringify(header_arr_to_map(this.request_form)),
        body_type: this.body_type,
        folder_id: this.folder_id,
        name: this.name,
        description: this.description,
        folder_name: this.folder_name
      }
      this.$refs.RequestDetailEditModal.show(model);
    },
  },
  render(h) {
    return h('div', {
      style: {
        marginLeft: '5px'
      }
    }, [
      this.render_request_desc(h),
      this.render_request_url(h),
      h(MimeTypeTabs, {
        ref: 'MimeTypeTabs',
        props: {
          default_url: this.url,
          default_headers: this.headers
        },
        on: {
          url_input: (v) => {
            this.url = v
            this.$emit('url_input', this.url)
          },
          header_input: (v) => {
            this.headers = v
            this.$emit('header_input', this.headers)
          },
          request_json_input: (v) => {
            this.request_json = v
            this.$emit('request_json_input', v)
          },
          request_form_input: (v) => {
            this.request_form = v
            this.$emit('request_form_input', v)
          },
          request_body_type: (v) => {
            this.body_type = v
            this.$emit('request_body_type', v)
          }
        }
      }),
      h(ResponseMimeTypeTabs, {
        ref: 'ResponseMimeTypeTabs'
      }),
      h(RequestDetailEditModal, {
        ref: 'RequestDetailEditModal',
        on: {
          folder: (v) => {
            if (v) {
              this.folder_id = v.value
              this.folder_name = v.label
            }
          },
          name: (v) => {
            this.name = v
            this.$emit('name_input', v)
          },
          description: (v) => {
            this.description = v
          },
          submit: () => {
            this.$emit('refresh_collection')
          }
        }
      })
    ])
  }
}
