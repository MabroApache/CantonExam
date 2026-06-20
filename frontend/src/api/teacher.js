import request from '@/utils/request'

export default {
  login(data) {
    return request.post('/teacher/login', data)
  },
  
  register(data) {
    return request.post('/teacher/register', data)
  },
  
  getById(id) {
    return request.get(`/teacher/${id}`)
  },
  
  getList() {
    return request.get('/teacher/list')
  },
  
  search(params) {
    return request.get('/teacher/search', { params })
  },
  
  add(data) {
    return request.post('/teacher', data)
  },
  
  update(data) {
    return request.put('/teacher', data)
  },
  
  delete(id) {
    return request.delete(`/teacher/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/teacher/batch', { data: ids })
  },
  
  updatePassword(data) {
    return request.post('/teacher/password', data)
  }
}