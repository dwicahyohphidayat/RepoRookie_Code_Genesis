package org.akarintitech

static Map<String, String> preprocess(String repoUrl, String branch, int targetPort) {
    def repoName = repoUrl.tokenize('/').last().replace('.git', '').replace('_', '-').toLowerCase()
    def firstWord = repoName.tokenize('-').first().toLowerCase()
    def remainingWords = repoName.tokenize('-').drop(1).join('-').toLowerCase()

    def variables = [
        AITPROJNAME: "${repoName}-${branch.toLowerCase()}",
        AITGITCLIENT: firstWord,
        AITGITSERVICE: remainingWords,
        AITGITNAME: repoName,
        AITGITBRANCH: branch.toLowerCase(),
        AITTARGETPORT: targetPort.toString()
    ]

    return variables
}

    static String replaceVariables(String content, Map<String, String> variables) {
        variables.each { key, value ->
            content = content.replace("\${${key}}", value)
        }
        return content
    }
}
