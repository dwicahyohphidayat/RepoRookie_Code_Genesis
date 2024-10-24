def call(Map config) {
    sh 'pwd'
    sh 'uname -a'
    sh 'ls -alR /home/'
    sh "sonar-scanner"
}
