package maas

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/8/13
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
class FileHashMap {
    private final Map<File, Integer> fileHashCodeMap

    private final BufferedWriter writer

    FileHashMap(String outputFilename) {
        fileHashCodeMap = new HashMap<File, Integer>()

        writer = new BufferedWriter(new FileWriter(outputFilename))
        writer.writeLine("hash_code,file_path")
    }

    int addFile(File file) {
        final int hashCode = file.absolutePath.hashCode()

        if (!fileHashCodeMap.containsKey(file)) {
            fileHashCodeMap.put(file, hashCode)
            writer.writeLine("$hashCode,${file.absolutePath}")
        }

        return hashCode
    }

    void close() {
        writer.close()
    }
}
