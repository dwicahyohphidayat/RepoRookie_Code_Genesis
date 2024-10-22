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
        def process = new ProcessBuilder("/bin/bash", "-c", script).start()
        process.waitFor()
        return process.exitValue() == 0
    }

    // Function to put data into Vault if the path does not exist
    def putDataIfNotExists = { path, data ->
        if (!pathExists(path)) {
            def script = """
            #!/bin/bash
            export PATH=\$PATH:/usr/local/bin
            vault kv put ${path} ${data}
            """
            def process = new ProcessBuilder("/bin/bash", "-c", script).start()
            process.waitFor()
            if (process.exitValue() == 0) {
                println "Data successfully written to ${path}"
            } else {
                println "Failed to write data to ${path}"
                println process.errorStream.text
            }
        } else {
            println "Vault path ${path} already exists. Doing nothing."
        }
    }

    // Execute the function
    putDataIfNotExists(vaultPath, initData)
}
