package com.ruoyi.system.service.edu;

public interface IEduAiService
{
    String answerHomeworkQuestion(Long questionId, String prompt);

    String generateCourseNotice(Long courseId, String prompt);

    String generateTeachingSuggestion(Long courseId, String prompt);
}
