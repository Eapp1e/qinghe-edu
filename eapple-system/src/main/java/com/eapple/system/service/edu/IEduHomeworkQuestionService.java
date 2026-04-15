package com.eapple.system.service.edu;

import java.util.List;
import com.eapple.system.domain.edu.EduHomeworkQuestion;

public interface IEduHomeworkQuestionService
{
    List<EduHomeworkQuestion> selectQuestionList(EduHomeworkQuestion question);

    EduHomeworkQuestion selectQuestionById(Long questionId);

    int insertQuestion(EduHomeworkQuestion question);

    int deleteQuestionByIds(Long[] questionIds);

    String regenerateAnswer(Long questionId);
}
