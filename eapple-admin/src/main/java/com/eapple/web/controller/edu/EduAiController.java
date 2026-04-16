package com.eapple.web.controller.edu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.system.service.edu.IEduAiService;

@RestController
@RequestMapping("/edu/ai")
public class EduAiController extends BaseController
{
    @Autowired
    private IEduAiService aiService;

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_teacher') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @GetMapping("/models")
    public AjaxResult models()
    {
        return success(aiService.getAvailableModels());
    }

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_teacher') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @GetMapping("/currentModel")
    public AjaxResult currentModel()
    {
        return success(aiService.getCurrentModel());
    }

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_teacher') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @PutMapping("/currentModel")
    public AjaxResult updateCurrentModel(@RequestBody ModelBody body)
    {
        return success(aiService.updateCurrentModel(body.getModelName()));
    }

    public static class ModelBody
    {
        private String modelName;

        public String getModelName()
        {
            return modelName;
        }

        public void setModelName(String modelName)
        {
            this.modelName = modelName;
        }
    }
}
