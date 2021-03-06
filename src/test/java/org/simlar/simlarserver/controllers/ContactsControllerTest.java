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

package org.simlar.simlarserver.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simlar.simlarserver.testdata.TestUser;
import org.simlar.simlarserver.xml.XmlContact;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
public final class ContactsControllerTest extends ContactsControllerBaseTest {
    private void assertWrongCredentials(final String username, final String password) {
        assertEquals(10, requestError(username, password, "*0002*|*0003*"));
    }

    @Test
    public void testWrongCredentials() {
        assertWrongCredentials(null, "xxxxxxx");
        assertWrongCredentials("*", "xxxxxxx");
        assertWrongCredentials(TestUser.U1.getSimlarId(), null);
        assertWrongCredentials(TestUser.U1.getSimlarId(), "xxxxxxx");
    }

    private void assertEmptyContactList(final String contactList) {
        assertNull(requestContactList(TestUser.U1, contactList));
    }

    @Test
    public void testEmptyContactList() {
        assertEmptyContactList(null);
        assertEmptyContactList("");
        assertEmptyContactList(TestUser.U2.getSimlarId() + ' ' + TestUser.SIMLAR_ID_NOT_REGISTERED);
    }

    @Test
    public void testReceiveContactsStatus() {
        final List<XmlContact> contacts = requestContactList(TestUser.U1, TestUser.U2.getSimlarId() + '|' + TestUser.SIMLAR_ID_NOT_REGISTERED);
        assertNotNull(contacts);
        assertEquals(2, contacts.size());
        assertEquals(TestUser.U2.getSimlarId(), contacts.get(0).getSimlarId());
        assertEquals(1, contacts.get(0).getStatus());
        assertEquals(TestUser.SIMLAR_ID_NOT_REGISTERED, contacts.get(1).getSimlarId());
        assertEquals(0, contacts.get(1).getStatus());
    }
}
