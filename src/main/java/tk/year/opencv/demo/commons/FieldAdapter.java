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
package tk.year.opencv.demo.commons;

import java.lang.reflect.Field;

public class FieldAdapter<T> implements PropertyAdapter<T> {

    private final Field field;
    private final Object object;

    public FieldAdapter(final Field field, final Object object) {
        this.field = field;
        this.object = object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            return (T) field.get(object);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException("Failed to read field value: " + field, e);
        }
    }

    @Override
    public void set(final T value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            field.set(object, value);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException("Failed to write field value: " + field, e);
        }
    }
}
