// vars/akarintiPipeline.groovy
def call(Map config) {

    config = org.akarintitech.ConfigValidator.validateAndInitialize(config, env)

    def namespace = config.namespace
    def envinfra = config.envinfra
    def targetPort = config.targetPort
    def repoUrl = config.repoUrl
    def branch = config.branch
    def skaffoldScheme = config.skaffold
    def dockerfile = config.dockerfile
    def buildEnv = config.buildEnv

    pipeline {
        stages {
            stage('Sonar Scan') {
                when {
                    expression { config.sonarscan == 'yes' }
                }
                agent {
                    kubernetes {
                        label 'eci-agent-sonarscanner'
                    }
                }
                steps {
                    container('jnlp') {
                        checkout([$class: 'GitSCM', branches: [[name: "*/${branch}"]], userRemoteConfigs: [[url: "${repoUrl}"]]])
                        sonarscan(namespace, envinfra, repoUrl, branch, targetPort, skaffoldScheme, buildEnv, dockerfile)
                    }
                    script {
                        if (config.tests?.unit?.enabled == 'yes' || config.tests?.integration?.enabled == 'yes') {
                            container('test') {
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

            stage('Build and Deploy') {
                agent {
                    kubernetes {
                        label 'eci-agent-skaffold'
                    }
                }
                steps {
                    container('jnlp') {
                        checkout([$class: 'GitSCM', branches: [[name: "*/${branch}"]], userRemoteConfigs: [[url: "${repoUrl}"]]])
                        skaffold(namespace, envinfra, repoUrl, branch, targetPort, skaffoldScheme, buildEnv, dockerfile)
                    }
                }
            }
        }
    }
}

