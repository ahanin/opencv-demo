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

import org.opencv.core.Mat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilteringImageProvider implements Provider<Mat> {

    private Provider<Mat> imgProvider;
    private Provider<ImageFilters> imageFiltersProvider;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public FilteringImageProvider(final Provider<Mat> imgProvider,
                                  final Provider<ImageFilters> imageFiltersProvider) {
        this.imgProvider = imgProvider;
        this.imageFiltersProvider = imageFiltersProvider;
    }

    @Override
    public Mat get() {

        final Mat img = imgProvider.get();
        Mat dst = new Mat();
        img.copyTo(dst);
        for (final ImageFilters.Entry entry : imageFiltersProvider.get()) {
            if (entry.isEnabled() && entry.getFilter().isApplicable()) {
                try {
                    dst = entry.getFilter().filter(dst);
                } catch (final Exception ex) {
                    logger.error("Unable to apply filter: {}", entry.getFilter(), ex);
                    return dst;
                }
            }
        }

        return dst;
    }

    @Override
    public boolean present() {
        return imgProvider.present();
    }
}
