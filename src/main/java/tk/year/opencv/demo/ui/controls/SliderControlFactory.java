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

import tk.year.opencv.demo.commons.PropertyAdapter;
import tk.year.opencv.demo.ui.PropertyControlFactory;

import java.awt.Component;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderControlFactory implements PropertyControlFactory<Integer, Slider> {
    @Override
    public Component getControl(final PropertyAdapter<Integer> propertyAdapter,
                                final Slider parameter, final Object context) {

        final JSlider slider = new JSlider(parameter.min(), parameter.max(), propertyAdapter.get());
        slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent e) {
                    if (!slider.getValueIsAdjusting()) {
                        propertyAdapter.set(slider.getValue());
                    }
                }
            });
        return slider;
    }
}
