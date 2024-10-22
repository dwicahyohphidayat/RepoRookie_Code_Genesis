package org.akarintitech

// ConfigValidator.groovy
class ConfigValidator {
    static Map validateAndInitialize(Map config, def env) {
        if (!config.targetPort) {
            error "The 'targetPort' parameter is required"
        }
        if (config.tests?.unit?.enable == 'yes' && config.tests?.integration?.enable == 'yes') {
            error "Both unit and integration tests cannot be enabled at the same time."
        }

        // Initialize default values
        config.namespace = config.infra
        config.envinfra = config.infra
        config.targetPort = config.targetPort ?: 3000

        // Validate env infra
        def validInfras = [
            'ait-internal-dev',
            'ait-internal-stg',
            'ait-internal-prd',
            'ait-product-dev',
            'ait-product-prd'
        ]
        
        if (!validInfras.contains(config.infra)) {
            error "Invalid 'config.infra' value. Must be one of: ${validInfras.join(', ')}"
        }
        
        // Validate repoUrl and branch
        def repoUrl = env.GIT_URL ?: config.repo
        def branch = env.GIT_BRANCH ?: config.branch

        if (!repoUrl) {
            error "Both 'env.GIT_URL' and 'config.repo' are empty. One of them must be provided."
        }

        if (!branch) {
            error "Both 'env.GIT_BRANCH' and 'config.branch' are empty. One of them must be provided."
        }

        config.repoUrl = repoUrl
        config.branch = branch

        return config
    }
}
