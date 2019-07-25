import {axiosInstance} from "../../../plugins/axios";

export function ajax_get_detail_list(kw) {
  let form = {
    'kw': kw
  }
  return axiosInstance({
    url: '/api/details/_search',
    method: 'get',
    params: form
  })
}

export function ajax_delete_detail_by_id(id) {
  return axiosInstance({
    url: '/api/details/' + id,
    method: 'delete',
  })
}

export function ajax_add_request_detail(form) {
  return axiosInstance({
    url: '/api/details',
    method: 'post',
    data: form
  })
}

export function ajax_update_request_detail(form) {
  return axiosInstance({
    url: '/api/details/' + form.id,
    method: 'patch',
    data: form
  })
}
