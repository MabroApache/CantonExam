import request from '@/utils/request'

export default {
  login(data) {
    return request.post('/candidate/login', data)
  },

  register(data) {
    return request.post('/candidate/register', data)
  },

  getById(id) {
    return request.get(`/candidate/${id}`)
  },

  getList() {
    return request.get('/candidate/list')
  },

  search(params) {
    return request.get('/candidate/search', { params })
  },

  add(data) {
    return request.post('/candidate', data)
  },

  update(data) {
    return request.put('/candidate', data)
  },

  delete(id) {
    return request.delete(`/candidate/${id}`)
  },

  deleteBatch(ids) {
    return request.delete('/candidate/batch', { data: ids })
  },

  audit(data) {
    return request.post('/candidate/audit', data)
  },

  updatePassword(data) {
    return request.post('/candidate/password', data)
  }
}