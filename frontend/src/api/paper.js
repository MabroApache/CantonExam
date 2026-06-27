import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/paper/${id}`)
  },

  getList() {
    return request.get('/paper/list')
  },

  search(params) {
    return request.get('/paper/search', { params })
  },

  getByCreatorId(creatorId) {
    return request.get(`/paper/creator/${creatorId}`)
  },

  // 新增试卷（管理员）
  add(data) {
    return request.post('/paper/add', data)
  },

  // 手动组卷 - 支持为每道题设置分值
  manualCreate(data) {
    return request.post('/paper/manual', data)
  },

  // 自动组卷 - 支持设置各题型数量和总分值
  autoCreate(data) {
    return request.post('/paper/auto', data)
  },

  // 获取试卷题目列表
  getQuestions(paperId) {
    return request.get(`/paper/questions/${paperId}`)
  },

  update(data) {
    return request.put('/paper', data)
  },

  delete(id) {
    return request.delete(`/paper/${id}`)
  },

  deleteBatch(ids) {
    return request.delete('/paper/batch', { data: ids })
  }
}