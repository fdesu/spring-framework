package org.springframework.test.context.configuration.method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ContextConfigurationOnMethodTest.TypeContext.class)
public class ContextConfigurationOnMethodTest extends ContextConfigurationOnMethodParent {

    @Autowired
    private String bean;

    @Test
    @ContextConfiguration(classes = MethodContext.class)
    public void methodLevelAnnotationShouldOverrideTypeAnnotation() {
        assertEquals("method", bean);
    }

    @Test
    @Override
    public void shouldPickUpParentBean() {
        assertEquals("parent", bean);
    }

    @Test
    public void typeLevelAnnotationShouldInjectRightValue() {
        assertEquals("type", bean);
    }


    public static class TypeContext {
        @Bean
        public String testBean() {
            return "type";
        }
    }

    public static class MethodContext {
        @Bean
        public String testBean() {
            return "method";
        }
    }

}

class ContextConfigurationOnMethodParent {

    @ContextConfiguration(classes = ParentConfig.class)
    public void shouldPickUpParentBean() {
    }

    public static class ParentConfig {
        @Bean
        public String testBean() {
            return "parent";
        }
    }

}
