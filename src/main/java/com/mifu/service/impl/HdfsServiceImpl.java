package com.mifu.service.impl;

import com.mifu.model.domain.Hdfs;
import com.mifu.service.HdfsService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
public class HdfsServiceImpl implements HdfsService {

    /*1.查询所有根路径下Hadoop中的文件*/
    @Override
    public List<Hdfs> findAll(String path) {
        try {
            // 1.设置Hadoop链接地址
            Configuration configuration = new Configuration();
            configuration.set("fs.defaultFS", "hdfs://192.168.10.102:8020");
            System.setProperty("HADOOP_USER_NAME", "mifu");

            // 2.获取文件系统
            FileSystem fs = FileSystem.get(configuration);
            //格式化日期对象
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //创建一个集合保存遍历数据
            List<Hdfs> fileList = new ArrayList<>();
            //根据路径遍历Hadoop中的文件及文件夹
            if ("".equals(path)) {
                path = "/";
            }
            FileStatus[] fileStatuses = fs.listStatus(new Path(path));

            for (FileStatus fileStatus : fileStatuses) {
                /*创建一个Hadoop实例用于存储Hadoop数据*/
                Hdfs file = new Hdfs();
                //获取Hadoop中的文件名字
                file.setName(fileStatus.getPath().getName());
                //获取Hadoop中的文件日期
                file.setDate(format.format(fileStatus.getModificationTime()));
                //获取Hadoop中的文件大小
                file.setSize(String.valueOf(fileStatus.getLen()));
                //获取Hadoop中的文件权限
                file.setAuthority(String.valueOf(fileStatus.getPermission()));
                //获取Hadoop的文件类型（文件夹或文件）
                if (fileStatus.isDirectory()) {
                    file.setType("contents");
                } else {
                    file.setType("file");
                }
                fileList.add(file);
            }
            //排序规则：文件夹在前，文件在后
            Collator collator = Collator.getInstance(Locale.CHINA);
            fileList.sort((f1, f2) -> (collator.compare(f1.getType(), f2.getType())));
            return fileList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void download(String filename, String path) {
        String filename1 = filename;
        filename = "/" + filename;
        try {
            Configuration conf = new Configuration();
            //这里指定使用的是HDFS文件系统
            conf.set("fs.defaultFS", "hdfs://192.168.10.102:8020/");
            //通过如下的方式进行客户端身份的设置
            System.setProperty("HADOOP_USER_NAME", "mifu");
            //通过FileSystem的静态方法获取文件系统客户端对象
            FileSystem fs = FileSystem.get(conf);
            //也可以通过如下的方式去指定文件系统的类型，并且同时设置用户身份
            //FileSystem fs = FileSystem.get(new URI("hdfs://node1:9000"),conf,"mifu");
            if ("".equals(path)) {
                path = "/";
                fs.copyToLocalFile(false, new Path(path + filename1), new Path("D:\\MifuTest\\" + filename1), true); //下载到D盘
            } else {
                fs.copyToLocalFile(false, new Path(path + filename), new Path("D:\\MifuTest\\" + filename1), true); //下载到D盘
            }
            //关闭我们的文件系统
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean mkdirFile(String filename) {
        try {
            // 1.连接hadoop
            Configuration configuration = new Configuration();
            configuration.set("fs.defaultFS", "hdfs://192.168.10.102:8020");
            System.setProperty("HADOOP_USER_NAME", "mifu");
            // 2.获取文件系统
            FileSystem fileSystem = FileSystem.get(configuration);
            // 3.创建目录
            boolean mkdirs = fileSystem.mkdirs(new Path("/" + filename));
            // 4.关闭资源
            fileSystem.close();
            return mkdirs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean deleteFile(String filename, String path) {
        try {
            // 1.连接hadoop
            Configuration configuration = new Configuration();
            configuration.set("fs.defaultFS", "hdfs://192.168.10.102:8020");
            System.setProperty("HADOOP_USER_NAME", "mifu");
            // 2.获取文件系统
            FileSystem fileSystem = FileSystem.get(configuration);
            // 3.删除，可以删除目录，也可以删除文件，如：/aa.txt
            // 3.1 如果要删除的路径是一个文件，参数recursive是true或false都行
            // 3.2 如果要删除的路径是一个目录，参数recursive为true就是递归删除，
            //如果填false，而下级目录还有文件，就会报错
            boolean flag;
            flag = fileSystem.delete(new Path("/" + filename), true);

            if (!"".equals(path)) {
                flag = fileSystem.delete(new Path(path + "/" + filename), true);
            }
            // 4.关闭资源
            fileSystem.close();
            return flag;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void upload(File file, String path) {
        try {
            // 1.连接hadoop
            Configuration configuration = new Configuration();
            configuration.set("fs.defaultFS", "hdfs://192.168.10.102:8020");
            System.setProperty("HADOOP_USER_NAME", "mifu");
            //2.读取本地文件
            FileInputStream fileInputStream = new FileInputStream(file);
            //3.找到文件系统，上传到目的地
            FileSystem fileSystem = FileSystem.get(configuration);
            FSDataOutputStream outputStream = fileSystem.create(new Path("/" + path));
            IOUtils.copyBytes(fileInputStream, outputStream, configuration);
            //4.释放资源
            fileInputStream.close();
            outputStream.close();
            fileSystem.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
