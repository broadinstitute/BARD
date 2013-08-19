import groovy.io.FileType
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("Compile")

target(main: "Create zip file of all files in this tree") {
  def targetFile = new File("./target/archive.zip")
  def dir = new File(".")
  FileOutputStream fos = new FileOutputStream(targetFile);
  ZipOutputStream zos = new ZipOutputStream(fos);
  println "Writing zip file to ${targetFile.absolutePath}"
  dir.eachFileRecurse (FileType.FILES) { file ->
//    println(file)
    if(targetFile.absolutePath == file.absolutePath) {
      return
    }

    ZipEntry ze = new ZipEntry(file.toString());
    zos.putNextEntry(ze);
    FileInputStream input = new FileInputStream(file)

    IOUtils.copy(input, zos);
    
    input.close()
    zos.closeEntry()
  }
  zos.close()
}

setDefaultTarget(main)
