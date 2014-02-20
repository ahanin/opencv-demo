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
package tk.year.opencv.demo.filters;

import org.opencv.core.Mat;

import tk.year.opencv.demo.commons.ImageFilter;
import tk.year.opencv.demo.ui.annotations.FilterPanelFactory;
import tk.year.opencv.demo.ui.controls.Slider;

@FilterPanelFactory
public class Contrast implements ImageFilter {

    @Slider(min = 0, max = 100)
    private int value = 10;

    @Override
    public Mat filter(final Mat src) {

        final Mat dst = new Mat(src.rows(), src.cols(), src.type());
        src.convertTo(dst, -1, 10d * value / 100, 0);
        return dst;
    }

    @Override
    public boolean isApplicable() {
        return true;
    }
}
