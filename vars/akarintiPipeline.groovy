def call(Map config) {
    // Validate required parameters
    if (!config.branch) {
        error "Parameter 'branch' is required"
    }
    if (!config.repo) {
        error "Parameter 'repo' is required"
    }
//    if (!config.deploy_branch_regex) {
//        error "Parameter 'deploy_branch_regex' is required"
//    }
//    if (!config.application) {
//        error "Parameter 'application' is required"
//    }
//    if (!config.application.tribe) {
//        error "Parameter 'application.tribe' is required"
//    }
//    if (!config.application.squad) {
//        error "Parameter 'application.squad' is required"
//    }
//    if (!config.application.service_name) {
//        error "Parameter 'application.service_name' is required"
//    }
//    if (!config.application.deployment_repo) {
//        error "Parameter 'application.deployment_repo' is required"
//    }
//    if (!config.sonar) {
//        error "Parameter 'sonar' is required"
//    }
//    if (!config.sonar.serverId) {
//        error "Parameter 'sonar.serverId' is required"
//    }
//    if (!config.test) {
//        error "Parameter 'test' is required"
//    }
//    if (!config.test.integration) {
//        error "Parameter 'test.integration' is required"
//    }
//    if (!config.test.integration.elasticsearch) {
//        error "Parameter 'test.integration.elasticsearch' is required"
//    }
//
//    // Print configuration for debugging
    echo "Running AkarintiPipeline with config: ${config}"
//
//    // Example logic using the parameters
    stage('Checkout') {
        steps {
            container('jnlp') {
                checkout([$class: 'GitSCM', branches: [[name: "*/${config.branch}"]], userRemoteConfigs: [[url: "${config.repo}"]]])
            }
        }
    }

    stage('Build') {
        echo "Building project branch: ${config.dev}"
        steps {
            container('jnlp') {
                sh 'skaffold run -vdebug -n test --tail'
            }
        }
    }
//
//    stage('Test') {
//        echo "Running tests with Elasticsearch version: ${config.test.integration.elasticsearch.version}"
//        // Add test steps here
//    }
//
//    stage('SonarQube Analysis') {
//        echo "Running SonarQube analysis on server: ${config.sonar.serverId}"
//        // Add SonarQube analysis steps here
//    }
//
//    stage('Deploy') {
//        if (env.BRANCH_NAME ==~ config.deploy_branch_regex) {
//            echo "Deploying to branch: ${env.BRANCH_NAME}"
//            // Add deployment steps here
//        } else {
//            echo "Skipping deployment for branch: ${env.BRANCH_NAME}"
//        }
//    }
}
