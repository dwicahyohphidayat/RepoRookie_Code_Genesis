def call(Map config) {

    def namespace = config.namespace
    def envinfra = config.envinfra
    def repoUrl = config.repoUrl
    def branch = config.branch
    def targetPort = config.targetPort
    def skaffold = config.skaffold
    def buildEnv = config.buildEnv
    def dockerfile = config.dockerfile

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

        def tempSkaffoldFile = 'temp-skaffold.yaml'
        def tempDeploymentFile = "${k8sDir}/3temp-deployment.yaml"
        def tempServiceFile = "${k8sDir}/2temp-service.yaml"
        def tempExternalsecretFile = "${k8sDir}/1temp-externalsecret.yaml"
        def tempSecretFile = "${k8sDir}/0temp-secret.yaml"
    
        def filesToProcess = [
            [path: 'template/k8s/skaffold.yaml', tempFile: tempSkaffoldFile],
            [path: "template/k8s/${envinfra}/deployment.yaml", tempFile: tempDeploymentFile],
            [path: "template/k8s/${envinfra}/service.yaml", tempFile: tempServiceFile],
            [path: "template/k8s/${envinfra}/externalsecret.yaml", tempFile: tempExternalsecretFile],
            [path: 'manifests/secret.yaml', tempFile: tempSecretFile, preprocess: false]
        ]
    
        filesToProcess.each { file ->
            def content = file.preprocess == false ? libraryResource(file.path) : replaceAITVars(file.path, variables)
            writeFile file: file.tempFile, text: content
        }

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
