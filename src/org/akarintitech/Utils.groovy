package org.akarintitech

import groovy.yaml.YamlSlurper

class Utils {
    static Map readYaml(String filePath) {
        def input = new File(filePath).text
        def yaml = new YamlSlurper().parseText(input)
        return yaml
    }
}
