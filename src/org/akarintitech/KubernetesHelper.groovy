package org.akarintitech

import groovy.json.JsonSlurper

class KubernetesHelper {

    def script

    KubernetesHelper(script) {
        this.script = script
    }

    def deploy(String namespace, String deploymentYaml) {
        script.sh "kubectl apply -n ${namespace} -f ${deploymentYaml}"
    }

    def delete(String namespace, String deploymentYaml) {
        script.sh "kubectl delete -n ${namespace} -f ${deploymentYaml}"
    }

    def getPods(String namespace, String labelSelector) {
        def podsJson = script.sh(script: "kubectl get pods -n ${namespace} -l ${labelSelector} -o json", returnStdout: true).trim()
        def pods = new JsonSlurper().parseText(podsJson)
        return pods.items.collect { it.metadata.name }
    }

    def waitForPods(String namespace, String labelSelector, int timeoutSeconds = 300) {
        def startTime = System.currentTimeMillis()
        while ((System.currentTimeMillis() - startTime) < timeoutSeconds * 1000) {
            def pods = getPods(namespace, labelSelector)
            def allRunning = pods.every { pod ->
                def statusJson = script.sh(script: "kubectl get pod ${pod} -n ${namespace} -o json", returnStdout: true).trim()
                def status = new JsonSlurper().parseText(statusJson)
                status.status.phase == 'Running'
            }
            if (allRunning) {
                script.echo "All pods are running."
                return
            }
            script.echo "Waiting for pods to be in 'Running' state..."
            script.sleep(10)
        }
        script.error "Timeout waiting for pods to be in 'Running' state."
    }
}
