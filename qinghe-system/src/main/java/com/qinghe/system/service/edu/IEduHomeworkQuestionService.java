package com.qinghe.system.service.edu;

import java.util.List;
import com.qinghe.system.domain.edu.EduHomeworkQuestion;

public interface IEduHomeworkQuestionService
{
    List<EduHomeworkQuestion> selectQuestionList(EduHomeworkQuestion question);

    EduHomeworkQuestion selectQuestionById(Long questionId);

    int insertQuestion(EduHomeworkQuestion question);

    int deleteQuestionByIds(Long[] questionIds);

    String regenerateAnswer(Long questionId);
}
