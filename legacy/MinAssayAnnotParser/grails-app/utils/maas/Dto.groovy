package maas

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/1/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
class Dto {
    final String aidFromCell

    final Long aid

    final File sourceFile
    final Integer rowNum

    List<ContextDTO> contextDTOs = []

    public Dto(String aidFromCell, File sourceFile, Integer rowNum) {
        this.aidFromCell = aidFromCell
        aid = aidFromCell.isDouble() ? (Long)(Double.valueOf(aidFromCell)) : null

        this.sourceFile = sourceFile
        this.rowNum = rowNum
    }

    @Override
    String toString(){
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(aidFromCell)
                    .append("\t")
                    .append(aid)
                    .append("\t")
                    .append(sourceFile)
                    .append("\t")
                    .append(rowNum)
        return stringBuilder.toString()
    }

}
