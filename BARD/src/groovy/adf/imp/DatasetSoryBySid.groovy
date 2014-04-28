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

import au.com.bytecode.opencsv.CSVReader
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import org.apache.commons.collections.iterators.IteratorChain
import org.apache.commons.io.LineIterator
import org.mapdb.Pump
import org.mapdb.Serializer

/**
 * Used to sort data files
 *
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/10/14
 * Time: 12:07 PM
 */
class DatasetSoryBySid {
    static final int SORT_BATCH_SIZE = 100000;

    static final Comparator<Comparable> comparableComparator = new Comparator<Comparable>() {
        int compare(Comparable t, Comparable t1) {
            return t.compareTo(t1)
        }
    }

    static class DerivedCSVReader extends CSVReader {
        Reader originalReader;

        DerivedCSVReader(Reader reader, Reader originalReader) {
            super(reader)
            this.originalReader = originalReader;
        }

        @Override
        void close() throws IOException {
            super.close()
            this.originalReader.close();
        }
    }

    public static CSVReader sortCsvFile(String filename) {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String header = bufferedReader.readLine();
        Iterator<String> lineIterator = new LineIterator(bufferedReader);
        Iterator<String> sortedLineIterator = Pump.sort(lineIterator, false, SORT_BATCH_SIZE, comparableComparator, Serializer.STRING);
        Reader vReader = new ReaderFromLineIterator(new IteratorChain([header].iterator(), sortedLineIterator), "\n");
        return new DerivedCSVReader(vReader, bufferedReader);
    }
}
