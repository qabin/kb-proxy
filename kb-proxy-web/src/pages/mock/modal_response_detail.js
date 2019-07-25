import PPSection from '../../components/elements/pp_section'
import Editor from '../../plugins/editor/vue-editor/vue-editor'

export default {
  name: 'modal_response_detail',
  data: () => ({
    show: false,
  }),
  props: {
    value: {
      required: true,
      type: [String, Object]
    }
  },
  methods: {
    render_header_close_btn(h) {
      return h('div', {
        slot: 'end',
        staticClass: 'cursor-pointer'
      }, [
        h('q-icon', {
          staticClass: 'icon-red-hover',
          props: {
            name: 'clear',
            size: '20px',
            color: 'primary'
          },
          nativeOn: {
            click: () => {
              this.show = false;
            }
          }
        }, [h('q-tooltip', {
          props: {
            offset: [5, 5]
          }
        }, ['关闭'])])
      ])
    },
    render_response_body(h) {
      return h(PPSection, {
        staticClass: 'font-13 text-dark',
        props: {
          label: '响应结果'
        }
      }, [
        this.render_header_close_btn(h),
        h(Editor, {
          props: {
            disable: true,
            value: this.value,
            width: '100%',
            toolbar: false,
            height: '780px'
          }
        })
      ])
    },
  },
  render(h) {
    return h('div', {}, [
      h('span', {
        staticClass: 'text-primary text-weight-bold cursor-pointer',
        on: {
          click: () => {
            this.show = true
          }
        }
      }, ['详情']),
      h('div', {
        staticClass: 'items-center font-13 text-left pp-text-wrap',
        style: {
          width: '50vw',
          zIndex: 1000,
          display: 'none',
          position: 'fixed',
          top: '50px',
          right: 0,
          bottom: 0,
          overflow: 'auto',
          backgroundColor: 'white',
          boxShadow: '-3px 0px 6px 0px rgba(128, 128, 128, 0.56)'
        },
        directives: [{
          name: 'show',
          value: this.show
        }]
      }, [
        h('div', {
          staticClass: 'q-ma-md'
        }, [
          this.render_response_body(h)
        ])
      ])])
  }
}
