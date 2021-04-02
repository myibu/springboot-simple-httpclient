package org.github.myibu.httpclient.springboot;

import com.github.myibu.httpclient.annotation.HttpClient;
import org.springframework.beans.factory.support.*;
import org.springframework.context.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author myibu
 * @since 1.0
 */
@Component
public class HttpClientRegistrar implements ImportBeanDefinitionRegistrar,
        EnvironmentAware, ResourceLoaderAware {

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private Environment environment;

    private ResourcePatternResolver resourcePatternResolver;

    private MetadataReaderFactory metadataReaderFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attrs = metadata
                .getAnnotationAttributes(EnableHttpClient.class.getName());
        // get package name where EnableHttpClient is annotated
        String[] defaultBasePackages =  new String[]{ClassUtils.getPackageName(metadata.getClassName())};
        String[] basePackages = (String[])attrs.getOrDefault("basePackages", new String[]{});
        if (basePackages.length == 0) {
            basePackages = defaultBasePackages;
        }
        Set<String> validatedBasePackages = new HashSet<>();
        for (String backPackage: basePackages) {
            if (null != backPackage && !backPackage.isEmpty()) {
                validatedBasePackages.add(backPackage);
            }
        }
        // scan in class path
        Set<Class<?>> allClazz = new LinkedHashSet<>();
        for (String basePackage : validatedBasePackages) {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
            try {
                Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();
                        Class<?> clazz;
                        try {
                            clazz = Class.forName(className);
                            allClazz.add(clazz);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Set<Class<?>> httpClients = new LinkedHashSet<>();
        for (Class<?> clazz : allClazz){
            if (null != clazz.getAnnotation(HttpClient.class)) {
                httpClients.add(clazz);
            }
        }
        // register bean for httpClient
        for (Class<?> clazz : httpClients){
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getConstructorArgumentValues().addGenericArgumentValue(clazz);
            definition.setBeanClass(HttpClientFactoryBean.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(clazz.getSimpleName(), definition);
        }
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }
}
