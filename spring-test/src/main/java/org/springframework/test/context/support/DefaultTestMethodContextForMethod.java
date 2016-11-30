package org.springframework.test.context.support;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.CacheAwareContextLoaderDelegate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.util.MetaAnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultTestMethodContextForMethod extends DefaultTestContextBootstrapper {

    private final Method testMethod;

    public DefaultTestMethodContextForMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

    @Override
    public TestContext buildTestContext() {
        return new DefaultTestContext(getBootstrapContext().getTestClass(),
                buildMergedContextConfiguration(testMethod),
                getCacheAwareContextLoaderDelegate());
    }

    private MergedContextConfiguration buildMergedContextConfiguration(Method method) {
        CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate = getCacheAwareContextLoaderDelegate();

        if (MetaAnnotationUtils.findAnnotationDescriptorForTypes(
                method, ContextConfiguration.class, ContextHierarchy.class) == null) {
            return buildDefaultMergedContextConfiguration(getBootstrapContext().getTestClass(),
                    cacheAwareContextLoaderDelegate);
        }

        if (AnnotationUtils.findAnnotation(method, ContextHierarchy.class) != null) {
            Map<String, List<ContextConfigurationAttributes>> hierarchyMap =
                    ContextLoaderUtils.buildContextHierarchyMap(method);
            MergedContextConfiguration parentConfig = null;
            MergedContextConfiguration mergedConfig = null;

            for (List<ContextConfigurationAttributes> list : hierarchyMap.values()) {
                List<ContextConfigurationAttributes> reversedList = new ArrayList<>(list);
                Collections.reverse(reversedList);

                // Don't use the supplied testClass; instead ensure that we are
                // building the MCC for the actual test class that declared the
                // configuration for the current level in the context hierarchy.
                Assert.notEmpty(reversedList, "ContextConfigurationAttributes list must not be empty");
                Class<?> declaringClass = reversedList.get(0).getDeclaringClass();

                mergedConfig = buildMergedContextConfiguration(
                        declaringClass, reversedList, parentConfig, cacheAwareContextLoaderDelegate, true);
                parentConfig = mergedConfig;
            }

            // Return the last level in the context hierarchy
            return mergedConfig;
        } else {
            List<ContextConfigurationAttributes> configAttributesList
                    = ContextLoaderUtils.resolveContextConfigurationAttributes(method);

            return buildMergedContextConfiguration(method.getDeclaringClass(),
                    configAttributesList,
                    null, cacheAwareContextLoaderDelegate, true);
        }
    }
}
