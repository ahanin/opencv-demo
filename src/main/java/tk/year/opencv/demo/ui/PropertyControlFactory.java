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

import tk.year.opencv.demo.commons.PropertyAdapter;

import java.awt.Component;

import java.lang.annotation.Annotation;

/**
 * Used for creation of controls, which main purpose is to provide user interface element to manipulate property value.
 * Produced {@link Component}s must set the property value using provided {@link PropertyAdapter} whenever it is changed.
 *
 * @param <T>
 * @see tk.year.opencv.demo.ui.annotations.PropertyControlFactory
 * @see javax.swing.JComponent#listenerList
 */
public interface PropertyControlFactory<T, P extends Annotation> {

    /**
     * Creates a UI element controlling the property. Whenever the value is manipulated,
     * the property value must be changed using the provided {@code propertyAdapter}. The initial
     * state of control can be initialized with the value retrieved with {@link
     * PropertyAdapter#get()}
     *
     * @param propertyAdapter an adapter to the controlled property value
     * @param parameter parameter as factory annotation
     * @param context
     *
     * @return newly created UI component
     */
    public Component getControl(final PropertyAdapter<T> propertyAdapter, final P parameter,
                                final Object context);
}
