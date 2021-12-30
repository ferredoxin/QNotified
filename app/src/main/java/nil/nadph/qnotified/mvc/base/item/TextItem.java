/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.mvc.base.item;

import androidx.annotation.NonNull;

public interface TextItem extends BaseItem {

    @NonNull
    TextType getTextType();

    void setTextType(@NonNull TextType textType);

    @NonNull
    CharSequence getText();

    void setText(@NonNull CharSequence text);

    enum TextType {
        /**
         * Huge title
         */
        TEXT_TYPE_TITLE,
        /**
         * Medium text
         */
        TEXT_TYPE_LABEL,
        /**
         * Small text to show help or function state information
         */
        TEXT_TYPE_DESCRIPTION,
    }
}
