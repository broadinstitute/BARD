package molspreadsheet

class SunburstHandlerTagLib {

    RingManagerService ringManagerService

    def makeSunburst = { attrs, body ->
        RingNode root =  ringManagerService.createStub()
        out <<   ringManagerService.writeRingTree(root)
        out <<   "\n"
        out <<   ringManagerService.defineColors(root)
    }



}
