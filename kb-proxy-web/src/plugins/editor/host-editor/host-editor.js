import VueEditor from '../vue-editor/vue-editor'
import 'brace/mode/sh'
import 'brace/theme/eclipse'

export default {
  name:'host_editor',
  mixins:[VueEditor],
  methods:{
    render_tools_bar_left(h) {
      return h('div', {
        staticClass: 'row items-center col-grow text-left full-height'
      }, [])
    },
    initView() {
      this.contentBackup = this.value && this.value !== null ? this.value : ''
      let vm = this;
      let editor_div = this.$refs.vue_editor
      let editor = vm.editor = ace.edit(editor_div)
      this.disable && editor_div.classList.add('ace_content_disable')

      this.$emit('init', editor)

      editor.$blockScrolling = Infinity
      editor.setOption("enableEmmet", false)
      editor.getSession().setMode('ace/mode/sh')
      editor.setTheme('ace/theme/eclipse')
      editor.getSession().setUseWrapMode(true)
      editor.setShowPrintMargin(false)
      editor.setValue(this.contentBackup)

      editor.on('change', vm.onChange);
      if (vm.disable) {
        editor.setReadOnly(true)
      }
    },
  }
}
