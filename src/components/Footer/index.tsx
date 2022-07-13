import {GithubOutlined} from '@ant-design/icons';
import {DefaultFooter} from '@ant-design/pro-components';
import {PLANET_LINK} from "@/constants";

const Footer: React.FC = () => {
  const defaultMessage = '米芾出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'blog',
          title: '快乐二哈',
          href: PLANET_LINK,
          blankTarget: true, //是否打开新页面跳转,否则在当前页面跳转
        },
        {
          key: 'disk',
          title: '米芾云盘',
          href: 'https://github.com/mifuRD',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <><GithubOutlined/>米芾 Github</>,
          href: 'https://github.com/mifuRD',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
