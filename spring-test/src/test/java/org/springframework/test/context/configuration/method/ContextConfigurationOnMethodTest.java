/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.configuration.method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    public void typeLevelAnnotationShouldInjectRightValue() {
        assertEquals("type", bean);
    }

    @Test
    @Override
    public void shouldPickUpParentBean() {
        assertEquals("parent", bean);
    }

    @Test
    @Override
    public void shouldPickUpParentParentBean() {
        assertEquals("parentParent", bean);
    }

    @Test
    @Override
    public void shouldPickUpMetaParent() {
        assertEquals("metaOnParent", bean);
    }

    @Test
    @Override
    public void shouldPickUpMetaParentParent() {
        assertEquals("metaOnParentParent", bean);
    }

    // ---------------------------------------------------------------

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

    public static class ParentConfig {
        @Bean
        public String testBean() {
            return "parent";
        }
    }

    public static class ParentParentConfig {
        @Bean
        public String testBean() {
            return "parentParent";
        }
    }

    public static class MetaParentConfig {
        @Bean
        public String testBean() {
            return "metaOnParent";
        }
    }

    public static class MetaParentParentConfig {
        @Bean
        public String testBean() {
            return "metaOnParentParent";
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ContextConfiguration(classes = MetaParentConfig.class)
    public @interface MetaOnParent {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ContextConfiguration(classes = MetaParentParentConfig.class)
    public @interface MetaOnParentParent {}
}

class ContextConfigurationOnMethodParentParent {
    @ContextConfiguration(classes = ContextConfigurationOnMethodTest.ParentConfig.class)
    public void shouldPickUpParentBean() {}

    @ContextConfigurationOnMethodTest.MetaOnParent
    public void shouldPickUpMetaParent() {}
}

class ContextConfigurationOnMethodParent extends ContextConfigurationOnMethodParentParent {
    @ContextConfiguration(classes = ContextConfigurationOnMethodTest.ParentParentConfig.class)
    public void shouldPickUpParentParentBean() {}

    @ContextConfigurationOnMethodTest.MetaOnParentParent
    public void shouldPickUpMetaParentParent() {}
}