pipeline {
    agent any

    triggers {
        // Poll SCM every 5 minutes to check for new git commits
        pollSCM('*/5 * * * *')
    }

    environment {
        SRE_EMAIL = 'srengty@gmail.com'
    }

    stages {
        stage('Build & Test') {
            steps {
                sh '''
                    if [ -f pom.xml ]; then
                        project_dir="."
                    elif [ -f scratch_app/pom.xml ]; then
                        project_dir="scratch_app"
                    else
                        echo "No pom.xml found in repository root or scratch_app" >&2
                        exit 1
                    fi

                    cd "$project_dir"
                    chmod +x mvnw || true
                    mkdir -p src/main/resources/graphql-client

                    if [ -x ./mvnw ]; then
                        ./mvnw clean test -Dspring.profiles.active=test
                    else
                        mvn clean test -Dspring.profiles.active=test
                    fi
                '''
            }
        }

        stage('Deploy via Ansible') {
            steps {
                // Execute the Ansible playbook using the local system's ansible-playbook
                sh 'ansible-playbook -i inventory.ini deploy.yml -e app_branch="${BRANCH_NAME}"'
            }
        }
    }

    post {
        failure {
            script {
                // Send email notification to SRE and the culprit developer(s)
                emailext (
                    subject: "BUILD FAILED: Job '${env.JOB_NAME}' [${env.BUILD_NUMBER}]",
                    body: """<p>Build <b>FAILED</b> for job <b>${env.JOB_NAME}</b> (Build #${env.BUILD_NUMBER}).</p>
                             <p>Please check the console output log at: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                             <p>Error was detected during the build/test execution phase.</p>""",
                    mimeType: 'text/html',
                    to: "${SRE_EMAIL}",
                    recipientProviders: [culprits(), developers()]
                )
            }
        }
    }
}
