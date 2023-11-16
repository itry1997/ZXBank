<template>
  <div>
    <el-card id="search">
      <el-row>
        <el-col :span="23">
          <!-- 搜索框 -->
          <el-input v-model="searchModel.userid" placeholder="创建人" clearable></el-input>
          <el-input v-model="searchModel.title" placeholder="代办事项" clearable></el-input>

          <el-button @click="getTaskList" type="primary" round icon="el-icon-search">查询</el-button>
        </el-col>
        <el-col :span="1">
          <el-button @click="openEditUI(null)" type="primary" round icon="el-icon-plus"></el-button>
        </el-col>
      </el-row>
    </el-card>
    <!--  结果列表  -->
    <el-card>
      <el-table
        :data="taskList"
        stripe
        style="width: 100%"
      >
        <el-table-column
          label="序号"
          width="80"
        >
          <template slot-scope="scope">
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column
          prop="title"
          label="任务标题"
          width="180"
        >
      </el-table-column>
        <el-table-column
          prop="description"
          label="任务描述"
          
        >
        </el-table-column>
        <el-table-column
          prop="userid"
          label="创建人"
          width="180"
        >
        </el-table-column>
        <el-table-column
          prop="endtime"
          label="截止日期"
          width="180"
        >
        </el-table-column>
        <el-table-column
          prop="status"
          label="任务状态"
          width="180"
        >
          <template slot-scope = "scope">
            <el-tag v-if="scope.row.status">正常</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column
          prop="email"
          label="电子邮件"
        >
        </el-table-column> -->
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button @click="openEditUI(scope.row.id)" type="primary" icon="el-icon-edit" circle></el-button>
            <el-button @click="deleteTaskById(scope.row)" type="danger" icon="el-icon-delete" circle></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页组件-->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo"
      :page-sizes="[5, 10, 15, 20]"
      :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    >
    </el-pagination>

    <!-- 用户信息编辑对话框  -->
    <el-dialog @close="clearForm" :title="title" :visible.sync="dialogFormVisible">
      <el-form :model="taskForm" :rules="rules" ref="taskFormRef">
        <el-form-item label="任务标题" :label-width="formLabelWidth" prop="username">
          <el-input v-model="taskForm.userid" autocomplete="off" 
          :disabled="taskForm.id !== null && taskForm.id !== undefined"></el-input>
        </el-form-item>
        <el-form-item
          label="任务描述"
          :label-width="formLabelWidth"
          prop="describe"
          
        >
          <el-input type="password" v-model="taskForm.describe" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="截止日期" :label-width="formLabelWidth" prop="endtime">
          <!-- <el-input v-model="taskForm.phone" autocomplete="off"></el-input> -->
          <el-date-picker
              v-model="taskForm.endtime"
              type="datetime"
              placeholder="选择日期时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              format="yyyy-MM-dd HH:mm:ss">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="事项状态" :label-width="formLabelWidth"
        v-if="taskForm.id !== null && taskForm.id !== undefined">
          <el-switch
            v-model="taskForm.status"
            :active-value="1"
            :inactive-value="0"
          >
          </el-switch>

        </el-form-item>
        <el-form-item label="创建人" :label-width="formLabelWidth">
          <el-input v-model="taskForm.userid" autocomplete="off"  :disabled="taskForm.id !== null && taskForm.id !== undefined"></el-input>
        </el-form-item>
     </el-form> 
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveTask">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import taskApi from '@/api/task'


export default {
  data() {

    return {
      formLabelWidth: '130px',
      taskForm: { roleIdList: [] },
      dialogFormVisible: false,
      title: '',
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 5
      },
      taskList: [],
      pickerOptions1: {
          shortcuts: [{
            text: '今天',
            onClick(picker) {
              picker.$emit('pick', new Date());
            }
          }, {
            text: '昨天',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              picker.$emit('pick', date);
            }
          }, {
            text: '一周前',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', date);
            }
          }]
        },
        rules: {
        userid: [
          { required: true, message: '请输入创建人', trigger: 'blur' },
    
        ],
        title: [
          { required: true, message: '请输入任务标题', trigger: 'blur' },
          
        ],
        describe: [
          { required: true, message: '请输入任务描述', trigger: 'blur' },
         
        ],
        endtime: [
          { required: true, message: '请输入截止日期', trigger: 'blur' },
  
        ]
      }
        
      };
    },
 
  methods: {
    // getALLRole() {
    //   roleApi.getALLRole().then(respose => {
    //     this.roleList = respose.data
    //   })
    // },
    deleteTaskById(user) {
      this.$confirm(`确认删除任务 ${user.title} ?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        taskApi.deleteTaskById(user.id).then(response => {
          this.$message({
            type: 'success',
            message: response.message
          })
          this.getTaskList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    saveTask() {
      // 触发表单验证
      this.$refs['taskFormRef'].validate((valid) => {
        if (valid) {
          // 提交请求给后台
          taskApi.saveTask(this.taskForm).then(response => {
            // 成功提示
            this.$message({
              message: response.message,
              type: 'success'
            })
            // 关闭对话框
            this.dialogFormVisible = false
            // 刷新表格
            this.getTaskList()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    clearForm() {
      this.taskForm = { roleIdList: [] }
      this.$refs.taskFormRef.clearValidate()
    },
    openEditUI(id) {
      if (id == null) {
        this.title = '新增用户'
      } else {
        this.title = '修改用户'
        // 根据id查询用户数据
        taskApi.getTaskById(id).then(response => {
          // console.log(response.data)
          this.taskForm = response.data
          console.log(this.taskForm)
        })
      }
      this.dialogFormVisible = true
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getTaskList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getTaskList()
    },
    getTaskList() {
      taskApi.getTaskList(this.searchModel).then(response => {
        this.taskList = response.data.rows
        this.total = response.data.total
      })
    }
  },
  created() {
    this.getTaskList()
    // this.getALLRole()
  }
}
</script>

<style>
#search .el-input {
  width: 200px;
  margin-right: 10px;
}

.el-dialog .el-input {
  width: 85%;
}
</style>
