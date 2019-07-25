import {axiosInstance} from "../../plugins/axios";

export function ajax_request_api_by_server(url, method, header, json, form, body_type) {
  let model = {
    'url': url,
    'method': method,
    'header': header,
    'request_json': json,
    'request_form': form,
    'body_type': body_type
  }
  return axiosInstance({
    url: '/api/request/by/server',
    method: 'post',
    data: model
  })
}
