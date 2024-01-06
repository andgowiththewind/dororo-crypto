// ###########################################################################################################
// #                                             简化版                                                       #
// ###########################################################################################################
import axios from 'axios'
import {Notification, MessageBox, Message, Loading} from 'element-ui'

// 创建axios实例
const service = axios.create({
    timeout: 10 * 1000,
    headers: {
        Accept: 'application/json; charset=utf-8',
        'Content-Type': 'application/json; charset=utf-8',
        'devIng': process.env.NODE_ENV === 'development',
    },
    responseType: 'json',
})

// request拦截器
service.interceptors.request.use(config => {
    // 如果是GET请求,且有参数,则将参数拼接到url后面
    if (config.method === 'get' && config.params) {
        let url = config.url + '?' + tansParams(config.params);
        url = url.slice(0, -1);
        config.params = {};
        config.url = url;
    }
    return config;
}, error => {
    console.log(error)
    Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(
    // 请求成功时(res.status === 200)
    res => {
        // 未设置状态码则默认成功状态
        const code = res.data.code || 200;
        // 获取错误提示信息
        const msg = res.data.msg || '未知错误';
        // 但凡不是200的状态码,都认为是错误
        if (code !== 200) {
            // 弹框信息
            Notification.error({title: '错误', message: msg, position: 'bottom-right'});
            // 返回错误
            return Promise.reject(new Error(msg));
        }
        // 如果是200,则返回数据
        return res.data;
    },
    error => {
        // console.log('$#err#$', error)
        let message = '未知错误';
        if (message == "Network Error") {
            message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
            message = "系统接口请求超时";
        } else if (error.response && error.response.data && error.response.data.msg) {
            message = error.response.data.msg;
        } else if (message.includes("Request failed with status code")) {
            message = "系统接口" + message.substr(message.length - 3) + "异常";
        }
        Message({message: message, type: 'error', duration: 5 * 1000})
        return Promise.reject(error)
    }
)

/**
 * 转换请求参数
 * @param params 参数
 * @returns {string} 转换后的参数
 */
function tansParams(params) {
    let result = ''
    for (const propName of Object.keys(params)) {
        const value = params[propName];
        var part = encodeURIComponent(propName) + "=";
        if (value !== null && value !== "" && typeof (value) !== "undefined") {
            if (typeof value === 'object') {
                for (const key of Object.keys(value)) {
                    if (value[key] !== null && value[key] !== "" && typeof (value[key]) !== 'undefined') {
                        let params = propName + '[' + key + ']';
                        var subPart = encodeURIComponent(params) + "=";
                        result += subPart + encodeURIComponent(value[key]) + "&";
                    }
                }
            } else {
                result += part + encodeURIComponent(value) + "&";
            }
        }
    }
    return result;
}

export default service