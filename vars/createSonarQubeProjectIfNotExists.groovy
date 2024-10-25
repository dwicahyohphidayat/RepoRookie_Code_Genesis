// vars/createSonarQubeProjectIfNotExists.groovy
import groovy.json.JsonSlurper

def call(String projectName, String mainBranchName) {
    script {
        def sonarQubeUrl = env.SONAR_HOST_URL
        def sonarQubeToken = env.SONAR_TOKEN + ':'
        def sonarQubeApiUrl = "${sonarQubeUrl}/api/projects/search?projects=${projectName}"
        def projectExists = false

        // Ensure the HTTP Request plugin is available
        // Check if the project exists on SonarQube
        def response = httpRequest(
            url: sonarQubeApiUrl,
            customHeaders: [[name: 'Authorization', value: "Basic ${sonarQubeToken.bytes.encodeBase64().toString()}"]],
            validResponseCodes: '200'
        )

        def jsonResponse = new JsonSlurper().parseText(response.content)
        projectExists = jsonResponse.components.size() > 0

        if (!projectExists) {
            // Create the project on SonarQube
            def createProjectUrl = "${sonarQubeUrl}/api/projects/create"
            def createProjectResponse = httpRequest(
                url: createProjectUrl,
                httpMode: 'POST',
                customHeaders: [[name: 'Authorization', value: "Basic ${sonarQubeToken.bytes.encodeBase64().toString()}"]],
                requestBody: "name=${projectName}&project=${projectName}&branch=${mainBranchName}",
                contentType: 'APPLICATION_FORM'
            )

            echo "Project '${projectName}' created on SonarQube with main branch '${mainBranchName}'."
        } else {
            echo "Project '${projectName}' already exists on SonarQube."
        }
    }
}

