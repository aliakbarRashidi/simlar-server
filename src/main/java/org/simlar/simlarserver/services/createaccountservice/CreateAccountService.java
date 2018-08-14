/*
 * Copyright (C) 2017 The Simlar Authors.
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

package org.simlar.simlarserver.services.createaccountservice;

import lombok.extern.java.Log;
import org.simlar.simlarserver.database.models.AccountCreationRequestCount;
import org.simlar.simlarserver.database.repositories.AccountCreationRequestCountRepository;
import org.simlar.simlarserver.services.settingsservice.SettingsService;
import org.simlar.simlarserver.services.smsservice.SmsService;
import org.simlar.simlarserver.services.subscriberservice.SubscriberService;
import org.simlar.simlarserver.utils.LibPhoneNumber;
import org.simlar.simlarserver.utils.Password;
import org.simlar.simlarserver.utils.SimlarId;
import org.simlar.simlarserver.utils.SmsText;
import org.simlar.simlarserver.xmlerrorexceptions.XmlErrorFailedToSendSmsException;
import org.simlar.simlarserver.xmlerrorexceptions.XmlErrorInvalidTelephoneNumberException;
import org.simlar.simlarserver.xmlerrorexceptions.XmlErrorNoRegistrationCodeException;
import org.simlar.simlarserver.xmlerrorexceptions.XmlErrorNoSimlarIdException;
import org.simlar.simlarserver.xmlerrorexceptions.XmlErrorTooManyConfirmTriesException;
import org.simlar.simlarserver.xmlerrorexceptions.XmlErrorTooManyRequestTriesException;
import org.simlar.simlarserver.xmlerrorexceptions.XmlErrorWrongRegistrationCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

@Log
@Component
public final class CreateAccountService {
    private static final Pattern REGEX_REGISTRATION_CODE = Pattern.compile("\\d{6}");

    private final SmsService smsService;
    private final SettingsService settingsService;
    private final AccountCreationRequestCountRepository accountCreationRepository;
    private final SubscriberService subscriberService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public CreateAccountService(final SmsService smsService, final SettingsService settingsService, final AccountCreationRequestCountRepository accountCreationRepository, final SubscriberService subscriberService, final PlatformTransactionManager transactionManager) {
        this.smsService = smsService;
        this.settingsService = settingsService;
        this.accountCreationRepository = accountCreationRepository;
        this.subscriberService = subscriberService;
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public AccountRequest createAccountRequest(final String telephoneNumber, final String smsText, final String ip) {
        final SimlarId simlarId = SimlarId.createWithTelephoneNumber(telephoneNumber);
        if (simlarId == null) {
            throw new XmlErrorInvalidTelephoneNumberException("invalid telephone number: " + telephoneNumber);
        }

        if (!LibPhoneNumber.isValid(telephoneNumber)) {
            throw new XmlErrorInvalidTelephoneNumberException("libphonenumber invalidates telephone number: " + telephoneNumber);
        }

        final AccountCreationRequestCount dbEntry = updateRequestTries(simlarId, ip);
        final int requestTries = dbEntry.getRequestTries();
        if (requestTries > settingsService.getAccountCreationMaxRequestsPerSimlarIdPerDay()) {
            throw new XmlErrorTooManyRequestTriesException("too many create account requests: " + requestTries + " for number: " + telephoneNumber);
        }

        dbEntry.setRegistrationCode(Password.generateRegistrationCode());
        final String smsMessage = SmsText.create(smsText, dbEntry.getRegistrationCode());
        if (!smsService.sendSms(telephoneNumber, smsMessage)) {
            throw new XmlErrorFailedToSendSmsException("failed to send sms to '" + telephoneNumber + "' with text: " + smsMessage);
        }

        dbEntry.setPassword(Password.generate());
        accountCreationRepository.save(dbEntry);

        log.info("created account request for simlarId: " + simlarId);
        return new AccountRequest(simlarId, dbEntry.getPassword());
    }

    private AccountCreationRequestCount readAccountCreationRequest(final SimlarId simlarId) {
        final AccountCreationRequestCount dbEntry = accountCreationRepository.findBySimlarId(simlarId.get());
        return dbEntry != null ? dbEntry :
                new AccountCreationRequestCount(simlarId.get(), Password.generate(), Password.generateRegistrationCode());
    }

    private AccountCreationRequestCount updateRequestTries(final SimlarId simlarId, final String ip) {
        return transactionTemplate.execute(status -> {
            final AccountCreationRequestCount dbEntry = readAccountCreationRequest(simlarId);
            final Instant now = Instant.now();
            final Instant savedTimestamp = dbEntry.getTimestamp();
            if (savedTimestamp != null && Duration.between(savedTimestamp.plus(Duration.ofDays(1)), now).compareTo(Duration.ZERO) > 0) {
                dbEntry.setRequestTries(1);
            } else{
                dbEntry.incrementRequestTries();
            }
            dbEntry.setTimestamp(now);
            dbEntry.setIp(ip);
            return accountCreationRepository.save(dbEntry);
        });
    }

    public void confirmAccount(final String simlarIdString, final CharSequence registrationCode) {
        final SimlarId simlarId = SimlarId.create(simlarIdString);
        if (simlarId == null) {
            throw new XmlErrorNoSimlarIdException("confirm account request with simlarId: " + simlarIdString);
        }

        if (!checkRegistrationCode(registrationCode)) {
            throw new XmlErrorNoRegistrationCodeException("confirm account request with simlarId: " + simlarId + " and registrationCode: " + registrationCode);
        }

        final AccountCreationRequestCount creationRequest = transactionTemplate.execute(status -> {
            final AccountCreationRequestCount dbEntry = accountCreationRepository.findBySimlarId(simlarId.get());
            if (dbEntry == null) {
                throw new XmlErrorNoSimlarIdException("confirm account request with no creation request in db for simlarId: " + simlarId);
            }

            dbEntry.incrementConfirmTries();
            return accountCreationRepository.save(dbEntry);
        });

        final int confirmTries = creationRequest.getConfirmTries();
        if (confirmTries > settingsService.getAccountCreationMaxConfirms()) {
            throw new XmlErrorTooManyConfirmTriesException("Too many confirm tries(" + confirmTries + ") for simlarId: " + simlarId);
        }

        if (!Objects.equals(creationRequest.getRegistrationCode(), registrationCode)) {
            throw new XmlErrorWrongRegistrationCodeException("confirm account request with wrong registration code: " + registrationCode + " for simlarId: " + simlarId);
        }

        subscriberService.save(simlarId, creationRequest.getPassword());
    }

    private static boolean checkRegistrationCode(final CharSequence input) {
        return input != null && REGEX_REGISTRATION_CODE.matcher(input).matches();
    }
}
