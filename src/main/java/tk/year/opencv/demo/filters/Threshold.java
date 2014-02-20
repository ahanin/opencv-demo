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

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import tk.year.opencv.demo.commons.ImageFilter;
import tk.year.opencv.demo.ui.annotations.FilterPanelFactory;
import tk.year.opencv.demo.ui.controls.Choice;
import tk.year.opencv.demo.ui.controls.Slider;

import java.util.HashMap;
import java.util.Map;

@FilterPanelFactory
public class Threshold implements ImageFilter {

    private static final String THRESH_BINARY = "THRESH_BINARY";
    private static final String THRESH_BINARY_INV = "THRESH_BINARY_INV";
    private static final String THRESH_TRUNC = "THRESH_TRUNC";
    @Slider(min = 0, max = 100)
    private int value;
    @Slider(min = 0, max = 100)
    private int maxVal;
    @Choice(values = "typeMap")
    private Integer type;

    private static final Map<Integer, String> typeMap = new HashMap<>();

    static {
        typeMap.put(Imgproc.THRESH_BINARY, THRESH_BINARY);
        typeMap.put(Imgproc.THRESH_BINARY_INV, THRESH_BINARY_INV);
        typeMap.put(Imgproc.THRESH_TRUNC, THRESH_TRUNC);
    }

    public Threshold() {}

    @Override
    public Mat filter(final Mat src) {

        final Mat dst = new Mat();

        Imgproc.threshold(src,
                          dst,
                          value,
                          CvType.ELEM_SIZE(src.type()) * (double) maxVal / 100 * 100,
                          type);
        return dst;
    }

    @Override
    public boolean isApplicable() {
        return type != null;
    }
}
