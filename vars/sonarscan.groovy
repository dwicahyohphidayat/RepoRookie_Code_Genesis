def call(Map config) {
    createSonarQubeProjectIfNotExists('Your_Project_Name', 'main')
    sh "sonar-scanner"
}
