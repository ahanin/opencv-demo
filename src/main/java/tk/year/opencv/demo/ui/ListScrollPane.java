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

import sun.awt.VerticalBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListScrollPane<E> extends JPanel {

    private final JPanel content;
    private final ListAdapter<E> listAdapter;

    private class Tuple {

        private E element;
        private Component component;

        private Tuple(final E element, final Component component) {
            this.element = element;
            this.component = component;
        }

        private E getElement() {
            return element;
        }

        private Component getComponent() {
            return component;
        }
    }

    private List<Tuple> cache;

    public ListScrollPane(final ListAdapter<E> listAdapter) {
        this.listAdapter = listAdapter;
        this.cache = new ArrayList<>(listAdapter.getSize());

        content = new JPanel();
        content.setLayout(new VerticalBagLayout());

        setLayout(new GridBagLayout());

        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        final JScrollPane scrollPane = new JScrollPane(content);
        add(scrollPane, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1;
        c.weighty = 1;

        update();
    }

    private Component createElementComponent(final E element) {
        return listAdapter.getComponent(element);
    }

    public void update() {
        synchronized (this) {
            content.removeAll();
            final List<Tuple> cache = new ArrayList<>(listAdapter.getSize());
            for (int i = 0; i < listAdapter.getSize(); i++) {
                final Tuple tuple = ensureElementTuple(listAdapter.getElementAt(i));
                content.add(tuple.getComponent());
                cache.add(tuple);
            }
            this.cache = cache;
        }
    }

    private Tuple ensureElementTuple(final E element) {
        final Iterator<Tuple> it = cache.iterator();
        Tuple tuple = null;
        while (tuple == null && it.hasNext()) {
            final Tuple test = it.next();
            if (test.getElement() == element) {
                tuple = test;
            }
        }

        if (tuple == null) {
            tuple = new Tuple(element, createElementComponent(element));
        }

        return tuple;
    }

}
