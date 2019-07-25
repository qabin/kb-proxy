import {axiosInstance} from "../../../plugins/axios";

export function ajax_get_request_list(maxId,kw) {
  let form = {
    'maxId': maxId,
    'kw':kw
  }
  return axiosInstance({
    url: '/api/requests/_search',
    method: 'get',
    params: form
  })
}

export function ajax_get_request_by_id(id) {
  return axiosInstance({
    url: '/api/requests/' + id,
    method: 'get',
  })
}


export function ajax_delete_requests() {
  return axiosInstance({
    url: '/api/requests',
    method: 'delete',
  })
}
