// vars/akarintiPipeline.groovy
def call(Map config) {

pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            metadata:
              namespace: jenkins-agent
              labels:
                jenkins: agent
            spec:
              tolerations:
              - key: virtual-kubelet.io/provider
                operator: Equal
                value: alibabacloud
                effect: NoSchedule
              affinity:
                nodeAffinity:
                  requiredDuringSchedulingIgnoredDuringExecution:
                    nodeSelectorTerms:
                    - matchExpressions:
                      - key: kubernetes.io/hostname
                        operator: In
                        values:
                        - virtual-kubelet-ap-southeast-5a
                        - virtual-kubelet-ap-southeast-5b
              imagePullSecrets:
              - name: docker-registry-secret
              containers:
              - name: jnlp
                image: aitops/tools:skaffold-vault
                tty: true
                env:
                - name: KUBECONFIG
                  value: /kubeconfig/kubeconfig
                - name: VAULT_TOKEN
                  valueFrom:
                    secretKeyRef:
                      name: hashicorp-secret
                      key: vault-token
                - name: VAULT_ADDR
                  value: http://vault.vault.svc.cluster.local:8200
                volumeMounts:
                - name: kubeconfig-secret
                  mountPath: /kubeconfig
                resources:
                  requests:
                    cpu: "250m"
                    memory: "256Mi"
                  limits:
                    cpu: "500m"
                    memory: "512Mi"
              volumes:
              - name: kubeconfig-secret
                secret:
                  secretName: kubeconfig-secret
            """
        }
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
            }
        }
    }
}


}
