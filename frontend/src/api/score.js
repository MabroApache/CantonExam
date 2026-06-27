import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/score/${id}`)
  },

  getList() {
    return request.get('/score/list')
  },

  search(params) {
    return request.get('/score/search', { params })
  },

  getByExamId(examId) {
    return request.get(`/score/exam/${examId}`)
  },

  getByCandidateId(candidateId) {
    return request.get(`/score/candidate/${candidateId}`)
  },

  delete(id) {
    return request.delete(`/score/${id}`)
  },

  deleteBatch(ids) {
    return request.delete('/score/batch', { data: ids })
  }
}