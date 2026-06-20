import request from '@/utils/request'

export default {
  // 管理员登录
  login(data) {
    return request.post('/admin/login', data)
  },
  
  // 获取管理员信息
  getById(id) {
    return request.get(`/admin/${id}`)
  },
  
  // 获取管理员列表
  getList() {
    return request.get('/admin/list')
  },
  
  // 条件查询
  search(params) {
    return request.get('/admin/search', { params })
  },
  
  // 新增管理员
  add(data) {
    return request.post('/admin', data)
  },
  
  // 更新管理员
  update(data) {
    return request.put('/admin', data)
  },
  
  // 删除管理员
  delete(id) {
    return request.delete(`/admin/${id}`)
  },
  
  // 批量删除
  deleteBatch(ids) {
    return request.delete('/admin/batch', { data: ids })
  },
  
  // 修改密码
  updatePassword(data) {
    return request.post('/admin/password', data)
  }
}