def call(String namespace, String envinfra) {
    // Write the content of skaffold.yaml to a temporary file
    def skaffoldYamlContent = libraryResource('manifests/skaffold.yaml')
    def tempSkaffoldFile = 'temp-skaffold.yaml'
    writeFile file: tempSkaffoldFile, text: skaffoldYamlContent

    // Create the k8s/${envinfra} directory in the workspace
    def k8sDir = "k8s/${envinfra}"
    sh "mkdir -p ${k8sDir}"

    // Write the content of deployment.yaml, service.yaml, and config.yaml to temporary files in the k8s/${envinfra} directory
    def deploymentYamlContent = libraryResource("manifests/k8s/${envinfra}/deployment.yaml")
    def serviceYamlContent = libraryResource("manifests/k8s/${envinfra}/service.yaml")
    def configYamlContent = libraryResource("manifests/k8s/${envinfra}/config.yaml")
    def tempDeploymentFile = "${k8sDir}/temp-deployment.yaml"
    def tempServiceFile = "${k8sDir}/temp-service.yaml"
    def tempConfigFile = "${k8sDir}/temp-config.yaml"
    writeFile file: tempDeploymentFile, text: deploymentYamlContent
    writeFile file: tempServiceFile, text: serviceYamlContent
    writeFile file: tempConfigFile, text: configYamlContent

    try {
        // Run Skaffold using the temporary files
        sh "skaffold run -f ${tempSkaffoldFile} --namespace ${namespace}"
    } finally {
        // Ensure the temporary files are deleted
        sh "rm -f ${tempSkaffoldFile} ${tempDeploymentFile} ${tempServiceFile} ${tempConfigFile}"
    }
}
