pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9.11' // Make sure this matches your Maven installation name in Jenkins
        jdk 'JDK-11' // Make sure this matches your JDK installation name in Jenkins
    }
    
    environment {
        // Project specific variables
        PROJECT_NAME = 'MLX_API_Automation'
        MAVEN_OPTS = '-Xmx1024m'
        
        // Email notification settings
        EMAIL_RECIPIENTS = 'your-email@example.com' // Update with your email
        
        // Report paths
        EXTENT_REPORT_PATH = 'test-output/ExtentReports/'
        SUREFIRE_REPORT_PATH = 'target/surefire-reports/'
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo '========== Checking out code from GitHub =========='
                    checkout scm
                }
            }
        }
        
        stage('Clean Workspace') {
            steps {
                script {
                    echo '========== Cleaning previous build artifacts =========='
                    bat 'mvn clean'
                }
            }
        }
        
        stage('Compile') {
            steps {
                script {
                    echo '========== Compiling the project =========='
                    bat 'mvn compile'
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    echo '========== Running API Automation Tests =========='
                    try {
                        bat 'mvn test'
                    } catch (Exception e) {
                        echo "Tests failed but continuing to generate reports"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                script {
                    echo '========== Publishing Test Reports =========='
                    
                    // Publish TestNG/Surefire XML reports
                    junit allowEmptyResults: true, 
                          testResults: '**/target/surefire-reports/*.xml'
                    
                    // Archive Extent HTML Reports
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'test-output/ExtentReports',
                        reportFiles: '*.html',
                        reportName: 'MLX API Extent Report',
                        reportTitles: 'MLX Order API Test Results'
                    ])
                    
                    // Archive artifacts
                    archiveArtifacts artifacts: '**/test-output/ExtentReports/*.html', 
                                     fingerprint: true,
                                     allowEmptyArchive: true
                }
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                script {
                    echo '========== Running Code Quality Checks =========='
                    // Optional: Add SonarQube or other code quality tools here
                    echo 'Code quality analysis can be integrated here'
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo '========== Cleaning up workspace =========='
                // Clean up workspace if needed
            }
        }
        
        success {
            script {
                echo '========== Build Successful =========='
                emailext(
                    subject: "✅ SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                    body: """
                        <html>
                        <body style="font-family: Arial, sans-serif;">
                            <h2 style="color: #28a745;">✅ Build Successful</h2>
                            <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                            <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                            <p><strong>Test Report:</strong> <a href="${env.BUILD_URL}MLX_API_Extent_Report/">View Extent Report</a></p>
                            <p><strong>Status:</strong> <span style="color: green; font-weight: bold;">SUCCESS</span></p>
                            <hr>
                            <p style="font-size: 12px; color: #666;">
                                This is an automated message from Jenkins CI/CD Pipeline.
                            </p>
                        </body>
                        </html>
                    """,
                    mimeType: 'text/html',
                    to: "${env.EMAIL_RECIPIENTS}",
                    attachLog: true
                )
            }
        }
        
        failure {
            script {
                echo '========== Build Failed =========='
                emailext(
                    subject: "❌ FAILURE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                    body: """
                        <html>
                        <body style="font-family: Arial, sans-serif;">
                            <h2 style="color: #dc3545;">❌ Build Failed</h2>
                            <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                            <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                            <p><strong>Console Output:</strong> <a href="${env.BUILD_URL}console">View Console</a></p>
                            <p><strong>Status:</strong> <span style="color: red; font-weight: bold;">FAILURE</span></p>
                            <hr>
                            <p><strong>Action Required:</strong> Please check the console output for error details.</p>
                            <p style="font-size: 12px; color: #666;">
                                This is an automated message from Jenkins CI/CD Pipeline.
                            </p>
                        </body>
                        </html>
                    """,
                    mimeType: 'text/html',
                    to: "${env.EMAIL_RECIPIENTS}",
                    attachLog: true
                )
            }
        }
        
        unstable {
            script {
                echo '========== Build Unstable (Some Tests Failed) =========='
                emailext(
                    subject: "⚠️ UNSTABLE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                    body: """
                        <html>
                        <body style="font-family: Arial, sans-serif;">
                            <h2 style="color: #ffc107;">⚠️ Build Unstable</h2>
                            <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                            <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                            <p><strong>Test Report:</strong> <a href="${env.BUILD_URL}MLX_API_Extent_Report/">View Extent Report</a></p>
                            <p><strong>Status:</strong> <span style="color: orange; font-weight: bold;">UNSTABLE (Some tests failed)</span></p>
                            <hr>
                            <p><strong>Note:</strong> Some tests failed but the build completed. Check the test reports for details.</p>
                            <p style="font-size: 12px; color: #666;">
                                This is an automated message from Jenkins CI/CD Pipeline.
                            </p>
                        </body>
                        </html>
                    """,
                    mimeType: 'text/html',
                    to: "${env.EMAIL_RECIPIENTS}",
                    attachLog: true
                )
            }
        }
    }
}
