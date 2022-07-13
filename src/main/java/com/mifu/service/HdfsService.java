package com.mifu.service;



import com.mifu.model.domain.Hdfs;

import java.io.File;
import java.util.List;

public interface HdfsService {

    /*查询所有的数据*/
    List<Hdfs> findAll(String path);

    /*创建文件*/
    Boolean mkdirFile(String filename);

    /*删除文件*/
    Boolean deleteFile(String filename,String path);

    /*上传文件*/
    void upload(File file, String path);

    /*下载文件*/
    void download(String filename,String path);

}
