import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/share/${id}`)
  },
  
  getList() {
    return request.get('/share/list')
  },
  
  search(params) {
    return request.get('/share/search', { params })
  },
  
  getByStudentId(studentId) {
    return request.get(`/share/student/${studentId}`)
  },
  
  add(data) {
    return request.post('/share', data)
  },
  
  update(data) {
    return request.put('/share', data)
  },
  
  delete(id) {
    return request.delete(`/share/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/share/batch', { data: ids })
  },
  
  view(id) {
    return request.post(`/share/view/${id}`)
  },
  
  like(id) {
    return request.post(`/share/like/${id}`)
  }
}