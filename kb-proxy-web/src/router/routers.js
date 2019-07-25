import left from '../layouts/left-menu'
import header from '../layouts/header-menu'
import footer from '../layouts/footer-menu'
import mock_detail_path from '../pages/mock/mock_detail_path_bar'

export default [
  {
    path: '',
    component: () => import('../layouts/index'),
    children: [
      {
        path: '/',
        components: {
          left, header, footer,
          page: () => import('../pages/proxy/index')
        }
      },
      {
        path: '/index',
        components: {
          left, header, footer,
          page: () => import('../pages/proxy/index')
        }
      },
      {
        path: '/repeater',
        components: {
          left, header, footer,
          page: () => import('../pages/repeater/index')
        }
      },
      {
        path: '/mock',
        components: {
          left, header, footer,
          page: () => import('../pages/mock/index')
        }
      },
      {
        path: '/mock/detail',
        components: {
          left, header, footer, path: mock_detail_path,
          page: () => import('../pages/mock/mock_detail')
        }
      },
      {
        path: '/env',
        components: {
          left, header, footer,
          page: () => import('../pages/env/index')
        }
      },
    ]
  },
  {
    path: '/login',
    component: () => import('../pages/user/login')
  },
  {
    path: '/register',
    component: () => import('../pages/user/register')
  },
]
