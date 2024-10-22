def call(String vaultPath, String initData) {
    println "Executing vault command"
    println "Vault Path: ${vaultPath}"
    println "Init Data: ${initData}"

    // Function to check if the Vault path exists
    def pathExists = { path ->
        def script = """
        #!/bin/bash
        export PATH=\$PATH:/usr/local/bin
        vault kv get ${path}
        """
        def result = sh(script: script, returnStatus: true, returnStdout: false)
        return result == 0
    }

    // Function to put data into Vault if the path does not exist
    def putDataIfNotExists = { path, data ->
        if (!pathExists(path)) {
            def script = """
            #!/bin/bash
            export PATH=\$PATH:/usr/local/bin
            vault kv put ${path} ${data}
            """
            def result = sh(script: script, returnStatus: true, returnStdout: false)
            if (result == 0) {
                println "Data successfully written to ${path}"
            } else {
                println "Failed to write data to ${path}"
            }
        } else {
            println "Vault path ${path} already exists. Doing nothing."
        }
    }

    // Execute the function
    putDataIfNotExists(vaultPath, initData)
}
