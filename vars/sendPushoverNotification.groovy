/**
 * Send Pushover notification
 * @param message The notification message to send
 * @param title Optional title (defaults to 'Jenkins')
 * @param priority Optional priority (-2 to 2, defaults to 0)
 * @param sound Optional sound (defaults to 'R2D2')
 */
def call(String message, String title = 'Jenkins', int priority = 0, String sound = 'R2D2') {
  withCredentials([
    string(credentialsId: 'pushover-user-token', variable: 'PUSHOVER_USER'),
    string(credentialsId: 'pushover-jenkins-token', variable: 'PUSHOVER_APP')
  ]) {
    try {
      echo "📱 Sending Pushover notification..."

      def buildUrl = env.BUILD_URL ?: ''

      sh """
        curl -s -f \
          -F "token=${PUSHOVER_APP}" \
          -F "user=${PUSHOVER_USER}" \
          -F "title=${title}" \
          -F "message=${message}" \
          -F "sound=${sound}" \
          -F "priority=${priority}" \
          -F "url=${buildUrl}" \
          "https://api.pushover.net/1/messages.json" > /dev/null
      """

      echo "✅ Pushover notification sent successfully"
    } catch (Exception e) {
      echo "⚠️ Warning: Failed to send Pushover notification: ${e.message}"
      // Don't fail the build if notification fails
    }
  }
}