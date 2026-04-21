package com.eapple.system.service.edu;

import java.util.List;
import com.eapple.system.domain.edu.EduAiModelOption;

public interface IEduAiService
{
    String answerHomeworkQuestion(Long questionId, String prompt);

    String generateCourseNotice(Long courseId, String prompt);

    String generateTeachingSuggestion(Long courseId, String prompt);

    String generateCourseRecommendation(Long studentUserId, String prompt);

    String generateOnlineResourceRecommendation(Long userId, String prompt);

    String getCurrentModel();

    List<EduAiModelOption> getAvailableModels();

    String updateCurrentModel(String modelName);
}
