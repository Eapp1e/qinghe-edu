package com.eapple.web.controller.edu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.eapple.common.annotation.Log;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
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

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_teacher') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @Log(title = "网课推荐", businessType = BusinessType.OTHER)
    @PostMapping("/online-resource-recommend")
    public AjaxResult onlineResourceRecommend(@RequestBody OnlineResourceBody body)
    {
        if (body == null || StringUtils.isEmpty(body.getInterest()))
        {
            throw new ServiceException("请输入想学习的内容");
        }
        if (body.getResources() == null || body.getResources().isEmpty())
        {
            throw new ServiceException("当前资源库为空，暂时无法推荐");
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("用户想学内容：").append(body.getInterest()).append("\n");
        prompt.append("资源库：\n");
        for (OnlineResourceItem item : body.getResources())
        {
            if (item == null || StringUtils.isEmpty(item.getTitle()) || StringUtils.isEmpty(item.getLink()))
            {
                continue;
            }
            prompt.append("- 标题：").append(item.getTitle())
                    .append("；来源：").append(StringUtils.defaultString(item.getSource(), ""))
                    .append("；分类：").append(StringUtils.defaultString(item.getCategory(), ""))
                    .append("；描述：").append(StringUtils.defaultString(item.getDescription(), ""))
                    .append("；链接：").append(item.getLink())
                    .append("\n");
        }

        String content = aiService.generateOnlineResourceRecommendation(SecurityUtils.getUserId(), prompt.toString());
        List<JSONObject> recommendations = normalizeRecommendationList(content);
        if (recommendations.isEmpty())
        {
            recommendations = buildLocalRecommendations(body.getInterest(), body.getResources());
        }
        return success(recommendations);
    }

    private List<JSONObject> normalizeRecommendationList(String content)
    {
        JSONArray array = parseRecommendationArray(content);
        List<JSONObject> result = new ArrayList<>();
        for (int i = 0; i < array.size(); i++)
        {
            Object item = array.get(i);
            if (item instanceof JSONObject)
            {
                JSONObject object = (JSONObject) item;
                if (StringUtils.isNotEmpty(object.getString("title")) && StringUtils.isNotEmpty(object.getString("link")))
                {
                    result.add(object);
                }
            }
        }
        return result;
    }

    private JSONArray parseRecommendationArray(String content)
    {
        if (StringUtils.isEmpty(content))
        {
            return new JSONArray();
        }

        JSONArray parsed = tryParseArray(content);
        if (parsed != null)
        {
            return parsed;
        }

        String normalized = content.trim()
                .replace("```json", "")
                .replace("```JSON", "")
                .replace("```", "")
                .trim();

        parsed = tryParseArray(normalized);
        if (parsed != null)
        {
            return parsed;
        }

        try
        {
            JSONObject object = JSON.parseObject(normalized);
            JSONArray array = object.getJSONArray("recommendations");
            if (array == null)
            {
                array = object.getJSONArray("data");
            }
            if (array == null)
            {
                array = object.getJSONArray("items");
            }
            if (array != null)
            {
                return array;
            }
        }
        catch (Exception ignored)
        {
        }

        int start = normalized.indexOf('[');
        int end = normalized.lastIndexOf(']');
        if (start >= 0 && end > start)
        {
            parsed = tryParseArray(normalized.substring(start, end + 1));
            if (parsed != null)
            {
                return parsed;
            }
        }
        return parseLineRecommendations(normalized);
    }

    private JSONArray tryParseArray(String text)
    {
        try
        {
            return JSON.parseArray(text);
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private JSONArray parseLineRecommendations(String text)
    {
        JSONArray result = new JSONArray();
        if (StringUtils.isEmpty(text))
        {
            return result;
        }
        Pattern linkPattern = Pattern.compile("(https?://[^\\s\\]）>]+)");
        String[] lines = text.split("\\r?\\n");
        for (String rawLine : lines)
        {
            String line = StringUtils.trim(rawLine);
            if (StringUtils.isEmpty(line))
            {
                continue;
            }
            Matcher matcher = linkPattern.matcher(line);
            if (!matcher.find())
            {
                continue;
            }
            String link = matcher.group(1);
            String title = line.substring(0, matcher.start()).replaceAll("^[\\-\\d\\.、\\s]+", "").trim();
            String reason = line.substring(matcher.end()).replaceAll("^[：:：\\-\\s]+", "").trim();
            JSONObject object = new JSONObject();
            object.put("title", StringUtils.isEmpty(title) ? "推荐资源" : title);
            object.put("link", link);
            object.put("reason", StringUtils.isEmpty(reason) ? "与当前学习兴趣较匹配" : reason);
            result.add(object);
        }
        return result;
    }

    private List<JSONObject> buildLocalRecommendations(String interest, List<OnlineResourceItem> resources)
    {
        List<JSONObject> result = new ArrayList<>();
        if (resources == null || resources.isEmpty())
        {
            return result;
        }

        String normalizedInterest = StringUtils.defaultString(interest).toLowerCase();
        String[] keywords = normalizedInterest.split("[,，、\\s]+");
        List<JSONObject> matched = new ArrayList<>();
        List<JSONObject> secondary = new ArrayList<>();

        for (OnlineResourceItem item : resources)
        {
            if (item == null || StringUtils.isEmpty(item.getTitle()) || StringUtils.isEmpty(item.getLink()))
            {
                continue;
            }

            String resourceText = (StringUtils.defaultString(item.getTitle()) + " "
                    + StringUtils.defaultString(item.getCategory()) + " "
                    + StringUtils.defaultString(item.getDescription()) + " "
                    + StringUtils.defaultString(item.getSource())).toLowerCase(Locale.ROOT);

            int score = 0;
            for (String keyword : keywords)
            {
                String current = StringUtils.trim(keyword);
                if (StringUtils.isNotEmpty(current) && resourceText.contains(current))
                {
                    score++;
                }
            }

            JSONObject object = new JSONObject();
            object.put("title", item.getTitle());
            object.put("link", item.getLink());
            object.put("reason", score > 0
                    ? "与“" + interest + "”较匹配，可优先从该资源开始学习。"
                    : "属于当前资源库中较通用的优质学习入口，可作为延伸学习参考。");

            if (score > 0)
            {
                matched.add(object);
            }
            else
            {
                secondary.add(object);
            }
        }

        result.addAll(matched);
        for (JSONObject item : secondary)
        {
            if (result.size() >= 4)
            {
                break;
            }
            result.add(item);
        }
        if (result.size() > 4)
        {
            return new ArrayList<>(result.subList(0, 4));
        }
        return result;
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

    public static class OnlineResourceBody
    {
        private String interest;

        private List<OnlineResourceItem> resources = new ArrayList<>();

        public String getInterest()
        {
            return interest;
        }

        public void setInterest(String interest)
        {
            this.interest = interest;
        }

        public List<OnlineResourceItem> getResources()
        {
            return resources;
        }

        public void setResources(List<OnlineResourceItem> resources)
        {
            this.resources = resources;
        }
    }

    public static class OnlineResourceItem
    {
        private String title;
        private String source;
        private String category;
        private String description;
        private String link;

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getSource()
        {
            return source;
        }

        public void setSource(String source)
        {
            this.source = source;
        }

        public String getCategory()
        {
            return category;
        }

        public void setCategory(String category)
        {
            this.category = category;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getLink()
        {
            return link;
        }

        public void setLink(String link)
        {
            this.link = link;
        }
    }
}
