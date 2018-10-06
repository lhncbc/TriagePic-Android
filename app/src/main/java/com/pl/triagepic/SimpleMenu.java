/*
 * Informational Notice:
 *
 * This software, the ”TBD,” was developed under contract funded by the National Library of Medicine, which is part of the National Institutes of Health, an agency of the Department of Health and Human Services, United States Government.
 *
 * The license of this software is an open-source BSD-like license.  It allows use in both commercial and non-commercial products.
 *
 * The license does not supersede any applicable United States law.
 *
 * The license does not indemnify you from any claims brought by third parties whose proprietary rights may be infringed by your usage of this software.
 *
 * Government usage rights for this software are established by Federal law, which includes, but may not be limited to, Federal Acquisition Regulation (FAR) 48 C.F.R. Part 52.227-14, Rights in Data—General.
 * The license for this software is intended to be expansive, rather than restrictive, in encouraging the use of this software in both commercial and non-commercial products.
 *
 * LICENSE:
 *
 * Government Usage Rights Notice:  The U.S. Government retains unlimited, royalty-free usage rights to this software, but not ownership, as provided by Federal law.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above Government Usage Rights Notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above Government Usage Rights Notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *  The names, trademarks, and service marks of the National Library of Medicine, the National Institutes of Health, and the names of any of the software developers shall not be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITEDTO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.pl.triagepic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;

public class SimpleMenu implements Menu {

    private Context mContext;
    private Resources mResources;

    private ArrayList<SimpleMenuItem> mItems;

    public SimpleMenu(Context context) {
        mContext = context;
        mResources = context.getResources();
        mItems = new ArrayList<SimpleMenuItem>();
    }

    public Context getContext() {
        return mContext;
    }

    public Resources getResources() {
        return mResources;
    }

    public MenuItem add(CharSequence title) {
        return addInternal(0, 0, title);
    }

    public MenuItem add(int titleRes) {
        return addInternal(0, 0, mResources.getString(titleRes));
    }

    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        return addInternal(itemId, order, title);
    }

    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return addInternal(itemId, order, mResources.getString(titleRes));
    }

    /**
     * Adds an item to the menu.  The other add methods funnel to this.
     */
    private MenuItem addInternal(int itemId, int order, CharSequence title) {
        final SimpleMenuItem item = new SimpleMenuItem(this, itemId, order, title);
        mItems.add(findInsertIndex(mItems, order), item);
        return item;
    }

    private static int findInsertIndex(ArrayList<? extends MenuItem> items, int order) {
        for (int i = items.size() - 1; i >= 0; i--) {
            MenuItem item = items.get(i);
            if (item.getOrder() <= order) {
                return i + 1;
            }
        }

        return 0;
    }

    public int findItemIndex(int id) {
        final int size = size();

        for (int i = 0; i < size; i++) {
            SimpleMenuItem item = mItems.get(i);
            if (item.getItemId() == id) {
                return i;
            }
        }

        return -1;
    }

    public void removeItem(int itemId) {
        removeItemAtInt(findItemIndex(itemId));
    }

    private void removeItemAtInt(int index) {
        if ((index < 0) || (index >= mItems.size())) {
            return;
        }
        mItems.remove(index);
    }

    public void clear() {
        mItems.clear();
    }

    public MenuItem findItem(int id) {
        final int size = size();
        for (int i = 0; i < size; i++) {
            SimpleMenuItem item = mItems.get(i);
            if (item.getItemId() == id) {
                return item;
            }
        }

        return null;
    }

    public int size() {
        return mItems.size();
    }

    public MenuItem getItem(int index) {
        return mItems.get(index);
    }

    // Unsupported operations.

    public SubMenu addSubMenu(CharSequence charSequence) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public SubMenu addSubMenu(int titleRes) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public int addIntentOptions(int i, int i1, int i2, ComponentName componentName,
            Intent[] intents, Intent intent, int i3, MenuItem[] menuItems) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void removeGroup(int i) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void setGroupCheckable(int i, boolean b, boolean b1) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void setGroupVisible(int i, boolean b) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void setGroupEnabled(int i, boolean b) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public boolean hasVisibleItems() {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void close() {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public boolean performShortcut(int i, KeyEvent keyEvent, int i1) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public boolean isShortcutKey(int i, KeyEvent keyEvent) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public boolean performIdentifierAction(int i, int i1) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }

    public void setQwertyMode(boolean b) {
        throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
    }
}
