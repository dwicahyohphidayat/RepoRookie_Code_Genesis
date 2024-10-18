def call(String namespace) {
    echo "Deploying with Skaffold in namespace: ${namespace}..."
    sh "skaffold run -f ${libraryResource('skaffold.yaml')} --namespace ${namespace}"
}
