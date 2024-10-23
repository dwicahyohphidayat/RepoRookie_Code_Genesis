def call(Map config) {

    // Load the pod template YAML content
    def podTemplate = libraryResource('template/pod/build.yaml')

    pipeline {
        agent {
            kubernetes {
                yaml podTemplate
            }
        }
        stages {
            stage('Check Resource') {
                steps {
                    script {
                        // Print the pod template content for debugging
                        echo "Pod Template: ${podTemplate}"
                    }
                }
            }
            // Add your other stages here
        }
    }
}

