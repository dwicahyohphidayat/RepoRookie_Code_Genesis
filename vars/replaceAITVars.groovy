def call(String filePath, Map variables = [:]) {
    def content = org.akarintitech.Preprocessor.replaceVariables(libraryResource(filePath), variables)
    return content
}
