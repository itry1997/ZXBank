<template>
  <div class="register">
    <section class="form-container">
      <div class="manage-tip">
        <span class="title">后台管理系统</span>
        <el-form
          :rules="rules"
          ref="ruleFormRef"
          :model="registerUser"
          class="registerForm"
          label-width="80px"
        >
          <el-form-item label="用户名" prop="name">
            <el-input
              v-model="registerUser.name"
              placeholder="请输入用户名"
            ></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="registerUser.email"
              placeholder="请输入邮箱"
            ></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerUser.password"
              placeholder="请输入密码"
              type="password"
            ></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="password2">
            <el-input
              v-model="registerUser.password2"
              placeholder="请确认密码"
              type="password"
            ></el-input>
          </el-form-item>
          <!-- <el-form-item label="选择身份">
            <el-select v-model="registerUser.identity" placeholder="选择身份">
              <el-option label="管理员" value="manager"></el-option>
              <el-option label="普通用户" value="employee"></el-option>
            </el-select>
          </el-form-item> -->

          <el-form-item>
            <el-button @click="handleSubmit" class="submit-btn">注册</el-button>
          </el-form-item>
        </el-form>
      </div>
    </section>
  </div>
</template>
<script>

import { register} from '@/api/user'
export default{
  name: "Register",

  data() {
    var validateEmail = (rule, value, callback) => {
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    if (!value) {
      callback(new Error('请输入邮箱地址'));
    } else if (!emailRegex.test(value)) {
      callback(new Error('请输入有效的邮箱地址'));
    } else {
      callback();
    }
  };
    var validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请重新输入密码"));
      } else if (value !== this.registerUser.password) {
        callback(new Error("两次密码不一致!"));
      } else {
        callback();
      }
    };
    return {
      ruleFormRef: null,
      registerUser: {
        name: "",
        email: "",
        password: "",
        password2: "",
        // identity: "",
      },
      
      rules: {
        name: [
          { required: true, message: "用户名不能为空", trigger: "change" },
          { min: 2, max: 30, message: "长度在2到30个字符之间", trigger: "blur" },
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
          { min: 6, max: 30, message: "长度在6到30个字符之间", trigger: "blur" },
        ],
        password2: [
          { min: 6, max: 30, message: "长度在6到30个字符之间", trigger: "blur" },
          { validator: validatePass2, trigger: "blur" },
        ],
        email: [
        { required: true, message: "邮箱不能为空", trigger: "blur" },
        { validator: this.validateEmail, trigger: 'blur' }
      ],
        
      }
    };
  },

  methods: {
    handleSubmit() {
      this.$refs.ruleFormRef.validate((valid) => {
        if (valid) {
          register(this.registerUser)
            .then(response => {
              console.log(response.data);
              this.$message({
                message: "用户注册成功.",
                type: "success",
              });
              this.$router.push("/");
            })
            .catch(error => {
              console.error("error submit!", error);
            });
        } else {
          console.log("error submit!");
          return false;
        }
      });
    },
  }
};
</script>
<style>
  .register{
  position: relative;
  width: 100%;
  height: 100%;
  /* background: url(./assets/bg.jpg) no-repeat center center; */
  background-size: 100% 100%;
}
.form-container {
  width: 500px;
  height: 300px;
  position: absolute;
  top: 10%;
  left: 34%;
  padding: 25px;
  border-radius: 5px;
  text-align: center;
}
.form-container .manage-tip .title {
  font-family: "Microsoft YaHei";
  font-weight: bold;
  font-size: 26px;
  color: #fff;
}

.registerForm,
.loginForm {
  margin-top: 20px;
  background-color: #fff;
  padding: 20px 40px 20px 20px;
  border-radius: 5px;
  box-shadow: 0px 5px 10px #cccc;
}
.submit-btn {
  width: 50%;
}

.tiparea {
  text-align: right;
  font-size: 12px;
  color: #333;
}
.tiparea p a {
  color: #409eff;
}
</style>

