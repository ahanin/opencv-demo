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
package tk.year.opencv.demo.ui.controls;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import tk.year.opencv.demo.commons.PropertyAdapter;
import tk.year.opencv.demo.commons.Tuple;
import tk.year.opencv.demo.ui.PropertyControlFactory;
import tk.year.opencv.demo.ui.annotations.Element;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ChoiceControlFactory<V> implements PropertyControlFactory<V, Choice> {
    @Override
    public Component getControl(final PropertyAdapter<V> propertyAdapter,
                                final Choice choice, final Object context) {

        final Tuple<V, String>[] elements;

        if (!choice.values().isEmpty()) {
            final String fieldName = choice.values();
            final Map<V, String> map = getValues(context, fieldName);
            final Collection<Tuple<V, String>> transform = Collections2.transform(map.entrySet(), new EntryTupleFunction<V>());
            elements = transform.toArray(new Tuple[transform.size()]);
        } else {
            final Collection tmp = Collections2.transform(Arrays.asList(choice.value()), new ElementTupleFunction());
            final Collection<Tuple<V, String>> transform = (Collection<Tuple<V, String>>) tmp;
            elements = transform.toArray(new Tuple[transform.size()]);
        }

        final JComboBox<V> comboBox = new JComboBox<>(new ChoiceComboBoxModel<>(elements));
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                propertyAdapter.set((V) comboBox.getSelectedItem());
            }
        });
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
                final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index > -1 && index < elements.length && comp instanceof JLabel) {
                    final JLabel label = (JLabel) comp;
                    label.setText(elements[index].getB());
                }
                return comp;
            }
        });
        return comboBox;
    }

    private Map<V, String> getValues(final Object context, final String fieldName) {
        final Field field;
        try {
            field = context.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field " + fieldName + " not found.", e);
        }

        Preconditions.checkState(field.getType().equals(Map.class), "Field %s.%s must be a Map", context.getClass(), field.getName());

        final Map<V, String> map;
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            map = (Map<V, String>) field.get(context);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("Failed to acquire value of " + field.toGenericString());
        }
        return map;
    }

    public static class ChoiceComboBoxModel<V> implements ComboBoxModel<V> {

        private final Tuple<V, String>[] elements;
        private V selected;

        private EventListenerList eventListenerList = new EventListenerList();

        public ChoiceComboBoxModel(final Tuple<V, String>[] elements) {
            this.elements = elements;
        }

        @Override
        public void setSelectedItem(final Object anItem) {
            this.selected = (V) anItem;
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public int getSize() {
            return elements.length;
        }

        @Override
        public V getElementAt(final int index) {
            return elements[index].getA();
        }

        @Override
        public void addListDataListener(final ListDataListener l) {
            eventListenerList.add(ListDataListener.class, l);
        }

        @Override
        public void removeListDataListener(final ListDataListener l) {
            eventListenerList.remove(ListDataListener.class, l);
        }
    }

    private static class ElementTupleFunction implements Function<Element, Tuple<String, String>> {
        @Override
        public Tuple<String, String> apply(final Element element) {
            return new Tuple<>(element.value(), element.displayName());
        }
    }

    private class EntryTupleFunction<V> implements Function<Map.Entry<V, String>, Tuple<V, String>> {
        @Override
        public Tuple<V, String> apply(final Map.Entry<V, String> entry) {
            return new Tuple<>(entry.getKey(), entry.getValue());
        }
    }
}
