import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/course/${id}`)
  },
  
  getList() {
    return request.get('/course/list')
  },
  
  search(params) {
    return request.get('/course/search', { params })
  },
  
  getByTeacherId(teacherId) {
    return request.get(`/course/teacher/${teacherId}`)
  },
  
  add(data) {
    return request.post('/course', data)
  },
  
  update(data) {
    return request.put('/course', data)
  },
  
  delete(id) {
    return request.delete(`/course/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/course/batch', { data: ids })
  }
}