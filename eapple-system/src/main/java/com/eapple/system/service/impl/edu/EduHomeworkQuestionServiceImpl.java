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
import com.eapple.system.util.EduSchoolScopeUtils;

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
        if (SecurityUtils.hasExactRole("edu_teacher"))
        {
            question.setTeacherUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            question.setParentUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            question.setStudentUserId(SecurityUtils.getUserId());
        }
        EduSchoolScopeUtils.applySchoolScope(question);
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
            throw new ServiceException("请先维护学生档案");
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
                "课程：" + question.getCourseName() + "\n标题：" + question.getQuestionTitle() + "\n内容：" + question.getQuestionContent());
        questionMapper.updateQuestionAnswer(question.getQuestionId(), answer, "1", "normal");
        return rows;
    }

    @Override
    public int deleteQuestionByIds(Long[] questionIds)
    {
        for (Long questionId : questionIds)
        {
            verifyQuestionDeletePermission(questionId);
        }
        return questionMapper.deleteQuestionByIds(questionIds);
    }

    @Override
    public String regenerateAnswer(Long questionId)
    {
        EduHomeworkQuestion question = questionMapper.selectQuestionById(questionId);
        if (question == null)
        {
            throw new ServiceException("问题记录不存在");
        }
        String answer = aiService.answerHomeworkQuestion(questionId,
                "课程：" + question.getCourseName() + "\n标题：" + question.getQuestionTitle() + "\n内容：" + question.getQuestionContent());
        questionMapper.updateQuestionAnswer(questionId, answer, "1", "normal");
        return answer;
    }

    private Long resolveStudentUserId(Long studentUserId)
    {
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            return SecurityUtils.getUserId();
        }
        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            if (studentUserId == null)
            {
                throw new ServiceException("家长提问时必须选择孩子");
            }
            EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(studentUserId);
            if (profile == null || !SecurityUtils.getUserId().equals(profile.getParentUserId()))
            {
                throw new ServiceException("只能为已关联的孩子提问");
            }
            return studentUserId;
        }
        return studentUserId;
    }

    private void verifyQuestionDeletePermission(Long questionId)
    {
        EduHomeworkQuestion question = questionMapper.selectQuestionById(questionId);
        if (question == null)
        {
            return;
        }
        Long currentUserId = SecurityUtils.getUserId();
        if (SecurityUtils.hasExactRole("edu_student") && !currentUserId.equals(question.getStudentUserId()))
        {
            throw new ServiceException("只能删除自己的作业问答");
        }
        if (SecurityUtils.hasExactRole("edu_parent") && !currentUserId.equals(question.getParentUserId()))
        {
            throw new ServiceException("只能删除已绑定孩子的作业问答");
        }
    }
}
