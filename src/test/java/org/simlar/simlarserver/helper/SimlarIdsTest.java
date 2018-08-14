/*
 * Copyright (C) 2016 The Simlar Authors.
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

package org.simlar.simlarserver.helper;

import org.junit.Test;
import org.simlar.simlarserver.utils.SimlarId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.simlar.simlarserver.helper.SimlarIds.createContacts;

public final class SimlarIdsTest {
    private static void createContactsTest(final List<String> expected, final int amount) {
        assertEquals(expected, createContacts(amount).stream().map(SimlarId::get).collect(Collectors.toList()));
    }

    @Test
    public void testCreateContacts() {
        createContactsTest(Collections.emptyList(), -1);
        createContactsTest(Collections.emptyList(), 0);

        createContactsTest(Collections.singletonList("*1*"), 1);
        createContactsTest(Arrays.asList("*1*", "*2*"), 2);
        createContactsTest(Arrays.asList("*1*", "*2*", "*3*", "*4*", "*5*", "*6*", "*7*", "*8*", "*9*", "*10*"), 10);
    }
}