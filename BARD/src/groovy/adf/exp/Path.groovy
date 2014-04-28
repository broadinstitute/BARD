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

package adf.exp

import bard.db.experiment.JsonResult

/**
 * Created by ddurkin on 3/20/14.
 */

class Path {
    ResultKey result;

    Path parent

    Path(JsonResult jr, parent) {
        this.result = new ResultKey(jr);
        this.parent = parent
    }

    List<Path> getPath() {
        if (this.parent == null) {
            return [this]
        } else {
            def path = parent.getPath()
            path.add(this)
            return path
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Path)) return false

        Path that = (Path) o

        if (parent != that.parent) return false
        if (result != that.result) return false

        return true
    }

    int hashCode() {
        int result1
        result1 = (result != null ? result.hashCode() : 0)
        result1 = 31 * result1 + (parent != null ? parent.hashCode() : 0)
        return result1
    }

    String toString() {
        return result.toString()
    }
}
