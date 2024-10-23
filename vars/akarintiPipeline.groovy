// vars/akarintiPipeline.groovy
def call(Map config) {

    def podTemplate = libraryResource('template/pod/build.yaml')    

    pipeline {
        agent {
            kubernetes {
                label 'eci-jenkins-agent'
                yaml podTemplate
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
