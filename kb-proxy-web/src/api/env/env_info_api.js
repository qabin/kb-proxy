import {axiosInstance} from "../../plugins/axios";

export function ajax_add_env_info(model) {
  return axiosInstance({
    url: '/api/env',
    method: 'post',
    data: model
  })
}


export function ajax_env_info_search(kw) {
  let form = {
    kw: kw
  }
  return axiosInstance({
    url: '/api/env/_search',
    method: 'get',
    params: form
  })
}


export function ajax_delete_env_info_by_id(id) {
  return axiosInstance({
    url: '/api/env/' + id,
    method: 'delete',
  })
}

export function ajax_update_env_info_by_id(id, body) {
  return axiosInstance({
    url: '/api/env/' + id,
    method: 'patch',
    data: body
  })
}

export function ajax_start_env_info(id) {
  return axiosInstance({
    url: '/api/env/' + id + '/_start',
    method: 'get',
  })
}

export function ajax_stop_env_info(id) {
  return axiosInstance({
    url: '/api/env/' + id + '/_stop',
    method: 'get',
  })
}
