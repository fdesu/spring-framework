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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ContextHierarchyOnMethodTest.MainContext.class)
public class ContextHierarchyOnMethodTest extends ContextHierarchyOnMethodParent {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private String testBean;

    @Test
    @ContextHierarchy({
            @ContextConfiguration(classes = FirstContext.class),
            @ContextConfiguration(classes = SecondContext.class)
    })
    public void shouldLoadContextsInStraightOrder() {
        assertEquals("second", testBean);
    }

    @Test
    @ContextHierarchy({
            @ContextConfiguration(classes = SecondContext.class),
            @ContextConfiguration(classes = FirstContext.class)
    })
    public void shouldLoadContextsInReverseOrder() {
        assertEquals("first", testBean);
    }

    @Test
    @Override
    public void shouldLoadContextsInStraightOrderFromParent() {
        assertEquals("parent", testBean);
    }

    @Test
    @Override
    public void shouldLoadContextsInReverseOrderFromParentParent() {
        assertEquals("parentParent", testBean);
    }

    @Test
    @Override
    public void shouldLoadContextsForMetaOnParent() {
        assertEquals("metaOnParent", testBean);
    }

    @Test
    @Override
    public void shouldLoadContextsForMetaOnParentParent() {
        assertEquals("metaOnParentParent", testBean);
    }

    @Test
    @ContextHierarchy({
            @ContextConfiguration(classes = FirstContext.class),
            @ContextConfiguration(classes = SecondContext.class)
    })
    public void shouldCreateProperHierarchy() {
        assertNotNull("child ApplicationContext", applicationContext);
        assertNotNull("parent ApplicationContext", applicationContext.getParent());
        assertNull("grandparent ApplicationContext", applicationContext.getParent().getParent());
    }

    // --------------------------------------------------------------------------

    public static class FirstContext {
        @Bean
        public String testBean() {
            return "first";
        }
    }

    public static class SecondContext {
        @Bean
        public String testBean() {
            return "second";
        }
    }

    public static class ParentLastContext {
        @Bean
        public String testBean() {
            return "parent";
        }
    }

    public static class ParentParentLastContext {
        @Bean
        public String testBean() {
            return "parentParent";
        }
    }

    public static class MainContext {
        @Bean
        public String testBean() {
            return "main";
        }
    }

    public static class MetaOnParentContext {
        @Bean
        public String testBean() {
            return "metaOnParent";
        }
    }

    public static class MetaOnParentParentContext {
        @Bean
        public String testBean() {
            return "metaOnParentParent";
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ContextHierarchy({@ContextConfiguration(classes = MetaOnParentContext.class)})
    public @interface MetaOnParent {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ContextHierarchy({@ContextConfiguration(classes = MetaOnParentParentContext.class)})
    public @interface MetaOnParentParent {}

}

class ContextHierarchyOnMethodParent extends ContextHierarchyOnMethodParentParent {
    @ContextHierarchy({@ContextConfiguration(classes = ContextHierarchyOnMethodTest.ParentLastContext.class)})
    public void shouldLoadContextsInStraightOrderFromParent() {}

    @ContextHierarchyOnMethodTest.MetaOnParent
    public void shouldLoadContextsForMetaOnParent() {}
}

class ContextHierarchyOnMethodParentParent {
    @ContextHierarchy({@ContextConfiguration(classes = ContextHierarchyOnMethodTest.ParentParentLastContext.class)})
    public void shouldLoadContextsInReverseOrderFromParentParent() {}

    @ContextHierarchyOnMethodTest.MetaOnParentParent
    public void shouldLoadContextsForMetaOnParentParent() {}
}
