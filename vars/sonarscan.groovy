def call(Map config) {
    sh "echo $JAVA_HOME"
    sh "echo $PATH"
    sh "java -version"
    sh "sonar-scanner"
}
