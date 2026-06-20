import request from '@/utils/request'

export default {
  getById(id) {
    return request.get(`/record/${id}`)
  },
  
  getList() {
    return request.get('/record/list')
  },
  
  search(params) {
    return request.get('/record/search', { params })
  },
  
  // 开始考试
  startExam(data) {
    return request.post('/record/start', data)
  },
  
  // 提交试卷
  submitExam(data) {
    return request.post('/record/submit', data)
  },
  
  // 手动阅卷
  gradeExam(data) {
    return request.post('/record/grade', data)
  },
  
  // 获取学生答案列表
  getAnswers(recordId) {
    return request.get(`/record/answers/${recordId}`)
  },
  
  // 根据考试ID和学生ID获取答题详情
  getByExamId(examId, studentId) {
    return request.get(`/record/exam/${examId}/student/${studentId}`)
  },
  
  delete(id) {
    return request.delete(`/record/${id}`)
  },
  
  deleteBatch(ids) {
    return request.delete('/record/batch', { data: ids })
  }
}