/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.core;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;


public class DataSource implements Serializable {
    private static final long serialVersionUID = 0xf4bcf943bf1d45d4l;
    private static final Logger log = Logger.getLogger(DataSource.class);
    public static final DataSource DEFAULT =
            new DataSource(DataSource.class.getName(), "v1.0",
                    "http://bard.nih.gov");

    protected String name;
    protected String url;
    protected String version;
    protected String description;
    protected Date date;

    protected DataSource() {
    }

    public DataSource(String name) {
        this(name, "*", null);
    }

    public DataSource(String name, String version) {
        this(name, version, null);
    }

    public DataSource(String name, String version, String url) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(version)) {
            final String message = "DataSource name and version can't be null";
            log.error(message);
            throw new IllegalArgumentException
                    (message);
        }
        this.name = name;
        this.url = url;
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DataSource)) return false;
        DataSource ds = (DataSource) obj;

        return name.equals(ds.name)
                && (version.equals("*")
                || ds.version.equals("*")
                || version.equals(ds.version));
    }

    public static DataSource getCurrent() {
        return DEFAULT;
    }
    //TODO: Legacy code from NCGC, two datasources could be equal but then return different hashcodes
    //TODO: This should be fixed
    public int hashCode() {
        return name.hashCode() ^ version.hashCode();
    }
}
