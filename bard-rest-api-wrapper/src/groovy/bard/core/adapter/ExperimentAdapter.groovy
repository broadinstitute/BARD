package bard.core.adapter

import bard.core.interfaces.ExperimentAdapterInterface
import bard.core.rest.spring.experiment.ExperimentAbstract
import bard.core.rest.spring.util.NameDescription
import bard.core.util.MatchedTermsToHumanReadableLabelsMapper

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 3/19/14
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentAdapter implements ExperimentAdapterInterface{

    final ExperimentAbstract experiment
    final Double score
    final NameDescription matchingField
    List<Long>  projectIdList = []
    Long ncgcWarehouseId;
    String status

    Boolean experimentFiles = Boolean.FALSE

    public ExperimentAdapter(ExperimentAbstract experiment, Double score = 0, NameDescription nameDescription = null){
        this.experiment = experiment
        this.matchingField = nameDescription
        this.score = score
    }

    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if (matchFieldName) {
            matchFieldName = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(matchFieldName)
            return "Matched Field: " + matchFieldName
        }
        return ""
    }

    @Override
    Long getId() {
        return experiment.getBardExptId()
    }

    public Double getScore() {
        return score
    }

    @Override
    String getName() {
        return experiment.name
    }

    @Override
    String getDescription() {
        return experiment.description
    }

    public Long getCapExptId() {
        return experiment.getCapExptId()
    }

    public Long getCapAssayId(){
        return experiment.capAssayId
    }

    public Long getActiveCompounds(){
        return experiment.activeCompounds
    }

    public List<Long> getProjectIdList(){
        return projectIdList
    }

    public Long getCompoundsTested(){
        return experiment.compounds
    }

    public Long getNcgcWarehouseId(){
        return ncgcWarehouseId
    }

    Boolean getExperimentFiles() {
        return experimentFiles
    }

    public Long getSubstancesTested(){
        return experiment.substances
    }

    public String getStatus(){
        return status
    }
}
