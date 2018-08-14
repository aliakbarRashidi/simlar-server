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

package org.simlar.simlarserver.controllers;

import lombok.AllArgsConstructor;
import org.simlar.simlarserver.services.twilio.TwilioSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
final class TwilioController {
    public  static final String REQUEST_PATH = "twilio/delivery-report.json";
    private static final Logger LOGGER       = Logger.getLogger(TwilioController.class.getName());

    private final TwilioSmsService twilioSmsService;

    /**
     * This method handles http post requests. You may test it with:
     * <blockquote>
     * curl --data "MessageSid=12345678&MessageStatus=Queued&To=123&ErrorCode=30008" http://localhost:8080/twilio/delivery-report.json
     * </blockquote>
     *
     * @param messageSid
     *            Twilio's message id
     * @param to
     *            receipient's telephone number
     * @param messageStatus
     *            e.g. failed, undelivered, queued, sent, delivered
     * @param errorCode
     *            Twilio error code
     */
    @SuppressWarnings("SpellCheckingInspection")
    @RequestMapping(value = REQUEST_PATH, method = RequestMethod.POST)
    public void postDeliveryReport(
            @RequestParam(name = "MessageSid")                  final String messageSid,
            @RequestParam(name = "To")                          final String to,
            @RequestParam(name = "MessageStatus")               final String messageStatus,
            @RequestParam(name = "ErrorCode", required = false) final String errorCode
    ) {
        LOGGER.info(REQUEST_PATH + " requested with messageSid=" + messageSid + " to=" + to + " messageStatus=" + messageStatus + " errorCode=" + errorCode);

        twilioSmsService.handleDeliveryReport(to, messageSid, messageStatus, errorCode);
    }
}