def call() {
    echo 'Running integration tests...'
    // Example integration test steps
    sh 'mvn verify -Pintegration-tests'
}
