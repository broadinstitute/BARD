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
/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/10/14
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
class ReaderFromLineIterator extends Reader  {
    Iterator<String> iterator;
    char[] next;
    int offset;
    boolean reachedEnd = false;
    char[] terminator;

    public ReaderFromLineIterator(Iterator<String> iterator, String terminator) {
        this.iterator = iterator;
        this.terminator = terminator.getChars();
        populateNext();
    }

    void populateNext() {
        if(!iterator.hasNext()) {
            reachedEnd = true;

            // clear out the locals because they should never get used again
            offset = -1;
            next = null;
            iterator = null;
            return;
        }

        String nextString = iterator.next();
        next = new char[nextString.length()+terminator.length];
        nextString.getChars(0, nextString.length(), next, 0)
        System.arraycopy(terminator, 0, next, nextString.length(), terminator.length)
        offset = 0;
    }

    @Override
    int read(char[] dest, int offset, int length) throws IOException {
        int charsCopied = 0;

        while(true) {
            if(reachedEnd) {
                if(charsCopied == 0)
                    charsCopied = -1;
                break;
            }

            int copyLen = Math.min(next.length-this.offset, length-charsCopied);
            System.arraycopy(next, this.offset, dest, offset, copyLen);
            offset += copyLen;
            this.offset += copyLen;
            charsCopied += copyLen;

            // did we reach the end of the current buffer
            if(this.offset == next.length) {
                populateNext();
            }

            // did we successfully reach the length
            if(length == charsCopied)
                break;
        }

        return charsCopied;
    }

    @Override
    void close() throws IOException {
        this.iterator = null;
    }
}

