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
  for(dir in [new File("."), new File("../BardDomainModel")]) {
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
