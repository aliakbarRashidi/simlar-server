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

package org.simlar.simlarserver;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class SubscriberService {
    private static final String        DOMAIN = "";
    private static final Logger        logger = Logger.getLogger(SubscriberService.class.getName());

    private final SubscriberRepository subscriberRepository;

    @Autowired
    public SubscriberService(final SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public boolean save(final SimlarId simlarId, final String password) {
        if (simlarId == null) {
            return false;
        }

        if (password == null || password.isEmpty()) {
            return false;
        }

        final Subscriber subscriber = new Subscriber(simlarId.get(), DOMAIN, password, "", Hash.md5(simlarId.get() + ":" + DOMAIN + ":" + password),
                Hash.md5(simlarId.get() + "@" + DOMAIN + ":" + DOMAIN + ":" + password));

        subscriber.setId(findSubscriberId(simlarId));
        return subscriberRepository.save(subscriber) != null;
    }

    private Long findSubscriberId(final SimlarId simlarId) {
        final List<Long> ids = subscriberRepository.findIdByUsernameAndDomain(simlarId.get(), DOMAIN);
        if (ids == null || ids.isEmpty()) {
            return null;
        }

        if (ids.size() != 1) {
            logger.severe("found more than 1 subscriber for simlarID=" + simlarId);
        }

        return ids.get(0);
    }

    public boolean checkCredentials(final String simlarId, final String ha1) {
        if (!SimlarId.check(simlarId)) {
            return false;
        }

        if (ha1 == null || ha1.isEmpty()) {
            return false;
        }

        final List<String> savedHa1s = subscriberRepository.findHa1ByUsernameAndDomain(simlarId, DOMAIN);
        if (savedHa1s == null || savedHa1s.isEmpty()) {
            return false;
        }

        if (savedHa1s.size() != 1) {
            logger.severe("found more than 1 subscriber for simlarID=" + simlarId);
        }

        return ha1.equals(savedHa1s.get(0));
    }

    public int getStatus(final String simlarId) {
        if (!SimlarId.check(simlarId)) {
            return 0;
        }

        return subscriberRepository.findHa1ByUsernameAndDomain(simlarId, DOMAIN).isEmpty() ? 0 : 1;
    }
}
