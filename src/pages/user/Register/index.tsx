//这个是直接复制登录Login来更改的  发现封装的按钮没有修改的地方  这里官方文档也没有说明 这也是封装的缺点
//不过后面追源码(LoginForm)发现了这个:
//  var submitter = proFormProps.submitter === false ? false : (0, _objectSpread2.default)((0, _objectSpread2.default)({
//     searchConfig: {
//       submitText: intl.getMessage('loginForm.submitText', '登录')
//     },
import Footer from '@/components/Footer';
import {PLANET_LINK, SYSTEM_LOGO} from '@/constants';
import {register} from '@/services/ant-design-pro/api';
import {LockOutlined, UserOutlined,} from '@ant-design/icons';
import {LoginForm, ProFormText,} from '@ant-design/pro-components';
import {message, Tabs} from 'antd';
import React, {useState} from 'react';
import {history} from 'umi';
import styles from './index.less';
//★★★ctrl+alt+o可以快速地将没用的引用去掉

const Register: React.FC = () => {
  const [type, setType] = useState<string>('account');
  // 表单提交
  const handleSubmit = async (values: API.RegisterParams) => {
    const {userPassword, checkPassword} = values;
    // 校验
    if (userPassword !== checkPassword) {//这个全等号 不只是判断值相不相等 还可以判断类型相不相等(比如必须都是字符串)
      message.error('两次输入的密码不一致');
      return;
    }

    try {
      // 注册
      const id = await register(values);
      if (id) {
        const defaultLoginSuccessMessage = '注册成功！';
        message.success(defaultLoginSuccessMessage);

        /** 此方法会跳转到 redirect 参数所在的位置 */ //★★★这个代码的意思就是之前请求的路径没有访问到  被前端登录拦截了  我们就希望注册成功后  他能回到原来访问的位置
        if (!history) return;
        const {query} = history.location;
        history.push({
          pathname: '/user/login',
          query,
        }); //这里我自己修改了一下
        return;
      }
      // else {
      // throw new Error('register error id = {}',id); 这里java后遗症了 哈哈哈 去网上百度了个前端的模板字符串的语法
      // throw new Error(`register error id = ${id}`);  优化后 这个id不用了 我们封装了BaseResponse
      // throw new Error(res.description); 用了 前端的封装(全局响应处理器) 之后 又回到原来的代码
      //   throw new Error();
      // }

    } catch (error: any) { //给他一个任意类型  不然下面的error.message会小报错
      const defaultLoginFailureMessage = '注册失败，请重试！';
      message.error(defaultLoginFailureMessage);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <LoginForm //这里官方文档也没有说明 不过追源码可以解决
          submitter={{
            searchConfig: {
              submitText: '注册'
            }
          }}
          logo={<img alt="logo" src={SYSTEM_LOGO}/>}
          title="Hadoop网盘"
          subTitle={<a href={PLANET_LINK} target="_blank" rel="noreferrer">SpringBoot和AntDesignPro的学习项目</a>}
          initialValues={{
            autoLogin: true,
          }}
          onFinish={async (values) => {
            await handleSubmit(values as API.RegisterParams);
          }}
        >
          <Tabs activeKey={type} onChange={setType}>
            <Tabs.TabPane key="account" tab={'账号注册'}/>
          </Tabs>

          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请输入账号'}
                rules={[
                  {
                    required: true,
                    message: '账号是必填项！',
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请输入密码'}
                rules={[
                  {
                    required: true,
                    message: '密码是必填项！',
                  },
                  {
                    min: 8,
                    type: "string",
                    message: '密码长度不能小于8位！',
                  },
                ]}
              />
              <ProFormText.Password
                name="checkPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请确认密码'}
                rules={[
                  {
                    required: true,
                    message: '确认密码是必填项！',
                  },
                  {
                    min: 8,
                    type: "string",
                    message: '密码长度不能小于8位！',
                  },
                ]}
              />
              <ProFormText.Password
                name="planetCode"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请确认星球编号'}
                rules={[
                  {
                    required: true,
                    message: '星球编号是必填项！',
                  },
                ]}
              />
            </>
          )}

          <div
            style={{
              marginBottom: 24,
            }}
          >
          </div>
        </LoginForm>
      </div>
      <Footer/>
    </div>
  );
};

export default Register;

//todo 1.现在注册失败时还没有较为友好的提示(浏览器看的时候:返回的是我们后端定义的-1) 后期优化一下  2.前端密码重复提示不太友好(继续优化)
