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

/**
 * Created by dlahr on 4/27/2014.
 */

final String licenseFile = "c:/Local/code_and_repos/therapeutics_platform/BARD/BARD/userScripts/addLicense/license.txt"
final String startDirectoryPath =
//        "c:/Local/code_and_repos/therapeutics_platform/BARD/BARD"  //for groovy, java, gsp, properties
//        "c:/Local/code_and_repos/therapeutics_platform/BARD/BARD/userScripts/addLicense/test"  //for testing
        "c:/Local/code_and_repos/therapeutics_platform/BARD/BARD/web-app"  //for js and css

File startDirectory = new File(startDirectoryPath)

//webapp/jquery-mobile
//webapp/marvin
//webapp/backbone.js

Set<File> excludeSet =
//        [new File(startDirectoryPath, "functional-spock"), new File(startDirectoryPath, "shopping-cart-0.8.2")] as Set<File>
        buildExcludeList(new File(startDirectory, "js"),
                "c:/Local/code_and_repos/therapeutics_platform/BARD/BARD/userScripts/addLicense/excludeJs.txt")
excludeSet.addAll(buildExcludeList(new File(startDirectory, "css"),
        "c:/Local/code_and_repos/therapeutics_platform/BARD/BARD/userScripts/addLicense/excludeCss.txt"))

excludeSet.add(new File(startDirectory, "jquery.mobile-1.3.1"))
excludeSet.add(new File(startDirectory, "marvin"))
excludeSet.add(new File(startDirectory, "backbone.js"))

//for (File f : excludeSet) {
//    println(f.getAbsolutePath())
//}
//return

LicenseAdder la = new LicenseAdder(excludeSet)
la.licenseText = readLicense(licenseFile)
//println("read license:")
//println(la.licenseText.join("\n"))


la.add(startDirectory)

return

class LicenseAdder {
    private final Map<String, CommentInfo> commentMap

    private static final File tempFile = new File("temp_license_file.txt")

    List<String> licenseText

    final Set<File> excludeFileSet

    LicenseAdder(Collection<File> excludeFiles) {
        CommentInfo commentInfo = new CommentInfo(["/*", "*/"], true)

        commentMap = new HashMap<>()

        commentMap.put("groovy", commentInfo)
        commentMap.put("gsp", new CommentInfo(["%{--","--}%"], true))
        commentMap.put("java", commentInfo)
        commentMap.put("properties", new CommentInfo(["#"], false))

        commentMap.put("css", commentInfo)
        commentMap.put("js", commentInfo)


        excludeFileSet = Collections.unmodifiableSet(new HashSet<File>(excludeFiles))
    }

    void add(File directory) {
        for (File f : directory.listFiles()) {
            if (! excludeFileSet.contains(f)) {
                if (f.isDirectory()) {
                    add(f)
                } else {
                    for (String suffix : commentMap.keySet()) {
                        String extension = ".$suffix".toString()
                        if (f.getName().toLowerCase().endsWith(extension)) {
                            println("section comment: ${f.getAbsolutePath()}")
                            addLicenseToFile(f, commentMap.get(suffix))
                            break
                        }
                    }
                }
            }
        }
    }

    void addLicenseToFile(File file, CommentInfo ci) {
        if (tempFile.exists()) {
            throw new RuntimeException("temp file already exists:  ${tempFile.getAbsolutePath()}")
        }

        String origPath = file.getAbsolutePath()
        file.renameTo(tempFile)

        BufferedWriter writer = new BufferedWriter(new FileWriter(origPath))
        if (ci.isSection) {
            writer.write(ci.commentSymbols[0])
            writer.write(" ")
        }

        for (String line : licenseText) {
            if (! ci.isSection) {
                writer.write(ci.commentSymbols[0])
            }

            writer.write(line)
            writer.newLine()
        }

        if (ci.isSection) {
            writer.write(" ")
            writer.write(ci.commentSymbols[1])
            writer.newLine()
        }

        writer.newLine()

        BufferedReader reader = new BufferedReader(new FileReader(tempFile))
        String line
        while ((line = reader.readLine()) != null) {
            writer.write(line)
            writer.newLine()
        }
        reader.close()

        tempFile.delete()

        writer.close()
    }
}

List<String> readLicense(String licenseFile) {
    List<String> result = new LinkedList<>()

    BufferedReader reader = new BufferedReader(new FileReader(licenseFile))
    String line
    while ((line = reader.readLine()) != null) {
        result.add(line)
    }
    reader.close()

    return result
}

Set<File> buildExcludeList(File startDirectory, String excludeFilePath) {
//    println("buildExcludeList startDir:${startDirectory.getAbsolutePath()}")

    List<String> filePatternList = new LinkedList<>()

    BufferedReader reader = new BufferedReader(new FileReader(excludeFilePath))

    String line
    while ((line = reader.readLine()) != null) {
        filePatternList.add(line.toLowerCase())
    }

    reader.close()

    Set<File> result = new HashSet<>()

    search(result, startDirectory, filePatternList)

    return result
}

void search(Set<File> fileSet, File dir, List<String> filePatternList) {
//    println("search ${dir.isDirectory()}")
    for (File f : dir.listFiles()) {
//        println(f.getAbsolutePath())

        for (String fp : filePatternList) {
//            println("fp:$fp")
            if (f.getAbsolutePath().toLowerCase().contains(fp)) {
//                println("added")
                fileSet.add(f)
                break
            }
        }

        if (f.isDirectory() && ! fileSet.contains(f)) {
            search(fileSet, f, filePatternList)
        }
    }
}

class CommentInfo {
    List<String> commentSymbols
    boolean isSection

    CommentInfo(List<String> commentSymbols, boolean isSection) {
        this.commentSymbols = commentSymbols
        this.isSection = isSection
    }
}
