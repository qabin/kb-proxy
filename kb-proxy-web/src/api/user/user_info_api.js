import {axiosInstance} from '../../plugins/axios'

export function ajax_get_user_info() {
  return axiosInstance({
    url: '/api/user/info',
    method: 'get',
  })
}

export function ajax_update_user_info(model) {
  return axiosInstance({
    url: '/api/user/info',
    method: 'patch',
    data: model
  })
}
