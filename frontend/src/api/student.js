import request from '@/utils/request'

export default {
  login(data) {
    return request.post('/student/login', data)
  },
  
  register(data) {
    return request.post('/student/register', data)
  },
  
  getById(id) {
    return request.get(`/student/${id}`)
  },
  
  getList() {
    return request.get('/student/list')
  },
  
  search(params) {
    return request.get('/student/search', { params })
  },
  
  add(data) {
    return request.post('/student', data)
  },
  
  update(data) {
    return request.put('/student', data)
  },
  
  delete(id) {
    return request.delete(`/student/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/student/batch', { data: ids })
  },
  
  audit(data) {
    return request.post('/student/audit', data)
  },
  
  updatePassword(data) {
    return request.post('/student/password', data)
  }
}