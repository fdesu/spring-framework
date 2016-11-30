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

public class DefaultTestMethodContextBootstrapper extends DefaultTestContextBootstrapper {

    private final Method testMethod;

    public DefaultTestMethodContextBootstrapper(Method testMethod) {
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
