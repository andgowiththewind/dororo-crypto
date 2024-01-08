import request from '@/utils/request';


export function submitConditionPagingQuery(_data) {
    return request({
        url: '/tableData/getInsightTableData',
        method: 'post',
        data: _data,
        headers: {
            enableAntiShake: true
        }
    })
}