// Run cross OS commands
def xCommand(command) {
    if(isUnix()){
        sh command
    }else{
        bat command
    }
}

pipeline {
    agent any
    environment {
        GIT_REPO_URL    = 'https://github.com/Leandro-Sousa-1190800/ddd-forum-odsoft'
        GIT_REPO_BRANCH = 'main'
    }
    tools {
            nodejs 'NodeJS 22.9.0'
        }
    triggers {
        pollSCM('H/5 * * * *')
        }
    stages {
        stage('Pull Repository'){
            steps{
                echo('Pulling repo...')
                git branch: "${GIT_REPO_BRANCH}",
                    url: "${GIT_REPO_URL}"
                echo("Repository pulled successfully!\nRepository URL: ${GIT_REPO_URL}\nRepository Branch: ${GIT_REPO_BRANCH}")
            }
        }
        stage('Build'){
            steps{
                //xCommand('mvn clean install')
                echo('Cleaning...')
                xCommand('mvn clean')
                echo('Cleaning completed!\nValidating project...')
                xCommand('mvn validate')
                echo('Validation completed!\nCompiling...')
                xCommand('mvn compile')
                echo('Compiled successfully!')
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                xCommand('mvn verify') // - performs any integration tests that maven finds in the project
                xCommand('mvn test') //
                echo 'Testing done!'
            }
        }
        stage('Dependency Analysis') {
            steps {
                echo 'Checking project for unused dependencies..'
                xCommand('mvn dependency:analyze') // - checks for unused declared or used undeclared dependencies
                echo 'Dependency checks concluded!'
            }
        }
        stage('Package') {
            steps {
                xCommand('mvn -DskipTests package') // - builds maven project and creates JAR/WAR files skipping tests
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
                xCommand('mvn deploy')
            }
        }
    }
}