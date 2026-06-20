import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/question/${id}`)
  },
  
  getList() {
    return request.get('/question/list')
  },
  
  search(params) {
    return request.get('/question/search', { params })
  },
  
  getByCourseId(courseId) {
    return request.get(`/question/course/${courseId}`)
  },
  
  getByTypeId(typeId) {
    return request.get(`/question/type/${typeId}`)
  },
  
  getByTeacherId(teacherId) {
    return request.get(`/question/teacher/${teacherId}`)
  },
  
  add(data) {
    return request.post('/question', data)
  },
  
  update(data) {
    return request.put('/question', data)
  },
  
  delete(id) {
    return request.delete(`/question/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/question/batch', { data: ids })
  }
}