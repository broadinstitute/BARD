grails {
    plugins {
        springsecurity {
            useBasicAuth = true
            basic.realmName = 'CAP'
            filterChain.chainMap = [
                    '/assayDefinition/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/panel/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/bardWebInterface/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/chemAxon/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/context/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/contextItem/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/dictionaryTerms/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/document/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/doseResponseCurve/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/element/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/experiment/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/mergeAssayDefinition/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/moveExperiments/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/person/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/project/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/sandbox/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/splitAssayDefinition/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
                    '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
            ]
        }
    }
}
