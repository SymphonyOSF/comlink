#!/usr/bin/env groovy

@Library('SFE-RTC-pipeline') _

node(params.JENKINS_NODE_LABEL) {
    cleanWs()
    checkout scm

    def common = load("jenkins_files/common.groovy")
    def tagParam = params.VERSION_TAG ? "--tag ${params.VERSION_TAG}" : ""

    try {
        common.withNvmVer {
            stage("Install") {
                sh "yarn install --frozen-lockfile"
            }
            stage("Step version") {
                sh "yarn run step-version set ${tagParam}"
            }
            stage("Build") {
                sh "yarn build"
            }
            stage("Unit Test") {
                sh "yarn test"
            }

            stage("Publish") {
                sh "yarn run step-version tag ${tagParam}"

                sh "yarn run env | grep registry"
                sh "yarn publish --non-interactive --verbose"

                withGitCredentials("githubaccess") {
                    sh "git push --tags"
                }
            }
        }
    } finally {
        cleanWs()
    }
}
