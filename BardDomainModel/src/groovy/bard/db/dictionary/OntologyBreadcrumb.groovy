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

package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/26/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
class OntologyBreadcrumb {

//    List<BiologyDescriptor> biologyDescriptors
    def biologyDescriptors
    def instanceDescriptors
    def assayDescriptors

    public OntologyBreadcrumb(Element element) {
        biologyDescriptors = element.biologyDescriptors.sort(new DescriptorLabelComparator())
        instanceDescriptors = element.instanceDescriptors.sort(new DescriptorLabelComparator())
        assayDescriptors = element.assayDescriptors.sort(new DescriptorLabelComparator())
    }


    Descriptor getPreferedDescriptor() {
        Descriptor preferredDescriptor
        if (biologyDescriptors) {
            preferredDescriptor = biologyDescriptors.first()
        }
        else if (instanceDescriptors) {
            preferredDescriptor = instanceDescriptors.first()
        }
        else if (assayDescriptors) {
            preferredDescriptor = assayDescriptors.first()
        }
    }

}
