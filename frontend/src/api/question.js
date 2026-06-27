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

  getByTypeId(typeId) {
    return request.get(`/question/type/${typeId}`)
  },

  getByCreatorId(creatorId) {
    return request.get(`/question/creator/${creatorId}`)
  },

  // 根据部门ID获取题目
  getByDepartmentId(departmentId) {
    return request.get(`/question/department/${departmentId}`)
  },

  // 根据标签搜索题目
  getByTags(tags) {
    return request.get('/question/tags', { params: { tags } })
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