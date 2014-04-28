%{-- Copyright (c) 2014, The Broad Institute
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
 --}%

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span8">

            <g:formRemote url="[controller: 'splitAssayDefinition', action: 'splitExperiments']"
                          name="splitExperiments"
                          update="[success: 'confirmResponse', failure: 'confirmResponse']">

                <div class="control-group">
                    <label><h3>Select one or more Experiments to move to new Assay</h3>
                    </label>
                </div>

                <div class="control-group">
                    <g:hiddenField name="assay.id" value="${assay.id}"/>
                    <g:each var="experiment" in="${assay.experiments}">
                        <g:checkBox name="experimentIds" value="${experiment.id}" checked="false"/> ${experiment.id +' : '+experiment.experimentName} <br/>
                    </g:each>
                 </div>

                <div class="control-group">
                    <div class="controls">
                        <input type="submit" class="btn btn-primary" name="Split" value="Split">
                    </div>
                </div>

                <div id="confirmResponse"></div>
                <br/>

            </g:formRemote>

        </div>

        <div class="span2">

        </div>
    </div>
</div>
