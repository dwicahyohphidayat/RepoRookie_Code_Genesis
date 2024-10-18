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

    def namespace = 'test'
    def repoUrl = env.GIT_URL // Retrieve the repository URL from Jenkins environment variables
    def branch = env.GIT_BRANCH // Retrieve the branch name from Jenkins environment variables
    def targetPort = 3000 // Example target port

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

