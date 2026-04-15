package com.eapple.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.system.domain.edu.EduCourse;
import com.eapple.system.domain.edu.EduHomeworkQuestion;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduCourseMapper;
import com.eapple.system.mapper.edu.EduHomeworkQuestionMapper;
import com.eapple.system.mapper.edu.EduStudentProfileMapper;
import com.eapple.system.service.edu.IEduAiService;
import com.eapple.system.service.edu.IEduHomeworkQuestionService;

@Service
public class EduHomeworkQuestionServiceImpl implements IEduHomeworkQuestionService
{
    @Autowired
    private EduHomeworkQuestionMapper questionMapper;

    @Autowired
    private EduStudentProfileMapper profileMapper;

    @Autowired
    private EduCourseMapper courseMapper;

    @Autowired
    private IEduAiService aiService;

    @Override
    public List<EduHomeworkQuestion> selectQuestionList(EduHomeworkQuestion question)
    {
        if (SecurityUtils.hasRole("edu_teacher"))
        {
            question.setTeacherUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasRole("edu_parent"))
        {
            question.setParentUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasRole("edu_student"))
        {
            question.setStudentUserId(SecurityUtils.getUserId());
        }
        return questionMapper.selectQuestionList(question);
    }

    @Override
    public EduHomeworkQuestion selectQuestionById(Long questionId)
    {
        return questionMapper.selectQuestionById(questionId);
    }

    @Override
    public int insertQuestion(EduHomeworkQuestion question)
    {
        Long studentUserId = resolveStudentUserId(question.getStudentUserId());
        EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(studentUserId);
        if (profile == null)
        {
            throw new ServiceException("璇峰厛缁存姢瀛︾敓妗ｆ");
        }
        if (question.getCourseId() != null)
        {
            EduCourse course = courseMapper.selectCourseById(question.getCourseId());
            if (course != null)
            {
                question.setCourseName(course.getCourseName());
                question.setTeacherUserId(course.getTeacherUserId());
            }
        }
        question.setStudentUserId(profile.getStudentUserId());
        question.setStudentName(profile.getStudentName());
        question.setParentUserId(profile.getParentUserId());
        question.setAnswerStatus("0");
        question.setSafetyFlag("normal");
        question.setCreateBy(SecurityUtils.getUsername());
        int rows = questionMapper.insertQuestion(question);
        String answer = aiService.answerHomeworkQuestion(question.getQuestionId(),
                "璇剧▼锛? + question.getCourseName() + "\n鏍囬锛? + question.getQuestionTitle() + "\n鍐呭锛? + question.getQuestionContent());
        questionMapper.updateQuestionAnswer(question.getQuestionId(), answer, "1", "normal");
        return rows;
    }

    @Override
    public int deleteQuestionByIds(Long[] questionIds)
    {
        return questionMapper.deleteQuestionByIds(questionIds);
    }

    @Override
    public String regenerateAnswer(Long questionId)
    {
        EduHomeworkQuestion question = questionMapper.selectQuestionById(questionId);
        if (question == null)
        {
            throw new ServiceException("闂璁板綍涓嶅瓨鍦?);
        }
        String answer = aiService.answerHomeworkQuestion(questionId,
                "璇剧▼锛? + question.getCourseName() + "\n鏍囬锛? + question.getQuestionTitle() + "\n鍐呭锛? + question.getQuestionContent());
        questionMapper.updateQuestionAnswer(questionId, answer, "1", "normal");
        return answer;
    }

    private Long resolveStudentUserId(Long studentUserId)
    {
        if (SecurityUtils.hasRole("edu_student"))
        {
            return SecurityUtils.getUserId();
        }
        if (SecurityUtils.hasRole("edu_parent"))
        {
            if (studentUserId == null)
            {
                throw new ServiceException("瀹堕暱鎻愰棶鏃跺繀椤婚€夋嫨瀛╁瓙");
            }
            EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(studentUserId);
            if (profile == null || !SecurityUtils.getUserId().equals(profile.getParentUserId()))
            {
                throw new ServiceException("鍙兘涓哄凡鍏宠仈鐨勫瀛愭彁闂?);
            }
            return studentUserId;
        }
        return studentUserId;
    }
}
