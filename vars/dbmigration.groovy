def call() {
    echo 'Running database migration...'
    // Example database migration steps
    sh 'flyway migrate'
}
