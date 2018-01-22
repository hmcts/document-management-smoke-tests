#!groovy

properties([
    [
        $class: 'GithubProjectProperty',
        displayName: 'Document Management Smoke Tests',
        projectUrlStr: 'https://github.com/hmcts/document-management-smoke-tests/'
    ],
    pipelineTriggers([
        [$class: 'GitHubPushTrigger']
    ])
])

@Library('Reform') _

String channel = '#dm-pipeline'

def branchName = ('master' == "${env.BRANCH_NAME}") ? "${env.BRANCH_NAME}" : "${env.CHANGE_BRANCH}"

node {
    try{

        stage('Checkout') {
            deleteDir()
            echo sh(returnStdout: true, script: 'env')
            checkout scm
        }

        try {
            stage('Start App with Docker') {
                sh "docker-compose -f docker-compose.yml -f docker-compose-test.yml pull && docker-compose up --build -d"
            }

            stage('Run Smoke tests in docker') {
                sh "docker-compose -f docker-compose.yml -f docker-compose-test.yml run -e GRADLE_OPTS -e http_proxy -e https_proxy -e no_proxy document-management-store-smoke-tests"
            }
        }
        catch (e){
            throw e
        }
        finally {
            sh "docker-compose logs --no-color > logs.txt"
            archiveArtifacts 'logs.txt'
            sh "docker-compose down"
        }

//        stage('Run against Test') {
//            sh 'echo Running Smoke tests on Test'
//            build job: 'evidence/smoke-tests-pipeline/master', parameters: [
//                [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: 'test'],
//               [$class: 'StringParameterValue', name: 'BRANCH', value: branchName]
//            ]
//        }

        if ('master' == "${env.BRANCH_NAME}") {
            stage('Publish Docker') {
                dockerImage([imageName: 'evidence/smoke-tests'])
            }
        }

    } catch (e){
        notifyBuildFailure channel: channel
        throw e
    }
    notifyBuildFixed channel: channel
}
