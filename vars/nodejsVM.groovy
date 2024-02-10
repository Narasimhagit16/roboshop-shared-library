def call(Map configMap) {
pipeline {
    agent {
        node {
            label 'AGENT-1'
        }
    }
    options {
        timeout(time: 1, unit : 'HOURS')
        disableConcurrentBuilds()
        ansiColor('xterm')

    }
    parameters {
    //     string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')

    //     text(name: 'BIOGRAPHY', defaultValue: '', description: 'Enter some information about the person')

           booleanParam(name: 'Deploy', defaultValue: false)

    //     choice(name: 'CHOICE', choices: ['One', 'Two', 'Three'], description: 'Pick something')

    //     password(name: 'PASSWORD', defaultValue: 'SECRET', description: 'Enter a password')
    }

    environment{
        packageVersion=""
        // it is defined in pipeline globals
        //nexusURL="172.31.10.115:8081"
    }
    stages {
        stage('Get the Package Version') {
            steps {
                script{
                    def packageJson = readJSON file: 'package.json'
                    packageVersion = packageJson.version

                }
            }
        }
        stage('Install Dependencies') {
            steps {
               sh """
                echo "${packageVersion}"
                 npm install
               """
            }
        }

        stage('Unit Testing') {
            steps {
               sh """
                echo "Unit test"
               """
            }
        }

        stage('Sonar Scan'){
            steps {
                sh """
                  echo "we are skipping sonar-scanner"
                 """
            }
        }
        stage('Build'){
            steps {
                sh """
                ls -la
                zip -q -r ${configMap.component} ./* -x ".git" -x "*.zip"
                ls -ltr
                 """
            }
        }
        stage('Publish Artifacts'){
            steps {
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: pipelineGlobals.nexusURL(),
                    groupId: 'com.roboshop',
                    version: "${packageVersion}",   
                    repository: "${configMap.component}",
                    credentialsId: 'nexus-auth',
                    artifacts: [
                        [artifactId: "${configMap.component}",
                        classifier: '',
                        file: "${configMap.component}.zip",
                        type: 'zip']
                    ]
                )
            }
        }
        stage('Deploy'){
            when {
                expression{
              params.Deploy
            }
            }
                steps {
                    script{
                        def params = [
                            string(name:'version', value: "$packageVersion"),
                            string(name:'environment', value: "dev")
                            ]
                        build job: "${configMap.component}-deploy" , wait : true, parameters : params
                        }
                }
        }

    }
    post { 
        always { 
            echo 'I will always say Hello again Narasimha!'
            deleteDir()
        }
        failure { 
            echo 'Pipeline failed!'
        }
        success { 
            echo 'I will always say when success'
        } 
    }
 }
}
