import ModalMixin from "../../components/elements/MixinsBaseModal"
import {required} from "vuelidate/lib/validators"
import {ajax_add_request_detail, ajax_update_request_detail} from "../../api/repeater/collection/request_detail_api"
import PpField from "../../components/elements/pp_field"
import FolderSelector from '../../components/elements/ComponentFolderSelector'

export default {
  name: 'modal_request_detail_add',
  mixins: [ModalMixin],
  props: {},
  data: () => ({
    model: {
      id: null,
      url: null,
      method: null,
      headers: [],
      request_json: null,
      request_form: null,
      body_type: null,
      folder_id: null,
      name: null,
      description: null
    },
    folder: null,
    generic_placeholder: '待添加',
    labelWidth: 110,
    minWidth: '550px',
    maxWidth: '550px',
  }),
  validations: {
    model: {
      name: {required},
      folder_id: {required},
    },
  },
  watch: {
    folder: {
      handler(nv, ov) {
        nv && nv.value && (this.model.folder_id = nv.value)
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    title() {
      return '编辑请求信息'
    },
    init(v) {
      this.model = {
        id: v.id,
        url: v.url,
        method: v.method,
        headers: v.headers,
        request_json: v.request_json,
        request_form: v.request_form,
        body_type: v.body_type,
        folder_id: v.folder_id,
        name: v.name,
        description: v.description
      };
      this.folder = {
        value: v.folder_id,
        label: v.folder_name
      }
    },
    submit({done, close}) {
      if (this.model.id) {
        ajax_update_request_detail(this.model)
          .then(() => {
            close();
            this.$emit('submit');
          })
          .catch(() => {
            done();
            this.$q.err('请求保存失败!');
          });
      } else {
        ajax_add_request_detail(this.model)
          .then(() => {
            close();
            this.$emit('submit');
          })
          .catch(() => {
            done();
            this.$q.err('请求保存失败!');
          });
      }

    },
    cancel() {
    },
    field_class(others) {
      return 'q-mb-md ' + others
    },
    render_contents(h) {
      return [
        this.render_name(h),
        this.render_folder(h),
        this.render_description(h),

      ];
    },
    render_name(h) {
      return h(PpField, {
        staticClass: this.field_class(),
        props: {labelWidth: this.labelWidth, label: '名称', required: true, error: this.$v.model.name.$error}
      }, [
        h('q-input', {
          props: {
            color: 'tertiary',
            value: this.model.name,
            placeholder: this.generic_placeholder,
            hideUnderline: true
          },
          on: {
            input: (v) => {
              this.model.name = v
              this.$emit('name', v)
            }
          }
        })
      ])
    },
    render_folder(h) {
      return h(PpField, {
        staticClass: this.field_class(),
        props: {labelWidth: this.labelWidth, label: '文件夹', required: true, error: this.$v.model.folder_id.$error}
      }, [
        h(FolderSelector, {
          props: {
            value: this.folder,
            placeholder: this.generic_placeholder
          },
          on: {
            input: (v) => {
              this.folder = v
              this.$emit('folder', v)
            }
          }
        })
      ])
    },
    render_description(h) {
      return h('q-input', {
        staticClass: 'q-pa-xs q-mt-lg pp-radius-3 exclude_selectable',
        style: {border: '1px solid var(--q-color-grey-5)', marginLeft: '8px'},
        attrs: {rows: '7'},
        props: {
          type: 'textarea',
          maxHeight: 100,
          color: 'tertiary',
          value: this.model.description,
          placeholder: '描述',
          hideUnderline: true
        },
        on: {
          input: (v) => {
            this.model.description = v
            this.$emit('description', v)
          }
        }
      })
    }

  }
}
