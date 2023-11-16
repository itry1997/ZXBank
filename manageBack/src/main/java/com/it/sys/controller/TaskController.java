package com.it.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.vo.Result;
import com.it.sys.dto.TaskDto;
import com.it.sys.entity.Task;
import com.it.sys.entity.User;
import com.it.sys.service.ITaskService;
import com.it.sys.service.IUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private ITaskService taskService;


    // 接口测试正确
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody Task task){
        taskService.addTask(task);
        return Result.success("新增任务成功");
    }

    // 接口测试正确
    @DeleteMapping("/delete")
    public Result<String> deleteUserById(@RequestParam("id") Integer id,@RequestHeader("X-Token") String token){
        Boolean aBoolean = taskService.deleteUserById(id, token);
        if (aBoolean){
            return Result.success("删除成功");
        }else {
            return Result.fail("删除失败");
        }

    }

    // 接口测试正确
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody Task task, @RequestHeader("X-Token") String token){
        Integer id = task.getId();
        Boolean b = taskService.updateTask(id, task, token);
        if(b) {
            return Result.success("修改任务成功");
        }else{
            return Result.fail("修改失败");
        }
    }

    // 已测试正确
    @GetMapping("/list")
    public Result<Map<String,Object>> getTaskListPage(@RequestParam(value = "pageNo") Long pageNo,
                                                      @RequestParam(value = "pageSize") Long pageSize,
                                                      @RequestParam(value = "title",required = false) String title,
                                                      @RequestParam(value = "userid",required = false) Integer userid){
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(title != null,Task::getTitle,title);
        wrapper.eq(userid != null,Task::getUserid,userid);
        Page<Task> page = new Page<>(pageNo, pageSize);
        taskService.page(page,wrapper);

        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());

        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Task> getTaskafter(@PathVariable("id") Integer id){
        Task task = taskService.findTaskById(id);
        return Result.success(task);
    }

    @GetMapping("/getById")
    public Result<TaskDto> getById(@RequestParam("id") Integer id){
        TaskDto taskDto = taskService.getById(id);
        return Result.success(taskDto);
    }


}
