def call(String vaultPath, String initData) {
    println "Executing vault command"
    println "PATH: ${System.getenv('PATH')}"
    println "Vault Path: ${vaultPath}"
    println "Init Data: ${initData}"
    sh "vault --version"

    // Function to check if the Vault path exists
    def pathExists = { path ->
        def process = "/usr/local/bin/vault kv get ${path}".execute()
        process.waitFor()
        return process.exitValue() == 0
    }

    // Function to put data into Vault if the path does not exist
    def putDataIfNotExists = { path, data ->
        if (!pathExists(path)) {
            def command = "/usr/local/bin/vault kv put ${path} ${data}"
            def process = command.execute()
            process.waitFor()
            if (process.exitValue() == 0) {
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
