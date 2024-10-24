def call(Map config) {
    def podTemplate
    if (!config.testImage) {
        podTemplate = libraryResource('template/pod/build.yaml')
    } else if (config.tests?.unit?.enabled) {
        podTemplate = libraryResource('template/pod/build-unit.yaml')
        podTemplate = podTemplate.replace('${config.testImage}', config.testImage)
    } else if (config.tests?.integration?.enabled) {
        podTemplate = libraryResource('template/pod/build-integration.yaml')
        podTemplate = podTemplate.replace('${config.testImage}', config.testImage)
        podTemplate = podTemplate.replace('${config.dbTestImage}', config.dbTestImage)
    } else {
        podTemplate = libraryResource('template/pod/build.yaml') // Default fallback
    }
    return podTemplate
}