import {axiosInstance} from "../../../plugins/axios";

export function ajax_get_history_list(kw) {
  let form = {
    'kw':kw
  }
  return axiosInstance({
    url: '/api/history/_search',
    method: 'get',
    params: form
  })
}

export function ajax_delete_history_by_id(id) {
  return axiosInstance({
    url: '/api/history/'+id,
    method: 'delete',
  })
}

export function ajax_delete_all_history() {
  return axiosInstance({
    url: '/api/history/_delete',
    method: 'delete',
  })
}
