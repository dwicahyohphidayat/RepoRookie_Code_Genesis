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
    def testImage = config.testImage
    pipeline {
        agent {
            kubernetes {
                label 'eci-jenkins-agent'
                yaml libraryResource('template/pod/build.yaml')
            }
        }
        stages {
            stage('Check Resource') {
                steps {
                    script {
                        def podTemplate = libraryResource('template/pod/build.yaml')
                        echo "Pod Template: ${podTemplate}"
                    }
                }
            }
            // Add your other stages here
        }
    }
}
