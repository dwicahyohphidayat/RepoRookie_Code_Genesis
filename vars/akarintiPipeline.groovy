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

    // Check for conflicting test configurations
    if (config.tests?.unit?.enable == 'yes' && config.tests?.integration?.enable == 'yes') {
        error "Both unit and integration tests cannot be enabled at the same time."
    }

    def namespace = 'ait-internal-dev'
    def envinfra = 'ait-internal-dev' // Example env infra
    def targetPort = 3000 // Example target port

    // Check Jenkins environment variables first
    def repoUrl = env.GIT_URL ?: config.repo
    def branch = env.GIT_BRANCH ?: config.branch

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

