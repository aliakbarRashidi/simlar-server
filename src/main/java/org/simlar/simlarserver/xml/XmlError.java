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

package org.simlar.simlarserver.xml;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public final class XmlError {
    private int    id;
    private String message;

    public XmlError() {
        // needed for JAXBContext
    }

    public XmlError(final int id, final String message) {
        this.id = id;
        this.message = message;
    }

    @XmlAttribute
    public int getId() {
        return id;
    }

    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    @SuppressWarnings("unused")
    private void setId(final int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    @XmlAttribute
    public String getMessage() {
        return message;
    }

    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    @SuppressWarnings("unused")
    private void setMessage(final String message) {
        this.message = message;
    }
}