package com.qinghe.system.domain.edu;

public class EduAiModelOption
{
    private String modelName;

    private String displayName;

    private String description;

    public EduAiModelOption()
    {
    }

    public EduAiModelOption(String modelName, String displayName, String description)
    {
        this.modelName = modelName;
        this.displayName = displayName;
        this.description = description;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
