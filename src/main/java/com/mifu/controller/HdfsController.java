package com.mifu.controller;


import com.mifu.common.R;
import com.mifu.common.ResultCode;
import com.mifu.model.domain.Hdfs;
import com.mifu.service.HdfsService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/hdfs")
@CrossOrigin
public class HdfsController {

    @Autowired //注入
    HdfsService hdfsService;

    /*获取Hadoop中所有的目录和文件*/
    @PostMapping("/findAll")
    public R findAll(@RequestParam String path) { //前端传入这个请求
        List<Hdfs> list = hdfsService.findAll(path);
        return R.ok().data("fileList", list);
    }

    /*下载文件*/
    @PostMapping("/download")
    public R download(@RequestParam String filename, @RequestParam String path) {
        hdfsService.download(filename, path);
        return R.ok();
    }

    /*创建文件夹*/

    /**
     * 测试时需要不加跟路径
     *
     * @param filename
     * @return
     */
    @PostMapping("/mkdir")
    public R mkdirFile(@RequestParam String filename) {
        Boolean flag = hdfsService.mkdirFile(filename);
        if (flag) {
            return R.ok().code(ResultCode.SUCCESS);
        } else {
            return R.error();
        }
    }

    /*删除文件*/
    @PostMapping("/delete")
    public R deleteFile(@RequestParam String filename, @RequestParam String path) {
        Boolean flag = hdfsService.deleteFile(filename, path);
        if (flag) {
            return R.ok();
        } else {
            return R.error();
        }

    }

    /*上传文件*/

    /**
     * 测试时需要不加跟路径
     *
     * @param pathname
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    public R upload(@RequestParam String pathname, MultipartFile file) {
        try {
            File file1 = new File("path");
            FileUtils.copyInputStreamToFile(file.getInputStream(), file1);
            if ("".equals(pathname)) {
                hdfsService.upload(file1, file.getOriginalFilename());
            } else {
                hdfsService.upload(file1, pathname + "/" + file.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.ok();
    }
}

