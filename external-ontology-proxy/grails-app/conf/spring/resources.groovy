// Place your Spring DSL code here
beans = {
    externalOntologyFactory(bard.validation.ext.RegisteringExternalOntologyFactory) { bean ->
        bean.factoryMethod = "getInstance"
    }
}
