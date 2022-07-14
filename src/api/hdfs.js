import request from '@/utils/request'

export default {
  // 1 生成统计数据
  getAllList(path) {
    return request({
      url: `/api/hdfs/findAll?path=${path}`,
      method: 'post',
      data: path
    })
  },
  /* 下载文件*/
  download(filename, path) {
    return request({
      url: `/api/hdfs/download?filename=${filename}&path=${path}`,
      method: 'post',
      data: {
        filename,
        path
      }
    })
  },
  /* 删除文件*/
  deleteFile(filename, path) {
    return request({
      url: `/api/hdfs/delete?filename=${filename}&path=${path}`,
      method: 'post',
      data: {
        filename,
        path
      }
    })
  },
  /* 新建文件夹*/
  mkdirFile(filename) {
    return request({
      url: `/api/hdfs/mkdir?filename=${filename}`,
      method: 'post',
      data: filename
    })
  }
}
