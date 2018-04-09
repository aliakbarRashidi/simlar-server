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

package org.simlar.simlarserver.database.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.simlar.simlarserver.utils.SimlarId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;

@SuppressWarnings("ClassWithTooManyMethods")
@Data
@NoArgsConstructor
@Entity
@Table(name = "simlar_account_creation_request", indexes = {
        @Index(name = "request_timestamp", columnList = "timestamp"),
        @Index(name = "request_ip", columnList = "ip") })
public final class AccountCreationRequestCount {
    @SuppressWarnings("FieldCanBeLocal")
    @Id
    @Column(nullable = false, length = 64)
    private String simlarId;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(nullable = false, length = 64)
    private String registrationCode;

    @Column(nullable = false, columnDefinition = "int(10) unsigned")
    @ColumnDefault("1")
    private int requestTries;

    @Column(nullable = false, columnDefinition = "int(10) unsigned")
    @ColumnDefault("0")
    private int calls;

    @Column(nullable = false, columnDefinition = "int(10) unsigned")
    @ColumnDefault("0")
    private int confirmTries;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp timestamp;

    @Column(nullable = false, length = 64)
    private String ip;

    @SuppressWarnings("UnnecessaryThis")
    public AccountCreationRequestCount(final String simlarId, final String password, final String registrationCode, final String ip) {
        this.simlarId         = simlarId;
        this.password         = password;
        this.registrationCode = registrationCode;
        this.requestTries     = 1;
        this.confirmTries     = 0;
        this.timestamp        = Timestamp.from(Instant.now());
        this.ip               = ip;
    }

    public AccountCreationRequestCount(final SimlarId simlarId, final String password, final String registrationCode, final String ip) {
        this(simlarId.get(), password, registrationCode, ip);
    }

    public void incrementRequestTries() {
        requestTries++;
    }

    public void incrementCalls() {
        calls++;
    }

    public void incrementConfirmTries() {
        confirmTries++;
    }

    @SuppressWarnings("TypeMayBeWeakened") // Instant instead of Temporal
    public Instant getTimestamp() {
        return timestamp == null ? null : timestamp.toInstant();
    }

    public void setTimestamp(final Instant instant) {
        timestamp = Timestamp.from(instant);
    }
}
