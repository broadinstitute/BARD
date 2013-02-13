package bard.core.rest.spring.util;


import bard.core.rest.spring.util.DictionaryElement;

import javax.xml.bind.annotation.*;
import java.util.List;
@XmlAccessorType( XmlAccessType.NONE )
@XmlRootElement(name = "dictionary")
public class CapDictionary{


    protected List<DictionaryElement> elements;
    //we keep the key value pair in a map so we can look them up easily
    Map<Long, DictionaryElement> dictionaryElementMap = [:]

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
    void loadDictionary() {
        final List<DictionaryElement> dictionaryElements = this.elements ?: []
        for (DictionaryElement dictionaryElement : dictionaryElements) {
           dictionaryElementMap.put(dictionaryElement.elementId, dictionaryElement)
        }
    }
}

