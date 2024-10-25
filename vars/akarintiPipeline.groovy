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
            stage('Test') {
                when {
                    expression { config.sonarscan == 'yes' && (config.tests?.unit?.enabled == 'yes' || config.tests?.integration?.enabled == 'yes') } 
                }
                steps {
                    container('test') {
                        codetest(config, this)
                    }        
                }
            }
            stage('Sonar Scan') {
                when {
                    expression { config.sonarscan == 'yes' }
                }
                steps {
                    container('jnlp') {
                        sonarscan(config.repoUrl)
                    }
                }
            }
            stage('Build and Deploy') {
                when {
                    expression { config.runbuild == 'yes' }
                }
                steps {
                    container('jnlp') {
                        skaffold(config)
                    }
                }
            }
        }
    }
}
