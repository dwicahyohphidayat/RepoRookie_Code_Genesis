// vars/akarintiPipeline.groovy
def call(Map config) {

    config = org.akarintitech.ConfigValidator.validate(config, env)

    def namespace = config.namespace
    def envinfra = config.envinfra
    def targetPort = config.targetPort
    def repoUrl = config.repoUrl
    def branch = config.branch

    pipeline {
        agent {
            kubernetes {
                label 'eci-agent-skaffold'
            }
        }
        stages {
            stage('Checkout') {
                steps {
                    container('jnlp') {
                        checkout([$class: 'GitSCM', branches: [[name: "*/${branch}"]], userRemoteConfigs: [[url: "${repoUrl}"]]])
                    }
                }
            }
            stage('Build') {
                steps {
                    echo "Building project: ${config}"
                    container('jnlp') {
                        skaffold(namespace, envinfra, repoUrl, branch, targetPort)
                    }
                }
            }
            stage('Test') {
                when {
                    expression { config.tests?.unit?.enable == 'yes' || config.tests?.integration?.enable == 'yes' }
                }
                steps {
                    script {
                        def testHelper = new org.akarintitech.TestHelper(this)
                        if (config.tests?.unit?.enable == 'yes') {
                            testHelper.runUnitTests(config.tests.unit.framework)
                        } else if (config.tests?.integration?.enable == 'yes') {
                            testHelper.runIntegrationTests(config.tests.integration.framework)
                        } else {
                            echo "No tests to run."
                        }
                    }
                }
            }
        }
    }
}

