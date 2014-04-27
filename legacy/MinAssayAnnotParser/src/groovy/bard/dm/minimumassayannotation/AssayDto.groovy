package bard.dm.minimumassayannotation

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 1/6/13
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayDto {
    final String aidFromCell

    final Long aid

    final File sourceFile
    final Integer rowNum

    List<ContextDTO> assayContextDTOList
    List<ContextDTO> measureContextDTOList

    public AssayDto(String aidFromCell, File sourceFile, Integer rowNum) {
        this.aidFromCell = aidFromCell
        aid = aidFromCell.isDouble() ? (Long)(Double.valueOf(aidFromCell)) : null

        this.sourceFile = sourceFile
        this.rowNum = rowNum

        assayContextDTOList = new LinkedList<ContextDTO>()
        measureContextDTOList = new LinkedList<ContextDTO>()
    }
}
