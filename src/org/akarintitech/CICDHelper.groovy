package org.akarintitech

class CICDHelper {

    def script

    CICDHelper(script) {
        this.script = script
    }

    def checkoutCode(String repoUrl, String branch = 'main') {
        script.checkout([
            $class: 'GitSCM',
            branches: [[name: "*/${branch}"]],
            userRemoteConfigs: [[url: repoUrl]]
        ])
    }

    def runTests(String testCommand = './gradlew test') {
        script.sh testCommand
    }

    def buildProject(String buildCommand = './gradlew build') {
        script.sh buildCommand
    }

    def deployApplication(String deployCommand) {
        script.sh deployCommand
    }

    def notify(String message, String channel = '#general') {
        script.echo "Sending notification to ${channel}: ${message}"
        // Add your notification logic here, e.g., Slack, email, etc.
    }
}
