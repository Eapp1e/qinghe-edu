package com.qinghe.system.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "edu.ai")
public class EduAiProperties
{
    private boolean enabled;
    private String endpoint;
    private String apiKey;
    private String model = "gpt-4o-mini";
    private Integer timeoutSeconds = 30;
    private Integer maxPromptLength = 1200;
    private List<String> bannedKeywords = new ArrayList<>();

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public Integer getTimeoutSeconds()
    {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds)
    {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Integer getMaxPromptLength()
    {
        return maxPromptLength;
    }

    public void setMaxPromptLength(Integer maxPromptLength)
    {
        this.maxPromptLength = maxPromptLength;
    }

    public List<String> getBannedKeywords()
    {
        return bannedKeywords;
    }

    public void setBannedKeywords(List<String> bannedKeywords)
    {
        this.bannedKeywords = bannedKeywords;
    }
}
