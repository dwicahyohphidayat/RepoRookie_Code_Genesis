// vars/akarintiPipeline.groovy
def call(Map config) {

    config = org.akarintitech.ConfigValidator.validateAndInitialize(config, env, this)
    def podTemplate = selectPodTemplate(config)

    pipeline {
        agent {
            kubernetes {
                yaml podTemplate
                showRawYaml false 
            }
        }
        stages {
            stage('Checkout') {
                steps {
                    container('jnlp') {
                        checkout([$class: 'GitSCM', branches: [[name: "*/${config.branch}"]], userRemoteConfigs: [[url: "${config.repo}"]]])
                    }
                }
            }
            stage('Sonar Scan') {
                when {
                    expression { config.sonarscan == 'yes' }
                }
                steps {
                    container('jnlp') {
                        sonarscan(config)
                    }
                    script {
                        if (config.tests?.unit?.enabled == 'yes' || config.tests?.integration?.enabled == 'yes') {
                            container('test') {
                                def testHelper = new org.akarintitech.TestHelper(this)
                                if (config.tests?.unit?.enabled == 'yes') {
                                    testHelper.runUnitTests(config.tests.unit.framework)
                                } else if (config.tests?.integration?.enabled == 'yes') {
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
                steps {
                    container('jnlp') {
                        skaffold(config)
                    }
                }
            }
        }
    }
}

