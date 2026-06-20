import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/notice/${id}`)
  },
  
  getList() {
    return request.get('/notice/list')
  },
  
  search(params) {
    return request.get('/notice/search', { params })
  },
  
  getPublished() {
    return request.get('/notice/published')
  },
  
  add(data) {
    return request.post('/notice', data)
  },
  
  publish(data) {
    return request.post('/notice/publish', data)
  },
  
  update(data) {
    return request.put('/notice', data)
  },
  
  delete(id) {
    return request.delete(`/notice/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/notice/batch', { data: ids })
  }
}