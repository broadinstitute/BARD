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

package adf.imp

import adf.imp.ReaderFromLineIterator
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/10/14
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
class ReaderFromLineIteratorUnitSpec extends Specification {
    def testReadSubLine() {
        setup:
        ReaderFromLineIterator r = new ReaderFromLineIterator(["abc"].iterator(), "");
        char[] buf= new char[4];
        int c;

        when:
        c = r.read(buf, 0, 2)

        then:
        buf[0] == 'a'
        buf[1] == 'b'
        c == 2
    }

    def testTerminator() {
        setup:
        ReaderFromLineIterator r = new ReaderFromLineIterator(["a","b"].iterator(), "\n");
        char[] buf= new char[4];
        int c;

        when:
        c = r.read(buf, 0, 4)
        String s = new String(buf)

        then:
        c == 4
        s == "a\nb\n"
    }

    def testNonAlignedReads() {
        setup:
        ReaderFromLineIterator r = new ReaderFromLineIterator(["a", "b", "c"].iterator(), "");
        char[] buf= new char[4];
        int c;

        when:
        c = r.read(buf, 0, 2)

        then:
        buf[0] == 'a'
        buf[1] == 'b'
        c == 2

        when:
        c = r.read(buf, 2, 2)

        then:
        buf[2] == 'c'
        c == 1

        when:
        c = r.read(buf, 0, 1)

        then:
        c == -1
    }

    def testBoundaryAlignedReads() {
        setup:
        ReaderFromLineIterator r = new ReaderFromLineIterator(["a", "b", "c"].iterator(), "");
        char[] buf= new char[3];
        int c;

        when:
        c = r.read(buf, 0, 1)

        then:
        buf[0] == 'a'
        c == 1

        when:
        c = r.read(buf, 1, 1)

        then:
        buf[1] == 'b'
        c == 1

        when:
        c = r.read(buf, 2, 1)

        then:
        buf[2] == 'c'
        c == 1

        when:
        c = r.read(buf, 0, 1)

        then:
        c == -1
    }
}
