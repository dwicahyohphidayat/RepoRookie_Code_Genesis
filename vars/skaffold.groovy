def call(String namespace, String envinfra, String repoUrl, String branch, int targetPort, String skaffold, String buildEnv, String dockerfile) {
    if (skaffold == "pre-defined") {

        // Preprocess variables
        def variables = org.akarintitech.Preprocessor.preprocess(namespace, repoUrl, branch, targetPort, dockerfile)

        // Create vault path if not exists
        def vaultPath = "akarintitech/${variables.AITPROJNAME}"
        def initData = "TZ=Asia/Jakarta"
        vault(vaultPath, initData)

        // Check if env need to be inserted during build process
        if (buildEnv == "yes") {
           dockerfileUpdater(vaultPath, dockerfile)
        }

        // Create the k8s/${envinfra} directory in the workspace
        def k8sDir = "k8s/${envinfra}"
        sh "mkdir -p ${k8sDir}"

        // Write the content of skaffold.yaml to a temporary file
        def skaffoldYamlContent = org.akarintitech.Preprocessor.replaceVariables(libraryResource('template/k8s/skaffold.yaml'), variables)
        def tempSkaffoldFile = 'temp-skaffold.yaml'
        writeFile file: tempSkaffoldFile, text: skaffoldYamlContent

        // Write the content of deployment.yaml, service.yaml, and externalsecret.yaml to temporary files in the k8s/${envinfra} directory
        def deploymentYamlContent = org.akarintitech.Preprocessor.replaceVariables(libraryResource("template/k8s/${envinfra}/deployment.yaml"), variables)
        def serviceYamlContent = org.akarintitech.Preprocessor.replaceVariables(libraryResource("template/k8s/${envinfra}/service.yaml"), variables)
        def externalsecretYamlContent = org.akarintitech.Preprocessor.replaceVariables(libraryResource("template/k8s/${envinfra}/externalsecret.yaml"), variables)
        def tempDeploymentFile = "${k8sDir}/3temp-deployment.yaml"
        def tempServiceFile = "${k8sDir}/2temp-service.yaml"
        def tempExternalsecretFile = "${k8sDir}/1temp-externalsecret.yaml"
        writeFile file: tempDeploymentFile, text: deploymentYamlContent
        writeFile file: tempServiceFile, text: serviceYamlContent
        writeFile file: tempExternalsecretFile, text: externalsecretYamlContent

        // Write the content of secret.yaml to temporary files in the k8s/${envinfra} directory
        def secretYamlContent = libraryResource("manifests/secret.yaml")
        def tempSecretFile = "${k8sDir}/0temp-secret.yaml"
        writeFile file: tempSecretFile, text: secretYamlContent

        try {
            // Run Skaffold using the temporary files
            sh "skaffold run -f ${tempSkaffoldFile} --namespace ${namespace}"
        } finally {
            // Ensure the temporary files are deleted
            sh "rm -f ${tempSkaffoldFile} ${tempDeploymentFile} ${tempServiceFile} ${tempExternalsecretFile}"
        }
    } else {
        // Run Skaffold with the default command
        sh "skaffold run --namespace ${namespace}"
    }
}
