pipeline {
    agent any

    triggers {
        // Poll SCM every 5 minutes to check for new git commits
        pollSCM('*/5 * * * *')
    }

    environment {
        // Force the use of Java 21, which is installed on the host
        JAVA_HOME = '/usr/lib/jvm/java-1.21.0-openjdk-amd64'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
        SRE_EMAIL = 'srengty@gmail.com'
    }

    stages {
        stage('Build & Test') {
            steps {
                dir('scratch_app') {
                    // Make mvnw executable (if needed) and run tests against the test profile
                    sh 'chmod +x mvnw || true'
                    sh './mvnw clean test -Dspring.profiles.active=test'
                }
            }
        }

        stage('Deploy via Ansible') {
            steps {
                // Execute the Ansible playbook using the local system's ansible-playbook
                sh 'ansible-playbook -i inventory.ini deploy.yml'
            }
        }
    }

    post {
        failure {
            script {
                // Send email notification to the culprit developer(s) and CC the SRE
                emailext (
                    subject: "BUILD FAILED: Job '${env.JOB_NAME}' [${env.BUILD_NUMBER}]",
                    body: """<p>Build <b>FAILED</b> for job <b>${env.JOB_NAME}</b> (Build #${env.BUILD_NUMBER}).</p>
                             <p>Please check the console output log at: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                             <p>Error was detected during the build/test execution phase.</p>""",
                    mimeType: 'text/html',
                    cc: "${SRE_EMAIL}",
                    recipientProviders: [culprits(), developers()]
                )
            }
        }
    }
}
