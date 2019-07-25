//import JsonEditor from '../../plugins/jsoneditor/VueJsoneditor'
import VueEditor from '../../plugins/editor/vue-editor/vue-editor'

export default {
  name: 'comp_response_json',
  data: () => ({
    response: null
  }),
  methods: {
    render_json_response(h) {
      return h(VueEditor, {
        props: {
          height: '450px',
          value: this.response,
          disable: true
        }
      })
    },
    refresh_response(v) {
      this.response = v || null
    },
  },
  render(h) {
    return h('div', {
    }, [
      this.render_json_response(h)
    ])
  },
}
