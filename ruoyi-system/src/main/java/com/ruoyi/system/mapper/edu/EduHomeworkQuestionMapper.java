package com.ruoyi.system.mapper.edu;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.edu.EduHomeworkQuestion;

public interface EduHomeworkQuestionMapper
{
    EduHomeworkQuestion selectQuestionById(Long questionId);

    List<EduHomeworkQuestion> selectQuestionList(EduHomeworkQuestion question);

    int insertQuestion(EduHomeworkQuestion question);

    int updateQuestionAnswer(@Param("questionId") Long questionId, @Param("aiAnswer") String aiAnswer,
            @Param("answerStatus") String answerStatus, @Param("safetyFlag") String safetyFlag);

    int deleteQuestionByIds(Long[] questionIds);

    Long countQuestions(EduHomeworkQuestion question);
}
