import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/department/${id}`)
  },

  getList() {
    return request.get('/department/list')
  },

  search(params) {
    return request.get('/department/search', { params })
  },

  add(data) {
    return request.post('/department', data)
  },

  update(data) {
    return request.put('/department', data)
  },

  delete(id) {
    return request.delete(`/department/${id}`)
  },

  deleteBatch(ids) {
    return request.delete('/department/batch', { data: ids })
  }
}