package org.springframework.test.util;

import org.junit.Test;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.util.MetaAnnotationUtils.findAnnotationDescriptorForMethod;

public class MetaAnnotationUtilsForMethod {

    private void assertAtComponentOnComposedAnnotation(Method method, String name,
                                                       Class<? extends Annotation> composedAnnotationType) {
        assertAtComponentOnComposedAnnotation(method, method.getDeclaringClass(), name, composedAnnotationType);
    }

    private void assertAtComponentOnComposedAnnotation(Method method, Class<?> rootDeclaringClass, String name,
                                                       Class<? extends Annotation> composedAnnotationType) {
        assertAtComponentOnComposedAnnotation(method, rootDeclaringClass, composedAnnotationType, name, composedAnnotationType);
    }

    private void assertAtComponentOnComposedAnnotation(Method method, Class<?> rootDeclaringClass,
                                                       Class<?> declaringClass, String name, Class<? extends Annotation> composedAnnotationType) {
        MetaAnnotationUtils.AnnotationDescriptor<Component> descriptor = findAnnotationDescriptorForMethod(method, Component.class);
        assertNotNull("AnnotationDescriptor should not be null", descriptor);
        assertEquals("rootDeclaringClass", rootDeclaringClass, descriptor.getRootDeclaringClass());
        assertEquals("declaringClass", declaringClass, descriptor.getDeclaringClass());
        assertEquals("annotationType", Component.class, descriptor.getAnnotationType());
        assertEquals("component name", name, descriptor.getAnnotation().value());
        assertNotNull("composedAnnotation should not be null", descriptor.getComposedAnnotation());
        assertEquals("composedAnnotationType", composedAnnotationType, descriptor.getComposedAnnotationType());
    }

    @Test
    public void findAnnotationDescriptorWithNoAnnotationPresent() throws Exception {
        assertNull(findAnnotationDescriptorForMethod(
                NonAnnotatedInterface.class.getMethod("something"), Transactional.class));
        assertNull(findAnnotationDescriptorForMethod(
                NonAnnotatedClass.class.getMethod("something"), Transactional.class));
    }

    @Test
    public void findAnnotationDescriptorWithInheritedAnnotationOnMethod() throws Exception {
        // Note: @Transactional is inherited
        Method inheritedAnnotationMethod = InheritedAnnotationClass.class.getMethod("something");

        assertEquals(InheritedAnnotationClass.class, findAnnotationDescriptorForMethod(
                inheritedAnnotationMethod,Transactional.class).getRootDeclaringClass());
        assertEquals(InheritedAnnotationClass.class, findAnnotationDescriptorForMethod(
                inheritedAnnotationMethod,Transactional.class).getDeclaringClass());
        assertEquals(inheritedAnnotationMethod, findAnnotationDescriptorForMethod(
                inheritedAnnotationMethod,Transactional.class).getDeclaredMethod());

        assertEquals(SubInheritedAnnotationClass.class, findAnnotationDescriptorForMethod(
                SubInheritedAnnotationClass.class.getMethod("something"), Transactional.class).getRootDeclaringClass());
        assertEquals(InheritedAnnotationClass.class, findAnnotationDescriptorForMethod(
                SubInheritedAnnotationClass.class.getMethod("something"), Transactional.class).getDeclaringClass());
        assertEquals(inheritedAnnotationMethod, findAnnotationDescriptorForMethod(
                SubInheritedAnnotationClass.class.getMethod("something"), Transactional.class).getDeclaredMethod());
    }

