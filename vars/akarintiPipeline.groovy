// vars/akarintiPipeline.groovy
def call(Map config) {
    // Validate required parameters
    if (!config.branch) {
        error "Parameter 'branch' is required"
    }
    if (!config.repo) {
        error "Parameter 'repo' is required"
    }

    echo "Running AkarintiPipeline with config: ${config}"

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
                        checkout([$class: 'GitSCM', branches: [[name: "*/${config.branch}"]], userRemoteConfigs: [[url: "${config.repo}"]]])
                    }
                }
            }
            stage('Build') {
                steps {
                    echo "Building project branch: ${config.branch}"
                    container('jnlp') {
                        sh 'skaffold run -vdebug -n test --tail'
                    }
                }
            }
            // Conditional Unit Test Stage
            stage('Unit Test') {
                when {
                    expression { config.tests?.unit?.enabled == 'yes' }
                }
                steps {
                    script {
                        def testHelper = new org.foo.TestHelper(this)
                        testHelper.runUnitTests(config.tests.unit.framework)
                    }
                }
            }
            // Conditional Integration Test Stage
            stage('Integration Test') {
                when {
                    expression { config.tests?.integration?.enabled == 'yes' }
                }
                steps {
                    script {
                        def testHelper = new org.foo.TestHelper(this)
                        testHelper.runIntegrationTests(config.tests.integration.framework)
                    }
                }
            }
        }
    }
}
