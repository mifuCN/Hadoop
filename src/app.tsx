//该文件是前端项目的全局文件  定义了进入新页面时最先执行的方法
import Footer from '@/components/Footer';
import RightContent from '@/components/RightContent';
import {BookOutlined, LinkOutlined} from '@ant-design/icons';
import type {Settings as LayoutSettings} from '@ant-design/pro-components';
import {PageLoading, SettingDrawer} from '@ant-design/pro-components';
import type {RunTimeLayoutConfig} from 'umi';
import {history, Link} from 'umi';
import defaultSettings from '../config/defaultSettings';
import {currentUser as queryCurrentUser} from './services/ant-design-pro/api';
import {RequestConfig} from "@@/plugin-request/request";

const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/user/login';
/**
 * 无需用户登录态的界面
 */
const NO_NEED_LOGIN_WHITE_LIST = ['/user/register', loginPath];//注册页白名单 这样不会被重定向

/** 获取用户信息比较慢的时候会展示一个 loading */
export const initialStateConfig = {
  loading: <PageLoading/>,
};
//找了很久没发现Ant Design Pro的请求前缀在哪  追源码  最后用这个方法:
//全局初化启动文件中 定义整个请求的前缀  这个用到的是UmiJs的特性
export const request: RequestConfig = {
  // prefix: '/api',
  timeout: 1000000,//这里这个值 发现后端一直没有接受到请求 应该是默认的1秒太小了  调大了之后就可以了 ★★★主要是方便debug
}

/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.CurrentUser;
  loading?: boolean;
  fetchUserInfo?: () => Promise<API.CurrentUser | undefined>;
}> {
  const fetchUserInfo = async () => {
    try {
      return await queryCurrentUser();
    } catch (error) {
      history.push(loginPath);  //这里也会自动重定向 双重拦截 刚刚进入是拦截
    }
    return undefined;
  };
  // 如果是无需登录的页面，不执行
  if (NO_NEED_LOGIN_WHITE_LIST.includes(history.location.pathname)) {
    return {
      fetchUserInfo,
      settings: defaultSettings,
    };
  }
  const currentUser = await fetchUserInfo();
  return {
    fetchUserInfo,
    currentUser,//前端的登录态
    settings: defaultSettings,
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({initialState, setInitialState}) => {
  return {
    rightContentRender: () => <RightContent/>,
    disableContentMargin: false,
    waterMarkProps: {
      // content: initialState?.currentUser?.username,
      content: "mifu的hadoop网盘",//这里我们给项目加一个水印
    },
    footerRender: () => <Footer/>,
    onPageChange: () => {
      /**
       * 测试的时候发现就是这个文件中的两端代码 导致没有登录的时候一直自动重定向 就会导致注册界面(没有登录才注册)这个页面出不来  有逻辑错误
       * 那我们就在这里写一个白名单  在这个白名单里的话 就直接return
       * 如果没有登录，重定向到 login
       * @author mifuRD
       */
      const {location} = history;
      if (NO_NEED_LOGIN_WHITE_LIST.includes(location.pathname)) {
        return;
      }
      if (!initialState?.currentUser) {
        history.push(loginPath);
      }
      // 被注释掉的这段是蚂蚁金服的同学写的代码  双重拦截  刷新时拦截
      // if (!initialState?.currentUser && location.pathname !== loginPath) {
      //   history.push(loginPath);
      // }
    },
    links: isDev
      ? [
        <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
          <LinkOutlined/>
          <span>OpenAPI 文档</span>
        </Link>,
        <Link to="/~docs" key="docs">
          <BookOutlined/>
          <span>业务组件文档</span>
        </Link>,
      ]
      : [],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children, props) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <>
          {children}
          {!props.location?.pathname?.includes('/login') && (
            <SettingDrawer
              disableUrlParams
              enableDarkTheme
              settings={initialState?.settings}
              onSettingChange={(settings) => {
                setInitialState((preInitialState) => ({
                  ...preInitialState,
                  settings,
                }));
              }}
            />
          )}
        </>
      );
    },
    ...initialState?.settings,
  };
};