    @Test
    public void findAnnotationDescriptorWithInheritedAnnotationOnInterface() throws Exception {
        // Note: @Transactional is inherited
        Method something = InheritedAnnotationInterface.class.getMethod("something");

        Transactional rawAnnotation = something.getAnnotation(Transactional.class);

        MetaAnnotationUtils.MethodAnnotationDescriptor<Transactional> descriptor;

        descriptor = findAnnotationDescriptorForMethod(something, Transactional.class);
        assertNotNull(descriptor);
        assertEquals(InheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(something, descriptor.getDeclaredMethod());
        assertEquals(rawAnnotation, descriptor.getAnnotation());

        Method somethingInSubInherited = SubInheritedAnnotationInterface.class.getMethod("something");
        descriptor = findAnnotationDescriptorForMethod(somethingInSubInherited, Transactional.class);
        assertNotNull(descriptor);
        assertEquals(SubInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(InheritedAnnotationInterface.class.getDeclaredMethod("something"), descriptor.getDeclaredMethod());
        assertEquals(rawAnnotation, descriptor.getAnnotation());

        Method somethingInSubSubInherited = SubSubInheritedAnnotationInterface.class.getMethod("something");
        descriptor = findAnnotationDescriptorForMethod(somethingInSubSubInherited, Transactional.class);
        assertNotNull(descriptor);
        assertEquals(SubSubInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(InheritedAnnotationInterface.class.getDeclaredMethod("something"), descriptor.getDeclaredMethod());
        assertEquals(rawAnnotation, descriptor.getAnnotation());
    }

    @Test
    public void findAnnotationDescriptorForNonInheritedAnnotationOnClass() throws Exception {
        // Note: @Order is not inherited.
        MetaAnnotationUtils.MethodAnnotationDescriptor<Order> something = findAnnotationDescriptorForMethod(
                NonInheritedAnnotationClass.class.getMethod("something"), Order.class);
        assertEquals(NonInheritedAnnotationClass.class, something.getRootDeclaringClass());
        assertEquals(NonInheritedAnnotationClass.class.getMethod("something"), something.getDeclaredMethod());

        MetaAnnotationUtils.MethodAnnotationDescriptor<Order> somethingOnSub = findAnnotationDescriptorForMethod(
                SubNonInheritedAnnotationClass.class.getMethod("something"), Order.class);
        assertEquals(SubNonInheritedAnnotationClass.class, somethingOnSub.getRootDeclaringClass());
        assertEquals(NonInheritedAnnotationClass.class.getMethod("something"), somethingOnSub.getDeclaredMethod());
    }

    @Test
    public void findAnnotationDescriptorForNonInheritedAnnotationOnInterface() throws Exception {
        // Note: @Order is not inherited.
        Order rawAnnotation = NonInheritedAnnotationInterface.class.getMethod("something").getAnnotation(Order.class);

        MetaAnnotationUtils.MethodAnnotationDescriptor<Order> descriptor;

        descriptor = findAnnotationDescriptorForMethod(NonInheritedAnnotationInterface.class.getMethod("something"), Order.class);
        assertNotNull(descriptor);
        assertEquals(NonInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(NonInheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(NonInheritedAnnotationInterface.class.getDeclaredMethod("something"), descriptor.getDeclaredMethod());
        assertEquals(rawAnnotation, descriptor.getAnnotation());

        descriptor = findAnnotationDescriptorForMethod(SubNonInheritedAnnotationInterface.class.getMethod("something"), Order.class);
        assertNotNull(descriptor);
        assertEquals(SubNonInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(NonInheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(NonInheritedAnnotationInterface.class.getDeclaredMethod("something"), descriptor.getDeclaredMethod());
        assertEquals(rawAnnotation, descriptor.getAnnotation());
    }

    @Test
    public void findAnnotationDescriptorWithMetaComponentAnnotation() throws Exception {
        assertAtComponentOnComposedAnnotation(HasMetaComponentAnnotation.class.getDeclaredMethod("something"),
                "meta1", MetaAnnotationUtilsTests.Meta1.class);
    }

/*    @Test
    public void findAnnotationDescriptorWithLocalAndMetaComponentAnnotation() throws Exception {
        Class<Component> annotationType = Component.class;
        MetaAnnotationUtils.AnnotationDescriptor<Component>
                descriptor = findAnnotationDescriptor(HasLocalAndMetaComponentAnnotation.class,
                annotationType);
        assertEquals(HasLocalAndMetaComponentAnnotation.class, descriptor.getRootDeclaringClass());
        assertEquals(annotationType, descriptor.getAnnotationType());
        assertNull(descriptor.getComposedAnnotation());
        assertNull(descriptor.getComposedAnnotationType());
    }

    @Test
    public void findAnnotationDescriptorForInterfaceWithMetaAnnotation() {
        assertAtComponentOnComposedAnnotation(InterfaceWithMetaAnnotation.class, "meta1", MetaAnnotationUtilsTests.Meta1.class);
    }

    @Test
    public void findAnnotationDescriptorForClassWithMetaAnnotatedInterface() {
        Component rawAnnotation = AnnotationUtils.findAnnotation(ClassWithMetaAnnotatedInterface.class,
                Component.class);

        MetaAnnotationUtils.AnnotationDescriptor<Component> descriptor;

        descriptor = findAnnotationDescriptor(ClassWithMetaAnnotatedInterface.class, Component.class);
        assertNotNull(descriptor);
        assertEquals(ClassWithMetaAnnotatedInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(MetaAnnotationUtilsTests.Meta1.class, descriptor.getDeclaringClass());
        assertEquals(rawAnnotation, descriptor.getAnnotation());
        assertEquals(MetaAnnotationUtilsTests.Meta1.class, descriptor.getComposedAnnotation().annotationType());
    }

    @Test
    public void findAnnotationDescriptorForClassWithLocalMetaAnnotationAndAnnotatedSuperclass() {
        MetaAnnotationUtils.AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(
                MetaAnnotatedAndSuperAnnotatedContextConfigClass.class, ContextConfiguration.class);

        assertNotNull("AnnotationDescriptor should not be null", descriptor);
        assertEquals("rootDeclaringClass", MetaAnnotatedAndSuperAnnotatedContextConfigClass.class, descriptor.getRootDeclaringClass());
        assertEquals("declaringClass", MetaConfig.class, descriptor.getDeclaringClass());
        assertEquals("annotationType", ContextConfiguration.class, descriptor.getAnnotationType());
        assertNotNull("composedAnnotation should not be null", descriptor.getComposedAnnotation());
        assertEquals("composedAnnotationType", MetaConfig.class, descriptor.getComposedAnnotationType());

        assertArrayEquals("configured classes", new Class[] { String.class },
                descriptor.getAnnotationAttributes().getClassArray("classes"));
    }

    @Test
    public void findAnnotationDescriptorForClassWithLocalMetaAnnotationAndMetaAnnotatedInterface() {
        assertAtComponentOnComposedAnnotation(ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, "meta2",
                MetaAnnotationUtilsTests.Meta2.class);
    }

    @Test
    public void findAnnotationDescriptorForSubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface() {
        assertAtComponentOnComposedAnnotation(SubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class,
                ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, "meta2", MetaAnnotationUtilsTests.Meta2.class);
    }

    *//**
     * @since 4.0.3
     *//*
    @Test
    public void findAnnotationDescriptorOnMetaMetaAnnotatedClass() {
        Class<MetaMetaAnnotatedClass> startClass = MetaMetaAnnotatedClass.class;
        assertAtComponentOnComposedAnnotation(startClass, startClass, MetaAnnotationUtilsTests.Meta2.class, "meta2", MetaAnnotationUtilsTests.MetaMeta.class);
    }

    *//**
     * @since 4.0.3
     *//*
    @Test
    public void findAnnotationDescriptorOnMetaMetaMetaAnnotatedClass() {
        Class<MetaMetaMetaAnnotatedClass> startClass = MetaMetaMetaAnnotatedClass.class;
        assertAtComponentOnComposedAnnotation(startClass, startClass, MetaAnnotationUtilsTests.Meta2.class, "meta2", MetaAnnotationUtilsTests.MetaMetaMeta.class);
    }

    *//**
     * @since 4.0.3
     *//*
    @Test
    public void findAnnotationDescriptorOnAnnotatedClassWithMissingTargetMetaAnnotation() {
        // InheritedAnnotationClass is NOT annotated or meta-annotated with @Component
        MetaAnnotationUtils.AnnotationDescriptor<Component>
                descriptor = findAnnotationDescriptor(InheritedAnnotationClass.class, Component.class);
        assertNull("Should not find @Component on InheritedAnnotationClass", descriptor);
    }

    *//**
     * @since 4.0.3
     *//*
    @Test
    public void findAnnotationDescriptorOnMetaCycleAnnotatedClassWithMissingTargetMetaAnnotation() {
        MetaAnnotationUtils.AnnotationDescriptor<Component>
                descriptor = findAnnotationDescriptor(MetaCycleAnnotatedClass.class,
                Component.class);
        assertNull("Should not find @Component on MetaCycleAnnotatedClass", descriptor);
    }

    // -------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithNoAnnotationPresent() throws Exception {
        assertNull(findAnnotationDescriptorForTypes(NonAnnotatedInterface.class, Transactional.class, Component.class));
        assertNull(findAnnotationDescriptorForTypes(NonAnnotatedClass.class, Transactional.class, Order.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithInheritedAnnotationOnClass() throws Exception {
        // Note: @Transactional is inherited
        assertEquals(
                InheritedAnnotationClass.class,
                findAnnotationDescriptorForTypes(InheritedAnnotationClass.class, Transactional.class).getRootDeclaringClass());
        assertEquals(
                InheritedAnnotationClass.class,
                findAnnotationDescriptorForTypes(SubInheritedAnnotationClass.class, Transactional.class).getRootDeclaringClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithInheritedAnnotationOnInterface() throws Exception {
        // Note: @Transactional is inherited
        Transactional rawAnnotation = InheritedAnnotationInterface.class.getAnnotation(Transactional.class);

        MetaAnnotationUtils.UntypedAnnotationDescriptor descriptor;

        descriptor = findAnnotationDescriptorForTypes(InheritedAnnotationInterface.class, Transactional.class);
        assertNotNull(descriptor);
        assertEquals(InheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(rawAnnotation, descriptor.getAnnotation());

        descriptor = findAnnotationDescriptorForTypes(SubInheritedAnnotationInterface.class, Transactional.class);
        assertNotNull(descriptor);
        assertEquals(SubInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(rawAnnotation, descriptor.getAnnotation());

        descriptor = findAnnotationDescriptorForTypes(SubSubInheritedAnnotationInterface.class, Transactional.class);
        assertNotNull(descriptor);
        assertEquals(SubSubInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(rawAnnotation, descriptor.getAnnotation());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesForNonInheritedAnnotationOnClass() throws Exception {
        // Note: @Order is not inherited.
        assertEquals(NonInheritedAnnotationClass.class,
                findAnnotationDescriptorForTypes(NonInheritedAnnotationClass.class, Order.class).getRootDeclaringClass());
        assertEquals(NonInheritedAnnotationClass.class,
                findAnnotationDescriptorForTypes(SubNonInheritedAnnotationClass.class, Order.class).getRootDeclaringClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesForNonInheritedAnnotationOnInterface() throws Exception {
        // Note: @Order is not inherited.
        Order rawAnnotation = NonInheritedAnnotationInterface.class.getAnnotation(Order.class);

        MetaAnnotationUtils.UntypedAnnotationDescriptor descriptor;

        descriptor = findAnnotationDescriptorForTypes(NonInheritedAnnotationInterface.class, Order.class);
        assertNotNull(descriptor);
        assertEquals(NonInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(NonInheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(rawAnnotation, descriptor.getAnnotation());

        descriptor = findAnnotationDescriptorForTypes(SubNonInheritedAnnotationInterface.class, Order.class);
        assertNotNull(descriptor);
        assertEquals(SubNonInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(NonInheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        assertEquals(rawAnnotation, descriptor.getAnnotation());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithLocalAndMetaComponentAnnotation() throws Exception {
        Class<Component> annotationType = Component.class;
        MetaAnnotationUtils.UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(
                HasLocalAndMetaComponentAnnotation.class, Transactional.class, annotationType, Order.class);
        assertEquals(HasLocalAndMetaComponentAnnotation.class, descriptor.getRootDeclaringClass());
        assertEquals(annotationType, descriptor.getAnnotationType());
        assertNull(descriptor.getComposedAnnotation());
        assertNull(descriptor.getComposedAnnotationType());
    }

    @Test
    public void findAnnotationDescriptorForTypesWithMetaComponentAnnotation() throws Exception {
        Class<HasMetaComponentAnnotation> startClass = HasMetaComponentAnnotation.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, "meta1", MetaAnnotationUtilsTests.Meta1.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithMetaAnnotationWithDefaultAttributes() throws Exception {
        Class<?> startClass = MetaConfigWithDefaultAttributesTestCase.class;
        Class<ContextConfiguration> annotationType = ContextConfiguration.class;

        MetaAnnotationUtils.UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(startClass, Service.class,
                ContextConfiguration.class, Order.class, Transactional.class);

        assertNotNull(descriptor);
        assertEquals(startClass, descriptor.getRootDeclaringClass());
        assertEquals(annotationType, descriptor.getAnnotationType());
        assertArrayEquals(new Class[] {}, ((ContextConfiguration) descriptor.getAnnotation()).value());
        assertArrayEquals(new Class[] { MetaAnnotationUtilsTests.MetaConfig.DevConfig.class, MetaAnnotationUtilsTests.MetaConfig.ProductionConfig.class },
                descriptor.getAnnotationAttributes().getClassArray("classes"));
        assertNotNull(descriptor.getComposedAnnotation());
        assertEquals(MetaAnnotationUtilsTests.MetaConfig.class, descriptor.getComposedAnnotationType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithMetaAnnotationWithOverriddenAttributes() throws Exception {
        Class<?> startClass = MetaConfigWithOverriddenAttributesTestCase.class;
        Class<ContextConfiguration> annotationType = ContextConfiguration.class;

        MetaAnnotationUtils.UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(startClass, Service.class,
                ContextConfiguration.class, Order.class, Transactional.class);

        assertNotNull(descriptor);
        assertEquals(startClass, descriptor.getRootDeclaringClass());
        assertEquals(annotationType, descriptor.getAnnotationType());
        assertArrayEquals(new Class[] {}, ((ContextConfiguration) descriptor.getAnnotation()).value());
        assertArrayEquals(new Class[] { MetaAnnotationUtilsTests.class },
                descriptor.getAnnotationAttributes().getClassArray("classes"));
        assertNotNull(descriptor.getComposedAnnotation());
        assertEquals(MetaAnnotationUtilsTests.MetaConfig.class, descriptor.getComposedAnnotationType());
    }

    @Test
    public void findAnnotationDescriptorForTypesForInterfaceWithMetaAnnotation() {
        Class<MetaAnnotationUtilsTests.InterfaceWithMetaAnnotation> startClass = MetaAnnotationUtilsTests.InterfaceWithMetaAnnotation.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, "meta1", MetaAnnotationUtilsTests.Meta1.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesForClassWithMetaAnnotatedInterface() {
        Component rawAnnotation = AnnotationUtils.findAnnotation(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class,
                Component.class);

        MetaAnnotationUtils.UntypedAnnotationDescriptor descriptor;

        descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class, Service.class,
                Component.class, Order.class, Transactional.class);
        assertNotNull(descriptor);
        assertEquals(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class, descriptor.getRootDeclaringClass());
        assertEquals(MetaAnnotationUtilsTests.Meta1.class, descriptor.getDeclaringClass());
        assertEquals(rawAnnotation, descriptor.getAnnotation());
        assertEquals(MetaAnnotationUtilsTests.Meta1.class, descriptor.getComposedAnnotation().annotationType());
    }

    @Test
    public void findAnnotationDescriptorForTypesForClassWithLocalMetaAnnotationAndMetaAnnotatedInterface() {
        Class<ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface> startClass = ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, "meta2", MetaAnnotationUtilsTests.Meta2.class);
    }

    @Test
    public void findAnnotationDescriptorForTypesForSubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface() {
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(
                SubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class,
                ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, "meta2", MetaAnnotationUtilsTests.Meta2.class);
    }

    *//**
     * @since 4.0.3
     *//*
    @Test
    public void findAnnotationDescriptorForTypesOnMetaMetaAnnotatedClass() {
        Class<MetaMetaAnnotatedClass> startClass = MetaMetaAnnotatedClass.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, startClass, MetaAnnotationUtilsTests.Meta2.class, "meta2",
                MetaAnnotationUtilsTests.MetaMeta.class);
    }

    *//**
     * @since 4.0.3
     *//*
    @Test
    public void findAnnotationDescriptorForTypesOnMetaMetaMetaAnnotatedClass() {
        Class<MetaMetaMetaAnnotatedClass> startClass = MetaMetaMetaAnnotatedClass.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, startClass, MetaAnnotationUtilsTests.Meta2.class, "meta2",
                MetaAnnotationUtilsTests.MetaMetaMeta.class);
    }

    *//**
     * @since 4.0.3
     *//*
    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesOnAnnotatedClassWithMissingTargetMetaAnnotation() {
        // InheritedAnnotationClass is NOT annotated or meta-annotated with @Component,
        // @Service, or @Order, but it is annotated with @Transactional.
        MetaAnnotationUtils.UntypedAnnotationDescriptor
                descriptor = findAnnotationDescriptorForTypes(InheritedAnnotationClass.class,
                Service.class, Component.class, Order.class);
        assertNull("Should not find @Component on InheritedAnnotationClass", descriptor);
    }

    *//**
     * @since 4.0.3
     *//*
    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesOnMetaCycleAnnotatedClassWithMissingTargetMetaAnnotation() {
        MetaAnnotationUtils.UntypedAnnotationDescriptor
                descriptor = findAnnotationDescriptorForTypes(MetaCycleAnnotatedClass.class,
                Service.class, Component.class, Order.class);
        assertNull("Should not find @Component on MetaCycleAnnotatedClass", descriptor);
    }*/

    // -------------------------------------------------------------------------

    static class HasMetaComponentAnnotation {
        @MetaAnnotationUtilsTests.Meta1
        public void something(){}
    }

    static class HasLocalAndMetaComponentAnnotation {
        @MetaAnnotationUtilsTests.Meta1
        @Transactional
        @MetaAnnotationUtilsTests.Meta2
        public void something(){}
    }

    interface InterfaceWithMetaAnnotation {
        @MetaAnnotationUtilsTests.Meta1
        void something();
    }

    static class ClassWithMetaAnnotatedInterface implements InterfaceWithMetaAnnotation {
        @Override
        public void something(){}
    }

    static class ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface implements InterfaceWithMetaAnnotation {
        @Override
        @MetaAnnotationUtilsTests.Meta2
        public void something(){}
    }

    static class SubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface extends ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface {
        @Override
        public void something(){}
    }

    static class MetaMetaAnnotatedClass {
        @MetaAnnotationUtilsTests.MetaMeta
        public void something(){}
    }

    static class MetaMetaMetaAnnotatedClass {
        @MetaAnnotationUtilsTests.MetaMetaMeta
        public void something(){}
    }

    static class MetaCycleAnnotatedClass {
        @MetaAnnotationUtilsTests.MetaCycle3
        public void something(){}
    }

    public class MetaConfigWithDefaultAttributesTestCase {
        @MetaAnnotationUtilsTests.MetaConfig
        public void something(){}
    }

    public class MetaConfigWithOverriddenAttributesTestCase {
        @MetaAnnotationUtilsTests.MetaConfig(classes = MetaAnnotationUtilsTests.class)
        public void something(){}
    }

    // -------------------------------------------------------------------------

    interface InheritedAnnotationInterface {
        @Transactional
        void something();
    }

    interface SubInheritedAnnotationInterface extends InheritedAnnotationInterface {
        @Override
        void something();
    }

    interface SubSubInheritedAnnotationInterface extends SubInheritedAnnotationInterface {
        @Override
        void something();
    }

    interface NonInheritedAnnotationInterface {
        @Order
        void something();
    }

    interface SubNonInheritedAnnotationInterface extends NonInheritedAnnotationInterface {
        @Override
        void something();
    }

    static class NonAnnotatedClass {
        public void something(){}
    }

    interface NonAnnotatedInterface {
        void something();
    }

    static class InheritedAnnotationClass {
        @Transactional
        public void something(){}
    }

    static class SubInheritedAnnotationClass extends InheritedAnnotationClass {
        @Override
        public void something(){}
    }

    static class NonInheritedAnnotationClass {
        @Order
        public void something(){}
    }

    static class SubNonInheritedAnnotationClass extends NonInheritedAnnotationClass {
        @Override
        public void something(){}
    }

    static class AnnotatedContextConfigClass {
        @ContextConfiguration(classes = Number.class)
        public void something(){}
    }

    static class MetaAnnotatedAndSuperAnnotatedContextConfigClass extends AnnotatedContextConfigClass {
        @MetaAnnotationUtilsTests.MetaConfig(classes = String.class)
        public void something(){}
    }

}
