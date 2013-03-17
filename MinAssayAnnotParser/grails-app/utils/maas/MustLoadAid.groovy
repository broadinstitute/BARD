package maas

import org.apache.commons.lang.StringUtils

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/7/13
 * Time: 8:36 AM
 * To change this template use File | Settings | File Templates.
 */
class MustLoadAid {
    static List<Long> mustLoadedAids(String fileName) {
        List<Long> aids = []
        new File(fileName).eachLine{String line ->
            line = StringUtils.trim(line)
            if (line ==~ /[0-9]+/) {
                aids << Long.valueOf(line)
            }
        }
        return aids
    }
}
