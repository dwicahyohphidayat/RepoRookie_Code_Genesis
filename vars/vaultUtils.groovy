// vars/vaultUtils.groovy

import org.yaml.snakeyaml.Yaml

def getDataAsYaml(String vaultPath) {
    def script = """
    #!/bin/bash
    export PATH=\$PATH:/usr/local/bin
    vault kv get -format=yaml ${vaultPath}
    """
    def yamlOutput = sh(script: script, returnStdout: true).trim()
    return yamlOutput
}

def parseYaml(String yamlData) {
    def yaml = new Yaml()
    def dataMap = yaml.load(yamlData)
    return dataMap
}
