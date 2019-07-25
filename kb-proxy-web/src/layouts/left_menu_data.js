import store from '../store/index'

const left_menu_data = [
  {
    label: '首页',
    to: '/index',
    icon: 'home',
  },
  {
    label: '重发',
    to: '/repeater',
    icon: 'redo',
  },
  {
    label: 'Mock',
    to: '/mock',
    icon: 'vpn_lock',
  },
  {
    label: '环境',
    to: '/env',
    icon: 'settings',
  },
]

export {left_menu_data}

const route_menu_cache = {}

const cache_route = (path, item) => {
  route_menu_cache[path] = item
}

export const route2menu = (path) => {
  if (route_menu_cache[path]) {
    return route_menu_cache[path]
  }
  let res = []
  for (let i in left_menu_data) {
    let item = left_menu_data[i]
    if (item.to === path && !item.ignore) {
      res.push(item)
    } else if (item.children) {
      for (let j in item.children) {
        let child_item = item.children[j]
        if (child_item.to && child_item.to === path) {
          res.push(item)
          res.push(child_item)
        }
      }
    }
  }
  cache_route(path, res)
  return res;
};
