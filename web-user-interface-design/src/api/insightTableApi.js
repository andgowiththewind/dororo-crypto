import request from '@/utils/request';

/**
 * 检查密码格式是否正确
 */
export function checkSecretKeyFn(query) {
    return request({
        url: '/sys/checkSecretKey',
        method: 'get',
        params: query,
        headers: {
            userPassword: (query && query.userPassword) ? encodeURIComponent(query.userPassword) : '',
            enableAntiShake: true,
        }
    })
}

/**
 * 统一提交加解密请求
 */
export function cryptoSubmitFn(_data) {
    let requestJson = {
        url: '/crypto/cryptoSubmit',
        method: 'post',
        data: _data
    };
    // 特殊:在请求头中设置一些data中的数据,通常用于过滤器校验
    let headers = {};

    // 密码
    if (_data.userPassword && _data.userPassword !== '') {
        headers['userPassword'] = encodeURIComponent(_data.userPassword);
    }

    requestJson.headers = headers;
    return request(requestJson)
}

/**
 * 表格样式_行样式_根据行号换不同的背景色
 */
export function rowStyleFn({row, rowIndex}) {
    return {backgroundColor: (rowIndex % 2 === 0 ? '#f0f9eb' : '#b7a476'), color: 'black'};
}

/**
 * 表格样式_单元格样式_部分列的单元格希望内容居中有些又希望左对齐
 *     //根据列的字段名进行判断
 *     // if (['name', 'absPath'].includes(column.property)) {return {textAlign: 'left'};}
 *     //根据下标进行判断,下标从0开始
 *     // if ([1, 2, 3, 4, 5].includes(columnIndex)) {return {textAlign: 'left'};}
 */
export function cellStyleFn({row, column, rowIndex, columnIndex}) {
    if (['name', 'absPath'].includes(column.property)) {
        return {textAlign: 'left'};
    }
    if (['status', 'percentage', 'readableFileSize', 'extName'].includes(column.property)) {
        return {textAlign: 'center'};
    }
}

export function headerCellStyleFn({row, column, rowIndex, columnIndex}) {
    return {backgroundColor: '#01847f', textAlign: 'center', color: 'yellow', fontSize: '10px'};
}