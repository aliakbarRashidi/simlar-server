/*
 * Copyright (C) 2015 The Simlar Authors.
 *
 * This file is part of Simlar. (https://www.simlar.org)
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
 *
 */

package org.simlar.simlarserver.utils;

public final class SimlarId {
    private final String simlarId;

    private SimlarId(final String simlarId) {
        this.simlarId = simlarId;
    }

    public static SimlarId create(final String simlarId) {
        if (!check(simlarId)) {
            return null;
        }

        return new SimlarId(simlarId);
    }

    public String get() {
        return simlarId;
    }

    public static boolean check(final String str) {
        return str != null && !str.isEmpty() && str.matches("\\*\\d+\\*");
    }
}
