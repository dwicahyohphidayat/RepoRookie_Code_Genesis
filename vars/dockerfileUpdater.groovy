// vars/dockerfileUpdater.groovy

def call(String vaultPath, String dockerfilePath) {
    // Retrieve and print the data in YAML format
    def yamlData = vaultUtils.getDataAsYaml(vaultPath)
    println "Retrieved YAML Data:"
    println yamlData

    // Parse the YAML data
    def dataMap = vaultUtils.parseYaml(yamlData)
    println "Parsed Data Map:"
    println dataMap

    // Extract the data section
    def envVars = dataMap.data.data

    // Convert the data to ARG and ENV format, adding a new line at the end
    def dockerfileArgs = envVars.collect { key, value ->
        "ARG ${key}=${value}\nENV ${key}=${value}"
    }.join("\n") + "\n"

    println "Dockerfile Arguments and Environment Variables:"
    println dockerfileArgs

    // Insert the ARG and ENV lines into the Dockerfile
    def dockerfileContent = new File(dockerfilePath).text
    def updatedDockerfileContent = dockerfileContent.replaceFirst(/(?m)^FROM .+$/, "\$0\n${dockerfileArgs}")

    // Write the updated content back to the Dockerfile
    new File(dockerfilePath).text = updatedDockerfileContent
}
