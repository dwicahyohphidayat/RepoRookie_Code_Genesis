package org.akarintitech

class TestHelper {
    def script

    TestHelper(script) {
        this.script = script
    }

    void runUnitTests(String framework) {
        script.echo "Running Unit Tests with framework: ${framework}"
        script.container('jnlp') {
            if (framework == 'testng') {
                script.sh './gradlew test --tests *TestNG*'
            } else if (framework == 'junit') {
                script.sh './gradlew test --tests *JUnit*'
            }
        }
    }

    void runIntegrationTests(String framework) {
        script.echo "Running Integration Tests with framework: ${framework}"
        script.container('jnlp') {
            if (framework == 'junit') {
                script.sh './gradlew integrationTest --tests *JUnit*'
            } else if (framework == 'testng') {
                script.sh './gradlew integrationTest --tests *TestNG*'
            }
        }
    }
}

