package bardwebquery

class CompoundOptionsTagLib {

    def compoundOptions = { attrs, body ->

        out << render(template: "/tagLibTemplates/compoundOptions", model: [cid: attrs.cid,
                smiles: attrs.smiles, sid: attrs.sid,
                imageWidth:attrs.imageWidth, imageHeight:attrs.imageHeight])
    }
}
