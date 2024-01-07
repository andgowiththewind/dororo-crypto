import request from '@/utils/request';

/**
 * 如果处于开发环境,则打印日志
 */
export function devConsoleLog(...data) {
    if (process.env.NODE_ENV === 'development' || process.env.VUE_APP_ENV === 'development') {
        console.log(...data);
    }
}

/**
 * 获取系统参数
 * @param key 参数key值
 */
export function getSysParam(key) {
    let query = {key: key};
    return request({
        url: '/sys/getSysParam',
        method: 'get',
        params: query
    })
}