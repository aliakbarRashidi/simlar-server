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

package org.simlar.simlarserver.services.delaycalculatorservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simlar.simlarserver.Application;
import org.simlar.simlarserver.helper.SimlarIds;
import org.simlar.simlarserver.utils.SimlarId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public final class DelayCalculatorServiceTest {
    @SuppressWarnings("CanBeFinal")
    @Autowired
    private DelayCalculatorService delayCalculatorService;

    private static void calculateDelayTest(final int minSeconds, final int count, final int maxSeconds) {
        final int resultDelay = DelayCalculatorService.calculateDelay(count);
        final String result = "calculateDelay(" + count + ") = " + resultDelay;
        assertTrue(minSeconds + " <= " + result, minSeconds <= resultDelay);
        assertTrue(result + " <= " + maxSeconds, resultDelay <= maxSeconds);
    }

    @Test
    public void calculateDelay() {
        calculateDelayTest(0,                       0,                   0);
        calculateDelayTest(0,                       100,                 0);
        calculateDelayTest(0,                       1000,                0);
        calculateDelayTest(0,                       2000,                1);
        calculateDelayTest(0,                       5000,                1);
        calculateDelayTest(1,                       6000,                2);
        calculateDelayTest(3,                       8000,                6);
        calculateDelayTest(5,                       10000,               8);
        calculateDelayTest(24 * 60 * 60,            100000,              24 * 60 * 60 * 10);
        calculateDelayTest(5 * 364 * 24 * 60 * 60,  100000000,           Integer.MAX_VALUE);
        calculateDelayTest(Integer.MAX_VALUE,       Integer.MAX_VALUE,   Integer.MAX_VALUE);
        calculateDelayTest(Integer.MAX_VALUE,       -1,                  Integer.MAX_VALUE);
        calculateDelayTest(Integer.MAX_VALUE,       -1000,               Integer.MAX_VALUE);
        calculateDelayTest(Integer.MAX_VALUE,       Integer.MIN_VALUE,   Integer.MAX_VALUE);
    }

    @Test
    public void calculateTotalRequestedContactsIncrement() {
        final SimlarId simlarId = SimlarId.create("*0001*");
        final LocalDateTime now = LocalDateTime.now();

        assertEquals(0, delayCalculatorService.calculateTotalRequestedContacts(simlarId, SimlarIds.createContacts(0), now));
        assertEquals(1, delayCalculatorService.calculateTotalRequestedContacts(simlarId, SimlarIds.createContacts(1), now));
        assertEquals(1, delayCalculatorService.calculateTotalRequestedContacts(simlarId, SimlarIds.createContacts(0), now));
        assertEquals(3, delayCalculatorService.calculateTotalRequestedContacts(simlarId, SimlarIds.createContacts(2), now));
        assertEquals(6, delayCalculatorService.calculateTotalRequestedContacts(simlarId, SimlarIds.createContacts(3), now));
        assertEquals(8, delayCalculatorService.calculateTotalRequestedContacts(simlarId, SimlarIds.createContacts(2), now));
    }

    @Test
    public void calculateTotalRequestedContactsDecreaseAfterADay() {
        final SimlarId simlarId = SimlarId.create("*0002*");
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime dayAfter = LocalDateTime.now().plusDays(1).plusSeconds(2);

        assertEquals(8, delayCalculatorService.calculateTotalRequestedContacts(simlarId, SimlarIds.createContacts(8), now));
        assertEquals(4, delayCalculatorService.calculateTotalRequestedContacts(simlarId, SimlarIds.createContacts(4), dayAfter));
    }

    @Test
    public void calculateTotalRequestedContactsSameContactsNoIncrement() {
        final SimlarId simlarId = SimlarId.create("*0003*");
        final SimlarId a = SimlarId.create("*0002*");
        final SimlarId b = SimlarId.create("*0003*");
        final SimlarId c = SimlarId.create("*0004*");
        final LocalDateTime now = LocalDateTime.now();

        assertEquals(3, delayCalculatorService.calculateTotalRequestedContacts(simlarId, Arrays.asList(a, b, c), now));
        assertEquals(3, delayCalculatorService.calculateTotalRequestedContacts(simlarId, Arrays.asList(b, a, c), now));
        assertEquals(3, delayCalculatorService.calculateTotalRequestedContacts(simlarId, Arrays.asList(c, b, a), now));
        assertEquals(3, delayCalculatorService.calculateTotalRequestedContacts(simlarId, Arrays.asList(a, b, c, a), now));
    }

    @Test
    public void calculateRequestDelay() {
        final SimlarId simlarId = SimlarId.create("*0004*");

        assertEquals(0, delayCalculatorService.calculateRequestDelay(simlarId, SimlarIds.createContacts(2000)));
        assertEquals(0, delayCalculatorService.calculateRequestDelay(simlarId, SimlarIds.createContacts(2001)));
        assertEquals(1, delayCalculatorService.calculateRequestDelay(simlarId, SimlarIds.createContacts(2000)));
        assertEquals(3, delayCalculatorService.calculateRequestDelay(simlarId, SimlarIds.createContacts(2001)));
        assertEquals(93, delayCalculatorService.calculateRequestDelay(simlarId, SimlarIds.createContacts(10000)));
        assertEquals(93, delayCalculatorService.calculateRequestDelay(simlarId, SimlarIds.createContacts(10000)));
        assertEquals(546, delayCalculatorService.calculateRequestDelay(simlarId, SimlarIds.createContacts(10001)));
        assertEquals(238440, delayCalculatorService.calculateRequestDelay(simlarId, SimlarIds.createContacts(100000)));
    }
}