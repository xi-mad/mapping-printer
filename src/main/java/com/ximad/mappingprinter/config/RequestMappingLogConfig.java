package com.ximad.mappingprinter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnWebApplication
public class RequestMappingLogConfig implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RequestMappingLogConfig.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<RequestMappingInfo, HandlerMethod> methods = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
        Map<String, List<String>> requestMapping = methods.entrySet()
                .stream()
                .map(e -> new AbstractMap.SimpleEntry<>(
                        e.getValue().getBeanType().getName(),
                        String.format("%s: %s%s",
                                e.getKey(),
                                e.getValue().getMethod().getName(),
                                Arrays.stream(e.getValue().getMethod().getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(",", "(", ")"))
                        )))
                .collect(Collectors.groupingBy(
                        AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toList())
                ));
        requestMapping.forEach((beanName, mappings) -> logger.info(String.format("\n\t%s:\n\t%s", beanName, String.join("\n\t", mappings))));
    }
}
