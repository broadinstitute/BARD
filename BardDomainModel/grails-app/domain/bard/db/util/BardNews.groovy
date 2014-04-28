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

package bard.db.util

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Contains information that would be used to notify users of maintenance work etc
 */
class BardNews {
    String entryId
    Date datePublished
    Date entryDateUpdated
    String title
    String link
    String authorName
    String authorEmail
    String authorUri
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static transients = []
    static mapping = {
        table('BARD_NEWS')
        id(column: "ID", generator: "sequence", params: [sequence: 'BARD_NEWS_ID_SEQ'])
    }

    static constraints = {
        entryId(nullable: false, blank: false, maxSize: 250)
        entryDateUpdated(nullable: true)
        title(nullable: false, blank: false, maxSize: 1020)
        link(nullable: false, blank: false, maxSize: 250)
        authorName(nullable: true, blank: false, maxSize: 125)
        authorEmail(nullable: true, blank: false, maxSize: 125)
        authorUri(nullable: true, blank: false, maxSize: 250)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true)
    }
}
