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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImageFilters implements Iterable<ImageFilters.Entry> {

    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public interface Entry {

        boolean isEnabled();

        void setEnabled(final boolean isEnabled);

        ImageFilter getFilter();

    }

    private List<Entry> entries = new ArrayList<>();

    public void moveUp(final Entry entry) {
        final int pos = entries.indexOf(entry);
        if (pos > 0) {
            swapListItem(entries, pos, pos - 1);
        }
    }

    public void moveDown(final Entry entry) {
        final int pos = entries.indexOf(entry);
        if (pos > -1 && pos < entries.size() - 1) {
            swapListItem(entries, pos, pos + 1);
        }
    }

    public void remove(final Entry entry) {
        entries.remove(entry);
    }

    private void swapListItem(final List<Entry> entries, final int posA, final int posB) {
        final Entry a = entries.get(posA);
        entries.set(posA, entries.get(posB));
        entries.set(posB, a);
    }

    public int getCount() {
        return entries.size();
    }

    public Entry getEntryAt(final int pos) {
        return entries.get(pos);
    }

    public ImageFilter getFilterAt(final int pos) {
        return entries.get(pos).getFilter();
    }

    public void add(final ImageFilter filter) {
        final EntryImpl entry = new EntryImpl();
        entry.setFilter(filter);
        entry.setEnabled(true);
        this.entries.add(entry);
    }

    public void clear() {
        this.entries.clear();
    }

    private class EntryImpl implements Entry {

        private boolean enabled;
        private ImageFilter filter;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        public ImageFilter getFilter() {
            return filter;
        }

        public void setFilter(final ImageFilter filter) {
            this.filter = filter;
        }
    }
}
