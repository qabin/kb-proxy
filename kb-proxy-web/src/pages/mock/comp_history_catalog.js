import {ajax_mock_proxy_history_search} from "../../api/mock/mock_proxy_history_api";
import CatalogBase from '../../components/elements/MixinCatalogBase'
import {CodeEnums, MethodEnums} from "../../utils/request_dictionary";
import ModelResponseDetail from './modal_response_detail'
export default {
  name: 'comp_history_catalog',
  mixins: [CatalogBase],
  data: () => ({
    history_list: [],
    mock_id: null,
    kw: null,
    pagination_ctl: {
      page: 1,
      rowsNumber: 0,
      rowsPerPage: 5
    },
    table_columns: [
      {
        name: 'method', align: 'left', field: 'method', label: '请求方式',
        renderData: {style: {maxWidth: '100px', width: '100px'}, staticClass: 'text-tertiary'},
        render: (h, props) => h('div', {
          staticClass:'text-'+MethodEnums[props.value].color
        }, [props.value || '--'])
      },
      {
        name: 'url', align: 'left', field: 'url', label: '链接',
        renderData: {style: {maxWidth: '300px', width: '300px'}, staticClass: 'text-tertiary'},
        render: (h, props) => h('div', {
          staticClass:'ellipsis',
          attrs:{
            title:props.value
          }
        }, [props.value || '--'])
      },
      {
        name: 'code', align: 'left', field: 'code', label: '状态',
        renderData: {style: {maxWidth: '100px', width: '100px'}, staticClass: 'text-tertiary'},
        render: (h, props) => h('span',{
          staticClass:'text-'+CodeEnums[props.value].color
        }, [props.value || '--'])
      },
      {
        name: 'create_time', align: 'left', field: 'create_time', label: '创建时间',
        renderData: {style: {maxWidth: '100px', width: '100px'}, staticClass: 'text-tertiary'},
        render: (h, props) => props.value || '--'
      },
      {
        name: 'response', align: 'left', field: 'response', label: '响应结果',
        renderData: {style: {maxWidth: '50px', width: '50px'}, staticClass: 'text-tertiary'},
        render: (h, props) => h(ModelResponseDetail,{
          props:{
            value:props.value,
          }
        },['详情'])
      },
    ]
  }),
  methods: {
    request() {
      this.mock_id && ajax_mock_proxy_history_search(this.mock_id, this.kw, this.page, this.size)
        .then(d => {
          this.rows = d.data.data || [];
          this.rowsNumber = d.data.count;
        })
        .catch(() => this.$q.err('获取记录异常'));
    },
    refresh_catalog(id) {
      this.rows = []
      this.rowsNumber = 0
      this.mock_id = id
      this.request()
    }
  },
  mounted() {
    this.refresh();
  },
  render(h) {
    return h('q-table', {
      staticClass: 'shadow-0 pp-border-3 q-table-dense' + this.table_class,
      props: {
        dense: true,
        separator: 'horizontal',
        color: 'primary',
        data: this.rows,
        columns: this.table_columns,
        rowKey: 'id',
        pagination: this.pagination_ctl,
        rowsPerPageOptions: [5, 10, 20],
        noDataLabel: '无记录',
        rowsPerPageLabel: this.rowsPerPageLabel,
        hideBottom: !this.rowsNumber,
        hideHeader: !this.rowsNumber
      },
      scopedSlots: this.__render_scope_slot(h),
      on: {request: this.__request}
    })
  }
}
