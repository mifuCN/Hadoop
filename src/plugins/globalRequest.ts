/**
 * request 网络请求工具
 * 全局响应处理器
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
//todo 这个代码后期好好理解一下  方便了前端的封装 不然一个一个优化data  太慢了  而且容易漏掉逻辑
//await 必须是async定义的 才能使用
import {extend} from 'umi-request';
import {message} from "antd";
import {history} from "@@/core/history";
import {stringify} from "querystring";

/**
 * 配置request请求时的默认参数
 */
const request = extend({
  credentials: 'include', // 默认请求是否带上cookie
  prefix: process.env.NODE_ENV === 'production' ? 'http://user-backend.code-nav.cn' : undefined
  // requestType: 'form',
});

/**
 * 所有请求拦截器
 */
request.interceptors.request.use((url, options): any => {
  console.log(`do request url = ${url}`)//模板字符串的写法

  return {
    url,
    options: {
      ...options,
      headers: {},
    },
  };
});

/**
 * 所有响应拦截器
 */
request.interceptors.response.use(async (response, options): Promise<any> => {
  const res = await response.clone().json();
  if (res.code === 0) {
    return res.data;
  }
  if (res.code === 40100) {
    message.error('请先登录');//重定向到登录页
    history.replace({
      pathname: '/user/login',
      search: stringify({
        redirect: location.pathname,
      }),
    });
  } else {
    message.error(res.description)
  }
  return res.data;
});

export default request;
