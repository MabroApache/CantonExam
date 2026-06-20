import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/questionType/${id}`)
  },
  
  getByCode(code) {
    return request.get(`/questionType/code/${code}`)
  },
  
  getList() {
    return request.get('/questionType/list')
  },
  
  search(params) {
    return request.get('/questionType/search', { params })
  },
  
  add(data) {
    return request.post('/questionType', data)
  },
  
  update(data) {
    return request.put('/questionType', data)
  },
  
  delete(id) {
    return request.delete(`/questionType/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/questionType/batch', { data: ids })
  }
}