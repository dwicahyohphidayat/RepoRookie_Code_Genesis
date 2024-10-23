package org.akarintitech

// ConfigValidator.groovy
class ConfigValidator {
    static Map validateAndInitialize(Map config, def env) {
        if (!config.targetPort) {
            error "The 'targetPort' parameter is required"
        }
        if (config.tests?.unit?.enabled == 'yes' && config.tests?.integration?.enabled == 'yes') {
            error "Both unit and integration tests cannot be enabled at the same time."
        }

        // Initialize default values
        config.targetPort = config.targetPort ?: 3000
        config.skaffold = config.skaffold ?: "pre-defined"
        config.buildEnv = config.buildEnv ?: "no"
        config.dockerfile = config.dockerfile ?: "Dockerfile"        
        config.sonarscan = config.sonarscan ?: "no"

        // validate config.sonarscan value
        if (config.sonarscan != "yes" && config.sonarscan != "no") {
           throw new IllegalArgumentException("Invalid value for config.sonarscan: ${config.sonarscan}. Allowed values are 'yes' or 'no'.")
        }

        // validate config.buildEnv value
        if (config.buildEnv != "yes" && config.buildEnv != "no") {
           throw new IllegalArgumentException("Invalid value for config.buildEnv: ${config.buildEnv}. Allowed values are 'yes' or 'no'.")
        }

        // Define the allowed Skafold values
        def allowedSkaffoldValues = ["user-defined", "pre-defined"]

        if (!allowedSkaffoldValues.contains(config.skaffold)) {
            error "Invalid 'config.skaffold' value. Must be one of: ${allowedSkaffoldValues.join(', ')}"
        }

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
        
        config.namespace = config.namespace ?: config.infra
        config.envinfra = config.infra
        
        // Validate repoUrl and branch
        def repoUrl = env.GIT_URL ?: config.repo
        def branch = env.GIT_BRANCH ?: config.branch

        if (!repoUrl) {
            error "Both 'env.GIT_URL' and 'config.repo' are empty. One of them must be provided."
        }

        if (!branch) {
            error "Both 'env.GIT_BRANCH' and 'config.branch' are empty. One of them must be provided."
        }

        def allowedBranchPattern = ~/^(dev|development|staging|main|master|aws-.*|alicloud-.*|azure-.*|gcp-.*)$/
        
        if (!branch.matches(allowedBranchPattern)) {
            error "Branch '${branch}' is not allowed. Allowed branches are: dev, development, staging, main, master, aws-*, alicloud-*, azure-*, gcp-*."
        }

        config.repoUrl = repoUrl
        config.branch = branch

        return config
    }
}
