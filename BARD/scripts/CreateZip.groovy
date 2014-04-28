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

import groovy.io.FileType
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils

includeTargets << grailsScript("_GrailsInit")

target(main: "Create zip file of all files in this tree") {
  def targetFile = new File("./target/package.zip")
  FileOutputStream fos = new FileOutputStream(targetFile);
  ZipOutputStream zos = new ZipOutputStream(fos);
  println "Writing zip file to ${targetFile.absolutePath}"

  for(dir in [new File("."), new File("../BardDomainModel"), new File("../functional-spock"), new File("../crowdUserRegistration")]) {
    dir.eachFileRecurse (FileType.FILES) { file ->
      if(targetFile.absolutePath == file.absolutePath) {
        return
      }

      def fn = file.toString()
      if(fn.startsWith("./")) {
        fn = "BARD/"+fn.substring(2)
      } else if(fn.startsWith("../")) {
        fn = fn.substring(3)
      }
      ZipEntry ze = new ZipEntry(fn);
      zos.putNextEntry(ze);
      FileInputStream input = new FileInputStream(file)

      IOUtils.copy(input, zos);
      
      input.close()
      zos.closeEntry()
    }
  }
  zos.close()
}

setDefaultTarget(main)
