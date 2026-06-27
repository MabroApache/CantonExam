import request from '@/utils/request'

export default {
  login(data) {
    return request.post('/creator/login', data)
  },

  register(data) {
    return request.post('/creator/register', data)
  },

  getById(id) {
    return request.get(`/creator/${id}`)
  },

  getList() {
    return request.get('/creator/list')
  },

  search(params) {
    return request.get('/creator/search', { params })
  },

  add(data) {
    return request.post('/creator', data)
  },

  update(data) {
    return request.put('/creator', data)
  },

  delete(id) {
    return request.delete(`/creator/${id}`)
  },

  deleteBatch(ids) {
    return request.delete('/creator/batch', { data: ids })
  },

  updatePassword(data) {
    return request.post('/creator/password', data)
  },

  audit(data) {
    return request.post('/creator/audit', data)
  }
}