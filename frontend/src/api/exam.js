import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/exam/${id}`)
  },
  
  getList() {
    return request.get('/exam/list')
  },
  
  search(params) {
    return request.get('/exam/search', { params })
  },
  
  getByStudentId(studentId) {
    return request.get(`/exam/student/${studentId}`)
  },
  
  add(data) {
    return request.post('/exam', data)
  },
  
  update(data) {
    return request.put('/exam', data)
  },
  
  delete(id) {
    return request.delete(`/exam/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/exam/batch', { data: ids })
  }
}