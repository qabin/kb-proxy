export default {
  props: {
    value: String,
    placeholder: {type: String, default: '按标题筛选...'},
    delay: {type: Number, default: 300},
    width: {type: Number, default: 300}
  },
  data: () => ({
    timer: null,
    fakeValue: ''
  }),
  watch: {
    value(v) {
      this.fakeValue = v;
    }
  },
  methods: {
    input(v) {
      this.fakeValue = v;
      if (this.timer)
        clearTimeout(this.timer);

      this.timer = setTimeout(() => {
        this.$emit('input', this.fakeValue);
      }, this.delay)

    }
  },
  render(h) {
    return h('q-input', {
      staticClass: 'shadow-0 pp-border-5 pp-radius-1',
      style: {padding: '4px', width: this.width + 'px'},
      props: {
        value: this.fakeValue,
        placeholder: this.placeholder,
        hideUnderline: true,
        color: 'primary',
        before: [{icon: 'search'}],
      },
      on: {input: this.input}
    }, [
      this.fakeValue
        ? h('q-icon', {
          slot: 'after',
          staticClass: 'text-faded cursor-pointer pp-selectable-color-primary',
          style: {fontSize: '18px'},
          props: {name: 'cancel'},
          nativeOn: {
            click: () => {
              this.fakeValue = '';
              this.$emit('input', '')
            }
          }
        })
        : null
    ])
  }
}
