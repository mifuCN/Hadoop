<template>
  <div class="app-container">
    <!--查询表单-->
    <el-form :inline="true" class="demo-form-inline">
      <div>
        <el-button type="warning" round @click.prevent="goLastFolder">返回上层</el-button>
        <el-button type="warning" round @click.prevent="goRootFolder">返回根目录</el-button>
        <div style="right: 200px;float: right">
          <el-button type="primary" @click="dialogVisible = true">点我上传文件</el-button>
          <el-button type="primary" @click="mkdir">新建文件夹</el-button>
        </div>
      </div>
    </el-form>

    <!-- 表格 -->
    <el-table :data="list" border fit highlight-current-row>
      <el-table-column label="序号" width="100" align="center">
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="name" label="文件名称" width="150" align="center"/>
      <el-table-column prop="type" label="文件类型" width="150" align="center"/>
      <el-table-column prop="size" label="文件大小" width="150" align="center"/>
      <el-table-column prop="authority" label="文件权限" width="150" align="center"/>
      <el-table-column prop="date" label="添加时间"/>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            @click="editFile(scope.row.name,scope.row.type)"
            v-text="scope.row.type === 'contents'? '查看' : '下载'">下载或查看目录
          </el-button>
          <el-button type="danger" size="mini" @click="removeDataById(scope.row.name)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--文件上传模块-->
    <el-dialog
      :visible.sync="dialogVisible"
      :before-close="handleClose"
      title="提示"
      width="30%">
      <el-upload
        :action="'http://localhost:8080/api/hdfs/uploadFile'"
        :data="{pathname:fileName}"
        :key="imagecropperKey"
        class="upload-demo"
        drag
        multiple>
        <i class="el-icon-upload"/>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="uploadSuccess">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
// 1.引入调用Teacher.js
import hdfs from '@/api/hdfs'

export default { // 编写核心代码的位置
  name: 'List',
  data() { // 定义变量和初始值
    return {
      list: null, // 查询之后接口返回的集合
      page: 1, // 当前页
      limit: 7, // 每页记录数
      teacherQuery: {}, // 条件封装对象
      dialogVisible: false,
      path: '', // 路径
      fileName: '',
      dialogFormVisible: false,
      form: {},
      imagecropperKey: 0
    }
  },
  created() {
    this.getList() // 初始化阶段调用接口获取目录和文件
  },
  methods: {
    // 调用Hadoop接口获取所有的目录和文件
    getList() {
      hdfs.getAllList('/').then(response => {
        this.list = response.data.fileList
      })
    },
    // 判断是目录还是文件进行修改
    editFile(contentsName, type) {
      console.log(contentsName)
      // 是文件类型，则调用后端下载方法，直接下载
      if (type === 'file') {
        hdfs.download(contentsName, this.path).then((response) => {
          this.$message.success('下载成功')
        }).catch(() => {
          this.$message.error('下载失败')
        })
      }
      // 是目录，修改路径
      if (type === 'contents') {
        if (this.path === '') {
          this.fileName = contentsName
          this.path = this.path + '/' + contentsName
        } else {
          this.fileName = this.fileName + '/' + contentsName
          this.path = this.path + '/' + contentsName
        }
        hdfs.getAllList(this.path).then((response) => {
          this.list = response.data.fileList // 根据最新路径重新赋值给list数组
        })
      }
    },
    // 上传文件
    uploadSuccess() {
      this.$message.success('上传成功')
      this.dialogVisible = false
      hdfs.getAllList(this.path).then((response) => {
        this.list = response.data.fileList // 根据最新路径重新赋值给list数组
        this.imagecropperKey = this.imagecropperKey + 1
      })
    },
    // 返回上一层
    goLastFolder() {
      let arr = this.path.split('/')
      arr = arr.slice(0, arr.length - 1)
      const pathName = arr.join('/')
      if (pathName === '') {
        hdfs.getAllList('/').then((resp) => {
          this.list = resp.data.fileList
          this.path = ''
          this.fileName = ''
        })
      } else {
        hdfs.getAllList(pathName).then((resp) => {
          this.list = resp.data.fileList
          this.path = pathName
          this.fileName = pathName.substring(1)
        })
      }
    },
    /* 回到根目录*/
    goRootFolder() {
      hdfs.getAllList('/').then((resp) => {
        this.list = resp.data.fileList
        this.path = ''
        this.fileName = ''
      })
    },
    /* 创建文件夹*/
    mkdir() {
      this.$prompt('请输入新建文件夹名', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({value}) => {
        console.log('fileName =' + this.fileName)
        if (this.fileName === '') {
          hdfs.mkdirFile(value).then((resp) => {
            if (resp.code === 20000) {
              this.getList()
            }
          })
        } else {
          hdfs.mkdirFile(this.fileName + '/' + value).then((resp) => {
            if (resp.code === 20000) {
              hdfs.getAllList(this.path).then((resp) => {
                this.list = resp.data.fileList
              })
            }
          })
        }
        this.$message.success('恭喜您，创建目录成功，目录名为：' + value)
      }).catch(() => {
        this.$message.info('取消创建文件夹')
      })
    },
    removeDataById(id) { // 数据删除功能
      /* 确认删除弹框*/
      this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        hdfs.deleteFile(id, this.path).then(response => {
          this.$message.success('删除成功')
          hdfs.getAllList(this.path).then((resp) => {
            this.list = resp.data.fileList
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    handleClose(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done()
        })
        .catch(_ => {
        })
    }
  }
}
</script>

<style scoped>

</style>
