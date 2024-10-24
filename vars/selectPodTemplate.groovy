def call(Map config) {
    def podTemplate
    if (config.sonarscan == 'no') {
        podTemplate = libraryResource('template/pod/build.yaml')
    } else if (config.sonarscan == 'yes' && config.tests?.integration?.enabled == 'no') {
        podTemplate = libraryResource('template/pod/build-unit.yaml')
        podTemplate = podTemplate.replace('${AITTESTIMAGE}', config.testImage)
    } else if (config.sonarscan == 'yes' && config.tests?.integration?.enabled == 'yes') {
        podTemplate = libraryResource('template/pod/build-integration.yaml')
        podTemplate = podTemplate.replace('${AITTESTIMAGE}', config.testImage)
        podTemplate = podTemplate.replace('${AITDBTESTIMAGE}', config.dbTestImage)
    } else {
        podTemplate = libraryResource('template/pod/build.yaml') // Default fallback
    }
    return podTemplate
}
