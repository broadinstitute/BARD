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

import adf.exp.JsonTransform
import adf.imp.Batch
import adf.imp.DatasetParser
import au.com.bytecode.opencsv.CSVWriter
import bard.db.dictionary.Element
import bard.db.experiment.JsonSubstanceResults
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/7/14
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Element])
@Mock([Element])
@TestMixin(GrailsUnitTestMixin)
public class DatasetImportUnitSpec extends Specification {

    String createFile(List<List<String>> rows) {
        File temp = File.createTempFile("test-data-imp", ".tmp");
        FileWriter out = new FileWriter(temp)
        CSVWriter writer = new CSVWriter(out)
        writer.writeNext(["sid", "a"].toArray(new String[0]))
        rows.each {
            writer.writeNext(it.collect {it.toString()} .toArray(new String[0]))
        }
        writer.close()
        return temp.absolutePath
    }

    void testSameSamples() {
        setup:
        String file1 = createFile([[1,2],[2,3]]);
        String file2 = createFile([[1,2],[2,3]]);
        Element element = Element.build(label: "a")
        Batch batch

        when:
        DatasetParser input = new DatasetParser([file1, file2], {e -> true})

        then:
        input.hasNext();

        when:
        batch = input.readNext() ;

        then:
        batch.datasets.size() == 2
        batch.sid == 1
        input.hasNext() ;

        when:
        Element.findByLabel("a") >> element
        batch = input.readNext() ;

        then:
        batch.datasets.size() == 2
        batch.sid == 2
        !input.hasNext()
    }

    void testOutOfOrderSids() {
        setup:
        String file1 = createFile([[2,3],[1,2]]);
        String file2 = createFile([[1,2],[2,3]]);
        Element element = Element.build(label: "a")
        Batch batch

        when:
        DatasetParser input = new DatasetParser([file1, file2], {e -> true})

        then:
        input.hasNext();

        when:
        batch = input.readNext() ;

        then:
        Element.findByLabel("a") >> element
        batch.datasets.size() == 2
        batch.sid == 1
        input.hasNext() ;

        when:
        batch = input.readNext() ;

        then:
        batch.datasets.size() == 2
        batch.sid == 2
        !input.hasNext()
    }

    void testDifferentSample() {
        String file1 = createFile([[2,3]])
        String file2 = createFile([[1,2]])
        Element element = Element.build(label: "a")
        Batch batch

        when:
        DatasetParser input = new DatasetParser([file1, file2], {e -> true})

        then:
        input.hasNext() ;

        when:
        batch = input.readNext() ;

        then:
        Element.findByLabel("a") >> element
        batch.datasets.size() == 1
        batch.sid == 1
        input.hasNext() ;

        when:
        batch = input.readNext() ;

        then:
        batch.datasets.size() == 1
        batch.sid == 2
        !input.hasNext()
    }
}
