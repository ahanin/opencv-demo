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

import tk.year.opencv.demo.commons.ImageFilter;
import tk.year.opencv.demo.ui.annotations.FilterPanelFactory;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class FilterControlFactoryBundle {

    private final UpdateActionInvoker updateActionInvoker;

    private Map<Class<? extends ImageFilter>, ImageFilterControlFactory> index = new HashMap<>();

    public FilterControlFactoryBundle(final UpdateActionInvoker updateActionInvoker) {
        this.updateActionInvoker = updateActionInvoker;
    }

    public Component createFilterControl(final ImageFilter filter) {
        final ImageFilterControlFactory controlFactory;
        if (index.containsKey(filter.getClass())) {
            controlFactory = index.get(filter.getClass());
        } else {
            final FilterPanelFactory anno = filter.getClass().getAnnotation(FilterPanelFactory.class);
            if (anno == null) {
                throw new IllegalStateException("Control factory is not defined for filter: " + filter.getClass() +
                        ". Did you annotate it with @FilterPanelFactory?");
            }

            final Class<? extends ImageFilterControlFactory> factoryClass = anno.value();
            try {
                controlFactory = factoryClass.newInstance();
                index.put(filter.getClass(), controlFactory);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Failed to instantiate control factory for " + factoryClass, e);
            }
        }

        if (controlFactory == null) {
            throw new IllegalStateException("No control factory found for filter: " + filter.getClass());
        }

        return controlFactory.createControl(filter, updateActionInvoker);
    }

}
