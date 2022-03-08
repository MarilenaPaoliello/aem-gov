package com.adobe.aem.gov.core.models;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;


import com.day.cq.wcm.api.Page;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

@Model(adaptables = Resource.class,
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TogglemenuModel {
    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE)
    @Default(values="No resourceType")
    protected String resourceType;
    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;
    @ValueMapValue
    (name="pages")
    private List<String> pages;

    

    public List<String> getPages() {
        if (pages != null) {
            return new ArrayList<String>(pages);     
        } else {
            return Collections.emptyList();
        }
    }

    public List<Page> getResourcePages(){
        List<Page> resourcePaths = new ArrayList<Page>();
        for(String path : pages){
            Resource itemResource = resourceResolver.resolve(path);
            Page node = itemResource.adaptTo(Page.class);
            resourcePaths.add(node);
        }

        return resourcePaths;
    }

    
}
