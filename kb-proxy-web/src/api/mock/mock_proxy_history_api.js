import {axiosInstance} from "../../plugins/axios";

export function ajax_mock_proxy_history_search(mock_id, kw, page, size) {
  let form = {
    mock_id: mock_id,
    kw: kw,
    page: page,
    size: size
  }
  return axiosInstance({
    url: '/api/mock/proxy/history/_search',
    method: 'get',
    params: form
  })
}
