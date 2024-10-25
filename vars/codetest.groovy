def call(Map config, def script) {
    def testHelper = new org.akarintitech.TestHelper(script)
    if (config.tests?.unit?.enabled == 'yes') {
        testHelper.runUnitTests(config.tests.unit.framework)
    } else if (config.tests?.integration?.enabled == 'yes') {
        testHelper.runIntegrationTests(config.tests.integration.framework)
    } else {
        echo "No tests to run."
    }
}
