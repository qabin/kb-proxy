//import JsonEditor from '../../plugins/jsoneditor/VueJsoneditor'
import BodyFormTable from './comp_request_body_form_table'
import {BodyTypeOptions} from "../../utils/repeater_dictionary";
import JsonEditor from '../../plugins/editor/vue-editor/vue-editor'
import {string_to_json} from "../../utils/data_format_utils";

export default {
  name: 'comp_request_body',
  data: () => ({
    request_json: null,
    request_form: [],
    body_type: null
  }),
  methods: {
    render_type_radio(h) {
      return h('div', {
        staticClass: 'text-left q-pt-md q-pb-md bg-grey-1 pp-border-4-no-bottom'
      }, [
        h('q-radio', {
          props: {
            val: BodyTypeOptions.JSON,
            label: 'Json',
            value: this.body_type
          },
          on: {
            input: (v) => {
              this.body_type = v
              this.$emit('request_body_type', v)
            }
          }
        }),
        h('q-radio', {
          props: {
            val: BodyTypeOptions.FORM,
            label: 'X-www-Form-UrlEncoded',
            value: this.body_type
          },
          on: {
            input: (v) => {
              this.body_type = v
              this.$emit('request_body_type', v)
            }
          }
        }),
      ])
    },
    render_json_body(h) {
      return h(JsonEditor, {
        props: {
          height: '300px',
          value: this.request_json,
          toolbar: false,
          type:'JSON'
        },
        on: {
          input: (v) => {
            this.request_json = v
            this.$emit('request_json_input', string_to_json(v))
          }
        }
      })
    },
    render_www_form_table(h) {
      return h(BodyFormTable, {
        ref: 'BodyFormTable',
        props: {
          default_params: this.request_form
        },
        on: {
          input: (v) => {
            this.request_form = v
            this.$emit('request_form_input', this.request_form)
          }
        }
      })
    },
    refresh_request_body(json, form, body_type) {
      this.request_form = form
      this.request_json = json
      this.body_type = body_type
      this.$refs.BodyFormTable.refresh_table(form)
    },
  },
  render(h) {
    return h('div', {}, [
      this.render_type_radio(h),
      h('div', {}, [
        h('div', {
          attrs: {
            hidden: this.body_type !== BodyTypeOptions.JSON
          }
        }, [this.render_json_body(h)]),
        h('div', {
          attrs: {
            hidden: this.body_type === BodyTypeOptions.JSON
          }
        }, [this.render_www_form_table(h)])
      ])
    ])
  }
}
