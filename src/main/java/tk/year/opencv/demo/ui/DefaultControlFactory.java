/*
 * OpenCV Demo
 * Copyright (C) 2014  Alexey Hanin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package tk.year.opencv.demo.ui;

import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import tk.year.opencv.demo.commons.FieldAdapter;
import tk.year.opencv.demo.commons.ImageFilter;

import tk.year.opencv.demo.commons.PropertyAdapter;
import tk.year.opencv.demo.ui.annotations.PropertyControlFactory;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Build property control based on the {@link ImageFilter} field annotation.
 */
public class DefaultControlFactory implements ImageFilterControlFactory<ImageFilter> {

    private static final Predicate<?super Annotation> FACTORY_ANNOTATION_PREDICATE = new Predicate<Annotation>() {
        @Override
        public boolean apply(final Annotation annotation) {
            return annotation.annotationType() == PropertyControlFactory.class ||
                    annotation.annotationType().isAnnotationPresent(PropertyControlFactory.class);
        }
    };

    private static final Predicate<Field> CONTROL_FIELD_PREDICATE = new Predicate<Field>() {
        @Override
        public boolean apply(final Field field) {
            for (final Annotation anno : field.getAnnotations()) {
                if (FACTORY_ANNOTATION_PREDICATE.apply(anno)) {
                    return true;
                }
            }

            return false;
        }
    };

    @Override
    public Component createControl(final ImageFilter filter,
                                   final UpdateActionInvoker updateActionInvoker) {

        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        final Collection<Field> propertyFields = Collections2.filter(
            Lists.newArrayList(filter.getClass().getDeclaredFields()),
            CONTROL_FIELD_PREDICATE);

        final GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        for (final Field field : propertyFields) {

            final Collection<Annotation> factoryAnnotations = Collections2.filter(
                Arrays.asList(field.getAnnotations()),
                FACTORY_ANNOTATION_PREDICATE);

            checkState(factoryAnnotations.size() < 2,
                       "More than one control factory annotations defined for %s.%s: %s",
                       field.getClass(),
                       field.getName(),
                       factoryAnnotations);

            final Annotation factoryAnnotation = factoryAnnotations.iterator().next();

            c.weightx = 0;
            c.gridx = 0;
            panel.add(new JLabel(field.getName()), c);

            c.gridx = 1;
            c.weightx = 1;

            final Component fieldControl = createFieldControl(factoryAnnotation, field, filter, updateActionInvoker);

            panel.add(fieldControl, c);

            c.gridy ++;
        }

        return panel;
    }

    private Component createFieldControl(final Annotation factoryAnnotation, final Field field,
                                         final ImageFilter filter, final UpdateActionInvoker updateActionInvoker) {

        final PropertyControlFactory controlFactoryAnnotation = factoryAnnotation.annotationType() == PropertyControlFactory.class
                                                                ? (PropertyControlFactory) factoryAnnotation
                                                                : factoryAnnotation.annotationType().getAnnotation(PropertyControlFactory.class);

        final Class<?extends tk.year.opencv.demo.ui.PropertyControlFactory> factoryClass = controlFactoryAnnotation
                                                                                   .value();
        final tk.year.opencv.demo.ui.PropertyControlFactory controlFactory;
        try {
            controlFactory = factoryClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to create control factory " + factoryClass +" for field " + field, e);
        }

        final SourceAwareUpdateActionInvoker sourceAwareUpdateActionInvoker = new SourceAwareUpdateActionInvoker(updateActionInvoker);
        final FieldAdapter fieldAdapter = new FieldAdapter(field, filter);
        @SuppressWarnings("unchecked")
        final UpdatingPropertyAdapterWrapper propertyAdapter = new UpdatingPropertyAdapterWrapper(fieldAdapter, sourceAwareUpdateActionInvoker);
        @SuppressWarnings("unchecked")
        final Component control = controlFactory.getControl(propertyAdapter, factoryAnnotation, filter);
        sourceAwareUpdateActionInvoker.setSource(control);

        return control;
    }

    private static class UpdatingPropertyAdapterWrapper<T> implements PropertyAdapter<T> {

        private PropertyAdapter<T> propertyAdapter;
        private final UpdateActionInvoker updateActionInvoker;

        private UpdatingPropertyAdapterWrapper(final PropertyAdapter<T> propertyAdapter,
                                               final UpdateActionInvoker updateActionInvoker) {
            this.propertyAdapter = propertyAdapter;
            this.updateActionInvoker = updateActionInvoker;
        }

        @Override
        public T get() {
            return propertyAdapter.get();
        }

        @Override
        public void set(final T value) {
            propertyAdapter.set(value);
            updateActionInvoker.invokeUpdateAction(this);
        }
    }

    private static class SourceAwareUpdateActionInvoker implements UpdateActionInvoker {

        private final UpdateActionInvoker updateActionInvoker;
        private Object source;

        public SourceAwareUpdateActionInvoker(final UpdateActionInvoker updateActionInvoker) {
            this.updateActionInvoker = updateActionInvoker;
        }

        private void setSource(final Object source) {
            this.source = source;
        }

        @Override
        public void invokeUpdateAction(final Object source) {
            this.updateActionInvoker.invokeUpdateAction(source);
        }
    }
}
