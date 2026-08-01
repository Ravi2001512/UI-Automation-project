pipeline {
    agent any

    stages {
        stage('Check Environment') {
            steps {
                sh '''
                    echo "JAVA_HOME=$JAVA_HOME"
                    which java
                    java -version
                    which javac
                    javac -version
                    mvn -version
                '''
            }
        }

        stage('Run UI Tests') {
            steps {
                sh "mvn clean test -Dsurefire.suiteXmlFiles=${params.TEST_SUITE}"
            }
        }
    }
}