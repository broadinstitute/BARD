package bard.core.rest.spring.util;


import bard.core.rest.spring.util.DictionaryElement;

import javax.xml.bind.annotation.*;
import java.util.List;
@XmlAccessorType( XmlAccessType.NONE )
@XmlRootElement(name = "dictionary")
public class CapDictionary implements Serializable{


    protected List<DictionaryElement> elements;

    public CapDictionary() {

    }


    @XmlElementWrapper(name = "elements")
    @XmlElement(name = "element")
    public List<DictionaryElement> getElements() {
        return this.elements;
    }

    public void setElements(List<DictionaryElement> dictionaryElements) {

        this.elements = dictionaryElements;
    }

}

