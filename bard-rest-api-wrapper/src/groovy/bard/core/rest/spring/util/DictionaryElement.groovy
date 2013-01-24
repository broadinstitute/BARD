package bard.core.rest.spring.util

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

//<element elementId='3' readyForExtraction='Ready' elementStatus='Published'>
//<label>assay protocol</label>
//    <link rel='edit' href='https://bard-qa.broadinstitute.org/dataExport/api/dictionary/element/3' type='application/vnd.bard.cap+xml;type=element' />
//</element>
@XmlAccessorType( XmlAccessType.NONE )
public class DictionaryElement {
    @XmlAttribute
    Long elementId
    @XmlElement
    String label
    @XmlAttribute
    String elementStatus

    @XmlElement
    String description


    public DictionaryElement() {

    }

}

