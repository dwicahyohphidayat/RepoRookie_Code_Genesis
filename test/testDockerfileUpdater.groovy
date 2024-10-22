// test/testDockerfileUpdater.groovy

// Load the vaultUtils script
this.class.classLoader.addClasspath('vars')
def vaultUtils = new GroovyShell().parse(new File('vars/vaultUtils.groovy'))

// Load the dockerfileUpdater script
def dockerfileUpdater = new GroovyShell().parse(new File('vars/dockerfileUpdater.groovy'))

// Set vaultUtils in the binding of dockerfileUpdater
dockerfileUpdater.binding.setVariable('vaultUtils', vaultUtils)

// Mock vault data in YAML format
def mockVaultData = """
data:
  data:
    VAR1: value1
    VAR2: value2
"""

// Mock Dockerfile content
def mockDockerfileContent = """
FROM ubuntu:latest
RUN apt-get update && apt-get install -y curl
"""

// Write mock data to temporary files
def vaultPath = "mockVaultData.yaml"
def dockerfilePath = "Dockerfile"

new File(vaultPath).text = mockVaultData
new File(dockerfilePath).text = mockDockerfileContent

// Call the dockerfileUpdater function
dockerfileUpdater.call(vaultPath, dockerfilePath)

// Read and print the updated Dockerfile content
def updatedDockerfileContent = new File(dockerfilePath).text
println "Updated Dockerfile Content:"
println updatedDockerfileContent

