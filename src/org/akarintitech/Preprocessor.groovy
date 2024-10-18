package org.akarintitech

class Preprocessor {
    static Map<String, String> preprocess(String repoUrl, String branch, int targetPort) {
        def repoName = repoUrl.tokenize('/').last().replace('.git', '').replace('_', '-')
        def firstWord = repoName.tokenize('-').first()
        def remainingWords = repoName.tokenize('-').drop(1).join('-')

        def variables = [
            AITPROJNAME: "${repoName}-${branch}",
            AITGITCLIENT: firstWord,
            AITGITSERVICE: remainingWords,
            AITGITNAME: repoName,
            AITGITBRANCH: branch,
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
