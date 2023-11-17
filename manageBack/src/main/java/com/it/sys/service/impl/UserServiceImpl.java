package com.it.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.it.common.utils.AesUtil;
import com.it.common.utils.JwtUtil;
import com.it.sys.entity.Menu;
import com.it.sys.entity.User;
import com.it.sys.entity.UserRole;
import com.it.sys.mapper.UserMapper;
import com.it.sys.mapper.UserRoleMapper;
import com.it.sys.service.IMenuService;
import com.it.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cuiwei
 * @since 2023-03-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Autowired
    private IMenuService menuService;

    @Override
    public Map<String, Object> login(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 根据用户名查询
        wrapper.eq(User::getUsername,user.getUsername());
        User loginUser = this.getOne(wrapper);
        // 用户名存在,并且密码和传入的密码匹配,则生成token,将用户信息存入redis

        String passwd = AesUtil.decrypt(user.getPassword(),"123XXXXXXXXXX456");

        // System.out.println("user.getPassword()  " + user.getPassword());
        // System.out.println("loginUser.getPassword() " + loginUser.getPassword());
        if(loginUser != null && passwordEncoder.matches(passwd,loginUser.getPassword())){
            Map<String,Object> data = new HashMap<>();
            // 待优化，最终方案jwt
//            String key = "user" + UUID.randomUUID();
            String token = jwtUtil.createToken(loginUser);
            data.put("token",token);
            loginUser.setPassword(null);
            // redis存储过期时间（30min）
//            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);
            return data;
        }
        return null;
    }


    //    @Override
//    public Map<String, Object> login(User user) {
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(User::getUsername,user.getUsername());
//        wrapper.eq(User::getPassword,user.getPassword());
//        User loginUser = this.getOne(wrapper);
//        if(loginUser != null){
//            Map<String,Object> data = new HashMap<>();
//            String key = "user" + UUID.randomUUID();
//            data.put("token",key);  // 待优化，最终方案jwt
//            loginUser.setPassword(null);
//            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);
//            return data;
//        }
//        return null;
//    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        // 从redis查询token
//        Object obj = redisTemplate.opsForValue().get(token);
        // 反序列化
//        User user = JSON.parseObject(JSON.toJSONString(obj), User.class);
        User user = null;
        try {
            user = jwtUtil.parseToken(token, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(user != null){
            Map<String,Object> data = new HashMap<>();
            data.put("name",user.getUsername());
            data.put("avatar",user.getAvatar());
            // 角色
            List<String> roleList = this.getBaseMapper().getRoleNamesByUserId(user.getId());
            data.put("roles",roleList);
            // 权限列表
            List<Menu> menuList = menuService.getMenuListByUserId(user.getId());
            data.put("menuList",menuList);
            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
//        redisTemplate.delete(token);
    }

    @Override
    @Transactional
    public void addUser(User user) {
        // 写入用户表
        this.baseMapper.insert(user);
        // 写入用户角色表
        List<Integer> roleIdList = user.getRoleIdList();
        if (roleIdList != null){
            for (Integer roleId : roleIdList){
                userRoleMapper.insert(new UserRole(null,user.getId(),roleId));
            }
        }
    }

    @Override
    public User getUserById(Integer id) {
        User user = this.getById(id);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        List<UserRole> userRoleList = userRoleMapper.selectList(wrapper);

        List<Integer> roleIdList = userRoleList.stream().map(userRole -> {
            return userRole.getRoleId();
        }).collect(Collectors.toList());
        user.setRoleIdList(roleIdList);
        return user;
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        // 更新用户表
        this.baseMapper.updateById(user);
        // 清除原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,user.getId());
        userRoleMapper.delete(wrapper);
        // 设置新的角色
        List<Integer> roleIdList = user.getRoleIdList();
        if (roleIdList != null){
            for (Integer roleId : roleIdList){
                userRoleMapper.insert(new UserRole(null,user.getId(),roleId));
            }
        }
    }

    @Override
    public void deleteUserById(Integer id) {
        this.baseMapper.deleteById(id);
        // 清除原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        userRoleMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public Map<String, Object> register(User user){
        Map<String,Object> data = new HashMap<>();
        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            data.put("Msg", "账号不能为空!");
            return data;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            data.put("Msg", "密码不能为空!");
            return data;
        }
        if (StringUtils.isBlank(user.getPassword2())) {
            data.put("Msg", "确认密码不能为空!");
            return data;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            data.put("Msg", "邮箱不能为空!");
            return data;
        }
        if (StringUtils.isBlank(user.getPhone())) {
            data.put("Msg", "电话不能为空!");
            return data;
        }
        if (StringUtils.isBlank(user.getAvatar())) {
            user.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        }

        if(!user.getPassword2().equals(user.getPassword())) {
            data.put("Msg", "两次输入密码不相等!");
            return data;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 验证账号
        wrapper.eq(User::getUsername,user.getUsername());
        User u = this.getOne(wrapper);
        if (u != null) {
            data.put("Msg", "该账号已存在!");
            return data;
        }
        wrapper.clear();

        // 验证邮箱
        wrapper.eq(User::getEmail,user.getEmail());
        User e = this.getOne(wrapper);
        if (e != null) {
            data.put("Msg", "该邮箱已被注册!");
            return data;
        }
        wrapper.clear();

        // 验证电话
        wrapper.eq(User::getPhone,user.getPhone());
        User p = this.getOne(wrapper);
        if (p != null) {
            data.put("Msg", "该电话已被注册!");
            return data;
        }
        wrapper.clear();

        // 注册账号
        user.setDeleted(0);
        user.setStatus(1);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.baseMapper.insert(user);
        // 写入用户角色表
        List<Integer> roleIdList = user.getRoleIdList();
        if (roleIdList != null){
            for (Integer roleId : roleIdList){
                userRoleMapper.insert(new UserRole(null,user.getId(),roleId));
            }
        }
        data.put("Msg","success");
        return data;
    }
}
