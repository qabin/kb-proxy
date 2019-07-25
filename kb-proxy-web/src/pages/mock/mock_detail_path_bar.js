export default {
  name: 'mock_detail_path_bar',
  data: () => ({
    breadcrumbsData: [
      {
        label: 'Mock列表',
        icon: 'vpn_lock',
        to: '/mock'
      },
      {
        name: 'mock_detail',
        label: 'Mock详情',
      }
    ],
  }),
  methods: {},
  render(h) {
    return h('div', {
      staticClass: 'row no-wrap q-ml-md items-center q-mt-sm'
    }, [
      h('div', {}, [
        h('q-icon', {
          staticClass: 'cursor-pointer',
          props: {
            name: 'arrow_back_ios',
            size: '20px',
            color: 'primary'
          },
          nativeOn: {
            click: () => {
              this.$router.back()
            }
          }
        }),
      ]),
      h('q-breadcrumbs', {
        staticClass: 'font-14',
        style: {
          height: '40px',
        }
      }, [
        this.breadcrumbsData.map(menu => [
          h('q-breadcrumbs-el', {
            props: {
              label: menu.label,
              icon: menu.icon,
              to: menu.to
            }
          })
        ])])
    ])
  }
}
