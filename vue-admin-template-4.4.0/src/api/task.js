import request from '@/utils/request'

export default {

  getTaskList(searchMoudle) {
    return request({
      url: 'task/list',
      method: 'get',
      params: {
        pageNo: searchMoudle.pageNo,
        pageSize: searchMoudle.pageSize,
        title: searchMoudle.title,
        userid: searchMoudle.userid
      }
    })
  },

  addTask(task) {
    return request({
      url: '/task',
      method: 'post',
      data: task
    })
  },

  getTaskById(id) {
    return request({
      url: `/task/${id}`,
      method: 'get'
    })
  },

  updateTask(task) {
    return request({
      url: '/task',
      method: 'put',
      data: task
    })
  },

  saveTask(task) {
    if (task.id == null || task.id == undefined) {
      return this.addTask(task)
    }
    return this.updateTask(task)
  },

  deleteTaskById(id) {
    return request({
      url: `/task/${id}`,
      method: 'delete'
    })
  }

}

