def call(String repoUrl) {
    def repoName = repoUrl.tokenize('/').last().replace('.git', '').replace('_', '-').toLowerCase()
    createSonarQubeProjectIfNotExists(repoName, 'scan/sonar')
    sh "sonar-scanner -Dsonar.projectKey=${repoName} -Dsonar.projectName=${repoName}"
}
