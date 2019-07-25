import {axiosInstance} from "../../../plugins/axios";

export function ajax_get_response_by_id(id) {
  let form = {
    'request_detail_id': id
  }
  return axiosInstance({
    url: '/api/responses',
    method: 'get',
    params: form
  })
}
