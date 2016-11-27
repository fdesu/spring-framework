package org.springframework.test.context.configuration.method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;

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
        assertEquals("second", testBean);
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

    public static class MainContext {
        @Bean
        public String testBean() {
            return "main";
        }
    }

}

class ContextHierarchyOnMethodParent {

    @ContextHierarchy({
            @ContextConfiguration(classes = ContextHierarchyOnMethodTest.FirstContext.class),
            @ContextConfiguration(classes = ContextHierarchyOnMethodTest.SecondContext.class)
    })
    public void shouldLoadContextsInStraightOrderFromParent() {
    }
}
